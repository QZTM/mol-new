package com.mol.purchase.service.activiti;

import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.github.pagehelper.PageHelper;
import com.mol.config.Constant;
import com.mol.config.NotificationConfig;
import com.mol.notification.SendNotification;
import com.mol.purchase.config.ExecutorConfig;
import com.mol.purchase.config.OrderStatus;
import com.mol.purchase.entity.*;
import com.mol.purchase.entity.activiti.ActHiProcinst;
import com.mol.purchase.entity.activiti.TaskDTO;
import com.mol.purchase.entity.dingding.login.AppAuthOrg;
import com.mol.purchase.entity.dingding.login.AppUser;
import com.mol.purchase.entity.dingding.purchase.enquiryPurchaseEntity.PurchaseDetail;
import com.mol.purchase.entity.dingding.solr.fyPurchase;
import com.mol.purchase.expertClient.ExpertClient;
import com.mol.purchase.mapper.newMysql.*;
import com.mol.purchase.mapper.newMysql.dingding.activiti.ActHiCommentMapper;
import com.mol.purchase.mapper.newMysql.dingding.activiti.ActHiProcinstMapper;
import com.mol.purchase.mapper.newMysql.dingding.purchase.fyPurchaseDetailMapper;
import com.mol.purchase.mapper.newMysql.dingding.purchase.fyPurchaseMapper;
import com.mol.purchase.service.token.TokenService;
import com.mol.purchase.supplierClient.SupplierClient;
import com.mol.sms.SendMsmHandler;
import com.mol.sms.XiaoNiuMsm;
import com.mol.sms.XiaoNiuMsmTemplate;
import entity.NotificationModel;
import entity.ServiceResult;
import entity.dd.DDUser;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.validation.ProcessValidator;
import org.activiti.validation.ProcessValidatorFactory;
import org.activiti.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import com.mol.purchase.mapper.newMysql.dingding.org.AppOrgMapper;
import com.mol.purchase.mapper.newMysql.dingding.user.AppUserMapper;
import org.springframework.util.concurrent.ListenableFuture;
import tk.mybatis.mapper.entity.Example;
import util.IdWorker;
import util.TimeUtil;


import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * ClassName:ActService
 * Package:com.purchase.service.activiti
 * Description
 *
 * @date:2019/9/19 15:57
 * @author:yangjiangyan
 */
@Service
public class ActService {
    //注入为我们自动配置好的服务
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    HistoryService historyService;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    ProcessEngineConfiguration processEngineConfiguration;
    @Autowired
    ProcessEngineFactoryBean processEngine;
    @Autowired
    ProcessEngine processEngines;
    @Autowired
    private AppOrgMapper appOrgMapper;
    @Autowired
    private AppUserMapper appUserMapper;
    @Autowired
    private ActHiProcinstMapper actHiProcinstMapper;
    @Autowired
    private fyPurchaseMapper purchaseMapper;
    @Autowired
    private fyPurchaseDetailMapper purchaseDetailMapper;
    @Autowired
    private ExpertUserMapper expertUserMapper;
    @Autowired
    private ExpertRecommendMapper expertRecommendMapper;
    @Autowired
    FyQuoteMapper quoteMapper;
    @Autowired
    BdSupplierSalesmanMapper supplierSalesmanMapper;
    @Autowired
    SendNotification sendNotificationImp;
    //获取dd的token
    @Autowired
    private TokenService tokenService;
    //供应商端token
    @Autowired
    private SupplierClient supplierClient;
    //专家端token
    @Autowired
    private ExpertClient expertClient;

    @Autowired
    private QuotePayresultMapper quotePayresultMapper;
    @Autowired
    AppOrgBuyChannelApproveMiddleMapper appOrgBuyChannelApproveMiddleMapper;
    @Autowired
    AppPurchaseApproveMapper appPurchaseApproveMapper;



    private  Logger log = LoggerFactory.getLogger(ActService.class);
    /**
     * 部署流程实例
     * @param model 流程模型
     */
    public void deployByModel(BpmnModel model,String name) {
        Deployment deploy = repositoryService.createDeployment().addBpmnModel(name+"bpmn20.xml", model).deploy();
        System.out.println("部署："+deploy.getKey());
        System.out.println("部署："+deploy.getId());
        System.out.println("部署："+deploy.getName());
    }
    /**
     * 部署流程实例(按照 文件部署)
     * @param name
     * @param bpmnPath
     * @param pngPath
     */
    public void deploy(String name,String bpmnPath,String pngPath) {
        processEngines.getRepositoryService()
                .createDeployment()
                .name(name)
                .addClasspathResource(bpmnPath)
                .addClasspathResource(pngPath)
                .deploy();
    }

    /**
     * 启动流程实例
     * @param key 流程的Id
     */
    public void startProcessInstance(String key,String businessKey) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, businessKey);
        log.info("启动流程实例 buskey:"+businessKey);
        log.info("启动流程实例 流程实例 id:"+processInstance.getId());
        log.info("启动流程实例 ProcessInstanceId:"+processInstance.getProcessInstanceId());
        log.info("启动流程实例 流程定义 id: "+processInstance.getDeploymentId());
    }

    /**
     * 查询个人任务
     * @param assignee 办理人
     * @return
     */
    public List<TaskDTO> getTask(String assignee,int pageNum,int pageSize) {
        List<Task> list=taskService.createTaskQuery().taskAssignee(assignee).list();
        System.out.println("任务："+list);
        List<TaskDTO> list1 = new ArrayList<>();
        for (Task task : list){
            //查询执行实例对应的业务id
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            String businessKey = pi.getBusinessKey();

            list1.add(new TaskDTO(task.getId(),task.getName(),task.getProcessInstanceId(),task.getAssignee(),businessKey,task.getCreateTime()));

        }
        return list1;
    }

    /**
     * 完成任务
     * @param map 流程变量可选
     * @param taskId 任务Id
     */
    public void completeTask(String taskId,String processInsId,Map map,String commment,String username) {
        System.out.println("taskId:"+taskId);
        //在session中获取处理人的id，存入
        Authentication.setAuthenticatedUserId(username);//批注人的名称  一定要写，不然查看的时候不知道人物信息

        taskService.addComment(taskId,processInsId,commment);

            taskService.complete(taskId,map);
    }

    /**
     * 个人历史任务查询
     * @param assignee  办理人
     * @param processId 流程实例Id
     * @return
     */
    public List<TaskDTO> getHisTask(String assignee, String processId) {
        List<HistoricTaskInstance> list=null;
        if (assignee == null && processId != null)
            list=  historyService.createHistoricTaskInstanceQuery().processInstanceId(processId).list();
        if(assignee != null && processId == null)
            list= historyService.createHistoricTaskInstanceQuery().taskAssignee(assignee).list();
        if (assignee != null && processId != null)
            list=historyService.createHistoricTaskInstanceQuery().taskAssignee(assignee).processInstanceId(processId).list();


        List<TaskDTO> list1 = new ArrayList<>();
        for (HistoricTaskInstance task : list)
            list1.add(new TaskDTO(task.getId(),task.getName(),task.getProcessInstanceId(),task.getAssignee(),null,task.getCreateTime(),task.getEndTime()));
        return list1;
    }

    /**
     * 查询流程状态
     * @param processInstanceId 流程实例Id
     */
    public String getStatus(String processInstanceId) {
        ProcessInstance pi=runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if(pi!=null){
            return "流程还在执行";
        }
        else return "流程已结束";
    }

    /**
     * 查询当前人的组任务
     * @param assignee
     * @return
     */
    public List<TaskDTO> getGroupTask(String assignee) {
        List<Task> list=  taskService.createTaskQuery().taskCandidateUser(assignee).list();
        List<TaskDTO> list1 = new ArrayList<>();
        for (Task task : list)
            list1.add(new TaskDTO(task.getId(),task.getName(),task.getProcessInstanceId(),task.getAssignee(),null,task.getCreateTime()));
        return list1;

    }

    /**
     * 查询任务的候选者
     * @param taskId
     * @return
     */
    public List getAllAssignee(String taskId) {
        List<IdentityLink> allAssignee=taskService.getIdentityLinksForTask(taskId);
        List list=new ArrayList();
        for (IdentityLink identityLink:allAssignee) {
            list.add(identityLink.getUserId());
        }
        return list;
    }

    /**
     * 拾取组任务
     * @param taskId   指定任务Id
     * @param assignee  指定办理人
     */
    public void claim(String taskId, String assignee) {
        taskService.claim(taskId,assignee);
    }


    //生成model实例
    public BpmnModel getModel(String _processId,String _processName,List<String> _list) {
        String processId=_processId;
        String processName=_processName;
        List<String> list=_list;
        if (processId==null || processName==null|| list==null ){
            return null;
        }
        //记录线的数量
        int count=1;

        BpmnModel bpmnModel = new BpmnModel();

        //设置process
        Process  process=new Process();
        process.setId(processId);
        process.setName(processName);

        // 创建开始结束节点 并添加至process
        StartEvent startEvent= new StartEvent();
        startEvent.setId("startevent1");
        startEvent.setName("Start");

        EndEvent endEvent=new EndEvent();
        endEvent.setId("endevent1");
        endEvent.setName("End");

        process.addFlowElement(startEvent);
        process.addFlowElement(endEvent);

        //创建审核结果节点
        ServiceTask serviceTask1 = new ServiceTask();
        serviceTask1.setId("servicetask1");
        serviceTask1.setName("审核通过");

        String imp ="expression";
        serviceTask1.setImplementationType(imp);

        serviceTask1.setImplementation("#{leaveService.changeStatus(execution,'7')}");


        ServiceTask serviceTask2 = new ServiceTask();
        serviceTask2.setId("servicetask2");
        serviceTask2.setName("审核未通过");
        serviceTask2.setImplementationType("expression");
        serviceTask2.setImplementation("#{leaveService.changeStatus(execution,'8')}");




        process.addFlowElement(serviceTask1);
        process.addFlowElement(serviceTask2);



        //创建任务节点 并添加至process
        if (list.size()==0 || list==null){
            return null;
        }
        if (list.size()==1){
            //人物节点
            UserTask userTask = genarateUserTask("usertask1", "审核人1");
            userTask.setAssignee(list.get(0));
            userTask.setFormKey("result");
            process.addFlowElement(userTask);

            //排他网管
            ExclusiveGateway exclusiveGateway = genarateExclusiveGateway("exclusivegateway1", "Exclusive Gateway1");
            List<SequenceFlow> out=new ArrayList<>();
            SequenceFlow se =new SequenceFlow();
            se.setId("sf1");
            se.setName("sf1");
            out.add(se);
            exclusiveGateway.setOutgoingFlows(out);
            process.addFlowElement(exclusiveGateway);

            //线
            SequenceFlow sequenceFlow1 = genarateSequenceFlow("flow" + count++, "",  startEvent.getId(),"usertask1", "");
            SequenceFlow sequenceFlow2 = genarateSequenceFlow("flow" + count++, "", "usertask1","exclusivegateway1", "");
            SequenceFlow sequenceFlow3 = genarateSequenceFlow("flow" + count++, "", "exclusivegateway1",serviceTask1.getId(), "${result=='pass'}");
            SequenceFlow sequenceFlow4 = genarateSequenceFlow("flow" + count++, "", "exclusivegateway1",serviceTask2.getId(), "${result=='refuse'}");
            process.addFlowElement(sequenceFlow1);
            process.addFlowElement(sequenceFlow2);
            process.addFlowElement(sequenceFlow3);
            process.addFlowElement(sequenceFlow4);
        }else {
            for (int i=0 ;i<list.size();i++){
                //人物节点
                UserTask userTask = genarateUserTask("usertask"+(i+1), "审核人"+(i+1));
                userTask.setAssignee(list.get(i));
                userTask.setFormKey("result");
                process.addFlowElement(userTask);

                //排他网管
                ExclusiveGateway exclusiveGateway = genarateExclusiveGateway("exclusivegateway"+(i+1), "Exclusive Gateway"+(i+1));
                List<SequenceFlow> out=new ArrayList<>();
                SequenceFlow se =new SequenceFlow();
                se.setId("sf"+i);
                se.setName("sf"+i);
                out.add(se);
                exclusiveGateway.setOutgoingFlows(out);
                process.addFlowElement(exclusiveGateway);

                //线
                if(i==0){
                    SequenceFlow sequenceFlow1 = genarateSequenceFlow("flow" + count++, "", startEvent.getId(), "usertask"+(i+1), "");
                    SequenceFlow sequenceFlow2 = genarateSequenceFlow("flow" + count++, "", "usertask"+(i+1), exclusiveGateway.getId(), "");
                    SequenceFlow sequenceFlow3 = genarateSequenceFlow("flow" + count++, "",  exclusiveGateway.getId(),serviceTask2.getId(), "${result=='refuse'}");
                    process.addFlowElement(sequenceFlow1);
                    process.addFlowElement(sequenceFlow2);
                    process.addFlowElement(sequenceFlow3);
                }
                if (i==list.size()-1){
                    SequenceFlow sequenceFlow1 = genarateSequenceFlow("flow" + count++, "", "exclusivegateway"+i, "usertask"+(i+1), "${result=='pass'}");
                    SequenceFlow sequenceFlow2 = genarateSequenceFlow("flow" + count++, "", "usertask"+(i+1),"exclusivegateway"+(i+1), "");
                    SequenceFlow sequenceFlow3 = genarateSequenceFlow("flow" + count++, "", "exclusivegateway"+(i+1),serviceTask1.getId(), "${result=='pass'}");
                    SequenceFlow sequenceFlow4 = genarateSequenceFlow("flow" + count++, "", "exclusivegateway"+(i+1),serviceTask2.getId(), "${result=='refuse'}");
                    process.addFlowElement(sequenceFlow1);
                    process.addFlowElement(sequenceFlow2);
                    process.addFlowElement(sequenceFlow3);
                    process.addFlowElement(sequenceFlow4);
                }
                if (i>0 && i<list.size()-1){
                    SequenceFlow sequenceFlow1 = genarateSequenceFlow("flow" + count++, "", "exclusivegateway" + i , "usertask"+(i+1), "${result=='pass'}");
                    SequenceFlow sequenceFlow2 = genarateSequenceFlow("flow" + count++, "", "usertask"+(i+1), "exclusivegateway" +(i+1), "");
                    SequenceFlow sequenceFlow3 = genarateSequenceFlow("flow" + count++, "",  "exclusivegateway" + (i+1),serviceTask2.getId(), "${result=='refuse'}");
                    process.addFlowElement(sequenceFlow1);
                    process.addFlowElement(sequenceFlow2);
                    process.addFlowElement(sequenceFlow3);
                }

            }
        }


        //线  审核结果-- 结束event
        SequenceFlow s1 = genarateSequenceFlow("flow" + count++, "", serviceTask1.getId(), endEvent.getId(), "");
        SequenceFlow s2 = genarateSequenceFlow("flow" + count++, "", serviceTask2.getId(), endEvent.getId(), "");
        process.addFlowElement(s1);
        process.addFlowElement(s2);

        bpmnModel.addProcess(process);

        //验证model对象
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] bytes = bpmnXMLConverter.convertToXML(bpmnModel);
        String s = null;
        try {
            s = new String(bytes,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(s);
        ProcessValidatorFactory factory = new ProcessValidatorFactory();
        ProcessValidator va = factory.createDefaultProcessValidator();
        List<ValidationError> validate = va.validate(bpmnModel);
        if (validate.size()>0){
            throw new RuntimeException("创建流程模型出错");
        }

        return bpmnModel;
    }

    /**
     * 创建用户人物节点
     */
    private UserTask genarateUserTask(String id, String name) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setName(name);
        return userTask;
    }
    /**
     * 创建排他网关
     */
    private ExclusiveGateway genarateExclusiveGateway(String id, String name) {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId(id);
        exclusiveGateway.setName(name);
        return exclusiveGateway;
    }
    /**
     * 创建连线
     */
    private SequenceFlow genarateSequenceFlow(String id, String name,String sourceRef, String tartgetRef, String conditionExpression) {
        SequenceFlow sequenceFlow = new SequenceFlow(sourceRef,tartgetRef);
        sequenceFlow.setId(id);
        if(name != null && name != ""){
            sequenceFlow.setName(name);
        }

        if(conditionExpression !=null && conditionExpression != ""){
            sequenceFlow.setConditionExpression(conditionExpression);
        }
        return sequenceFlow;
    }

    //查询所有评论
    public List getCommentList(String taskId) {
        List list=new ArrayList();
        Task task = processEngines.getTaskService()
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        String processInstanceId = task.getProcessInstanceId();
        List<HistoricTaskInstance> slist = processEngines.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        if (slist.size()>0 && slist!= null){
            for (HistoricTaskInstance hti : slist) {
                String id = hti.getId();
                List<Comment> taskComments = taskService.getTaskComments(id);
                list.add(taskComments);
            }
        }
        return list;
    }

    //查询公司信息
    public AppAuthOrg findappAuthOrgByOrgId(String orgId) {
        AppAuthOrg org=new AppAuthOrg();
        org.setId(orgId);
        AppAuthOrg appAuthOrg = appOrgMapper.selectOne(org);
        return appAuthOrg;
    }

    //查询user
    public AppUser getAppUserByUserDingId(String userid) {
        if (userid!=null){
            Example e = new Example(AppUser.class);
            e.and().andEqualTo("ddUserId",userid);
            return  appUserMapper.selectOneByExample(e);
        }else {
            return null;
        }

    }

    //查询user
    public AppUser findAppUserById(String sendUserId) {
        AppUser user = new AppUser();
        user.setId(sendUserId);
        return  appUserMapper.selectOne(user);
    }

    public ActHiProcinst getActHiprocinstByProcInstId(String processInsId) {
        return actHiProcinstMapper.getProByProId(processInsId);
    }

    public fyPurchase findPurchaseById(String businessKey) {
        return purchaseMapper.findOneById(businessKey);
    }

    public List<PurchaseDetail> findPurchaseDetailListByPurId(String id) {
        PurchaseDetail t=new PurchaseDetail();
        t.setFyPurchaseId(id);
        return purchaseDetailMapper.select(t);
    }

    public ExpertUser findExpertById(String id) {

        return expertUserMapper.findExpertUserById(id);
    }

    public FyQuote findQuoteById(String s) {
        FyQuote t=new FyQuote();
        t.setId(s);
        return quoteMapper.selectOne(t);
    }

    public SupplierSalesman findSupplierSalemanById(String supplierSalesmanId) {
        SupplierSalesman t=new SupplierSalesman();
        t.setId(supplierSalesmanId);
        return supplierSalesmanMapper.selectOne(t);
    }

    public String getNextSendUserId(String userId,String puchaseApproveListString) {
        if (userId==null || userId.length()==0){
            return "";
        }
        if (puchaseApproveListString==null || puchaseApproveListString.length()==0){
            return "";
        }
        String sendUserId="";
        String[] split = puchaseApproveListString.split(",");
        for(int i=0;i<split.length;i++){
            if (split[i].equals(userId)){
                if (i ==split.length-1){
                    break;
                }else {
                    sendUserId=split[i+1];
                    break;
                }

            }
        }
        return sendUserId;
    }

    //4.专家发通知
    @Async
    public ListenableFuture<Integer> getExpertSendMessage(String purId,List<PurchaseDetail> detailList,SendMsmHandler sendMsmHandler,XiaoNiuMsmTemplate templateName,String notificationTitlePass,String notificationTitleRefuse,String notificationContantPass,String notificationContantRefuse,String imagePass,String imageRefuse) {
        log.info("给订单推荐专家发送dd通知和短信");
        Set<String> expetSet = new HashSet<>();

        if (detailList.get(0)!=null ){
            log.info("有推荐专家");
            for (PurchaseDetail purchaseDetail : detailList) {
                if(purchaseDetail.getExpertId().length()>0){
                    String[] split = purchaseDetail.getExpertId().split(",");
                    for (String s : split) {
                        expetSet.add(s);
                    }
                }

            }
            List <ExpertUser> userList=new ArrayList<>();
            for (String s : expetSet) {
                ExpertUser exertUser=findExpertById(s);
                userList.add(exertUser);
            }
            try{
                for (ExpertUser expertUser : userList) {
                    //发送钉钉通知
                    NotificationModel nm = new NotificationModel();
                    nm.setAgentId(Constant.getInstance().getExpertAgentId());
                    nm.setContent(notificationContantPass);
                    nm.setImage(imagePass);
                    nm.setMessageUrl(NotificationConfig.EXPERT_APP);
                    nm.setText("摩尔易购");
                    nm.setToAllUser(false);
                    nm.setToken(expertClient.getToken());
                    nm.setUserList(expertUser.getDdId());
                    nm.setTitle(notificationTitlePass);
                    OapiMessageCorpconversationAsyncsendV2Response omar = sendNotificationImp.sendOANotification(nm);

                    log.info("钉钉给专家："+expertUser.getId()+"发送通知结果："+omar+",token:"+expertClient.getToken());
                    //发送短信通知
                    String s =sendMsmHandler.sendMsm(XiaoNiuMsm.SIGNNAME_MEYG, templateName, expertUser.getMobile());
                    log.info("钉钉给专家："+expertUser.getId()+"发送短信结果："+s);

                }
                return new AsyncResult<>(1);
            }catch (Exception e){
                e.printStackTrace();
                return new AsyncResult<>(0);
            }

        }
        log.info("查询推荐但是没有被选中的专家，发送通知和消息");
        List<ExpertRecommend> eReList=expertRecommendMapper.findExpertRecommendByPurIdAndExpertIdNotIn(purId,expetSet);
        if (eReList!=null && eReList.size()>0){
            log.info("给没有选中的专家推荐开始");

            List <ExpertUser> userNoChoiceList=new ArrayList<>();
            for (ExpertRecommend expertRecommend : eReList) {
                Example t = new Example(ExpertUser.class);
                t.and().andEqualTo("id",expertRecommend.getExpertId());
                ExpertUser eu = expertUserMapper.selectOneByExample(t);
                userNoChoiceList.add(eu);
            }
            if (userNoChoiceList.size()>0 && userNoChoiceList!=null){
                for (ExpertUser expertUser : userNoChoiceList) {
                    //发送钉钉通知
                    NotificationModel nm = new NotificationModel();
                    nm.setAgentId(Constant.getInstance().getExpertAgentId());
                    nm.setContent(notificationContantRefuse);
                    nm.setImage(imageRefuse);
                    nm.setMessageUrl(NotificationConfig.EXPERT_APP);
                    nm.setText("摩尔易购");
                    nm.setToAllUser(false);
                    nm.setToken(expertClient.getToken());
                    nm.setUserList(expertUser.getDdId());
                    nm.setTitle(notificationTitleRefuse);
                    OapiMessageCorpconversationAsyncsendV2Response omar = sendNotificationImp.sendOANotification(nm);

                    log.info("钉钉给专家："+expertUser.getId()+"发送通知结果："+omar+",token:"+expertClient.getToken());
                    //发送短信通知
                    String s =sendMsmHandler.sendMsm(XiaoNiuMsm.SIGNNAME_MEYG, templateName, expertUser.getMobile());
                    log.info("钉钉给专家："+expertUser.getId()+"发送短信结果："+s);
                }
            }
        }
        return null;
    }

    //报价人员
    @Async
    public ListenableFuture<Integer> getSaleManSendMessage(String purId,List<PurchaseDetail> detailList, SendMsmHandler sendMsmHandler, XiaoNiuMsmTemplate templateName,String notificationTitlePass,String notificationTitleRefuse,String notificationContantPass,String notificationContantRefuse,String imagePass,String imageRefuse) {
        //分两步
        //1.先给中标的发
        //中标的报价id
        Set<String> supplierSet=new HashSet<>();
        for (PurchaseDetail purchaseDetail : detailList) {
            supplierSet.add(purchaseDetail.getQuoteId());
        }
        if(supplierSet!=null && supplierSet.size()>0){
            log.info("给订单报价人员发送dd通知和短信（放送中标的）");
            List<SupplierSalesman> saleManList=new ArrayList<>();
            for (String s : supplierSet) {
                FyQuote quo=findQuoteById(s);
                //查询报价人员
                SupplierSalesman salesman =findSupplierSalemanById(quo.getSupplierSalesmanId());
                saleManList.add(salesman);
            }
            //发通知

            try{
                for (SupplierSalesman salesman : saleManList) {
                    //发送钉钉通知
                    NotificationModel nm = new NotificationModel();
                    nm.setAgentId(Constant.getInstance().getSupplierAgentId());
                    nm.setContent(notificationContantPass);
                    nm.setImage(imagePass);
                    nm.setMessageUrl(NotificationConfig.SUPPLIER_APP);
                    nm.setText("摩尔易购");
                    nm.setToAllUser(false);
                    nm.setToken(supplierClient.getToken());
                    nm.setUserList(salesman.getDdUserId());
                    nm.setTitle(notificationTitlePass);
                    nm.setPurId(purId);
                    nm.setMessageToPlatform(2);
                    OapiMessageCorpconversationAsyncsendV2Response omar = sendNotificationImp.sendOANotification(nm);
                    log.info("钉钉给报价人员："+salesman.getDdUserId()+"发送通知结果："+omar.getMessage()+",token:"+supplierClient.getToken());
                    //发送短信通知
                    String s =sendMsmHandler.sendMsm(XiaoNiuMsm.SIGNNAME_MEYG, XiaoNiuMsmTemplate.推送未中标结果模板(),salesman.getPhone());
                    log.info("钉钉给报价人员："+salesman.getId()+"发送短信结果："+s);

                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        //2.给未中标的发
        //先根据订单id查询所有的报价

        List<FyQuote>quoteList=quoteMapper.findQuteByPurIdAndIdNotEuqal(purId,supplierSet);
        if(quoteList!=null){
            log.info("给订单报价人员发送dd通知和短信（放送未中标的）");
            List<SupplierSalesman> saleManNoTList=new ArrayList<>();
            for (FyQuote fyQuote : quoteList) {
                Example o = new Example(SupplierSalesman.class);
                o.and().andEqualTo("id",fyQuote.getSupplierSalesmanId());
                SupplierSalesman ssm = supplierSalesmanMapper.selectOneByExample(o);
                saleManNoTList.add(ssm);
            }

            for (SupplierSalesman sm : saleManNoTList) {
                //发送钉钉通知
                NotificationModel nm = new NotificationModel();
                nm.setAgentId(Constant.getInstance().getSupplierAgentId());
                nm.setContent(notificationContantRefuse);
                nm.setImage(imageRefuse);
                nm.setMessageUrl(NotificationConfig.SUPPLIER_APP);
                nm.setText("摩尔易购");
                nm.setToAllUser(false);
                nm.setToken(supplierClient.getToken());
                nm.setUserList(sm.getDdUserId());
                nm.setTitle(notificationTitleRefuse);
                nm.setPurId(purId);
                nm.setMessageToPlatform(2);
                OapiMessageCorpconversationAsyncsendV2Response omar = sendNotificationImp.sendOANotification(nm);
                log.info("钉钉给报价人员："+sm.getDdUserId()+"发送通知结果："+omar.getMessage()+",token:"+supplierClient.getToken());
                //发送短信通知
                String s =sendMsmHandler.sendMsm(XiaoNiuMsm.SIGNNAME_MEYG, XiaoNiuMsmTemplate.推送未中标结果模板(),sm.getPhone());
                log.info("钉钉给报价人员："+sm.getId()+"发送短信结果："+s);
            }
        }
        return null;

    }

    @Async
    public ListenableFuture<Integer> getAuSendMessage(String staffId,SendMsmHandler sendMsmHandler,XiaoNiuMsmTemplate templateName,String notificationTitle,String notificationContant,String image) {
        log.info("给订单发起人员发送dd通知和短信");

        AppUser au = findAppUserById(staffId);
        try{
            //发送钉钉通知
//            ServiceResult serviceResult = sendNotificationImp.sendOaFromE(au.getId(), au.getUserName(), tokenService.getToken(), Constant.AGENTID_EXPERT);
//            log.info("钉钉给采购人员："+staffId+"发送通知结果："+serviceResult.getMessage());
            NotificationModel nm = new NotificationModel();
            nm.setAgentId(Constant.getInstance().getPurchaseAgentId());
            nm.setContent(notificationContant);
            nm.setImage(image);
//            nm.setMessageUrl(NotificationConfig.PURCHASE_APP);
            nm.setMessageUrl(NotificationConfig.PURCHASE_APP);
            nm.setText("摩尔易购");
            nm.setToAllUser(false);
            nm.setToken(tokenService.getToken());
            nm.setUserList(au.getDdUserId());
            nm.setTitle(notificationTitle);
            OapiMessageCorpconversationAsyncsendV2Response omar = sendNotificationImp.sendOANotification(nm);

            log.info("钉钉给订单发起人员："+au.getDdUserId()+"发送通知结果："+omar.getMessage()+",token:"+tokenService.getToken());
            //发送短信通知
            String s = sendMsmHandler.sendMsm(XiaoNiuMsm.SIGNNAME_MEYG, templateName, au.getMobile());
            log.info("钉钉给订单发起人员："+staffId+"发送短信结果："+s);
            return new AsyncResult<>(1);
        }catch (Exception e){
            e.printStackTrace();
            return new AsyncResult<>(0);
        }
    }

    public void updataExpertRecommendChecked(String  pur, List<PurchaseDetail> detailList) {
        if (detailList==null || detailList.size()==0){
            return;
        }
        String expertIdString = detailList.get(0).getExpertId();
        String[] expertIdArr = expertIdString.split(",");
        for (String expertId : expertIdArr) {
            expertRecommendMapper.updataAdoptByPurIdAndExpertId(pur,expertId);
        }
    }

    public void updataExpertRecommendNotChecked(String pur) {
        expertRecommendMapper.updataAdoptNotChecked(pur);
    }

    public void saveQuotePayresult(fyPurchase pur) {
        //判断专家支付费用 或者电子合同费用  是否需要
        if (pur.getExpertReview().equals("true") || pur.getElectronicContract().equals("true")){
            List<FyQuote> quoteList=quoteMapper.findSupplierIdListByPurId(pur.getId());
            if (quoteList!=null){
                for (FyQuote quote : quoteList) {
                    QuotePayresult t=new QuotePayresult();
                    t.setId(new IdWorker(2,2).nextId()+"");
                    t.setPurchaseId(pur.getId());
                    t.setSupplierId(quote.getPkSupplierId());
                    t.setPayResult(OrderStatus.SINGLE_SUPPLIER_PAY_NOT+"");
                    t.setStatus(OrderStatus.ALL_SUPPLIER_PAY_NOT+"");
                    t.setCreateTime(TimeUtil.getNowDateTime());
                    quotePayresultMapper.insert(t);
                }
            }
        }
    }

    public void updataPurchaseApprovalStartTime(String purId) {
        String nowDateTime = TimeUtil.getNowDateTime();
        log.info("审批环节时间设定"+nowDateTime);
        purchaseMapper.updataApprovalStartTime(purId,nowDateTime);
    }

    public int updataPurchaseApprovalEndTime(String purId) {
        log.info("审批结束时间设定");
        String nowDateTime = TimeUtil.getNowDateTime();
        return purchaseMapper.updataApprovalEndTime(purId, nowDateTime);
    }

    public AppOrgBuyChannelApproveMiddle findAppOrgBuyChannelApproveMiddleByOrgIdAndBuyChannellId(String orgId, String buyChannelId) {
        if (orgId!=null && buyChannelId!=null){
            Example t = new Example(AppOrgBuyChannelApproveMiddle.class);
            t.and().andEqualTo("appOrgId",orgId).andEqualTo("buyChannelId",buyChannelId);
            return appOrgBuyChannelApproveMiddleMapper.selectOneByExample(t);
        }
        return null;
    }

    public AppPurchaseApprove findAppPurchaseApproveByIdAndPurchaseMainPerson(String id) {
        AppPurchaseApprove t = new AppPurchaseApprove();
        t.setId(id);
        return appPurchaseApproveMapper.selectOne(t);
    }

    public AppPurchaseApprove findAppPurchaseApproveById(String id) {
        AppPurchaseApprove t = new AppPurchaseApprove();
        t.setId(id);
        return appPurchaseApproveMapper.selectOne(t);
    }


    public String insertAppPurchaseApprove(AppPurchaseApprove appPurchaseApprove,String processId) {
        AppPurchaseApprove apa=new AppPurchaseApprove();
        apa.setId(new IdWorker(1, 1).nextId()+"");
        apa.setPurchaseMainPerson(appPurchaseApprove.getPurchaseMainPerson());
        apa.setPurchaseApproveLeader(appPurchaseApprove.getPurchaseApproveLeader());
        apa.setPurchaseApproveList(appPurchaseApprove.getPurchaseApproveList());
        apa.setPurchaseActiveKey(processId);
        appPurchaseApproveMapper.insert(apa);
        return apa.getId();
    }

    public AppOrgBuyChannelApproveMiddle findAppOrgBuyChannelApprovMiddleByAppIdAndBuychannelId(String orgId, String buyChannelId) {
        AppOrgBuyChannelApproveMiddle t = new AppOrgBuyChannelApproveMiddle();
        t.setAppOrgId(orgId);
        t.setBuyChannelId(buyChannelId);
        return appOrgBuyChannelApproveMiddleMapper.selectOne(t);
    }

    public int updataAppOrgBuyChannelApprovMiddleByAppIdAndBuychannelId(AppOrgBuyChannelApproveMiddle appOrgBuyChannelApproveMiddle, String id) {

        AppOrgBuyChannelApproveMiddle t = new AppOrgBuyChannelApproveMiddle();
        t.setId(appOrgBuyChannelApproveMiddle.getId());
        t.setPurchaseApproveId(id+"");
        return appOrgBuyChannelApproveMiddleMapper.updateByPrimaryKeySelective(t);
    }



    //给议价负责人发送通知
    @Async
    public ListenableFuture<Integer> getPurMainPerson(String orgId, Integer buyChannelId,SendMsmHandler sendMsmHandler, XiaoNiuMsmTemplate templateName,String notificationTitle,String notificationContant,String image ) {
        if(orgId==null || buyChannelId==null){
            log.info("给议价负责人发送通知和短信失败！ 参数orgid:"+orgId+",buychannelId:"+buyChannelId);
            return null;
        }
        AppOrgBuyChannelApproveMiddle t = new AppOrgBuyChannelApproveMiddle();
        t.setAppOrgId(orgId);
        t.setBuyChannelId(buyChannelId+"");
        AppOrgBuyChannelApproveMiddle aobam=appOrgBuyChannelApproveMiddleMapper.selectOne(t);
        log.info("查询到AppOrgBuyChannelApproveMiddle ："+aobam);
        if (aobam==null && aobam.getPurchaseApproveId()==null){
            log.info("中间表数据查询失败");
            return null;
        }
        AppPurchaseApprove apa = appPurchaseApproveMapper.selectByPrimaryKey(aobam.getPurchaseApproveId());
        log.info("查询到的议价负责人id："+apa.getPurchaseMainPerson());
        if (apa==null && apa.getPurchaseMainPerson()==null){
            log.info("查询议价负责人查询失败:"+apa);
            return null;
        }
        AppUser ap  =new AppUser();
        ap.setId(apa.getPurchaseMainPerson());
        AppUser appUser = appUserMapper.selectOne(ap);
        log.info("查询到的议价负责人ddId:"+appUser.getDdUserId());
        if (appUser==null && appUser.getDdUserId()==null){
            log.info("查询议价负责人ddId查询失败:"+apa);
            return null;
        }
        //发送钉钉通知(成功）
        //ServiceResult serviceResult = sendNotificationImp.sendOaFromE(appUser.getDdUserId(),null,tokenService.getToken(),Constant.AGENTID);
        //log.info("钉钉给议价负责人员："+appUser.getId()+"发送通知结果："+serviceResult.getMessage());
        NotificationModel nm = new NotificationModel();
        nm.setAgentId(Constant.getInstance().getPurchaseAgentId());
        nm.setContent(notificationContant);
        nm.setImage(image);
        nm.setMessageUrl(NotificationConfig.PURCHASE_APP);
        nm.setText("摩尔易购");
        nm.setToAllUser(false);
        nm.setToken(tokenService.getToken());
        nm.setUserList(appUser.getDdUserId());
        nm.setTitle(notificationTitle);
        OapiMessageCorpconversationAsyncsendV2Response omar = sendNotificationImp.sendOANotification(nm);
        log.info("钉钉给议价负责人员："+appUser.getId()+"发送通知结果："+omar+",token:"+tokenService.getToken());
        //发送短信通知
        String s = sendMsmHandler.sendMsm(XiaoNiuMsm.SIGNNAME_MEYG, templateName, appUser.getMobile());
        log.info("钉钉给议价负责人员："+appUser.getId()+"发送短信结果："+s);
        return new AsyncResult<>(1);
    }

    //发送通知审批
    @Async
    public ListenableFuture<Integer> getApprove(String ddUserId,String phone ,SendMsmHandler sendMsmHandler, XiaoNiuMsmTemplate templateName,String notificationTitle,String notificationContant) {
        NotificationModel nm = new NotificationModel();
        nm.setAgentId(Constant.getInstance().getPurchaseAgentId());
        nm.setContent(notificationContant);
        nm.setImage(NotificationConfig.NOTIFICATION_IMAGE_url);
        nm.setMessageUrl(NotificationConfig.PURCHASE_APP);
        nm.setText("摩尔易购");
        nm.setToAllUser(false);
        nm.setToken(tokenService.getToken());
        nm.setUserList(ddUserId);
        nm.setTitle(notificationTitle);
        OapiMessageCorpconversationAsyncsendV2Response omar = sendNotificationImp.sendOANotification(nm);
        log.info("钉钉给议价负责人员："+ddUserId+"发送通知结果："+omar+",token:"+tokenService.getToken());
        //发送短信通知
        String s = sendMsmHandler.sendMsm(XiaoNiuMsm.SIGNNAME_MEYG, templateName,phone);
        log.info("钉钉给采购人员："+ddUserId+"发送短信结果："+s);
        return new AsyncResult<>(1);
    }

    public AppAuthOrg getAppOrgById(String appAuthOrgId) {
        if (appAuthOrgId!=null){
            Example t = new Example(AppAuthOrg.class);
            t.and().andEqualTo("id",appAuthOrgId);
            return appOrgMapper.selectOneByExample(t);
        }else {
            return null;
        }
    }

    public AppPurchaseApprove getAppPurchaseApproveById(String purchaseApproveId) {
        if (purchaseApproveId!=null){
            Example o = new Example(AppPurchaseApprove.class);
            o.and().andEqualTo("id",purchaseApproveId);
            return appPurchaseApproveMapper.selectOneByExample(o);
        }else {
            return null;
        }
    }
}

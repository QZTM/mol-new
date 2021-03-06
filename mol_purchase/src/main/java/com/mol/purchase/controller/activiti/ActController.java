package com.mol.purchase.controller.activiti;

import com.mol.config.NotificationConfig;
import com.mol.notification.SendNotification;
import com.mol.purchase.config.Constant;
import com.mol.purchase.config.OrderStatus;
import com.mol.purchase.entity.*;
import com.mol.purchase.entity.activiti.ActHiProcinst;
import com.mol.purchase.entity.dingding.login.AppAuthOrg;
import com.mol.purchase.entity.dingding.purchase.enquiryPurchaseEntity.PurchaseDetail;
import com.mol.purchase.entity.dingding.solr.fyPurchase;
import com.mol.purchase.mapper.newMysql.FyQuoteMapper;
import com.mol.purchase.service.activiti.ActService;
import com.mol.purchase.entity.dingding.login.AppUser;
import com.mol.purchase.service.token.TokenService;
import com.mol.purchase.util.JWTUtil;
import com.mol.sms.SendMsmHandler;
import com.mol.sms.XiaoNiuMsm;
import com.mol.sms.XiaoNiuMsmTemplate;
import entity.ServiceResult;
import entity.dd.DDUser;
import lombok.extern.java.Log;
import org.activiti.bpmn.model.BpmnModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import util.TimeUtil;


import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * ClassName:ActController
 * Package:com.purchase.controller.activiti
 * Description
 *
 * @date:2019/9/19 15:54
 * @author:yangjiangyan
 */
@RestController
@RequestMapping("/ac")
@Log
public class
ActController {
    private static final Logger logger=LoggerFactory.getLogger(ActController.class);

    @Autowired
    private Constant constant;
    @Autowired
    ActService actService;
    @Autowired
    SendNotification sendNotificationImp;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private FyQuoteMapper quoteMapper;

    private SendMsmHandler sendMsmHandler = SendMsmHandler.getSendMsmHandler();


    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    @ResponseBody
    public String hello(){
        return "hello world?";
    }

    /**
     * 部署流程实例
     * @param name  自定义实例名称
     * @param processId  流程id（key）
     * @param processName   流程name
     * @param orgId   公司id
     * @return
     */
    @RequestMapping(value = "/deploy",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResult deploy(String name, String processId, String processName, String orgId,String buyChannelId){
        //数据库查询出审核人员，用来构建bpmnMode对象
        if (orgId == null || buyChannelId==null){
            return ServiceResult.failure("公司或采购方式不得为空");
        }
        //AppAuthOrg org=actService.findappAuthOrgByOrgId(orgId);

        List<String> list= new ArrayList<>();
        AppOrgBuyChannelApproveMiddle abm = actService.findAppOrgBuyChannelApproveMiddleByOrgIdAndBuyChannellId(orgId, buyChannelId);
        AppPurchaseApprove apa=actService.findAppPurchaseApproveById(abm.getPurchaseApproveId());
        String aproveString = apa.getPurchaseApproveList();
        if (aproveString!=null){
            String[] split = aproveString.split(",");
            for (String s : split) {
                list.add(s);
            }
        }
        log.info("流程部署参与人员list："+list);
        BpmnModel model = actService.getModel(processId, processName, list);


        //addBpmnModel方式部署工作流
        actService.deployByModel(model,name);
        //将Key存到表中
        //插入一条新数据app_purchase_approve
        String id = actService.insertAppPurchaseApprove(apa, processId);
        logger.info("部署流程  app_purchase_approve表中返回的id值："+id);
        //将新插入数据的id 存到表app_org_buy_channel_approve_middle中
        AppOrgBuyChannelApproveMiddle abam = actService.findAppOrgBuyChannelApprovMiddleByAppIdAndBuychannelId(orgId, buyChannelId);
        logger.info("部署流程  查询到需要修改关联app_purchase_approve表id 的数据："+abam);
        int result= actService.updataAppOrgBuyChannelApprovMiddleByAppIdAndBuychannelId(abam,id);
        logger.info("部署流程   更新关联app_purchase_approve表id的结果："+result);
        if (result==1){
            logger.info("流程实例已部署");
            return ServiceResult.success("流程实例已部署");
        }else{
            logger.info("流程实例部署失败，请稍后重试");
            return ServiceResult.failureMsg("流程实例部署失败，请稍后重试");
        }
    }

    @GetMapping("/getActKey")
    public ServiceResult getActKey(String orgId,String buyChannelId){
        if (orgId==null || buyChannelId ==null ){
            return ServiceResult.failureMsg("审批实例查询失败，请稍后重试");
        }
        //先查询公司的
        AppOrgBuyChannelApproveMiddle appOrgBuyChannelApproveMiddle=actService.findAppOrgBuyChannelApproveMiddleByOrgIdAndBuyChannellId(orgId,buyChannelId);
        if (appOrgBuyChannelApproveMiddle!=null){
            AppPurchaseApprove appPurchaseApprove=actService.findAppPurchaseApproveByIdAndPurchaseMainPerson(appOrgBuyChannelApproveMiddle.getPurchaseApproveId());
            //查询
            if (appPurchaseApprove!=null){
                return ServiceResult.success(appPurchaseApprove);
            }else {
                return ServiceResult.failureMsg("审批实例查询失败，请稍后重试");
            }
        }else {
            return ServiceResult.failureMsg("审批实例查询失败，请稍后重试");
        }

    }

    /**
     * 启动流程实例
     * @param processKey 流程实例Key
     * @param businessKey 业务对象id(订单id)
     * @return
     */
    @PostMapping("/start")
    public ServiceResult start(@RequestParam String processKey,@RequestParam String businessKey,HttpServletRequest request){

        actService.startProcessInstance(processKey,businessKey);
        //设置订单的进入审批环节的时间
        actService.updataPurchaseApprovalStartTime(businessKey);

        //发送通知
        DDUser user = JWTUtil.getUserByRequest(request);
        ServiceResult serviceResult = sendNotificationImp.sendOaFromE(user.getUserid(), user.getName(), tokenService.getToken(), constant.getAgentId());
        logger.info("启动流程实例  发起通知  result:"+serviceResult.getMessage());

        //发送短信通知
        String s = sendMsmHandler.sendMsm(XiaoNiuMsm.SIGNNAME_MEYG, XiaoNiuMsmTemplate.提醒领导审批订单模板(), user.getMobile());
        logger.info("启动流程实例  发起短信  result:"+s);

        log.info("----审批流程已经启动----");

        return ServiceResult.success("流程实例已启动");
    }

    //查询当前个人任务
    @RequestMapping(value = "/task",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResult taskQuery(@RequestParam String assignee,int pageNum,int pageSize){
        //获取当前用户，
        logger.info("查询个人任务的id："+assignee);
        return ServiceResult.success(actService.getTask(assignee,pageNum,pageSize));
    }

    //完成个人任务
    @PostMapping("/complete")
    public ServiceResult complite(@RequestParam String taskId,@RequestParam String processInsId, @RequestParam String result, @RequestParam String comment, HttpServletRequest request){
        Map<String, Object> variables=new HashMap<>();
        variables.put("result",result);
        //获取登录人姓名

        DDUser user = JWTUtil.getUserByRequest(request);
        String name = user.getName();
        logger.info("完成个人审批任务 审批人信息："+user);
        actService.completeTask(taskId,processInsId,variables,comment,name);

        //1.订单ID
        ActHiProcinst hiProcinst=actService.getActHiprocinstByProcInstId(processInsId);
        logger.info("完成个人任务后查询到的订单id:"+hiProcinst.getBusinessKey());

        //2.订单
        fyPurchase pur=actService.findPurchaseById(hiProcinst.getBusinessKey());


        if (result.equals("pass")){
            logger.info("当前审批任务通过");

            AppUser appUser = actService.getAppUserByUserDingId(user.getUserid());
            logger.info("当前审批任务通过,通过ddId查询当前用户，查询到的用户id："+appUser.getId());
            AppAuthOrg aao=actService.getAppOrgById(appUser.getAppAuthOrgId());
            logger.info("当前审批任务通过,通过当前用户Id查询所属公司，查询到的公司id："+aao.getId());
            AppOrgBuyChannelApproveMiddle aobca = actService.findAppOrgBuyChannelApproveMiddleByOrgIdAndBuyChannellId(aao.getId(),pur.getBuyChannelId()+"");
            logger.info("当前审批任务通过,通过公司id和采购途径id查询中间表，查询到的中间表id："+aobca.getId());
            AppPurchaseApprove apa=actService.getAppPurchaseApproveById(aobca.getPurchaseApproveId());
            logger.info("当前审批任务通过,通过中间表查询审批负责人表，查询到的审批负责人表id："+apa.getId());

            //去获取下一个审批人的id
            String sendUserId=actService.getNextSendUserId(appUser.getId(),apa.getPurchaseApproveList());
            logger.info("下一个审批人的id："+sendUserId);


            if (sendUserId!="" || sendUserId==null ){
                logger.info("当前任务完成，向下一审批人发通知:"+sendUserId);
                //给下个任务处理人发通知和短信
                AppUser appUserById = actService.findAppUserById(sendUserId);
                ListenableFuture<Integer> purMainPersonSendMessage=actService.getApprove(appUserById.getDdUserId(),appUserById.getMobile(),sendMsmHandler, XiaoNiuMsmTemplate.提醒领导审批订单模板(),NotificationConfig.通过,NotificationConfig.审批负责人_NEW);
                //发送钉钉通知
                sendNotificationImp.sendOaFromE(appUserById.getDdUserId(),appUserById.getUserName(),tokenService.getToken(),constant.getAgentId());
                //发送短信通知
                sendMsmHandler.sendMsm(XiaoNiuMsm.SIGNNAME_MEYG, XiaoNiuMsmTemplate.提醒领导审批订单模板(),appUserById.getMobile());
            }else {
                logger.info("订单审批任务通过");

                //审批任务完成
                //查询订单相关信息

                //将报价表的状态设置为启用
                logger.info("将报价表的状态设置为启用");
                quoteMapper.updataApprovalStatusEnableByPurId(pur.getId(),TimeUtil.getNow(), OrderStatus.QUOTE_STATUS_USED+"");

                //3.订单详情
                List<PurchaseDetail> detailList =actService.findPurchaseDetailListByPurId(pur.getId());
                logger.info("修改专家推荐的采纳状态");
                if(detailList.size()>0){
                    logger.info("开始修改专家推荐的采纳状态："+detailList.size());
                    //修改选中的专家推荐表中的采纳状态
                    actService.updataExpertRecommendChecked(pur.getId(),detailList);
                    //修改未选中的专家推荐表中的采纳状态
                    actService.updataExpertRecommendNotChecked(pur.getId());
                }




                //保存订单，选中的供应商，是否支付
                //1.判断订单是否（电子合同，专家推荐）二有一，有则插入数据记录
                if ("true".equals(pur.getExpertReview()) || "true".equals(pur.getElectronicContract())){
                    logger.info("订单需要支付专家费用或者电子合同费用，插入新数据");
                    actService.saveQuotePayresult(pur);
                }
                //设置订单结束审批的时间
                int i = actService.updataPurchaseApprovalEndTime(pur.getId());
                logger.info("写入订单结束审批的时间 result:"+i);


                //1.给采购部门主管发短信，通知（P）
                ListenableFuture<Integer> purMainPersonSendMessage=actService.getPurMainPerson(pur.getOrgId(),pur.getBuyChannelId(),sendMsmHandler, XiaoNiuMsmTemplate.推送中标结果模板(),NotificationConfig.通过,NotificationConfig.议价负责人_PASS,NotificationConfig.通过图片);
                //2.给选中的专家发短信，通知（E)
                ListenableFuture<Integer> expertSendMessage = actService.getExpertSendMessage
                        (pur.getId(),detailList, sendMsmHandler, XiaoNiuMsmTemplate.给专家推送评审成功结果模板(),NotificationConfig.通过,NotificationConfig.拒绝,NotificationConfig.专家端_PASS,NotificationConfig.专家端_REFUSE,NotificationConfig.通过图片,NotificationConfig.拒绝图片);
                //3.给发起采购的采购人员发短信，通知(p)
                ListenableFuture<Integer> auSendMessage = actService.getAuSendMessage(pur.getStaffId(), sendMsmHandler, XiaoNiuMsmTemplate.推送中标结果模板(),NotificationConfig.通过,NotificationConfig.采购人_PASS,NotificationConfig.通过图片);
                //4.给供应商下的报价人发短信，通知(s)
                ListenableFuture<Integer> saleManSendMessage = actService.getSaleManSendMessage
                        (pur.getId(),detailList, sendMsmHandler, XiaoNiuMsmTemplate.推送中标结果模板(),NotificationConfig.通过,NotificationConfig.拒绝,NotificationConfig.供应商_PASS,NotificationConfig.供应商_REFUSE,NotificationConfig.通过图片,NotificationConfig.拒绝图片);
            }
        }else{
            //审批拒绝
            logger.info("当前审批任务状态为拒绝");
            //查询订单相关信息
            //3.订单详情
            List<PurchaseDetail> detailList =actService.findPurchaseDetailListByPurId(pur.getId());
            //将报价表的状态设置为淘汰，并启用
            logger.info("将报价表的状态设置为淘汰，并启用");
            quoteMapper.updataApprovalOverStatusAndTimeAndEnableByPurId(OrderStatus.QUOTE_REFUSE+"", TimeUtil.getNow(),OrderStatus.QUOTE_STATUS_USED+"");
            //修改未选中的专家推荐表中的adopt
            actService.updataExpertRecommendNotChecked(pur.getId());
            //1.给议价负责人发短信，通知
            ListenableFuture<Integer> purMainPersonSendMessage=actService.getPurMainPerson(pur.getOrgId(),pur.getBuyChannelId(),sendMsmHandler, XiaoNiuMsmTemplate.推送未中标结果模板(),NotificationConfig.拒绝,NotificationConfig.议价负责人_REFUSE,NotificationConfig.拒绝图片);
            //2.给选中的专家发短信，通知
            ListenableFuture<Integer> expertSendMessage = actService.getExpertSendMessage
                    (pur.getId(),detailList, sendMsmHandler, XiaoNiuMsmTemplate.给专家推送评审成功结果模板(),NotificationConfig.通过,NotificationConfig.拒绝,NotificationConfig.专家端_PASS,NotificationConfig.专家端_REFUSE,NotificationConfig.通过图片,NotificationConfig.拒绝图片);
            //3.给发起采购的采购人员发短信，通知
            ListenableFuture<Integer> auSendMessage = actService.getAuSendMessage(pur.getStaffId(), sendMsmHandler, XiaoNiuMsmTemplate.推送未中标结果模板(),NotificationConfig.拒绝,NotificationConfig.采购人_REFUSE,NotificationConfig.拒绝图片);
            //4.给供应商下的报价人发短信，通知
            ListenableFuture<Integer> saleManSendMessage = actService.getSaleManSendMessage
                    (pur.getId(),detailList, sendMsmHandler, XiaoNiuMsmTemplate.推送中标结果模板(),NotificationConfig.通过,NotificationConfig.拒绝,NotificationConfig.供应商_PASS,NotificationConfig.供应商_REFUSE,NotificationConfig.通过图片,NotificationConfig.拒绝图片);

        }


        return ServiceResult.success("任务已完成");
    }

    //查询该实例的所有批注以及批注人
    @GetMapping("/comments")
    public ServiceResult comments(@RequestParam String taskId){
        return ServiceResult.success(actService.getCommentList(taskId));
    }
    //查询历史任务
    @GetMapping("/his")
    public ServiceResult getHisTask(String assignee, String processId){
        return ServiceResult.success(actService.getHisTask(assignee,processId));
    }

    //查询流程状态
    @GetMapping("/status")
    public ServiceResult status(@RequestParam String processInstanceId){
        String status = actService.getStatus(processInstanceId);
        return ServiceResult.success(status);
    }

    //查询当前人的组任务
    @GetMapping("/group")
    public ServiceResult getGroupTask(@RequestParam String assignee){
        return ServiceResult.success(actService.getGroupTask(assignee));
    }

    //查询组任务的候选者
    @GetMapping("/canditate")
    public ServiceResult getAll(@RequestParam String taskId){
        return ServiceResult.success(actService.getAllAssignee(taskId));
    }

    //拾取组任务
    @PostMapping("/claim")
    public ServiceResult claim(@RequestParam String taskId, @RequestParam String assignee){
        actService.claim(taskId,assignee);
        return ServiceResult.success("任务已拾取");
    }

}

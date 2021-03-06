package com.mol.purchase.controller.dingding.workBench;

import com.mol.purchase.entity.*;
import com.mol.purchase.entity.activiti.ActHiProcinst;
import com.mol.purchase.entity.activiti.ActReProcdef;
import com.mol.purchase.entity.dingding.login.AppAuthOrg;
import com.mol.purchase.entity.dingding.login.AppUser;
import com.mol.purchase.entity.dingding.purchase.enquiryPurchaseEntity.PurchaseDetail;
import com.mol.purchase.entity.dingding.purchase.workBench.Ucharts;
import com.mol.purchase.entity.dingding.purchase.workBench.toBeNegotiated.NegotiatIng;
import com.mol.purchase.entity.dingding.solr.fyPurchase;
import com.mol.purchase.service.dingding.login.LoginService;
import com.mol.purchase.service.dingding.purchase.StrategyPurchaseService;
import com.mol.purchase.service.dingding.workBean.TobeNegotiatedService;
import entity.ServiceResult;
import lombok.extern.java.Log;
import org.apache.solr.client.solrj.io.eval.IFFTEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * ClassName:TobeNegotiatedController
 * Package:com.purchase.controller.dingding.workBench
 * Description
 *      E应用待议价controller
 * @date:2019/8/12 13:19
 * @author:yangjiangyan
 */
@RestController
@CrossOrigin
@RequestMapping("/negotiateding")
@Log
public class TobeNegotiatedController {

    @Autowired
    private TobeNegotiatedService negotiatedService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private StrategyPurchaseService strategyPurchaseService;


    /**
     * 待议价list
     * @param orgId
     * @param status negotiateding
     * @return
     */
//    @RequestMapping(value = "/getList", method = RequestMethod.GET)
//    public List<fyPurchase> getNetoList(String orgId,String status,String secondStatus,String userId ,int pageNum,int pageSize){
//
//        List<fyPurchase> list = new ArrayList<>();
//        //通过组织id 查询登录人的组织信息
//        AppAuthOrg appAuthOrg=loginService.AppAuthOrgByOrgId(orgId);
//        log.info("通过组织id 查询登录人的组织信息："+appAuthOrg);
//        //通过公司id，查询所有采购的议价负责人员list
//        List<AppOrgBuyChannelApproveMiddle> appOrgMidList=loginService.findAppOrgBuyChannelApproveMiddleByOrgIdAndPurchaseMainPersonId(orgId,userId);
//        //在list中查询当前登录人负责的内容list
//        //String mainPersonId = appAuthOrg.getPurchaseMainPerson();
//        //议价人员信息
//        //AppUser appUser = loginService.one(mainPersonId);
//
//        if (appOrgMidList!=null && appOrgMidList.size()>0){
//            log.info("登录信息属于采购负责人");
//            //是 展示所有状态，公司符合的order
//            //list=negotiatedService.findListByOrgId(orgId,status,secondStatus,pageNum,pageSize);
//            //获取负责的采购途径buychannelId
//            List<String > buyChannelList=loginService.getBuyChannelIdArrFromAppOrgMidList(appOrgMidList);
//            log.info("当前登录人负责议价的采购途径:"+buyChannelList);
//            list=negotiatedService.findListByOrgIdAndBuychannelAndStatus(orgId,status,secondStatus, buyChannelList,pageNum,pageSize);
//            if (pageNum==1){
//                //首页添加
//                List<fyPurchase> listIfOk = negotiatedService.findListIfOk(orgId, status, secondStatus, userId, pageNum, pageSize);
//                if (listIfOk.size()>0){
//                    list.addAll(listIfOk);
//                }
//            }
//        }else {
//            log.info("登录信息属于不采购负责人");
//            //查询全部，遍历
//            list=negotiatedService.findListIfOk(orgId,status,secondStatus,userId,pageNum,pageSize);
//        }
//
//
//        //要求list
//         return list;
//    }

    /**
     * 待议价数量
     */
    @GetMapping("/getTobenegotiatedCount")
    public ServiceResult getTobenegotiatedCount(String orgId,String status,String userId){
        log.info("查询待议价数量，参数：status:"+status+",userId:"+userId+",orgId"+orgId);

        if(status != null && userId != null && orgId!=null ){
            log.info("待议价查数量参数合法");
            Integer count=negotiatedService.findCountByUserIdAndStatus(orgId,userId,status);
            log.info("查询到自己发布的待议价的订单数量为："+count);
            Integer count_2=negotiatedService.findCountByStatusAndNePNotNullAndStaffIdNotEqualUserId(orgId,userId,status);
            log.info("查询到参与待议价的订单数量为："+count_2);
            return ServiceResult.success(count+count_2);
        }else {
            log.info("待议价查数量参数不合法");
            return ServiceResult.failureMsg("查询失败");
        }
    }

    /**
     * 待议价list
     */
    @GetMapping("/getTobenegotiatedList")
    public ServiceResult getTobenegotiatedList(String orgId,String status,String userId,int pageNum,int pageSize){
        log.info("查询待议价list，参数：status:"+status+",userId:"+userId);
        List<fyPurchase> list =new ArrayList<>();
        if(status != null && userId != null){
            log.info("待议价查list参数合法");
            list=negotiatedService.findListByUsrIdAndStatus(orgId,userId,status,pageNum,pageSize);
            if(pageNum==1){
              List<fyPurchase> purList=negotiatedService.findListByOrgIdAndStatusAndStaffIdNotEuqalUserId(orgId,userId,status);
              purList=negotiatedService.getList(purList,userId);
              log.info("参与的待议价的list："+purList.size());
              list.addAll(purList);
            }
            log.info("查询到待议价的订单数量为："+list.size());
            return ServiceResult.success(list);
        }else {
            log.info("待议价查list参数不合法");
            return ServiceResult.failureMsg("查询失败");
        }
    }

    /**
     * 已审批数量
     */
    @GetMapping("/getCompleteBargainingount")
    public ServiceResult getCompleteBargainingount(String orgId,String statusPass,String statusRefuse,String userId){
        log.info("查询已审批数量，参数：orgid："+orgId+",statusPass:"+statusPass+",statusRefuse:"+statusRefuse+",userId:"+userId);

        //查询
        if(orgId != null && userId != null && statusPass!=null && statusRefuse!=null){
            log.info("已审批查询数量参数合法");

            List<AppOrgBuyChannelApproveMiddle> aobcmList=negotiatedService.findAppOrgBuyChannelApproveMiddleByOrgId(orgId);
            List<String> channelIdList=negotiatedService.getChannelIdByUserId(aobcmList,userId);

            Integer count=0;
            if(channelIdList!=null && channelIdList.size()>0){
                count=negotiatedService.findCountByChannelListAndStatus(channelIdList,statusPass,statusRefuse);
                log.info("查询到已审批的订单数量为："+count);

            }
            return ServiceResult.success(count);

        }else {
            log.info("已审批查询数量参数不合法");
            return ServiceResult.failureMsg("查询失败");
        }
    }

    /**
     *已审批list
     */
    @GetMapping("/getCompleteBargainingList")
    public ServiceResult getCompleteBargainingList(String orgId,String statusPass,String statusRefuse,String userId,int pageNum,int pageSize){
        log.info("查询已审批数量，参数：orgid："+orgId+",statusPass:"+statusPass+",statusRefuse:"+statusRefuse+",userId:"+userId);
        if(orgId != null && userId != null && statusPass!=null && statusRefuse!=null){
            log.info("已审批查list参数合法");

            List<AppOrgBuyChannelApproveMiddle> aobcmList=negotiatedService.findAppOrgBuyChannelApproveMiddleByOrgId(orgId);
            List<String> channelIdList=negotiatedService.getChannelIdByUserId(aobcmList,userId);

            List<fyPurchase>list = new ArrayList<>();
            if(channelIdList!=null && channelIdList.size()>0){
                list=negotiatedService.findPurlistByChannelListAndStatus(channelIdList,statusPass,statusRefuse,pageNum,pageSize);
                log.info("查询到已审批的订单数量为："+list.size());

            }
            log.info("查询到已审批的订单数量为："+list.size());
            return ServiceResult.success(list);
        }else {
            log.info("已审批查询list参数不合法");
            return ServiceResult.failureMsg("查询失败");
        }
    }

    /**
     * 已议价数量
     */
    @GetMapping("/getCompleteBargainedCount")
    public ServiceResult getCompleteBargainedCount(String orgId,String status,String userId){
        log.info("查询已议价数量，参数：orgid："+orgId+",status:"+status+",userId:"+userId);

        int sum=0;
        //查询
        if(orgId != null && userId != null && status!=null ){
            log.info("已议价查询数量参数合法");

            //1.先查询自己发布的订单数量
            int count=negotiatedService.findCountByOrgIdAndUserIdAndStatus(orgId,userId,status);
            log.info("第一个count："+count);
            //2.查询有参与议价的订单
            List<fyPurchase> list=negotiatedService.findListByUserIdNotEqualStaffIdAndStatus(userId,status);
            //2.1 查询自己参与的数量
            int count2=negotiatedService.getCountFromPurList(list,userId);
            log.info("第二个count:"+count2);
            //3.返回sum;
            sum=count+count2;
            log.info("已议价sum:"+sum);
            return ServiceResult.success(sum);
        }else {
            log.info("已议价查询数量参数不合法");
            return ServiceResult.failureMsg("查询失败");
        }
    }

    /**
     *已议价list
     */
    @GetMapping("/getCompleteBargainedList")
    public ServiceResult getCompleteBargainedList(String orgId,String status,String userId,int pageNum,int pageSize){
        log.info("查询已议价List，参数：orgid："+orgId+",status:"+status+",userId:"+userId);
        if(orgId != null && userId != null && status!=null ){
            log.info("已议价查询list参数合法");
            List<fyPurchase> purList=negotiatedService.findListByOrgIdAndStaffId(orgId,userId,status,pageNum,pageSize);
            purList=negotiatedService.getList(purList,userId);
            if (purList==null || purList.size()<pageSize){
                pageNum++;
                List<fyPurchase> purList2=negotiatedService.findListByOrgIdAndStaffId(orgId,userId,status,pageNum,pageSize);
                purList2=negotiatedService.getList(purList,userId);
                purList.addAll(purList2);
            }
            return ServiceResult.success(purList);
        }else {
            log.info("已议价查询list参数不合法");
            return ServiceResult.failureMsg("查询失败");
        }
    }

    /**
     * -- 1.如果是议价负责人
     * -- 1.1
     * 	select * from fy_purchase where buy_channel_id ='4' ORDER BY create_time desc
     * -- 1.2 在加上 参与 议价的数量
     * 	select * from fy_purchase where negotiate_person is not null and id not in ( select id from fy_purchase where buy_channel_id ='4' )
     *
     * -- 	2.不是任何一个议价负责人
     * 		select * from fy_purchase where negotiate_person is not null
     * 	已议价
     */
    @GetMapping("/getComplateBargainningList")
    public List<fyPurchase> getComplateBargainningList(String orgId,String userId,int pageNum,int pageSize){
        if (orgId==null || userId==null){
            return  null;
        }
        //查询userID 是否为企业的议价负责人（可以负责多个采购渠道)
        List<AppOrgBuyChannelApproveMiddle> apamList=negotiatedService.findAppOrgBuyChannelApproveMiddleByOrgIdAndMainPersonId(orgId,userId);
        log.info("已议价list 查询userID 是否为企业的议价负责人"+apamList);
        if (apamList!=null && apamList.size()>0 ){
            log.info("已议价list  userID是企业的议价负责人"+apamList);

            //属于议价负责人
            List<String> buyChnnelList =new ArrayList<>();
            for (AppOrgBuyChannelApproveMiddle apam : apamList) {
                buyChnnelList.add(apam.getBuyChannelId());
            }
            //查询自己负责议价的订单
            List<fyPurchase> purList=negotiatedService.findFyPurchaseByBuyChannelAndOrgId(buyChnnelList,orgId,pageNum,pageSize);
            log.info("已议价list  查询自己负责议价的订单:"+purList.size());

            //查询辅助议价的订单
            //查询组织有参与辅助的订单
            List <fyPurchase> npNotNullList=negotiatedService.findFyPurchaseByNegotiatePersonNotNullAndOrgId(orgId);
            log.info("已议价list  查询组织有参与辅助的订单:"+npNotNullList.size());

            //在上一步的订单中找出userID 参与的订单(方法里判null)
            List <fyPurchase> npNotNullListInnerUserList=negotiatedService.findNegotiatePersonNotNullInnerUser(npNotNullList,userId);
            log.info("已议价list  在上一步的订单中找出userID 参与的订单:"+npNotNullListInnerUserList.size());

//            if (pageNum==1){
//                //第一个页面
//                if (purList.size()==0 || npNotNullListInnerUserList.size()==0){
//                    if (purList.size()>0){
//                        return purList;
//                    }
//                    if (npNotNullListInnerUserList.size()>0){
//                        return npNotNullListInnerUserList;
//                    }
//                }else {
//                    purList.addAll(npNotNullListInnerUserList);
//                    return purList;
//                }
//            }

            if (purList.size()==0){
                return npNotNullListInnerUserList;
            }
            if (npNotNullListInnerUserList.size()==0){
                return purList;
            }
            else {
               purList.addAll(npNotNullListInnerUserList);
               return purList;
            }

        }else {
            log.info("已议价list  userID不是企业的议价负责人");

            //不属于议价负责人
            //查询辅助议价的订单
            //查询组织有参与辅助的订单
            List <fyPurchase> npNotNullList=negotiatedService.findFyPurchaseByNegotiatePersonNotNullAndOrgId(orgId);
            log.info("已议价list  查询组织有参与辅助的订单"+npNotNullList);

            //在上一步的订单中找出userID 参与的订单(方法里判null)
            List <fyPurchase> npNotNullListInnerUserList=negotiatedService.findNegotiatePersonNotNullInnerUser(npNotNullList,userId);
            log.info("已议价list  在上一步的订单中找出userID 参与的订单"+npNotNullListInnerUserList);

            return npNotNullListInnerUserList;
        }
    }

    /**
     * 待审批List
     * 当前登录人参与的审批个人任务
     * @param arr  个人任务中的业务id 集合
     * @return
     */
    @RequestMapping(value = "/getTaskList", method = RequestMethod.GET)
    public List<fyPurchase> getList(@RequestParam(value = "arr",required = false) List<String> arr){
        List<fyPurchase> list= new ArrayList<>();
        if (arr.size()>0 && arr!=null){
            list=negotiatedService.findFyPurchaseByIdArr(arr);
        }else {
            list=null;
        }
        return list;

    }
    /**
     * 通过订单id查询订单与相关报价 信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/getPur",method = RequestMethod.GET)
    public Map<String,Object> getFypurchaseById(String id){
        Map<String,Object> map = new HashMap<>();

        //订单信息
        fyPurchase pur=negotiatedService.findFypurchaseById(id);
        log.info("待议价  查询订单信息："+pur);

        pur=negotiatedService.getStaffName(pur);
        log.info("待议价  查询订单的发起人："+pur.getStaffId());
        map.put("fyPurchase",pur);

        //报价信息
        Map<String, List> quoteMap = negotiatedService.findQuoteById(id);
        map.put("map",quoteMap);

//        List<Supplier> supplierList = new ArrayList<>();
        //查询订单详情表中具体物料选中的具体公司
        List<PurchaseDetail> detailList=negotiatedService.findFyPurchaseDetailById(id);
//        for (PurchaseDetail purchaseDetail : detailList) {//查看是否完成议价
//            if (purchaseDetail.getQuoteId()!=null&&purchaseDetail.getQuoteId()!=""){
//                map.put("detailList",detailList);
//                //查询对应的供应商信息
//                Supplier sl=negotiatedService.findSupplierByQuoteId(purchaseDetail.getQuoteId());
//                supplierList.add(sl);
//                break;
//            }
//        }
//        map.put("supplier",supplierList);
//        return map;

        if(detailList!=null && detailList.size()>0){
            map.put("detailList",detailList);
        }
        List<FyQuote> quoteList=negotiatedService.findSupplierIdByPurId(pur.getId());
        List<Supplier> supplierList=negotiatedService.findSupplierByQuoteList(quoteList);
        map.put("supplier",supplierList);
        return map;
    }

    /**
     * 查询拥有提交议价结果的权限人员
     * @param orgId
     * @return
     */
    @RequestMapping(value = "/getAppUser",method = RequestMethod.GET)
    public ServiceResult getAppUserByOrgId(String orgId,String buyChannelId){
        log.info("查询拥有提交议价结果的权限人员 组织id："+orgId+",采购方式id："+buyChannelId);
        //查询企业信息
        //AppAuthOrg appAuthOrg=loginService.AppAuthOrgByOrgId(orgId);
        //String purchaseMainPerson = appAuthOrg.getPurchaseMainPerson();
        //AppUser appUser=loginService.one(purchaseMainPerson);
        //AppUser appUser = loginService.one(purchaseMainPerson.getId());

        AppOrgBuyChannelApproveMiddle apcam=loginService.findAppOrgBuyChannelApproveMiddleByOrgIdAndBuychannelId(orgId,buyChannelId);
        log.info("查询到议价结果的权限人员："+apcam);
        if (apcam!=null){
            AppPurchaseApprove apa=loginService.findAppPurchaseApproveById(apcam.getPurchaseApproveId());
            log.info("查询到议价负责人详情表的数据："+apa);
            AppUser appUser=loginService.one(apa.getPurchaseMainPerson());
            log.info("查询到的议价负责人的信息："+appUser);
            return ServiceResult.success(appUser);
        }
        return ServiceResult.successMsg("没有查询到相关的议价负责人");
    }


    @GetMapping("/findMainPerson")
    public ServiceResult findPurMainPersonId(String purId){
        //根据业务id在历史流程实例表（act_hi_procinst）中查询流程定义id
        ActHiProcinst actHiProcinst =negotiatedService.findActHiProcinstByPurId(purId);
        log.info("根据业务id在历史流程实例表（act_hi_procinst）中查询流程定义:"+actHiProcinst);
        //流程定义id在流程定义数据表（act_re_procdef）中查询key
        ActReProcdef arp=negotiatedService.findActReProcdefByActId(actHiProcinst.getProcDefId());
        log.info("流程定义id在流程定义数据表（act_re_procdef）中查询key"+arp);
        //拿key在app_purchase_approve 中查询purchasemainperson
        AppPurchaseApprove apa=negotiatedService.findAppPurchaseApproveByKey(arp.getKey());
        log.info("拿key在app_purchase_approve 中查询purchasemainperson:"+apa);
        //拿purchasemainperson 在appuser中查询数据
        if (apa!=null){
            AppUser appUser=negotiatedService.findAppUserById(apa.getPurchaseMainPerson());
            log.info("拿purchasemainperson 在appuser中查询数据:"+appUser);
            return ServiceResult.success(appUser);
        }
        return ServiceResult.successMsg("查询失败");

    }

    /**
     * 在议价页面发起电话动作，将相关人员（除了管理人员）保存到 订单表中
     * @param id
     * @param callId
     * @return
     */
    @RequestMapping(value = "/saveNagotiaPersonList",method = RequestMethod.GET)
    public ServiceResult saveNagotiatePersonList(String id,String [] callId){
        log.info("保存参与的议价人员  订单id:"+id+",callId:"+callId);
        Integer integer = negotiatedService.updataAppUserListById(id, callId);
        log.info("保存参与的议价人员  订单id:"+id+",callId:"+callId+",保存结果："+integer);
        return ServiceResult.success("保存成功");

    }

    /**
     * 通过订单id 获取当事参与电话议价人员列表，编辑权限
     * @param id
     * @return
     */
    @RequestMapping(value = "/getNagotiaPersonList",method = RequestMethod.GET)
    public ServiceResult getNagotiaPersonList(String id){
        List<AppUser> list =negotiatedService.findAppUserListById(id);
        return ServiceResult.success(list);
    }

    /**
     * 跳转大数据页面，根据公司id，订单id获取相关过往信息
     * @param supplierId
     * @param pkMaterialId
     * @return
     */
    @RequestMapping(value = "/getBigDate",method = RequestMethod.GET)
    public ServiceResult getBigDate(String supplierId,String pkMaterialId,String purId){
        String time="";
        log.info("查询大数据 参数 ===> 供应商id:"+supplierId+",物料id"+pkMaterialId+",订单id："+purId);
        List<FyQuote> quoteList=negotiatedService.findQuoteByPurIdAndSupplierId(supplierId,purId);
        if (quoteList!= null && quoteList.size()>0){
            time = quoteList.get(0).getCreationtime();
        }
        Ucharts u=negotiatedService.getBigData(supplierId,pkMaterialId,time);
        return ServiceResult.success(u);
    }


    /**
     * 审批中查看订单相关的推荐专家
     * @param purId  订单id
     * @param supplierId  供应商id
     * @return
     */
    @GetMapping("/getExpert")
    public ServiceResult getExpert(String purId,String supplierId){
        List<ExpertRecommend> erList=negotiatedService.findExpertList(purId,supplierId);
        List<ExpertUser> euList=null;
        //查询报价id
        List<FyQuote > quote=negotiatedService.getFyQuoteByPurIdAndSupplierId(purId,supplierId);
        //查询订单详情
        PurchaseDetail detail=negotiatedService.getPurchaseDetailByPurIdAndQuoteId(purId,quote);
        if (erList!=null && erList.size()>0){
            //判断订单详情表中是否有确认专家id
            if (detail!=null){
                //已完成议价
                euList=negotiatedService.findExpertUserList(erList);
                String expertId = detail.getExpertId();
                String[] split = expertId.split(",");
                for (int i=0;i<euList.size();i++){
                    for (int j=0;j<split.length;j++){
                        if (euList.get(i).getId().equals(split[j])){
                            euList.get(i).setChecked(true);
                        }
                    }
                }
                return ServiceResult.success(euList);
            }else {
                //未议价
                euList=negotiatedService.findExpertUserList(erList);
                return ServiceResult.success(euList);
            }
        }else {
            return ServiceResult.success("没有专家推荐该报价",euList);
        }
    }


    /**
     * 保存待议价界面提交的对应数据
     * @param negotiatIng
     * @return
     */
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ServiceResult saveTobeNeg(@RequestBody NegotiatIng negotiatIng){
            negotiatedService.save(negotiatIng);
            return ServiceResult.success("保存成功");

    }

    /**
     * 保存待审批界面审批人建议以及订单是否通过审批的状态
     * @param purId
     * @param passOrNot
     * @param approverProposal
     * @return
     */
    @RequestMapping(value = "/saveToBeApproved",method = RequestMethod.GET)
    public ServiceResult saveToBeApproved(String purId,String passOrNot,String approverProposal){
        System.out.println(purId);
        System.out.println(passOrNot);
        System.out.println(approverProposal);
        negotiatedService.saveApproverProlosalAndStatus(purId,passOrNot,approverProposal);
        return ServiceResult.success("成功");
    }



}

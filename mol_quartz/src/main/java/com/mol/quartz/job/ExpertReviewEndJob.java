package com.mol.quartz.job;

import com.mol.config.NotificationConfig;
import com.mol.notification.SendNotification;
import com.mol.quartz.config.Constant;
import com.mol.quartz.entity.Purchase;
import com.mol.quartz.mapper.PurchaseMapper;
import com.mol.quartz.service.GetTokenService;
import com.mol.quartz.service.PurchaseService;
import entity.NotificationModel;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import util.TimeUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 专家评审结束任务实际逻辑
 */
@Log
public class ExpertReviewEndJob implements Job {

    @Autowired
    private Constant constant;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Autowired
    private GetTokenService getTokenService;


    private SendNotification sendNotificationImp = SendNotification.getSendNotification();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data=context.getTrigger().getJobDataMap();
        Object orderIdObj = data.get("orderId");
        String orderId = "";
        if(orderIdObj != null){
            orderId = (String) orderIdObj;
        }else{
            log.warning("专家评审结束定时任务------找不到orderId");
            throw new RuntimeException("需要处理的报价结束任务参数异常------找不到orderId");
        }
        Purchase updatePurchase = new Purchase();
        updatePurchase.setId(orderId);
        updatePurchase.setBargainingTime(TimeUtil.getNowDateTime());
        updatePurchase.setStatus("4");
        purchaseMapper.updateByPrimaryKeySelective(updatePurchase);
        //给当前订单发起机构的所属采购类型的议价负责人发送通知
        Purchase purchase = purchaseMapper.selectByPrimaryKey(orderId);
        //查询当前订单所属发起机构的所属采购类型：
        log.info("根据企业id("+purchase.getOrgId()+")和buyChannelId("+purchase.getBuyChannelId()+")去获取企业该采购类型的采购主要负责人的钉钉id:");
        Map paraMap = new HashMap();
        paraMap.put("orgId",purchase.getOrgId());
        paraMap.put("buyChannelId",purchase.getBuyChannelId());
        String purchaseMainPersonDDId = purchaseMapper.getPurchaseMainPersonDDIdByOrgAndChannel(paraMap);
        log.info("获取到的结果为："+purchaseMainPersonDDId);
        if(StringUtils.isEmpty(purchaseMainPersonDDId)){
            log.warning("没有设置该企业该采购方式的主要采购负责人！");
            return ;
        }
        String token = null;
        try {
            token = getTokenService.getPurchaseToken().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        log.info("通过eureka获取到的token："+token);

        NotificationModel t = new NotificationModel();
        t.setAgentId(constant.getPurchaseAgentId());
        t.setContent(NotificationConfig.审批负责人_NEW);
        t.setImage(NotificationConfig.评审图片);
        t.setMessageUrl(NotificationConfig.PURCHASE_APP);
        t.setText("茉尔易购");
        t.setTitle("新订单");
        t.setToAllUser(false);
        t.setToken(token);
        t.setUserList(purchaseMainPersonDDId);
        sendNotificationImp.sendOANotification(t);
        //SendNotification.getSendNotification().sendOaFromE(purchaseMainPersonDDId,"",token, constant.getPurchaseAgentId(),"审批",TimeUtil.getNowDateTime()+"有新的采购订单需要您审批，点击查看吧！","http://140.249.22.202:8082/static/upload/imgs/supplier/ask.png","eapp://pages/purchase/workbench/tobeapproved/tobeapproved");
    }
}

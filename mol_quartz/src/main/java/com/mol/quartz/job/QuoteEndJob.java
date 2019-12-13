package com.mol.quartz.job;

import com.mol.notification.SendNotification;
import com.mol.quartz.config.Constant;
import com.mol.quartz.entity.Purchase;
import com.mol.quartz.handler.AddJobHandler;
import com.mol.quartz.mapper.ExpertUserMapper;
import com.mol.quartz.mapper.PurchaseMapper;
import com.mol.quartz.service.GetTokenService;
import com.mol.quartz.service.PurchaseService;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import util.TimeUtil;

import java.util.*;
import java.util.concurrent.ExecutionException;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
@Log
public class QuoteEndJob implements Job{


	@Autowired
	private Constant constant;
	@Autowired
	private PurchaseService purchaseService;

	@Autowired
	SendNotification sendNotificationImp;

	@Autowired
	private PurchaseMapper purchaseMapper;

	@Autowired
	private AddJobHandler addJobHandler;

	@Autowired
	private ExpertUserMapper expertUserMapper;

	@Autowired
	private GetTokenService getTokenService;


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		JobDataMap data=context.getTrigger().getJobDataMap();

		Set set = data.keySet();
		Iterator iterator = set.iterator();
		if(iterator.hasNext()){
			String mapKey = iterator.next().toString();
			System.out.println("key:"+mapKey+",,,"+data.get(mapKey));
		}

		Object orderIdObj = data.get("orderId");
		String orderId = "";
		if(orderIdObj != null){
			orderId = (String) orderIdObj;
		}else{
			log.warning("报价结束定时任务------找不到orderId");
			throw new RuntimeException("需要处理的报价结束任务参数异常------找不到orderId");
		}
		System.out.print("JobTwo执行,参数：");
		System.out.println("orderId:"+orderId);
		Purchase purchase = purchaseMapper.selectByPrimaryKey(orderId);
		System.out.println("订单"+orderId+"根据主键查询到的purchase:");
		System.out.println(purchase);
		if(purchase == null){
			log.warning("订单"+orderId+"根据主键获取的purchase为空");
			throw new RuntimeException("订单"+orderId+"根据主键获取的purchase为空");
		}
		//根据该订单id查询该订单实际报价商家数是否大于需要的报价商家数
		//Integer compareResult = purchaseService.compareQuoteSellerNumAndSellerCountById(orderId);
		Object quoteSellerNumObj = purchase.getQuoteSellerNum();
		if(quoteSellerNumObj == null){
			log.warning("订单"+orderId+"要求报价的商家数为空！");
			throw new RuntimeException("订单"+orderId+"要求报价的商家数为空");
		}
		Integer quoteSellerNum = Integer.valueOf(quoteSellerNumObj.toString());
		System.out.println("quoteSellerNum:"+quoteSellerNum);

		Object quoteCountsObj = purchase.getQuoteCounts();
		if(quoteCountsObj == null){
			log.warning("订单"+orderId+"实际报价的商家数为空！");
			throw new RuntimeException("订单"+orderId+"实际报价的商家数为空");
		}
		Integer quoteCounts = Integer.valueOf(quoteCountsObj.toString());
		System.out.println("quoteCounts:"+quoteCounts);

		if(quoteCounts - quoteSellerNum < 0){
			Purchase updatePurchase = new Purchase();
			updatePurchase.setId(orderId);
			updatePurchase.setStatus("3");
			purchaseMapper.updateByPrimaryKeySelective(updatePurchase);
			return ;
		}else{
			//判断是否需要专家评审
			String expertReview = purchase.getExpertReview();
			if(StringUtils.isEmpty(expertReview)){
				log.warning("订单"+orderId+"是否需要专家评审为空");
				throw new RuntimeException("订单"+orderId+"是否需要专家评审为空");
			}
			log.info("订单"+orderId+",expertReview:"+expertReview);
			Purchase updatePurchase = new Purchase();
			updatePurchase.setId(orderId);
			if("true".equals(expertReview)){
				//调用另一个定时任务
				updatePurchase.setStatus("5");
				updatePurchase.setExpertTime(TimeUtil.getNowDateTime());
				purchaseMapper.updateByPrimaryKeySelective(updatePurchase);
				addJobHandler.addExpertReviewEndJob(orderId,AddJobHandler.EXPERTREVIEWDELAY);
				//todo:给所属行业类别的专家发送通知
				//1.获取该行业类别
				//2.查询所属该行业类别的钉钉id list
				//3.发送通知
				String marbasClassId = purchase.getPkMarbasclass();
				List<String> expertIdList = expertUserMapper.getExpertDDIdListByMarId(marbasClassId);
				log.info("给所属行业类别的专家发送通知：行业类别："+marbasClassId+",,获取到的专家集合大小："+expertIdList.size());
				String token = null;
				try {
					token = getTokenService.getExpertToken().get().trim();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				log.info("通过eureka获取到的专家端token："+token);
				if(expertIdList.size() == 0){
					log.info("没有符合条件的专家！");
					return ;
				}
				for(String expertId:expertIdList){
					if(StringUtils.isEmpty(expertId)){
						break;
					}
					log.info("给专家"+expertId+"发送通知...");
					sendNotificationImp.sendOaFromExpert(expertId, constant.getExpertAgentId(),token);
				}


			}else{

				updatePurchase.setStatus("4");
				updatePurchase.setBargainingTime(TimeUtil.getNowDateTime());
				purchaseMapper.updateByPrimaryKeySelective(updatePurchase);
				//todo :给当前订单发起机构的所属采购类型的议价负责人发送通知
				//查询当前订单所属发起机构的所属采购类型：
				log.info("根据企业id("+purchase.getOrgId()+")和buyChannelId("+purchase.getBuyChannelId()+")去获取企业该采购类型的采购主要负责人的钉钉id:");
				Map paraMap = new HashMap();
				paraMap.put("orgId",purchase.getOrgId());
				paraMap.put("buyChannelId",purchase.getBuyChannelId());
				String purchaseMainPersonDDId = purchaseMapper.getPurchaseMainPersonDDIdByOrgAndChannel(paraMap);
				log.info("获取到的结果为："+purchaseMainPersonDDId);
				String token = null;
				try {
					token = getTokenService.getPurchaseToken().get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				log.info("通过eureka获取到的token："+token);
				sendNotificationImp.sendOaFromE(purchaseMainPersonDDId,"",token,constant.getPurchaseAgentId(),"审批",TimeUtil.getNowDateTime()+"有新的采购订单需要您审批，点击查看吧！","http://140.249.22.202:8082/static/upload/imgs/supplier/ask.png","eapp://pages/purchase/workbench/tobeapproved/tobeapproved");
			}
		}







	}
}

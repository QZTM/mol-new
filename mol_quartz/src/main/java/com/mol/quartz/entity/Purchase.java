package com.mol.quartz.entity;

import lombok.Data;
import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name="fy_purchase")
public class Purchase implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	private String pkMarbasclass;
	private Integer buyChannelId;
    private String goodsType;
    private String goodsBrand;
    private String goodsName;
    private String goodsSpecs;
    private String goodsBranch;
    private int goodsQuantity;
    private String goodsDetail;
    private String createTime;
    private String orgId;
    private String status;
    private String processInstanceId;
    private String staffId;
    private String url;
    private String title;
    private String processCode;
    private String cropId;
    private String auditor;
    private String applyCause;
    private String remarks;
    //零配件供应商数
    private String supplierSellerNum;
    //供应商id
    private String pkSupplier;
    //订单号
    private String orderNumber;
    //采购方要求报价的最小商家数
    private String quoteSellerNum;
    //pdf_url
    private String reqFileUrl;
    //实际报价的商家数量
    private String quoteCounts;
    //截止时间
    private String deadLine;
    //供货周期
    private String supplyCycle;
    //支付方式
    private String payMent;
    //技术支持电话
    private String technicalSupportTelephone;
    //专家评审
    private String expertReview;
    //参与议价人员列表
    private String negotiatePerson;
    //待议价结束后的订单说明
    private String negotiatedExplain;
    //待审核结束后的审核人的建议
    private String approverProposal;
    //评审奖励
    private String expertReward;
    //采购预算
    private String budget;
    //参与订单评审的专家数量
    private String expertCounts;
    //订单状态进入专家推荐的时间
    private String expertTime;
    //订单状态进入议价环节的时间
    private String bargainingTime;
    //订单状态进入审批环节的时间
    private String approvalStartTime;
    //订单状态结束审批的时间（最终的订单状态）
    private String approvalEndTime;
}

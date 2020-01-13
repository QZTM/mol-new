package com.mol.purchase.entity.dingding.purchase.enquiryPurchaseEntity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "fy_purchase_detail")
public class PurchaseDetail {

    @Id
    private String id;
    private String fyPurchaseId;
    private String pkMaterial;
    private int goodsQuantity;
    //最终选中的报价
    private String quoteId;
    //最终选中的推荐专家
    private String expertId;

    //一级到货状态 0==未到货 1==到货
    private String oneLevelArrivalStatus;
    //一级到货时间
    private String oneLevelArrivalTime;
    //二级到货状态 0==未到货 1==到货
    private String twoLevelArrivalStatus;
    //二级到货时间
    private String twoLevelArrivalTime;
}

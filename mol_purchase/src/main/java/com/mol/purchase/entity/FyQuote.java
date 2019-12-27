package com.mol.purchase.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 木耳易购
 * 报价页面提交的数据
 */
@Data
@Table(name = "fy_quote")
public class FyQuote {
    @Id
    private String id;
    private String fyPurchaseId;
    private String pkMaterialId;
    private String quote;
    private String reason;
    private String pkSupplierId;
    private String supplierSalesmanId;
    private String creationtime;
    //上次报价
    private String lastQuotePrice;
    //供应商供货周期
    private String supplyCycle;
    //专家推荐
    private String expertRecommendation;
    //专家推荐的该公司报价数量
    private String expertAgreeCounts;

    private String approvalOverStatus;
    private String approvalOverTime;
    private String approvalStatusEnable;

}

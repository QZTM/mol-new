package com.mol.supplier.config;


public class OrderStatus {
    /**
     * 订单状态
     */
    /**
     * 正在询价   正在采集报价
     */
    public static final Integer waitingQuote=1;
    /**
     * 采购结束
     */
    public static final Integer PurchaseClosure=2;
    /**
     * 采购废止   废止
     */
    public static final Integer Purchaseabolish=3;
    /**
     *   等待议价
     */
    public static final Integer UnderReview=4;
    /**
     * 进度页面状态/--------------------------------
     */

    /**
     * 等待审核结果
     */
    public static final Integer EndOfBargaining=6;

    /**
     *  通过
     */
    public static final Integer pass=7;
    /**
     *  淘汰
     */
    public static final Integer refuse=8;

    //报价表的状态
    //通过
    public static final Integer QUOTE_PASS=1001;
    //拒绝
    public static final Integer QUOTE_REFUSE=1002;

    //是否启用
    //是
    public static final Integer QUOTE_STATUS_USED=1;
    //否
    public static final Integer QUOTE_STATUS_NOUSERD=0;

}

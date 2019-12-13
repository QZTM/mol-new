package com.mol.config;

import lombok.Getter;
import java.io.IOException;
import java.util.Properties;

/**
 * 项目中的常量定义类
 * @author ly
 */
public class Constant {

    @Getter
    private String expertDomain ;
    @Getter
    private String purchaseDomain;
    @Getter
    private String supplierDomain;
    @Getter
    private Long expertAgentId;
    @Getter
    private Long purchaseAgentId;
    @Getter
    private Long supplierAgentId;

    public Constant(){
        Properties prop = new Properties();
        try {
            prop.load(Constant.class.getResourceAsStream("/application.properties"));
            this.setPurchaseDomain(prop.getProperty("purchasedomain"));
            this.setPurchaseAgentId(Long.parseLong(prop.getProperty("purchaseagentid")));
            this.setExpertDomain(prop.getProperty("expertdomain"));
            this.setExpertAgentId(Long.parseLong(prop.getProperty("expertagentid")));
            this.setSupplierDomain(prop.getProperty("supplierdomain"));
            this.setSupplierAgentId(Long.parseLong(prop.getProperty("supplieragentid")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Constant constant = new Constant();

    public static synchronized Constant getInstance(){
        if(constant == null){
            return new Constant();
        }else{
            return constant;
        }
    }

    /**
     * 采购渠道常量
     */
    public static final Integer 线下采购 = 1;
    public static final Integer 线上采购 = 2;
    public static final Integer 战略采购 = 3;
    public static final Integer 询价采购 = 4;
    public static final Integer 单一来源 = 5;
    public static final Integer 在线招标 = 6;
    public static final Integer 生产原料 = 7;
    public static final Integer 零配件 = 8;


    public static String ROOT_DIR = System.getProperty("user.dir");
    public static Long orederStartNum =1L;

    public static Long  orderorederStartNum = 1L;


    private void setExpertDomain(String expertDomain) {
        this.expertDomain = expertDomain;
    }

    private void setPurchaseDomain(String purchaseDomain) {
        this.purchaseDomain = purchaseDomain;
    }

    public void setSupplierDomain(String supplierDomain) {
        this.supplierDomain = supplierDomain;
    }

    private void setExpertAgentId(Long expertAgentId) {
        this.expertAgentId = expertAgentId;
    }

    private void setPurchaseAgentId(Long purchaseAgentId) {
        this.purchaseAgentId = purchaseAgentId;
    }

    private void setSupplierAgentId(Long supplierAgentId) {
        this.supplierAgentId = supplierAgentId;
    }
}

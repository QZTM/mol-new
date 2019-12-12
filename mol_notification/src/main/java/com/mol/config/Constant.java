package com.mol.config;

/**
 * 项目中的常量定义类
 */
public class Constant {
    /**
     * 域名
     */
    //public static final String domain = "fyycg1.vaiwan.com";
    public static final String EXPERT_DOMAIN = "140.249.22.202:8084";

    public static final String PURCHASE_DOMAIN = "140.249.22.202:8082";
    public static final String THIRD_DOMAIN = "140.249.22.202:8082";
    //public static final String domain = "fyycg1.vaiwan.com";

    /**
     * E应用的agentdId，登录开发者后台可查看
     */
    public static final Long AGENTID = 272636313L;

    /**
     * 第三方报价平台agentdId
     */
    public static final long AGENTID_THIRDPLAT=280740196L;

    /**
     *专家端agentId
     */
    public static final long AGENTID_EXPERT=300343663L;





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












}

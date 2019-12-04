package com.mol.sms;

import lombok.Data;

@Data
public class XiaoNiuMsmTemplate implements MsmTemplate{


    /**
     * 小牛云模板ID
     */

    public static final String 供应商注册模板ID = "1001";
    public static final String 供应商认证模板ID = "1002";
    public static final String 提醒供应商报价模板ID = "1003";
    public static final String 提醒专家评审模板ID = "1004";
    public static final String 提醒领导审批订单模板ID = "1005";
    public static final String 给专家推送评审成功结果模板ID = "1006";
    public static final String 给专家发送评审失败结果模板ID = "1007";
    public static final String 推送中标结果模板ID = "1008";
    public static final String 推送未中标结果模板ID = "1009";
    public static final String 供应商修改信息模板ID = "1010";
    public static final String 供应商业务员修改信息模板ID = "1011";


    private static String id;
    private static String name;

    private XiaoNiuMsmTemplate(String id, String name){
        this.id = id;
        this.name = name;
    }

    public static XiaoNiuMsmTemplate 供应商注册模板(){
        return new XiaoNiuMsmTemplate(XiaoNiuMsmTemplate.供应商注册模板ID,"供应商注册验证码");
    }

    public static XiaoNiuMsmTemplate 供应商认证模板(){
        return new XiaoNiuMsmTemplate(XiaoNiuMsmTemplate.供应商认证模板ID,"供应商认证验证码");
    }

    public static XiaoNiuMsmTemplate 提醒供应商报价模板(){
        return new XiaoNiuMsmTemplate(XiaoNiuMsmTemplate.提醒供应商报价模板ID,"提醒供应商报价");
    }

    public static XiaoNiuMsmTemplate 提醒专家评审模板(){
        return new XiaoNiuMsmTemplate(XiaoNiuMsmTemplate.提醒专家评审模板ID,"提醒专家评审");
    }

    public static XiaoNiuMsmTemplate 提醒领导审批订单模板(){
        return new XiaoNiuMsmTemplate(XiaoNiuMsmTemplate.提醒领导审批订单模板ID,"提醒领导审批订单");
    }

    public static XiaoNiuMsmTemplate 给专家推送评审成功结果模板(){
        return new XiaoNiuMsmTemplate(XiaoNiuMsmTemplate.给专家推送评审成功结果模板ID,"给专家推送评审成功结果");
    }

    public static XiaoNiuMsmTemplate 给专家发送评审失败结果模板(){
        return new XiaoNiuMsmTemplate(XiaoNiuMsmTemplate.给专家发送评审失败结果模板ID,"给专家发送评审失败结果");
    }

    public static XiaoNiuMsmTemplate 推送中标结果模板(){
        return new XiaoNiuMsmTemplate(XiaoNiuMsmTemplate.推送中标结果模板ID,"推送中标结果");
    }

    public static XiaoNiuMsmTemplate 推送未中标结果模板(){
        return new XiaoNiuMsmTemplate(XiaoNiuMsmTemplate.推送未中标结果模板ID,"推送未中标结果");
    }

    public static XiaoNiuMsmTemplate 供应商修改信息模板(){
        return new XiaoNiuMsmTemplate(XiaoNiuMsmTemplate.供应商修改信息模板ID,"供应商修改信息");
    }

    public static XiaoNiuMsmTemplate 供应商业务员修改信息模板(){
        return new XiaoNiuMsmTemplate(XiaoNiuMsmTemplate.供应商业务员修改信息模板ID,"供应商业务员修改信息");
    }


    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

}

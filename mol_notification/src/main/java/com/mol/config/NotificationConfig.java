package com.mol.config;

import util.TimeUtil;

public class NotificationConfig {
    //图片地址
    public static final String NOTIFICATION_IMAGE_url="http://140.249.22.202:8082/static/upload/imgs/supplier/ask.png";

    //议价图片地址
    public static final String 议价图片="http://140.249.22.202:8719/notification/yijia.png";
    //评审图片地址
    public static final String 评审图片="http://140.249.22.202:8719/notification/pingsheng.png";
    //审批图片地址
    public static final String 审批图片="http://140.249.22.202:8719/notification/shenpi.png";
    //报价图片地址
    public static final String 报价图片="http://140.249.22.202:8719/notification/baojia.png";
    //拒绝图片地址
    public static final String 拒绝图片="http://140.249.22.202:8719/notification/refuse.png";
    //通过图片地址
    public static final String 通过图片="http://140.249.22.202:8719/notification/pass.png";



    //小程序首页地址
    public static final String PURCHASE_APP="eapp://pages/purchase/purchase";
    //小程序进度详情页地址(采购)
    public static final String 进度详情页_PURCHASE_APP="eapp://pages/scheduleRepair/scheduleDetail/scheduleDetail";
    //小程序议价详情页地址(议价开始)
    public static final String 议价开始详情页_PURCHASE_APP="eapp://pages/purchase/workbench/negotiatedPrice/negotiatedPrice";
    //小程序议价详情页地址(议价结束)
    public static final String 议价结束详情页_PURCHASE_APP="eapp://pages/purchase/workbench/completeBargainingList/completeBargaining/completeBargaining";
    //小程序审批详情页地址(审批开始)
    public static final String 审批开始详情页_PURCHASE_APP="eapp://pages/purchase/workbench/tobeapproved/tobeapprovedDetail/tobeapprovedDetail";
    //小程序审批详情页地址(审批结束)
    public static final String 审批结束详情页_PURCHASE_APP="eapp://pages/purchase/workbench/approved/approvedDetail/approvedDetail";
    //供应商首页地址
    public static final String SUPPLIER_APP="http://"+ Constant.getInstance().getSupplierDomain() +"/index/findAll";
    //专家端首页地址
    public static final String EXPERT_APP="http://"+ Constant.getInstance().getExpertDomain() +"/expert/findAll";

    //title
    public static final String 通过= "已通过";
    public static final String 拒绝= "已拒绝";


    //专家端消息体的内容
    public static final String 专家端_NEW= TimeUtil.getNowDateTime()+"有新的采购订单来了，快去推荐吧！";
    public static final String 专家端_PASS= TimeUtil.getNowDateTime()+"您参与推荐的订单已通过，点击查看！";
    public static final String 专家端_REFUSE= TimeUtil.getNowDateTime()+"您参与推荐的订单未通过审核，点击查看！";

    //供应商消息体的内容
    public static final String 供应商_NEW= TimeUtil.getNowDateTime()+"有新的采购订单来了，快去报价吧！";
    public static final String 供应商_PASS= TimeUtil.getNowDateTime()+"您报价的订单已通过，点击查看！";
    public static final String 供应商_REFUSE= TimeUtil.getNowDateTime()+"您报价的订单未通过审核，点击查看！";

    //议价负责人消息体的内容
    public static final String 议价负责人_NEW= TimeUtil.getNowDateTime()+"有新的采购订单来了，快去议价吧！";
    public static final String 议价负责人_PASS= TimeUtil.getNowDateTime()+"您议价的订单已通过，点击查看！";
    public static final String 议价负责人_REFUSE= TimeUtil.getNowDateTime()+"您议价的订单未通过审核，点击查看！";

    //审批负责人消息体的内容
    public static final String 审批负责人_NEW= TimeUtil.getNowDateTime()+"有新的采购订单来了，快去审批吧！";
    public static final String 审批负责人_PASS= TimeUtil.getNowDateTime()+"您审批的订单已通过，点击查看！";
    public static final String 审批负责人_REFUSE= TimeUtil.getNowDateTime()+"您审批的订单未通过审核，点击查看！";

    //审批负责人消息体的内容
    public static final String 采购人_PASS= TimeUtil.getNowDateTime()+"您采购的订单已通过，点击查看！";
    public static final String 采购人_REFUSE= TimeUtil.getNowDateTime()+"您采购的订单未通过审核，点击查看！";
}

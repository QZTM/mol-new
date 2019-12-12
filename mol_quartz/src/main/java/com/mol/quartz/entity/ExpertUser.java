package com.mol.quartz.entity;

import lombok.Data;

@Data
public class ExpertUser {

    private String id;//                  varchar(200) not null comment '专家表主键'
    private String name;//                varchar(200) null comment '专家名称',
    private String avatar;//              varchar(200) null comment '头像',
    private String dd_id;//               varchar(200) null comment '专家的钉钉id',
    private String org_id;//              varchar(200) null comment '专家所属公司',
    private String mobile;//              varchar(200) null comment '专家电话',
    private String create_time;//         varchar(200) null comment '创建时间',
    private String last_update_time;//    varchar(200) null comment '最后修改时间',
    private String last_signin_time;//    varchar(200) null comment '最后登录时间',
    private String authentication;//      varchar(200) null comment '认证   0=未认证 1 =审核中 2=认证成功 3=认证失败',
    private String expert_grade;//        varchar(200) null comment '专家等级 0= 普通专家 1=金牌专家',
    private String review_number;//       varchar(200) null comment '参与评审',
    private String pass;//                varchar(200) null comment '通过的次数',
    private String pass_rate;//           varchar(200) null comment '通过率',
    private String major;//               varchar(200) null comment '专业 所属行业',
    private String award;//               varchar(200) null comment '获得的奖励金额',
    private String balance;//             varchar(200) null comment '余额',
    private byte[] front_of_id;//         longblob     null comment '身份证正面',
    private byte[] reverse_of_id ;//      longblob     null comment '身份证反面',
    private byte[] front_of_business;//   longblob     null comment '名片正面',
    private byte[] reverse_of_business;// longblob     null comment '名片反面',
    private byte[] other_documents_one;// longblob     null comment '其他证件1',
    private byte[] other_documents_two;// longblob     null comment '其他证件2',
    private String birthday;//            varchar(200) null comment '出生年月',
    private String age;//                 varchar(200) null comment '年龄',
    private String work_life;//           varchar(200) null comment '工作年限',
    private String mar_id;//              varchar(200) null comment '认证的物料类型id',
    private String id_number;

}

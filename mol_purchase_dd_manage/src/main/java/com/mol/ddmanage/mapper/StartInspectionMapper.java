package com.mol.ddmanage.mapper;

import java.util.ArrayList;
import java.util.Map;

public interface StartInspectionMapper
{
    ArrayList<Map> Get_app_auth_org();//获取公司表
     ArrayList<Map> Get_fy_buy_channel();//获取状态表
    void insert_fy_buy_channel_row(String id,String name,String state);//在状态表插入一条数据

    ArrayList<Map> Get_app_purchase_approve();//获取审批流表
    void insert_app_purchase_approve(String id,String purchase_main_person,String purchase_approve_leader,String purchase_approve_list);//向审批流表中加入审批数据

    ArrayList<Map> Get_app_org_buy_channel_approve_middle();//获取审批人和审批中间表
    void insert_app_org_buy_channel_approve_middle(String id,String app_org_id,String buy_channel_id,String purchase_approve_id);//插入审批人和审批中间表数据
}

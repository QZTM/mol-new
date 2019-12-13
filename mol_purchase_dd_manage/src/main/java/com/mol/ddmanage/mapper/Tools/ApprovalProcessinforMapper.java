package com.mol.ddmanage.mapper.Tools;

import com.mol.ddmanage.Ben.Tools.ApprovalProcessSettingListben;

import java.util.Map;

public interface ApprovalProcessinforMapper
{
    ApprovalProcessSettingListben GetApprovalInfor(String id);

    void Updata_app_purchase_approve(String app_purchase_approve_id,String purchase_approve_list,String Unlock_ids_str);//更新审批人员
    void updata_fy_buy_channel(String fy_buy_channel_id,String state);//更新审批状态表
    Map Get_app_user(String user_id);//获取人员列表
    Map Get_app_user_id(String id);//获取人员列表
    Map Get_app_org_buy_channel_approve_middle(String id);//获取审核中间表
    Map Get_app_purchase_approve(String id);//获取审批表
}

package com.mol.ddmanage.mapper.Office;

import com.mol.ddmanage.Ben.PurchasOrderManagement.PurchasOrderinforben;

import java.util.ArrayList;
import java.util.Map;

public interface ElectronicContractSigninginforMapper
{
    ArrayList<PurchasOrderinforben> GetConfirmSupplier(String PurchasId);//获取已经确认的供应商报价的商品
    void Upload_Contract(String id,String contract_id,String upload_time,String upload_user_id);//上传合同记录
    void Supplier_Contract(String id,String purchase_id,String supplier_id,String contract_id,String template_id,String create_time,String start_sign_time,String sign_status);//合同对应的供应商
    Map GetContractId(String  purchasId, String supplierid);//获取此订单的供应商合同表
    Map GetCustomer_id(String open_id);//获取注册记录表

    Map Get_fy_purchase_supplier_contract(String ContractId);//获取订单合同签署状态表
    Map  Setfadada_auth_record(String customerId);//查看fadada_auth_record是否有对应的数据
    void AuthSynchronizeNotity(String id,String customer_id,String transaction_no,String authentication_type,String sign,String status,String create_time);//认证同步回调地址
    void AuthAsynchronousNotity(String customerId,String transaction_no,String status,String status_desc,String last_update_time);//认证异步回调地址
    void SignContractNotity(String contract_id,String sign_status);//手动签署合同回调 更改签署状态为采购方签署  更新fy_purchase_supplier_contract表
}

package com.mol.ddmanage.mapper.Office;

import java.util.Map;

public interface ElectronicContractSigninginforMapper
{
    void Upload_Contract(String id,String contract_id,String upload_time,String upload_user_id);//上传合同记录
    void Supplier_Contract(String id,String purchase_id,String supplier_id,String contract_id,String template_id,String create_time,String start_sign_time,String sign_status);//合同对应的供应商
    Map GetContractId(String  purchasId, String supplierid);
    Map GetCustomer_id(String open_id);

    Map  Setfadada_auth_record(String customerId);//查看fadada_auth_record是否有对应的数据
    void AuthSynchronizeNotity(String id,String customer_id,String transaction_no,String authentication_type,String sign,String status,String create_time);//认证同步回调地址
    void AuthAsynchronousNotity(String customerId,String transaction_no,String status,String status_desc,String last_update_time);//认证异步回调地址
}

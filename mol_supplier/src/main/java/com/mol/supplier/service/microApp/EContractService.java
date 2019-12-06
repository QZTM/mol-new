package com.mol.supplier.service.microApp;

import com.alibaba.fastjson.JSONObject;
import com.mol.fadada.handler.ContractHandler;
import entity.ServiceResult;
import org.springframework.stereotype.Service;

@Service
public class EContractService {


    public ServiceResult contractFiling(String contractId){
        String s = ContractHandler.contractFiling(contractId);
        JSONObject resultJsonObj = JSONObject.parseObject(s);
        if("success".equals(resultJsonObj.getString("result"))) {
            return ServiceResult.successMsg("归档成功");
        }else {
            return ServiceResult.failureMsg(resultJsonObj.getString("msg"));
        }
    }

}

package com.mol.deptpurchase.controller;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiDepartmentGetResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.mol.deptpurchase.service.AppTokenService;
import entity.ServiceResult;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Log
@Controller
@RequestMapping("/init")
public class InitController {

    @Autowired
    private AppTokenService appTokenService;


    @RequestMapping("/getUserInfoByAccessCode")
    @ResponseBody
    public String getUserInfoByAccessCode(String code){
        log.info("getUserInfoByAccessCode,code:"+code);
        /*获取钉钉用户id*/
        String userId = "";
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo");
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(code);
        request.setHttpMethod("GET");
        OapiUserGetuserinfoResponse response = new OapiUserGetuserinfoResponse();
        try{
            response = client.execute(request, appTokenService.getSupplierAppToken());
        }catch (Exception e){
            log.warning("通过免登授权码获取用户id失败（ddAPI）");
            throw new RuntimeException("通过免登授权码获取用户id失败（ddAPI）");
        }
        userId = response.getUserid();
        log.info("userId:"+userId);
        return userId;
    }

}

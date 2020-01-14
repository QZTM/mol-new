package com.mol.deptpurchase.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.taobao.api.ApiException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Log
@Service
public class AppTokenService {

    public String getSupplierAppToken(){
        log.info("..getToken...");
            try {
                DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
                OapiGettokenRequest request = new OapiGettokenRequest();
                request.setAppkey("dingahkyew1sw286fzdb");
                request.setAppsecret("8L568tbt80tvR8VXFHqs-adOn7vI5WciHEnapOKlUHeyDTOEvWCtw4M3qOBJrKxr");
                request.setHttpMethod("GET");
                OapiGettokenResponse response = client.execute(request);
                String token = response.getAccessToken();
                log.info("token:"+token);
                return token;
            } catch (ApiException e) {
                log.warning("getAccessToken failed");
                throw new RuntimeException("服务器通讯异常，请稍后再试");
            }
        }





    }


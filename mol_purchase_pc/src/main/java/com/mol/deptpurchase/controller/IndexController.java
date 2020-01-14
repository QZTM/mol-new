package com.mol.deptpurchase.controller;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.taobao.api.ApiException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@Log
public class IndexController {


    @RequestMapping("/index")
    public String showIndex(String code, HttpServletRequest request) throws ApiException {
        log.info("/index...code:"+code);

        DefaultDingTalkClient  client = new DefaultDingTalkClient("https://oapi.dingtalk.com/sns/getuserinfo_bycode");
        OapiSnsGetuserinfoBycodeRequest req = new OapiSnsGetuserinfoBycodeRequest();
        req.setTmpAuthCode(code);
        OapiSnsGetuserinfoBycodeResponse response = client.execute(req,"dingfps4ilpbxa6wmw0z","_GXmOidlkjda-vZzxdy24WSI7wcKlDsoQqCp-vNORdhWdNjaoYvsKlgjvJfgsBhf");
        return "index";
    }

    @RequestMapping("/{pageName}.html")
    public String returnHtml(@PathVariable("pageName") String pageName){
        return pageName;
    }

}

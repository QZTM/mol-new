package com.mol.expert.service.microApp;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiGetJsapiTicketRequest;
import com.dingtalk.api.response.OapiGetJsapiTicketResponse;
import com.mol.cache.CacheHandle;
import com.taobao.api.ApiException;
import lombok.Synchronized;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 获取微应用jsapiticket(缓存)
 */
@Service
public class MicroJsapiTicketService {

    private static final Logger bizLogger = LoggerFactory.getLogger(MicroJsapiTicketService.class);

    public static final String MICROAPPJSAPITICKETKEY = "zhuanjiajsApiTicket";

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private MicroTokenService microTokenService;

    @Synchronized
    public String getJsApiTicket() {
        String jsapiTicket = cacheHandle.getStr(MICROAPPJSAPITICKETKEY);
        if (StringUtils.isEmpty(jsapiTicket)) {
            bizLogger.info("访问钉钉服务器获取JSAPITICKET");
            DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/get_jsapi_ticket");
            OapiGetJsapiTicketRequest req = new OapiGetJsapiTicketRequest();
            req.setTopHttpMethod("GET");
            try {
                OapiGetJsapiTicketResponse execute = client.execute(req, microTokenService.getToken());
                System.out.println(execute.getExpiresIn());
                bizLogger.info("JSAPITICKET:" + execute.getTicket());
                jsapiTicket = execute.getTicket();
                cacheHandle.saveStr(MICROAPPJSAPITICKETKEY,2*60*58,jsapiTicket);
            } catch (ApiException e) {
                e.printStackTrace();
                throw new RuntimeException("获取ticket时出错");
            }
        }
        return jsapiTicket;
    }

}

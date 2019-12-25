package com.mol.supplier.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * 钉钉微应用参数
 */
@Component
@Configuration
@Log
public class MicroAttr {

    public MicroAttr(){
        log.info("初始化自定义配置类");
    }

    @Getter
    @Setter
    @Value("${corpid}")
    private String corpId ;
    @Getter
    @Setter
    @Value("${appkey}")
    private String appKey ;
    @Getter
    @Setter
    @Value("${appsecret}")
    private String appSecret ;
    @Getter
    @Setter
    @Value("${agentid}")
    private String agentId ;
    @Getter
    @Setter
    @Value("${server.port}")
    private String port ;

    @Getter
    @Setter
    @Value("${httppotocol}")
    public String http ;

    /**
     * 域名
     */
    @Getter
    @Setter
    @Value("${domain}")
    public String domain ;

    //供应商认证完成
    public static final Integer  SUPSTATE_SUCCESS = 1;
    //供应商认证正在审核
    public static final Integer  SUPSTATE_LOADING = 2;
    //供应商认证失败
    public static final Integer  SUPSTATE_FAIL = 4;
    //供应商未认证
    public static final Integer WITHOUT_SUPSTATE=0;
    //供应商未付款，未形成订单
    public static final Integer SUPSTATE_BEFORE_CREATE_PAY=5;
    //供应商未付款，已形成订单
    public static final Integer SUPSTATE_BEFORE_PAYOVER=6;


}

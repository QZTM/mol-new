package com.mol.expert.config.microApp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 钉钉微应用参数
 */
@Component
@Configuration
public class MicroAttr {
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

//    //专家端(杨江彦）
//    public static final String APP_KEY = "ding14jg3np2dvvtbiso";
//    public static final String APP_SECRET = "35rkiTbDCRDwG5APgIpeQm89Nwhy8dg52sCpXgHuZ1KYaY6vJVBnoaud_BjyPJn7";
//    public static final String AGENTID = "330312434";




    //供应商认证完成
    public static final Integer  SUPSTATE_SUCCESS = 1;
    //供应商认证正在审核
    public static final Integer  SUPSTATE_LOADING = 2;
    //供应商认证失败
    public static final Integer  SUPSTATE_FAIL = 4;
    //供应商未认证
    public static final Integer WITHOUT_SUPSTATE=0;




}

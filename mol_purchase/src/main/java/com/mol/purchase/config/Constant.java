package com.mol.purchase.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 项目中的常量定义类
 */
@Configuration
@Component
@Log
public class Constant {

    public Constant(){
        log.info("初始化自定义配置类");
    }

    /**
     * 域名
     */
    @Setter
    @Getter
    @Value("${domain}")
    private String domain ;
    /**
     * 企业corpid, 需要修改成开发者所在企业
     */
    @Setter
    @Getter
    @Value("${corpid}")
    private String corpId ;
    /**
     * 开发者后台->企业自建应用->选择您创建的E应用->查看->AppKey
     */
    @Setter
    @Getter
    @Value("${appkey}")
    private String appKey ;
    /**
     * 开发者后台->企业自建应用->选择您创建的E应用->查看->AppSecret
     */
    @Setter
    @Getter
    @Value("${appsecret}")
    private String appSecret;
    /**
     * 数据加密密钥。用于回调数据的加密，长度固定为43个字符，从a-z, A-Z, 0-9共62个字符中选取,您可以随机生成
     */
    public static final String ENCODING_AES_KEY = "68PJEQx4AXwe0EPqpdk6x493nRFdR233ON35xakZK63";

    /**
     * 加解密需要用到的token，企业可以随机填写。如 "12345"
     */
    public static final String TOKEN = "12345";

    /**
     * 应用的agentdId，登录开发者后台可查看
     */
    @Setter
    @Getter
    @Value("${agentid}")
    private Long agentId ;

    /**
     * 审批模板唯一标识，可以在审批管理后台找到PROC-2C58A0E4-4248-4246-B02C-A13CBB4EDF51
     */
    public static final String UNDERLINEPUR_PROCESS_CODE = "PROC-F0EF1DDD-66A1-48BF-9934-66BB18F4A1AC";
    public static final String ONLINEPUR_PROCESS_CODE="PROC-18D734DC-C7A2-4984-9226-050C06E61147";

    /**
     * 回调host
     */
    public String CALLBACK_URL_HOST = this.getDomain();

    /**
     * 采购渠道常量
     */
    public static final Integer 线下采购 = 1;
    public static final Integer 线上采购 = 2;
    public static final Integer 战略采购 = 3;
    public static final Integer 询价采购 = 4;
    public static final Integer 单一来源 = 5;
    public static final Integer 在线招标 = 6;
    public static final Integer 生产原料 = 7;
    public static final Integer 零配件 = 8;


    public static String ROOT_DIR = System.getProperty("user.dir");
    public static Long orederStartNum =1L;

    public static Long  orderorederStartNum = 1L;


}

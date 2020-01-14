package com.mol.ddmanage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Basic_config
{
    @Getter
    @Setter
    @Value("${localT}")
    public  String domain_name;//域名   http://fyycg66.vaiwan.com  http://zj.moleertech.cn:8080
    @Getter
    @Setter
    @Value("${testurl}")
    public String testurl;
    public final static String open_id="1202851016954982400";//接入法大大平台用户唯一id1202851016954982400
}

package com.mol.quartz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class Constant {

    /**
     * E应用的agentdId，登录开发者后台可查看
     */
    public Long AGENTID = 272636313L;
//    public static final Long AGENTID = 330159066L;

    /**
     * 第三方报价平台agentdId
     */
    public long AGENTID_THIRDPLAT=280740196L;

    /**
     *专家端agentId
     */
    public long AGENTID_EXPERT=300343663L;

}

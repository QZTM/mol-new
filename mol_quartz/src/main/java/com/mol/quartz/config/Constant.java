package com.mol.quartz.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@Data
public class Constant {

    /**
     * E应用的agentdId，登录开发者后台可查看
     */
    @Value("${purchaseagentid}")
    public Long purchaseAgentId ;
//    public static final Long AGENTID = 330159066L;

    /**
     * 第三方报价平台agentdId
     */
    @Value("${supplieragentid}")
    public long supplierAgentId;

    /**
     *专家端agentId
     */
    @Value("${expertagentid}")
    public long expertAgentId;

}

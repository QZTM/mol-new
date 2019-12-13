package com.mol.quartz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class Constant {

    /**
     * E应用的agentdId，登录开发者后台可查看
     */
    @Value("${purchase.agentid}")
    public Long purchaseAgentId ;
//    public static final Long AGENTID = 330159066L;

    /**
     * 第三方报价平台agentdId
     */
    @Value("{supplier.agentid}")
    public long supplierAgentId;

    /**
     *专家端agentId
     */
    @Value("{expert.agentid}")
    public long expertAgentId;


    public Long getPurchaseAgentId() {
        return purchaseAgentId;
    }

    public void setPurchaseAgentId(Long purchaseAgentId) {
        this.purchaseAgentId = purchaseAgentId;
    }

    public long getSupplierAgentId() {
        return supplierAgentId;
    }

    public void setSupplierAgentId(long supplierAgentId) {
        this.supplierAgentId = supplierAgentId;
    }

    public long getExpertAgentId() {
        return expertAgentId;
    }

    public void setExpertAgentId(long expertAgentId) {
        this.expertAgentId = expertAgentId;
    }
}

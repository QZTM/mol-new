package com.mol.quartz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "mol-expert")
public interface PurchaseClient {

    @RequestMapping("/app/getToken")
    public String getToken();

}

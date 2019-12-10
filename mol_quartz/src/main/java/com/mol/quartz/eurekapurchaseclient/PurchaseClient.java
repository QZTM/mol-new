package com.mol.quartz.eurekapurchaseclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "mol-purchase")
public interface PurchaseClient {

    @GetMapping(value = "/app/getToken", produces = { "application/json;charset=UTF-8" })
    public String getToken();

}

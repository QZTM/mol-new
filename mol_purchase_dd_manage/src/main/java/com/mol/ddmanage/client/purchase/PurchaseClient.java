package com.mol.ddmanage.client.purchase;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "mol-purchase",fallback = PurchaseClientImpl.class)
public interface PurchaseClient {

    @GetMapping(value = "/ac/deploy", produces = { "application/json;charset=UTF-8" })
    public String deploy(String name, String processId, String processName, String orgId,String buyChannelId);

}

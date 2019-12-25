package com.mol.ddmanage.client.purchase;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "mol-purchase",fallback = PurchaseClientImpl.class)
public interface PurchaseClient {

    @GetMapping(value = "/ac/deploy", produces = { "application/json;charset=UTF-8" })
    public String deploy(@RequestParam String name, @RequestParam String processId, @RequestParam String processName, @RequestParam String orgId, @RequestParam String buyChannelId);

}

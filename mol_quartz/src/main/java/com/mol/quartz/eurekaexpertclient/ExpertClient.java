package com.mol.quartz.eurekaexpertclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "mol-expert")
public interface ExpertClient {

    @GetMapping(value = "/molexpert/getToken", produces = { "application/json;charset=UTF-8" })
    public String getToken();

}

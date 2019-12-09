package com.mol.quartz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "mol-expert",url = "140.249.22.202:8084")
public interface ExpertClient {

    @RequestMapping("/molexpert/getToken")
    public String getToken();

}

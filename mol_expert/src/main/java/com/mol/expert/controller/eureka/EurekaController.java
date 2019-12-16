package com.mol.expert.controller.eureka;

import com.mol.expert.service.microApp.MicroTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/molexpert")
public class EurekaController {

    @Autowired
    private MicroTokenService microTokenService;

    @RequestMapping("/getToken")
    @ResponseBody
    public String getToken(){
        return microTokenService.getToken();
    }

}

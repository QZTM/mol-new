package com.mol.supplier.controller.microApp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/actuator")
@CrossOrigin
public class EurekaActuatorInfoController {

    @RequestMapping("/info")
    public String actuatorInfo(){
        return "actuator";
    }

}

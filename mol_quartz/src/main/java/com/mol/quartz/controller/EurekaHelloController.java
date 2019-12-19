package com.mol.quartz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@CrossOrigin
@RequestMapping("/actuator")
public class EurekaHelloController {

    @RequestMapping("/info")
    public String showHello(HttpServletRequest request){
        return "eureka";
    }

}

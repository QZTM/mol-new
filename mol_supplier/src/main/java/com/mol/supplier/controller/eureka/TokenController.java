package com.mol.supplier.controller.eureka;

import com.mol.supplier.service.microApp.MicroTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/molsupplier")
@CrossOrigin
public class TokenController {

    @Autowired
    MicroTokenService microTokenService;

    @RequestMapping("/getToken")
    public String getToken(){
        return microTokenService.getToken();
    }

}

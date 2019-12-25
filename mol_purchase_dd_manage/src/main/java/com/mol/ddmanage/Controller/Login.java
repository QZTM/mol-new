package com.mol.ddmanage.Controller;

import com.mol.ddmanage.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Login")
public class Login
{

    @Autowired
    LoginService loginService;
    @RequestMapping("/verification_login")//扫描二维码登录

    public String  user_login(/*@RequestParam Map map, HttpSession session, Model model*/)
    {
/*        Map map1=loginService.LoginService_dingding(map,session);
       // Map map1=loginService.LoginOAService_dingding(map,session);
       if ((Boolean) map1.get("rest")!=false)
       {
           model.addAttribute("name",map1.get("name").toString());//登录人的名子

           return "new_file";
       }
       else
       {
           return "new_file";
       }*/
        return "newfile";
    }


}

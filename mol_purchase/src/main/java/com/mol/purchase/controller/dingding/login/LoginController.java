package com.mol.purchase.controller.dingding.login;
import com.mol.purchase.service.dingding.login.LoginService;
import com.mol.purchase.service.token.TokenService;
import entity.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 企业内部E应用Quick-Start示例代码 实现了最简单的免密登录（免登）功能
 */
@RestController
@CrossOrigin
@RequestMapping("/app")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private TokenService tokenService;

    /**
     * 欢迎页面,通过url访问，判断后端服务是否启动
     */
    @RequestMapping(value = "/welcome", method = RequestMethod.POST)
    public String welcome() {
        return "welcome";
    }

    /**
     * 钉钉用户登录，显示当前登录用户的userId和名称
     *
     * @param requestAuthCode 免登临时code
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult login(@RequestParam(value = "authCode") String requestAuthCode) {
        return loginService.login(requestAuthCode);
    }

    /**
     * pc端用户登录
     * @param dduserid
     * @return
     */
    @RequestMapping(value = "/PClogin", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResult PClogin(@RequestParam(value = "dduserid") String dduserid) {
        return loginService.PClogin(dduserid);
    }

    @RequestMapping("/getToken")
    @ResponseBody
    public String getToken(){
        return tokenService.getToken();
    }




}



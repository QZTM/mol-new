package com.mol.expert.controller.microApp;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.oapi.lib.aes.DingTalkJsApiSingnature;
import com.mol.expert.config.ExpertStatus;
import com.mol.expert.entity.MicroApp.DDUser;
import com.mol.expert.service.expert.ExpertService;
import com.mol.expert.service.microApp.*;
import com.mol.expert.config.microApp.MicroAttr;
import com.mol.expert.entity.expert.ExpertUser;
import com.mol.expert.exception.microApp.OApiException;
import com.mol.expert.util.RandomStr;
import com.mol.expert.util.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/microApp/login")
public class ExpertLoginController {

    private Logger logger = LoggerFactory.getLogger(ExpertLoginController.class);
    @Autowired
    private MicroAttr microAttr;

    @Autowired
    private MicroGetDDUserInfoService microGetDDUserInfoService;

    @Autowired
    private MicroSupplierService microSupplierService;

    @Autowired
    private MicroSalesmanService microSalesmanService;

    @Autowired
    private MicroJsapiTicketService microJsapiTicketService;

    @Autowired
    private MicroTokenService microTokenService;

    @Autowired
    private ExpertService expertService;


    /**
     * 显示登陆/注册页面
     *
     * @param registType 注册类型，1为企业和业务员，2为添加业务员
     * @return
     */
    @RequestMapping("/show")
    public String login(String registType, Model model) {
        model.addAttribute("registType", registType);
        return "login";
    }


    @RequestMapping("/show1")
    public String login1(String registType, Model model) {
        model.addAttribute("registType", registType);
        return "login.1";
    }


    /**
     * 根据免登授权码和access_token获取用户信息
     * 往session中存放dduser,ddorg,dddept        supplier    salesman信息
     * 首先获取dduser，ddorg,dddept信息，根据人员的姓名和手机号码去供应商业务员表里面查询，如果查询到了记录，则直接封装 supplier  和   salesman
     * 如果没有查询到记录，那么根据ddorg的姓名去供应商表里面查询是否有记录，如果有记录，那么把该业务员绑定到该供应商下面，如果没有记录那么注册一个新的供应商，然后绑定业务员
     * <p>
     * 当前登录人员的状态可以分为以下几种：
     * <p>
     * 1.
     *
     * @param code
     * @return
     */
    @RequestMapping("/expertInfo")
    @ResponseBody
    public ServiceResult getSticket(String code, HttpSession session,HttpServletRequest request) {
        logger.info("进入expertInfo方法,code:"+code);
        //1.获取用户钉钉id‘
        String userDdId = microGetDDUserInfoService.getDDUserId(code);
        logger.info("获取到钉钉用户id："+userDdId);
        //2.通过ddId 查询专家库，
        ExpertUser expertUser=microSalesmanService.findExpertUser(userDdId);
        logger.info("method:getSticket describe:查询登录人信息  result:"+expertUser);
        if (expertUser==null){
            logger.info("method:getSticket describe:专家信息为空  ");
            //2.1 没有数据
            //获取电话号码
            //获取组织
            //获取头像
            //获取名称
            OapiUserGetResponse response = microGetDDUserInfoService.getUserDetail(userDdId);
            if (response == null) {
                logger.info("method:getSticket describe:查询登录人钉钉信息失败  result:"+response);
                throw new RuntimeException("获取用户信息失败");
            }
            DDUser ddUser = JSONObject.parseObject(JSONObject.toJSONString(response), DDUser.class);
            logger.info("获取到的用户详情：");
            logger.info(ddUser.toString());
            if (ddUser.getMobile() == null) {
                throw new RuntimeException("未获取到手机号码，请绑定手机号或开启通讯录权限后重试");
            }

            ExpertUser save = microSalesmanService.save(ddUser);
            logger.info("method:getSticket describe:保存用户信息  result:"+save);
            session.setAttribute("user",save);
            //2.1.2保存，存入session专家第一次登陆，提醒认证
        }else {
            //2.2 有数据 更新登陆时间  存入session
            microSalesmanService.updataSignInTime(expertUser);
            logger.info("method:getSticket describe:更新登录时间  result:"+expertUser);

            //2.2.1没有认证 提醒认证
            session.setAttribute("user",expertUser);
            if(Integer.parseInt(expertUser.getAuthentication())== ExpertStatus.EXPERT_UNCERTIFIED){
                return ServiceResult.success("请完善认证信息");
            }
            //2.2.2认证，欢迎使用
        }

    return ServiceResult.success("获取用户信息成功！");


    }



    /**
     * 计算当前请求的jsapi的签名数据<br/>
     * <p>
     * 如果签名数据是通过ajax异步请求的话，签名计算中的url必须是给用户展示页面的url
     *
     * @param request
     * @return
     */
    public Map getConfig(HttpServletRequest request) {
        String urlString = request.getRequestURL().toString();
        String queryString = request.getQueryString();

        String queryStringEncode = null;
        String url;
        if (queryString != null) {
            queryStringEncode = URLDecoder.decode(queryString);
            url = urlString + "?" + queryStringEncode;
        } else {
            url = urlString;
        }
        /**
         * 确认url与配置的应用首页地址一致
         */
        System.out.println(url);

        /**
         * 随机字符串
         */
        String nonceStr = RandomStr.getRandom(7,RandomStr.TYPE.LETTER);
        System.out.println(nonceStr);
        long timeStamp = System.currentTimeMillis() / 1000;
        String signedUrl = url;
        String accessToken = null;
        String ticket = null;
        String signature = null;

        try {
            accessToken = microTokenService.getToken();

            ticket = microJsapiTicketService.getJsApiTicket();
            signature = sign(ticket, nonceStr, timeStamp, signedUrl);

        } catch (OApiException e) {
            e.printStackTrace();
        }

        Map<String, Object> configValue = new HashMap<>();
        configValue.put("jsticket", ticket);
        configValue.put("signature", signature);
        configValue.put("nonceStr", nonceStr);
        configValue.put("timeStamp", timeStamp);
        configValue.put("corpId", microAttr.getCorpId());
        configValue.put("agentId", microAttr.getAgentId());

        //String config = JSON.toJSONString(configValue);
        return configValue;
    }



    public String sign(String ticket, String nonceStr, long timeStamp, String url) throws OApiException {
        try {
            return DingTalkJsApiSingnature.getJsApiSingnature(url, nonceStr, timeStamp, ticket);
        } catch (Exception ex) {
            throw new OApiException();
        }
    }





}

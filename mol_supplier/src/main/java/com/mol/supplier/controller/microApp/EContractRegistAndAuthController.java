package com.mol.supplier.controller.microApp;

import com.mol.fadada.handler.RegistAndAuthHandler;
import com.mol.fadada.pojo.AuthRecord;
import com.mol.fadada.pojo.RegistRecord;
import com.mol.supplier.config.Constant;
import com.mol.supplier.entity.MicroApp.Salesman;
import com.mol.supplier.entity.MicroApp.Supplier;
import com.mol.supplier.mapper.microApp.FadadaAuthRecordMapper;
import com.mol.supplier.service.microApp.MicroUserService;
import entity.ServiceResult;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import util.IdWorker;
import util.TimeUtil;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 电子合同平台注册及认证
 */
@Controller
@RequestMapping("/econtract")
@Log
public class EContractRegistAndAuthController {

    @Autowired
    private MicroUserService microUserService;

    @Autowired
    private FadadaAuthRecordMapper fadadaAuthRecordMapper;

    @Autowired
    private IdWorker idWorker;

    @RequestMapping("/checkRegist")
    @ResponseBody
    public ServiceResult checkRegist(HttpSession session, @RequestParam String registType) throws InterruptedException {
        Supplier supplier = microUserService.getSupplierFromSession(session);
        Salesman salesman = microUserService.getUserFromSession(session);
        //判断该单位是否注册了：
        RegistRecord rr = new RegistRecord();
        String openId = "";
        if("1".equals(registType)) {
            openId = salesman.getId();
        }else if("2".equals(registType)){
            openId = supplier.getPkSupplier();
        }
        ServiceResult registResult = RegistAndAuthHandler.checkIfRegisted(openId, registType);
        if (!registResult.isSuccess()) {
            return ServiceResult.failure("没有注册");
        } else {
            rr = (RegistRecord)registResult.getResult();
            return ServiceResult.success(rr.getCustomerId());
        }
    }


    /**
     * 企业注册
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/regist", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult toRegistOrg(String registType, HttpSession session) {
        Supplier supplier = microUserService.getSupplierFromSession(session);
        Salesman salesman = microUserService.getUserFromSession(session);
        String openId = "";
        if("1".equals(registType)) {
            openId = salesman.getId();
        }else if("2".equals(registType)){
            openId = supplier.getPkSupplier();
        }
        return RegistAndAuthHandler.regAccount(openId, registType);
    }


    @RequestMapping("/checkAuth")
    @ResponseBody
    public ServiceResult checkAuth(HttpSession session, @RequestParam String customerId, String authType) throws InterruptedException {
        Supplier supplier = microUserService.getSupplierFromSession(session);
        Salesman salesman = microUserService.getUserFromSession(session);
        //验证是否认证了：
        ServiceResult authResult = RegistAndAuthHandler.checkIfAuthedByCustomerId(customerId, authType);
        if (!authResult.isSuccess()) {
            return ServiceResult.failure("30000","没有认证",customerId);
        } else {
            return ServiceResult.success(authResult);
        }
    }


    @RequestMapping(value = "/getAuthUrl", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult getAuthUrl(String customerId, String authType,HttpSession session) {
        Supplier supplier = microUserService.getSupplierFromSession(session);
        log.info("getAuthUrl:...customerId:"+customerId+",authType:"+authType);
        ServiceResult sr = new ServiceResult();
        if ("1".equals(authType)) {
             sr = RegistAndAuthHandler.getAuthPersonurl(customerId, "http://"+Constant.domain + "/fddCallback/personAuth", "http://"+Constant.domain + "/fddCallback/personAuthTo?customerId="+customerId);
        } else if ("2".equals(authType)) {
             sr = RegistAndAuthHandler.getAuthCompanyurl(customerId, "http://"+Constant.domain + "/fddCallback/orgAuth", "http://"+Constant.domain + "/fddCallback/orgAuthTo?customerId="+customerId);
        }
        if(sr.isSuccess()) {
            Map paraMap = (HashMap)sr.getResult();
            //第一次存入数据库：
            //查询数据库中有没有该单位的认证记录：
            try {
                saveGetedAuthDataToDb(paraMap, customerId, authType);
            }catch (Exception e) {
                log.warning(e.getMessage());
            }
            return ServiceResult.success(paraMap.get("url"));
        }else {
            return ServiceResult.failure("获取失败");
        }
    }

    public void saveGetedAuthDataToDb(Map paraMap,String customerId,String authType){
        AuthRecord authRecordNew = new AuthRecord();
        //如果有记录，则更改状态：
        authRecordNew.setId(idWorker.nextId()+"");
        authRecordNew.setCustomerId(customerId);
        authRecordNew.setTransactionNo((String)paraMap.get("transactionNo"));
        authRecordNew.setUrl((String)paraMap.get("url"));
        authRecordNew.setStatus("0");
        authRecordNew.setAuthenticationType(authType);
        authRecordNew.setCreateTime(TimeUtil.getNowDateTime());
        fadadaAuthRecordMapper.insertSelective(authRecordNew);
    }

    @RequestMapping("/showAuthSuccess")
    public String showAuthSuccessPage(){
        return "fadada_auth_success";
    }
}
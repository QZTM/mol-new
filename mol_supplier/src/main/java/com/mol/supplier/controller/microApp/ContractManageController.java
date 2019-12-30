package com.mol.supplier.controller.microApp;

import com.mol.fadada.handler.ContractHandler;
import com.mol.fadada.handler.RegistAndAuthHandler;
import com.mol.fadada.handler.SignatureHandler;
import com.mol.fadada.pojo.AuthRecord;
import com.mol.fadada.pojo.RegistRecord;
import com.mol.supplier.config.Constant;
import com.mol.supplier.config.MicroAttr;
import com.mol.supplier.entity.MicroApp.Salesman;
import com.mol.supplier.entity.MicroApp.Supplier;
import com.mol.supplier.mapper.microApp.FadadaAuthRecordMapper;
import com.mol.supplier.service.microApp.MicroContractService;
import com.mol.supplier.service.microApp.MicroUserService;
import entity.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.entity.Example;
import util.IdWorker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 合同管理控制器
 */
@Controller
@RequestMapping("/contract")
public class ContractManageController {

    @Autowired
    private MicroContractService microContractService;

    @Autowired
    private MicroUserService microUserService;

    @Autowired
    private FadadaAuthRecordMapper fadadaAuthRecordMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MicroAttr microAttr;


    @RequestMapping("/checkAuth")
    @ResponseBody
    public ServiceResult checkAuth(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Supplier supplier = microUserService.getSupplierFromSession(session);
        //验证是否已认证，如果没有认证，则提示去认证：
        ServiceResult sr = RegistAndAuthHandler.checkIfRegisted(supplier.getPkSupplier(),"2");
        if(sr.isSuccess()){
            RegistRecord rr = (RegistRecord)sr.getResult();
            String customerId = rr.getCustomerId();
            Example example = new Example(AuthRecord.class);
            example.and().andEqualTo("customerId",customerId);
            AuthRecord authRecord = fadadaAuthRecordMapper.selectOneByExample(example);
            if(authRecord == null || !("4".equals(authRecord.getStatus()))){
                return ServiceResult.failureMsg("请先完成电子合同认证");
            }
            //response.sendRedirect("/contract/index");
        }else {
            return ServiceResult.failureMsg("请先完成电子合同认证");
        }
        return ServiceResult.successMsg("已完成电子合同认证");
    }


    @RequestMapping("/index")
    public String showContractIndex(Model model,HttpSession session){
        Salesman salesman = microUserService.getUserFromSession(session);
        List<Map> dataList = microContractService.getPurchaseAndContractList(salesman.getId());
        model.addAttribute("list",dataList);
        return "contract_index";
    }


    @RequestMapping("/showCheck")
    public String showCheckPage(HttpSession session, Model model, HttpServletResponse response) throws IOException {
        Supplier supplier = microUserService.getSupplierFromSession(session);
        //查询该商户是否注册过：
        ServiceResult serviceResult = RegistAndAuthHandler.checkIfRegisted(supplier.getPkSupplier(), "2");
        String customerId = "";
        if(serviceResult.isSuccess()) {
            RegistRecord rr = (RegistRecord)serviceResult.getResult();
            customerId = rr.getCustomerId();
            Example example = new Example(AuthRecord.class);
            example.and().andEqualTo("customerId",customerId).andEqualTo("authenticationType","2");
            AuthRecord authRecord = fadadaAuthRecordMapper.selectOneByExample(example);
            if(authRecord != null){
                return "redirect:"+authRecord.getUrl();
                //response.sendRedirect(authRecord.getUrl());
            }else{
                return "e_contract_auth";
            }
        }else {
            return "e_contract_auth";
        }
    }

    @RequestMapping("/showPersonCheck")
    public String showPersonCheckPage(HttpSession session, Model model, HttpServletResponse response) throws IOException {
        Salesman salesman = microUserService.getUserFromSession(session);
        //查询该商户是否注册过：
        ServiceResult serviceResult = RegistAndAuthHandler.checkIfRegisted(salesman.getId(), "1");
        String customerId = "";
        if(serviceResult.isSuccess()) {
            RegistRecord rr = (RegistRecord)serviceResult.getResult();
            customerId = rr.getCustomerId();
            Example example = new Example(AuthRecord.class);
            example.and().andEqualTo("customerId",customerId).andEqualTo("authenticationType","1");
            AuthRecord authRecord = fadadaAuthRecordMapper.selectOneByExample(example);
            if(authRecord != null){
                return "redirect:"+authRecord.getUrl();
                //response.sendRedirect(authRecord.getUrl());
            }else{
                return "e_contract_auth_person";
            }
        }else {
            return "e_contract_auth_person";
        }
    }





    /**
     * 供应商业务员签署合同
     * @param contractId
     */
    @RequestMapping("/sign")
    @ResponseBody
    public ServiceResult signContract(@RequestParam String contractId,String purchaseId, HttpSession session) throws InterruptedException {
        Supplier supplier = microUserService.getSupplierFromSession(session);
        String customerId = RegistAndAuthHandler.getCustomerIdByOpenId(supplier.getPkSupplier());
        ServiceResult extsign = ContractHandler.extsign(customerId, idWorker.nextId() + "", contractId, "2", "http://" + microAttr.getDomain() + "/fddCallback/signTo?customerId="+customerId+"&contract_id="+contractId+"&purchaseId="+purchaseId,"http://" + microAttr.getDomain() + "/fddCallback/sign?customerId="+customerId+"&purchaseId="+purchaseId);
        if(extsign.isSuccess()) {
            return ServiceResult.success(extsign.getResult());
        }else {
            return ServiceResult.failureMsg("获取签署地址失败");
        }
    }

    /**
     * 供应商业务员签署合同
     * @param contractId
     */
    @RequestMapping("/signPerson")
    @ResponseBody
    public ServiceResult signContractPerson(@RequestParam String contractId,String purchaseId, HttpSession session) throws InterruptedException {
        Supplier supplier = microUserService.getSupplierFromSession(session);
        Salesman salesman = microUserService.getUserFromSession(session);
        String customerId = RegistAndAuthHandler.getCustomerIdByOpenId(salesman.getId());
        ServiceResult extsign = ContractHandler.extsign(customerId, idWorker.nextId() + "", contractId, "2", "http://" + microAttr.getDomain() + "/fddCallback/signTo?customerId="+customerId+"&contract_id="+contractId+"&purchaseId="+purchaseId,"http://" + microAttr.getDomain() + "/fddCallback/sign?customerId="+customerId+"&purchaseId="+purchaseId);
        if(extsign.isSuccess()) {
            return ServiceResult.success(extsign.getResult());
        }else {
            return ServiceResult.failureMsg("获取签署地址失败");
        }
    }

    /**
     * 显示电子签章管理页面
     * @return
     */
    @RequestMapping("/sealManage")
    public String sealManage(HttpSession session){
        Supplier supplier = microUserService.getSupplierFromSession(session);
        String customerId = RegistAndAuthHandler.getCustomerIdByOpenId(supplier.getPkSupplier());
        //查询是否上传过电子签章：
        ServiceResult sr = SignatureHandler.getSignature(customerId);
        if(sr.isSuccess()){

        }else{

        }

        return "seal_manage";
    }

}

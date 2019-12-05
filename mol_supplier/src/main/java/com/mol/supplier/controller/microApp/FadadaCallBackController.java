package com.mol.supplier.controller.microApp;
import com.mol.fadada.handler.RegistAndAuthHandler;
import com.mol.fadada.pojo.AuthRecord;
import com.mol.fadada.pojo.SignResultRecord;
import com.mol.supplier.entity.MicroApp.PurchaseSupplierContract;
import com.mol.supplier.mapper.microApp.FadadaAuthRecordMapper;
import com.mol.supplier.mapper.microApp.FadadaSignResultRecordMapper;
import com.mol.supplier.mapper.microApp.MicroPurchaseSupplierContractMapper;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;
import util.IdWorker;
import util.TimeUtil;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Log
@Controller
@CrossOrigin
@RequestMapping("/fddCallback")
public class FadadaCallBackController {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private FadadaAuthRecordMapper fadadaAuthRecordMapper;

    @Autowired
    private FadadaSignResultRecordMapper fadadaSignResultRecordMapper;

    @Autowired
    private MicroPurchaseSupplierContractMapper microPurchaseSupplierContractMapper;

    @RequestMapping(value = "/personAuth")
    @ResponseBody
    public void personRegistCallback(@RequestParam Map paraMap){
        log.info("法大大个人认证异步回调事件：，，返回值："+paraMap.toString());
        authCallbackAction(paraMap);
    }


    /**
     * 0：未认证； 1：管理员资料已提 交； 2：企业基本资料(没 有申请表)已提交； 3：已提交待审核；
     * 4：审核通过； 5：审核不通过； 6 人工初审通过，
     * @param paraMap
     */
    @RequestMapping(value = "/orgAuth",method = RequestMethod.POST)
    @ResponseBody
    public void fddCallback(@RequestParam Map paraMap){
        log.info("法大大企业认证异步回调事件：，，返回值："+paraMap.toString());
        authCallbackAction(paraMap);
    }


    public void authCallbackAction(Map paraMap){
        String serialNo = (String)paraMap.get("serialNo");
        String customerId = (String)paraMap.get("customerId");
        String status = (String)paraMap.get("status");
        String statusDesc = "";
        Object obj = paraMap.get("statusDesc");
        if(obj != null) {
            statusDesc = (String) obj;
        }
        Object certStatusObj = paraMap.get("certStatus");
        String certStatus = "";
        if(certStatusObj != null) {
            certStatus =(String) certStatusObj;
        }
        log.info("customerId:"+customerId);
        Example example = new Example(AuthRecord.class);
        example.and().andEqualTo("customerId",customerId).andEqualTo("transactionNo",serialNo);
        AuthRecord authRecord = new AuthRecord();
        authRecord.setStatus(status);
        if(!StringUtils.isEmpty(statusDesc)) {
            authRecord.setStatusDesc(statusDesc);
        }
        if(!StringUtils.isEmpty(certStatus)) {
            authRecord.setCertStatus(certStatus);
        }
        fadadaAuthRecordMapper.updateByExampleSelective(authRecord,example);
    }






    @RequestMapping(value = "/personAuthTo")
    @ResponseBody
    public String personRegistTo(@RequestParam Map paraMap){
        log.info("法大大个人认证同步回调事件,,,"+paraMap.toString());

        String transactionNo = (String)paraMap.get("transactionNo");
        String status = (String)paraMap.get("status");
        String authenticationType = (String)paraMap.get("authenticationType");
        String customerId = (String)paraMap.get("customerId");
        //查询数据库中有没有该单位的认证记录：
        Example example = new Example(AuthRecord.class);
        example.and().andEqualTo("customerId",customerId);
        AuthRecord authRecord = fadadaAuthRecordMapper.selectOneByExample(example);
        AuthRecord authRecordNew = new AuthRecord();
        //如果有记录，则更改状态：
        if(authRecord != null) {
            authRecordNew.setId(authRecord.getId());
            authRecordNew.setStatus(status);
            authRecordNew.setTransactionNo(transactionNo);
            authRecordNew.setLastUpdateTime(TimeUtil.getNowDateTime());
            fadadaAuthRecordMapper.updateByPrimaryKeySelective(authRecordNew);
        }else {
            authRecordNew.setId(idWorker.nextId()+"");
            authRecordNew.setCustomerId(customerId);
            authRecordNew.setTransactionNo(transactionNo);
            authRecordNew.setStatus(status);
            authRecordNew.setAuthenticationType(authenticationType);
            authRecordNew.setCreateTime(TimeUtil.getNowDateTime());
            fadadaAuthRecordMapper.insertSelective(authRecordNew);
        }
        return "/index";
    }





    /**
     * 0：未认证； 1：管理员资料已提 交； 2：企业基本资料(没 有申请表)已提交； 3：已提交待审核； 4：审核通过； 5：审核不通过； 6 人工初审通过，
     * @param paraMap
     */
    @RequestMapping(value = "/orgAuthTo")
    public String fddAuthCallBack(@RequestParam Map paraMap, HttpServletResponse response){
        log.info("法大大企业认证同步回调事件,,,"+paraMap.toString());
        //法大大认证return url,,,{companyName=枣庄星联信息科技有限公司, transactionNo=6300f86ca6e24bfebd15f661d98c8e48, authenticationType=2, status=3, sign=Q0NBRUZBNzlENTFGNkM2QTkwQTAxNEQyRDFEMjE0NkM5RjBFOUZGOA==}
        String transactionNo = (String)paraMap.get("transactionNo");
        String status = (String)paraMap.get("status");
        String authenticationType = (String)paraMap.get("authenticationType");
        String customerId = (String)paraMap.get("customerId");
        //查询数据库中有没有该单位的认证记录：
        Example example = new Example(AuthRecord.class);
        example.and().andEqualTo("customerId",customerId);
        AuthRecord authRecord = fadadaAuthRecordMapper.selectOneByExample(example);
        AuthRecord authRecordNew = new AuthRecord();
        //如果有记录，则更改状态：
        if(authRecord != null) {
            authRecordNew.setId(authRecord.getId());
            authRecordNew.setStatus(status);
            authRecordNew.setTransactionNo(transactionNo);
            authRecordNew.setLastUpdateTime(TimeUtil.getNowDateTime());
            fadadaAuthRecordMapper.updateByPrimaryKeySelective(authRecordNew);
        }else {
            authRecordNew.setId(idWorker.nextId()+"");
            authRecordNew.setCustomerId(customerId);
            authRecordNew.setTransactionNo(transactionNo);
            authRecordNew.setStatus(status);
            authRecordNew.setAuthenticationType(authenticationType);
            authRecordNew.setCreateTime(TimeUtil.getNowDateTime());
            fadadaAuthRecordMapper.insertSelective(authRecordNew);
        }
        return "forward:/microApp/my/show";
    }



    @RequestMapping("/signTo")
    public String signCallBack(@RequestParam Map paraMap){
        log.info("*****法大大手动签署合同同步通知*****");
        this.handlerParaMapToDb(paraMap);
        return "forward:/contract/index";
    }

    @RequestMapping("/sign")
    @ResponseBody
    public void sign(@RequestParam Map paraMap){
        log.info("*****法大大手动签署合同异步通知*****");
        this.handlerParaMapToDb(paraMap);
    }

    public void handlerParaMapToDb(Map paraMap){
        System.out.println(paraMap.toString());
        String resultCode = (String)paraMap.get("result_code");
        String customerId = (String)paraMap.get("customerId");
        String purchaseId= (String)paraMap.get("purchaseId");
        String openId = RegistAndAuthHandler.getOpenIdByCustomerId(customerId);
        String contractId = (String)paraMap.get("contract_id");
        Example example1 = new Example(PurchaseSupplierContract.class);
        example1.and().andEqualTo("purchaseId",purchaseId).andEqualTo("supplierId",openId).andEqualTo("contractId",contractId);
        PurchaseSupplierContract purchaseSupplierContract1 = microPurchaseSupplierContractMapper.selectOneByExample(example1);
        if(purchaseSupplierContract1 != null && PurchaseSupplierContract.合同已归档.equals(purchaseSupplierContract1.getSignStatus())) {
            return ;
        }else if(PurchaseSupplierContract.采购方供应商都已签署.equals(purchaseSupplierContract1.getSignStatus())) {
            //todo:合同归档
        }else {
            if ("3000".equals(resultCode)) {
                //签章成功：contract_id

                String transactionId = (String) paraMap.get("transaction_id");
                Object resultDescObj = paraMap.get("result_desc");
                String resultDesc = "";
                if (resultDescObj != null) {
                    resultDesc = (String) resultDescObj;
                }

                Object downloadUrlObj = paraMap.get("download_url");
                String downloadUrl = "";
                if (downloadUrlObj != null) {
                    downloadUrl = (String) downloadUrlObj;
                }

                Object viewpdfUrlObj = paraMap.get("viewpdf_url");
                String viewpdfUrl = "";
                if (viewpdfUrlObj != null) {
                    viewpdfUrl = (String) viewpdfUrlObj;
                }

                Example example = new Example(SignResultRecord.class);
                example.and().andEqualTo("customerId", customerId).andEqualTo("contractId", contractId);
                SignResultRecord signResultRecordDb = fadadaSignResultRecordMapper.selectOneByExample(example);
                SignResultRecord srr = new SignResultRecord();
                srr.setCustomerId(customerId);
                srr.setTransactionId(transactionId);
                srr.setContractId(contractId);
                srr.setResultCode(resultCode);
                if (!StringUtils.isEmpty(resultDesc)) {
                    srr.setResultDesc(resultDesc);
                }
                if (!StringUtils.isEmpty(downloadUrl)) {
                    srr.setDownloadUrl(downloadUrl);
                }
                if (!StringUtils.isEmpty(viewpdfUrl)) {
                    srr.setViewpdfUrl(viewpdfUrl);
                }

                if (signResultRecordDb == null) {
                    srr.setId(idWorker.nextId() + "");
                    fadadaSignResultRecordMapper.insert(srr);
                } else {
                    srr.setId(signResultRecordDb.getId());
                    fadadaSignResultRecordMapper.updateByPrimaryKeySelective(srr);
                }
                PurchaseSupplierContract purchaseSupplierContract = new PurchaseSupplierContract();
                purchaseSupplierContract.setSignStatus(PurchaseSupplierContract.采购方供应商都已签署);
                microPurchaseSupplierContractMapper.updateByExampleSelective(purchaseSupplierContract, example1);
                //PurchaseSupplierContract purchaseSupplierContract = microPurchaseSupplierContractMapper.selectOneByExample(example1);

            } else {

            }
        }
    }
}

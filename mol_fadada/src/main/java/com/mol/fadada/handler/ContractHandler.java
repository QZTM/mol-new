package com.mol.fadada.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fadada.sdk.client.FddClientBase;
import com.fadada.sdk.client.authForfadada.ApplyCert;
import com.fadada.sdk.client.authForfadada.ApplyNumCert;
import com.fadada.sdk.client.request.ExtsignReq;
import com.mol.fadada.config.FddBaseClient;
import com.mol.fadada.dao.ContractUploadMapper;
import com.mol.fadada.pojo.ContractUploadRecord;
import com.mol.oos.OOSConfig;
import com.mol.oos.TYOOSUtil;
import entity.ServiceResult;
import lombok.Synchronized;
import lombok.extern.java.Log;
import util.IdWorker;
import util.TimeUtil;

import java.io.File;
import java.io.IOException;

/**
 * 法大大合同管理
 */
@Log
public class ContractHandler {

    private static FddClientBase clientBase = FddBaseClient.getFddClientBase();
    private static StringBuffer response = new StringBuffer("==================Welcome ^_^ ==================");


    /**
     * 上传法大大合同
     * @param contractTitle     合同标题
     * @param file              合同文件
     * @return                  ServiceResult(success=true, code=1, message=success, result=20191122110558962)，result为法大大合同编码
     * @throws IOException
     */
    @Synchronized
    public static ServiceResult uploadContract(String contractTitle, File file) throws IOException {
        String contractId = TimeUtil.getNow(TimeUtil.payOrderFormat);
        String resultStr = clientBase.invokeUploadDocs(contractId,contractTitle , file, "", ".pdf");
        log.info(resultStr);
        String result =  JSON.parseObject(resultStr).getString("result");
        ServiceResult sr = null;
        if("success".equals(result))
        {
            //存入数据库
            ContractUploadRecord contractUploadRecord = new ContractUploadRecord();
            contractUploadRecord.setId(new IdWorker().nextId()+"");
            contractUploadRecord.setContractId(contractId);
            contractUploadRecord.setUploadTime(TimeUtil.getNowDateTime());
            ContractUploadMapper contractUploadMapper = RecordDbHandler.getContractUploadMapper();
            System.out.println(contractUploadMapper);
            try{
                int insert = contractUploadMapper.insert(contractUploadRecord);
                //存入oos
                TYOOSUtil.getUtil().uploadObjToBucket(OOSConfig.法大大文件夹,"contract/fadaddaUploadBackup/"+contractId+".pdf",file);
            }catch (Exception e)
            {
                log.warning("法大大合同上传成功，但是数据库记录或者上传入oos发生异常！");
                e.printStackTrace();
            }
            sr = ServiceResult.success(contractId);
        }else{
            sr = ServiceResult.failureMsg("上传失败！");
        }
        log.info(sr.toString());
        return sr;
    }

    /**
     * 自动签署
     * @param Customer_id 客户编号
     * @param Transaction_id 交易号
     * @param Contract_id 合同编号
     * @param Sign_keyword 关键字盖章
     * @param Doc_title 文档标题
     * @return
     */
    public static ServiceResult ExtsignAuto(String Customer_id,String Transaction_id,String Contract_id,String Sign_keyword ,String Doc_title)
    {
        try {
            response.append("\n").append("自动签");
            FddClientBase base = FddBaseClient.getFddClientBase();
            ExtsignReq req = new ExtsignReq();
            req.setCustomer_id("");//客户编号
            req.setTransaction_id(Transaction_id);//交易号
            req.setContract_id(Contract_id);//合同编号
            req.setClient_role("1");//客户角色1 接入平台
            req.setSign_keyword(Sign_keyword);//关键字盖章
            req.setDoc_title(Doc_title);//文档标题
            String result = base.invokeExtSignAuto(req);
            JSONObject code=JSONObject.parseObject(result);

            if (code.get("code").equals("1000"))
            {
                return ServiceResult.success(result);
            }
            else
            {
                return ServiceResult.failure(result);
            }
         /*   response.append("\n").append(result);
            String viewpdf_url = JSON.parseObject(result).getString("viewpdf_url");
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + viewpdf_url);*/
        } catch (Exception e) {
            e.printStackTrace();
            return  ServiceResult.failure(e.toString());
        }
    }


    /**
     * 实名证书
     * @param customer_id 客户编号 注册账号时返回
     * @param verified_serialno  实名认证序列号 获取实名认证 地址时返回
     * @return
     */
    public static  ServiceResult ApplyCert(String customer_id,String verified_serialno)
    {
        try {
            response.append("\n").append("申请实名证书:");
            ApplyCert applyCert = new ApplyCert(FddBaseClient.APP_ID,FddBaseClient.APP_SECRET,FddBaseClient.V,FddBaseClient.HOST);
            String result = applyCert.invokeApplyCert(customer_id,verified_serialno);
            JSONObject code=JSONObject.parseObject(result);
            if (code.equals("1"))
            {
               return ServiceResult.success(result);
            }
            else
            {
                return ServiceResult.failure(result);
            }
        }
        catch (Exception e)
        {
            return ServiceResult.failure(e.toString());
        }
    }

    /**
     * 编号证书
     * @param customer_id  客户编号 注册账号时返回
     * @param verified_serialno 实名认证序列号 获取实名认证 地址时返回
     * @return
     */
    public static ServiceResult ApplyNumCert(String customer_id,String verified_serialno)
    {
        try {
            response.append("\n").append("编号证书申请:");
            ApplyNumCert applyNumCert = new ApplyNumCert(FddBaseClient.APP_ID,FddBaseClient.APP_SECRET,FddBaseClient.V,FddBaseClient.HOST);
            String result = applyNumCert.invokeapplyNumcert(customer_id,verified_serialno);
            JSONObject code=JSONObject.parseObject(result);
            if (code.equals("1"))
            {
                return ServiceResult.success(result);
            }
            else
            {
                return ServiceResult.failure(result);
            }
        }
        catch (Exception e)
        {
            return ServiceResult.failure(e.toString());
        }
    }

    public static void main(String[] args) {
        //上传合同
        File file = new File("c:/法大大.pdf");
        System.out.println(file.exists());
        log.info(""+file.exists());
        if(file.exists()){
            try{
                uploadContract("与大千公司的采购合同",file);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        //上传模板：
    }


}

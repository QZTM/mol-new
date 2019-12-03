package com.mol.fadada.handler;

import com.alibaba.fastjson.JSON;
import com.fadada.sdk.client.FddClientBase;
import com.mol.fadada.config.FddBaseClient;
import com.mol.fadada.pojo.Signature;
import entity.ServiceResult;
import lombok.extern.java.Log;
import org.junit.Test;
import java.io.File;

/**
 * 公章管理接口
 */
@Log
public class SignatureHandler {


    private static FddClientBase clientBase = FddBaseClient.getFddClientBase();


    public static ServiceResult uploadSignature(String customerId, File file){
        log.info("上传电子签章：customerId:"+customerId);
        if(!file.exists()){
            return ServiceResult.failureMsg("文件不存在");
        }
        String result = clientBase.invokeaddSignature(customerId,file,"");
        log.info("上传电子签章,,返回值："+result);//{"code":1,"data":{"signature_id":"4677201","signature_sub_info":null,"status":null},"msg":"success"}

        String msg = JSON.parseObject(result).getString("msg");
        if("success".equals(msg)){
            String data = JSON.parseObject(result).getString("data");
            String signatureId = JSON.parseObject(data).getString("signature_id");
            //Signature
        }

        System.out.println(result);
        return ServiceResult.success("上传成功",result);
    }


    @Test
    public void testuploadsignature(){
        ServiceResult abc = uploadSignature("B5FBFE6B13F18E896569F781991CFD02", new File("/usr/abc.jpeg"));
        System.out.println("abc:"+abc);
    };
}

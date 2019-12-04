package com.mol.fadada.handler;

import com.alibaba.fastjson.JSON;
import com.fadada.sdk.client.FddClientBase;
import com.mol.fadada.config.FddBaseClient;
import com.mol.fadada.dao.SignatureMapper;
import com.mol.fadada.pojo.Signature;
import com.mol.oos.OOSConfig;
import com.mol.oos.TYOOSUtil;
import entity.ServiceResult;
import lombok.extern.java.Log;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import util.IdWorker;
import util.TimeUtil;
import java.io.File;
import java.io.IOException;

/**
 * 公章管理接口
 */
@Log
public class SignatureHandler {


    private static FddClientBase clientBase = FddBaseClient.getFddClientBase();
    private static SignatureMapper signatureMapper = RecordDbHandler.getSignatureMapper();


    /**
     * 上传电子签章
     * @param customerId        法大大id
     * @param file              签章文件
     * @return
     * @throws IOException
     */
    public synchronized static ServiceResult uploadSignature(String customerId, File file) throws IOException {
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

            //先查询：
            EntityHelper.initEntityNameMap(Signature.class, new Config());
            Example example = new Example(Signature.class);
            example.and().andEqualTo("customerId",customerId);
            Signature signatureDb = signatureMapper.selectOneByExample(example);
            String id = "";
            if(signatureDb == null){
                id=new IdWorker().nextId()+"";
                Signature signature = new Signature();
                signature.setId(id);
                signature.setCustomerId(customerId);
                signature.setSignatureId(signatureId);
                signature.setCreateTime(TimeUtil.getNowDateTime());
                signatureMapper.insert(signature);
            }else{
                id=signatureDb.getId();
                signatureDb.setSignatureId(signatureId);
                signatureDb.setLastUpdateTime(TimeUtil.getNowDateTime());
                signatureMapper.updateByPrimaryKeySelective(signatureDb);
            }
            //存入oos
            TYOOSUtil.getUtil().uploadObjToBucket(OOSConfig.法大大文件夹,"contract/signatureBackup/"+id,file);
            return ServiceResult.success("上传成功",result);
        }
        return ServiceResult.failureMsg("上传失败");
    }


    /**
     * 查询上传记录
     * @param customerId
     * @return
     */
    public synchronized static ServiceResult getSignature(String customerId){
        EntityHelper.initEntityNameMap(Signature.class, new Config());
        Example example = new Example(Signature.class);
        example.and().andEqualTo("customerId",customerId);
        Signature signature = signatureMapper.selectOneByExample(example);
        if(signature == null){
            return ServiceResult.failureMsg("没有上传");
        }else{
            return ServiceResult.success("已上传",signature);
        }
    }

}

package com.mol.supplier.controller.microApp;

import com.mol.fadada.pojo.AuthRecord;
import com.mol.supplier.mapper.microApp.FadadaAuthRecordMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;
import util.IdWorker;
import util.TimeUtil;

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

    @RequestMapping(value = "/personAuth")
    @ResponseBody
    public void personRegistCallback(@RequestParam Map paraMap){
        log.info("法大大个人认证回调事件：，，返回值："+paraMap.toString());
        //先根据该customerId查看数据库是否有记录，如果有记录的话则修改字段，如果没有则新建记录
        Example example = new Example(AuthRecord.class);
        example.and().andEqualTo("customerId",paraMap.get("customerId"));
        AuthRecord authRecord = fadadaAuthRecordMapper.selectOneByExample(example);
        System.out.println("数据库中查询的认证记录："+authRecord);
        if(authRecord == null){
            authRecord = new AuthRecord();
            authRecord.setId(idWorker.nextId()+"");
            authRecord.setCustomerId((String)paraMap.get("customerId"));
            authRecord.setTransactionNo((String)paraMap.get("serialNo"));
            authRecord.setStatus((String)paraMap.get("status"));
            authRecord.setStatusDesc((String)paraMap.get("statusDesc"));
            authRecord.setCertStatus((String)paraMap.get("certStatus"));
            fadadaAuthRecordMapper.insert(authRecord);
        }else{
            authRecord = new AuthRecord();
            authRecord.setTransactionNo((String)paraMap.get("serialNo"));
            authRecord.setStatus((String)paraMap.get("status"));
            authRecord.setStatusDesc((String)paraMap.get("statusDesc"));
            authRecord.setCertStatus((String)paraMap.get("certStatus"));
            fadadaAuthRecordMapper.updateByExample(authRecord,example);
        }
    }

    @RequestMapping(value = "/personAuthTo")
    public String personRegistTo(@RequestParam Map paraMap){
        log.info("法大大个人认证return url,,,"+paraMap.toString());
        return "/index";
    }

    @RequestMapping(value = "/orgAuth")
    @ResponseBody
    public void fddCallback(@RequestParam Map paraMap){
        log.info("法大大企业认证回调事件：，，返回值："+paraMap.toString());

    }

    @RequestMapping(value = "/orgAuthTo")
    @ResponseBody
    public void fddAuthCallBack(@RequestParam Map paraMap){
        //法大大认证return url,,,{companyName=枣庄星联信息科技有限公司, transactionNo=6300f86ca6e24bfebd15f661d98c8e48, authenticationType=2, status=3, sign=Q0NBRUZBNzlENTFGNkM2QTkwQTAxNEQyRDFEMjE0NkM5RjBFOUZGOA==}
        String transactionNo = (String)paraMap.get("transactionNo");
        String status = (String)paraMap.get("status");
        String authenticationType = (String)paraMap.get("authenticationType");
        Example example = new Example(AuthRecord.class);
        example.and().andEqualTo("transactionNo",transactionNo);
        AuthRecord authRecord = fadadaAuthRecordMapper.selectOneByExample(example);
        AuthRecord authRecordNew = new AuthRecord();
        //如果有记录，则更改状态
        if(authRecord != null) {
            authRecordNew.setId(authRecord.getId());
            authRecordNew.setStatus(status);
            authRecordNew.setLastUpdateTime(TimeUtil.getNowDateTime());
            fadadaAuthRecordMapper.updateByPrimaryKeySelective(authRecordNew);
        }else {
            authRecordNew.setId(idWorker.nextId()+"");
            authRecordNew.setTransactionNo(transactionNo);
            authRecordNew.setStatus(status);
            authRecordNew.setAuthenticationType(authenticationType);
            authRecordNew.setCreateTime(TimeUtil.getNowDateTime());
            fadadaAuthRecordMapper.insertSelective(authRecordNew);
        }

        log.info("法大大认证return url,,,"+paraMap.toString());
    }



}

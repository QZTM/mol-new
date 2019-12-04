package com.mol.supplier.controller.microApp;

import com.mol.fadada.pojo.AuthRecord;
import com.mol.supplier.config.Constant;
import com.mol.supplier.mapper.microApp.FadadaAuthRecordMapper;
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

    @RequestMapping(value = "/personAuth")
    @ResponseBody
    public void personRegistCallback(@RequestParam Map paraMap){
        log.info("法大大个人认证异步回调事件：，，返回值："+paraMap.toString());
        //先根据该customerId查看数据库是否有记录，如果有记录的话则修改字段，如果没有则新建记录
        Example example = new Example(AuthRecord.class);
        example.and().andEqualTo("customerId",paraMap.get("customerId"));
        AuthRecord authRecord = fadadaAuthRecordMapper.selectOneByExample(example);
        Object statusdescObj = paraMap.get("statusDesc");
        Object certStatusObj = paraMap.get("certStatus");
        System.out.println("数据库中查询的认证记录："+authRecord);
        if(authRecord == null){
            authRecord = new AuthRecord();
            authRecord.setId(idWorker.nextId()+"");
            authRecord.setCustomerId((String)paraMap.get("customerId"));
            authRecord.setTransactionNo((String)paraMap.get("serialNo"));
            authRecord.setStatus((String)paraMap.get("status"));
            if(statusdescObj != null) {
                authRecord.setStatusDesc((String) statusdescObj);
            }
            if(certStatusObj != null) {
                  authRecord.setCertStatus((String) certStatusObj);
            }
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
     * 0：未认证； 1：管理员资料已提 交； 2：企业基本资料(没 有申请表)已提交； 3：已提交待审核；
     * 4：审核通过； 5：审核不通过； 6 人工初审通过，
     * @param paraMap
     */
    @RequestMapping(value = "/orgAuth")
    @ResponseBody
    public void fddCallback(@RequestParam Map paraMap){
        log.info("法大大企业认证异步回调事件：，，返回值："+paraMap.toString());
        String serialNo = (String)paraMap.get("serialNo");
        String customerId = (String)paraMap.get("customerId ");
        String status = (String)paraMap.get("status");
        String statusDesc = "";
        Object obj = paraMap.get("statusDesc");
        if(obj != null) {
            statusDesc = (String) obj;
        }
            Example example = new Example(AuthRecord.class);
            example.and().andEqualTo("customerId",customerId);
            AuthRecord authRecord = new AuthRecord();
            authRecord.setTransactionNo(serialNo);
            authRecord.setStatus(status);
            if(!StringUtils.isEmpty(statusDesc)) {
                authRecord.setStatusDesc(statusDesc);
            }
            fadadaAuthRecordMapper.updateByExampleSelective(authRecord,example);
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


}

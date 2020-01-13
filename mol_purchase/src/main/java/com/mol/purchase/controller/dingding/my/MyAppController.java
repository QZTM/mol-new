package com.mol.purchase.controller.dingding.my;

import com.mol.purchase.entity.dingding.solr.fyPurchase;
import com.mol.purchase.service.dingding.my.MyAppService;
import entity.ServiceResult;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/myApp")
@Log
public class MyAppController {

    @Autowired
    MyAppService myAppService;

    /**
     * app 我的页面，查询进行和已完成的订单
     * @param orgId
     * @param userId
     * @param status
     * @param status_second
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getList")
    public ServiceResult getList(String orgId,String userId,String status,String status_second,int pageNum,int pageSize){
        log.info("访问E引用查询个人页面的订单，参数： orgid:"+orgId+",userId:"+userId+",第一个status:"+status+",第二个status:"+status_second+",页码："+pageNum);
        if (orgId==null || userId==null || status==null || status_second==null){
            log.info("参数非法，查询失败！");
            return ServiceResult.failureMsg("查询失败");
        }
        List<fyPurchase> purList=myAppService.findPurListByOrgIdAndUserIdAndBetweenStatusAndStatusScond(orgId,userId,status,status_second,pageNum,pageSize);
        log.info("查询到的list的长度："+purList.size());
        return ServiceResult.success(purList);
    }
}

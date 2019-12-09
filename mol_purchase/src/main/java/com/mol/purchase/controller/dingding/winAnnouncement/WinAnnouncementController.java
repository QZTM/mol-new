package com.mol.purchase.controller.dingding.winAnnouncement;

import com.mol.purchase.entity.dingding.solr.fyPurchase;
import com.mol.purchase.service.dingding.winningannouncement.WinnAnnouncementService;
import entity.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName:TobeNegotiatedController
 * Package:com.purchase.controller.WinAnnouncement.WinAnnouncementController
 * Description
 * E应用中标公告Controller
 * @date:2019/8/12 13:19
 * @author:yangjiangyan
 */
@RequestMapping("/winAnnouncement")
@RestController
public class WinAnnouncementController {

    private static final Logger logger = LoggerFactory.getLogger(WinAnnouncementController.class);

    @Autowired
    private WinnAnnouncementService winnAnnouncementService;

    @GetMapping("/getWinAnnouncementList")
    public ServiceResult getWinAnnouncementList(String orgId,int pageNum,int pageSize){
        if (orgId==null){
            return ServiceResult.successMsg("查询失败！");
        }
        List<fyPurchase> list=winnAnnouncementService.getWinAnnounceList(orgId,pageNum,pageSize);
        logger.info("E应用 中标公告 查询中标list："+list);
        list=winnAnnouncementService.findPassSupplierCountOfPassPur(list);
        logger.info("E应用 中标公告 查询中标list的中标公司数："+list);
//        count=winnAnnouncementService.findPassCountByStatus(orgId);
//        logger.info("E应用 中标公告 查询订单数量："+list);
        return ServiceResult.success(list);
    }
}

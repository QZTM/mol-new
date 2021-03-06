package com.mol.ddmanage.Service.PurchasOrderManagement;

import com.mol.ddmanage.Ben.Permission.Dataviewingpermissionsben;
import com.mol.ddmanage.Ben.PurchasOrderManagement.PurchasOrderListben;
import com.mol.ddmanage.Service.Permission.VerificationPermissionService;
import com.mol.ddmanage.mapper.PurchasOrderManagement.PurchasOrderListMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Service
public class PurchasOrderListService
{
    @Resource
    PurchasOrderListMapper purchasOrderListMapper;
    @Resource
    VerificationPermissionService verificationPermissionService;
    public ArrayList<PurchasOrderListben>  PurchasOrderListShow(String buy_channel_id, HttpServletRequest httpServletRequest)
    {
        Dataviewingpermissionsben dataviewingpermissionsben = verificationPermissionService.DataviewingpermissionsLogic("purchase",httpServletRequest);//拿到查看数据的权限
        ArrayList<PurchasOrderListben>purchasOrderListbens=purchasOrderListMapper.PurchasOrderListShow(dataviewingpermissionsben.getAuthorityStatus(),dataviewingpermissionsben.getApp_userid(),buy_channel_id);
        for (int n=0;n<purchasOrderListbens.size();n++)
        {
            purchasOrderListbens.get(n).setNumber(String.valueOf(n));//添加编号

            if (purchasOrderListbens.get(n).getBuy_channel_id()!=null && purchasOrderListbens.get(n).getBuy_channel_id().equals("1"))
            {
                purchasOrderListbens.get(n).setBuy_channel_id("线下采购");
            }
            else if (purchasOrderListbens.get(n).getBuy_channel_id()!=null && purchasOrderListbens.get(n).getBuy_channel_id().equals("2"))
            {
                purchasOrderListbens.get(n).setBuy_channel_id("线上采购");
            }
            else if (purchasOrderListbens.get(n).getBuy_channel_id()!=null && purchasOrderListbens.get(n).getBuy_channel_id().equals("3"))
            {
                purchasOrderListbens.get(n).setBuy_channel_id("战略采购");
            }
            else if (purchasOrderListbens.get(n).getBuy_channel_id()!=null && purchasOrderListbens.get(n).getBuy_channel_id().equals("4"))
            {
                purchasOrderListbens.get(n).setBuy_channel_id("询价采购");
            }
            else if (purchasOrderListbens.get(n).getBuy_channel_id()!=null && purchasOrderListbens.get(n).getBuy_channel_id().equals("5"))
            {
                purchasOrderListbens.get(n).setBuy_channel_id("单一采购");
            }
            else if (purchasOrderListbens.get(n).getBuy_channel_id()!=null && purchasOrderListbens.get(n).getBuy_channel_id().equals("6"))
            {
                purchasOrderListbens.get(n).setBuy_channel_id("加工 维修");
            }
            else
            {
                purchasOrderListbens.get(n).setBuy_channel_id("暂无");
            }

        }
        return  purchasOrderListbens;
    }
}

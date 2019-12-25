package com.mol.ddmanage.Service;

import com.mol.ddmanage.client.purchase.PurchaseClient;
import com.mol.ddmanage.mapper.StartInspectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 启动检查
 * @author wjh
 */
@Service
public class StartInspectionService
{
    @Resource
    StartInspectionMapper startInspectionMapper;

    @Autowired
    private PurchaseClient purchaseClient;

    private static Map<Integer,String> buyChannelMap = new HashMap<Integer,String>();
    static {
        buyChannelMap.put(1,"线下采购");
        buyChannelMap.put(2,"线上采购");
        buyChannelMap.put(3,"战略采购");
        buyChannelMap.put(4,"询价采购");
        buyChannelMap.put(5,"单一来源");
        buyChannelMap.put(6,"在线招标");
        buyChannelMap.put(7,"生产原料");
        buyChannelMap.put(8,"零配件");
    }


    /**
     * 检测fy_buy_channel表是否数据完整，不完整的话自动补全
     */
    public  void Inspection_fy_buy_channel()
    {
        try {
            ArrayList<Map> fy_buy_channel=startInspectionMapper.Get_fy_buy_channel();
            for (int n=1;n<=8;n++)
            {
                //表不为空时
                if (fy_buy_channel.size()!=0)
                {
                    for (int n_1=0;n_1<fy_buy_channel.size();n_1++)
                    {
                        //如果表中有这条数据结束内部循环
                        if (fy_buy_channel.get(n_1).get("id").toString().equals(String.valueOf(n)))
                        {
                            break;
                        }
                        //最后一次循环依然没有在表中找到要查找的数据，此时需要插入一条数据
                        else if (n_1+1==fy_buy_channel.size())
                        {
                            startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),buyChannelMap.get(n),"1");
                        }
                    }
                }
                else //表为空时
                {
                    startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),buyChannelMap.get(n),"1");
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("检测fy_buy_channel表出问题："+e);
        }
    }

    /**
     * 检测app_purchase_approve表是否数据完整，不完整的话自动补全
     */
    public void Inspection_app_purchase_approve()
    {
        try
        {
           ArrayList<Map> app_purchase_approve=startInspectionMapper.Get_app_purchase_approve();
            for (int n=1;n<=8;n++)
            {
                //表不为空时
                if (app_purchase_approve.size()!=0)
                {
                    for (int n_1=0;n_1<app_purchase_approve.size();n_1++)
                    {
                        //如果表中有这条数据结束内部循环
                        if (app_purchase_approve.get(n_1).get("id").toString().equals(String.valueOf(n)))
                        {
                            break;
                        }
                        //最后一次循环依然没有在表中找到要查找的数据，此时需要插入一条数据
                        else if (n_1+1==app_purchase_approve.size())
                        {
                                startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                        }
                    }
                }
                //表为空时
                else
                {
                        startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                }

            }
        }
        catch (Exception e)
        {
            System.out.println("检测app_purchase_approve表出问题："+e);
        }
    }

    public void Inspection_app_org_buy_channel_approve_middle()
    {
        try
        {
            ArrayList<Map> app_org_buy_channel_approve_middle=startInspectionMapper.Get_app_org_buy_channel_approve_middle();
            for (int n=1;n<=8;n++)
            {
                //获取公司表
                ArrayList<Map> app_auth_org=startInspectionMapper.Get_app_auth_org();
                //表不为空时
                if (app_org_buy_channel_approve_middle.size()!=0)
                {
                    for (int n_1=0;n_1<app_org_buy_channel_approve_middle.size();n_1++)
                    {
                        //如果表中有这条数据结束内部循环
                        if (app_org_buy_channel_approve_middle.get(n_1).get("id").toString().equals(String.valueOf(n)))
                        {
                            break;
                        }
                        //最后一次循环依然没有在表中找到要查找的数据，此时需要插入一条数据
                        else if (n_1+1==app_org_buy_channel_approve_middle.size())
                        {

                            startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                            purchaseClient.deploy("n"+String.valueOf(n),"process"+String.valueOf(n),"processName"+String.valueOf(n),"1202851016954982400",String.valueOf(n));
                        }
                    }
                }
                //表为空时
                else
                {
                    startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                    purchaseClient.deploy("n"+String.valueOf(n),"process"+String.valueOf(n),"processName"+String.valueOf(n),"1202851016954982400",String.valueOf(n));
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("检测app_org_buy_channel_approve_middle表出问题："+e);
        }
    }
}

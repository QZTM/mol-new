package com.mol.ddmanage.Service;

import com.mol.ddmanage.Util.HttpCommunication;
import com.mol.ddmanage.mapper.StartInspectionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

@Service
public class StartInspectionService //启动检查
{
    @Resource
    StartInspectionMapper startInspectionMapper;

    /**
     * 检测fy_buy_channel表是否数据完整，不完整的话自动补全
     */
    public  void Inspection_fy_buy_channel()
    {
        try {
            ArrayList<Map> fy_buy_channel=startInspectionMapper.Get_fy_buy_channel();
            for (int n=1;n<=8;n++)
            {
                if (fy_buy_channel.size()!=0)//表不为空时
                {
                    for (int n_1=0;n_1<fy_buy_channel.size();n_1++)
                    {
                        if (fy_buy_channel.get(n_1).get("id").toString().equals(String.valueOf(n)))//如果表中有这条数据结束内部循环
                        {
                            break;
                        }
                        else if (n_1+1==fy_buy_channel.size())//最后一次循环依然没有在表中找到要查找的数据，此时需要插入一条数据
                        {
                            if (n==1)
                            {
                                startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"线下采购","1");
                            }
                            else if (n==2)
                            {
                                startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"线上采购","1");
                            }
                            else if (n==3)
                            {
                                startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"战略采购","1");
                            }
                            else if (n==4)
                            {
                                startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"询价采购","1");
                            }
                            else if (n==5)
                            {
                                startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"单一来源","1");
                            }
                            else if (n==6)
                            {
                                startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"在线招标","1");
                            }
                            else if (n==7)
                            {
                                startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"生产原料","1");
                            }
                            else if (n==8)
                            {
                                startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"零配件","1");
                            }
                        }
                    }
                }
                else //表为空时
                {
                    if (n==1)
                    {
                        startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"线下采购","1");
                    }
                    else if (n==2)
                    {
                        startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"线上采购","1");
                    }
                    else if (n==3)
                    {
                        startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"战略采购","1");
                    }
                    else if (n==4)
                    {
                        startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"询价采购","1");
                    }
                    else if (n==5)
                    {
                        startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"单一来源","1");
                    }
                    else if (n==6)
                    {
                        startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"在线招标","1");
                    }
                    else if (n==7)
                    {
                        startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"生产原料","1");
                    }
                    else if (n==8)
                    {
                        startInspectionMapper.insert_fy_buy_channel_row(String.valueOf(n),"零配件","1");
                    }
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
                if (app_purchase_approve.size()!=0)//表不为空时
                {
                    for (int n_1=0;n_1<app_purchase_approve.size();n_1++)
                    {
                        if (app_purchase_approve.get(n_1).get("id").toString().equals(String.valueOf(n)))//如果表中有这条数据结束内部循环
                        {
                            break;
                        }
                        else if (n_1+1==app_purchase_approve.size())//最后一次循环依然没有在表中找到要查找的数据，此时需要插入一条数据
                        {
                            if (n==1)
                            {
                                startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                            }
                            else if (n==2)
                            {
                                startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                            }
                            else if (n==3)
                            {
                                startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                            }
                            else if (n==4)
                            {
                                startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                            }
                            else if (n==5)
                            {
                                startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                            }
                            else if (n==6)
                            {
                                startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                            }
                            else if (n==7)
                            {
                                startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                            }
                            else if (n==8)
                            {
                                startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                            }
                        }
                    }
                }
                else //表为空时
                {
                    if (n==1)
                    {
                        startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                    }
                    else if (n==2)
                    {
                        startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                    }
                    else if (n==3)
                    {
                        startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                    }
                    else if (n==4)
                    {
                        startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                    }
                    else if (n==5)
                    {
                        startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                    }
                    else if (n==6)
                    {
                        startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                    }
                    else if (n==7)
                    {
                        startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                    }
                    else if (n==8)
                    {
                        startInspectionMapper.insert_app_purchase_approve(String.valueOf(n),"","","");
                    }
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
                ArrayList<Map> app_auth_org=startInspectionMapper.Get_app_auth_org();//获取公司表
                if (app_org_buy_channel_approve_middle.size()!=0)//表不为空时
                {
                    for (int n_1=0;n_1<app_org_buy_channel_approve_middle.size();n_1++)
                    {
                        if (app_org_buy_channel_approve_middle.get(n_1).get("id").toString().equals(String.valueOf(n)))//如果表中有这条数据结束内部循环
                        {
                            break;
                        }
                        else if (n_1+1==app_org_buy_channel_approve_middle.size())//最后一次循环依然没有在表中找到要查找的数据，此时需要插入一条数据
                        {
                            if (n==1)
                            {
                                 startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                                HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));

                            }
                            else if (n==2)
                            {
                                startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                                HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                            }
                            else if (n==3)
                            {
                                startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                                HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                            }
                            else if (n==4)
                            {
                                startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                                HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                            }
                            else if (n==5)
                            {
                                startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                                HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                            }
                            else if (n==6)
                            {
                                startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                                HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                            }
                            else if (n==7)
                            {
                                startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                                HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                            }
                            else if (n==8)
                            {
                                startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                                HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                            }
                        }
                    }
                }
                else//表为空时
                {
                    if (n==1)
                    {
                        startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                       String pp= HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                       System.out.println(pp);
                    }
                    else if (n==2)
                    {
                        startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                        String pp=  HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                        System.out.println(pp);
                    }
                    else if (n==3)
                    {
                        startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                        String pp=  HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                        System.out.println(pp);
                    }
                    else if (n==4)
                    {
                        startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                        String pp= HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                        System.out.println(pp);
                    }
                    else if (n==5)
                    {
                        startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                        String pp= HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                        System.out.println(pp);
                    }
                    else if (n==6)
                    {
                        startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                        String pp= HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                        System.out.println(pp);
                    }
                    else if (n==7)
                    {
                        startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                        String pp= HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                        System.out.println(pp);
                    }
                    else if (n==8)
                    {
                        startInspectionMapper.insert_app_org_buy_channel_approve_middle(String.valueOf(n),app_auth_org.get(0).get("id").toString(),String.valueOf(n),String.valueOf(n));
                        String pp=  HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(n)+"&processId=process"+String.valueOf(n)+"&processName=processName"+String.valueOf(n)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(n));
                        System.out.println(pp);
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("检测app_org_buy_channel_approve_middle表出问题："+e);
        }
    }
}

package com.mol.ddmanage.Service.Tools;

import com.mol.ddmanage.Ben.Tools.ApprovalProcessSettingListben;
import com.mol.ddmanage.mapper.Tools.ApprovalProcessinforMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApprovalProcessinforService
{
    @Resource
    ApprovalProcessinforMapper approvalProcessinforMapper;
    public Map GetApprovalInforLogic(String id)
    {
       ApprovalProcessSettingListben approvalProcessSettingListben=approvalProcessinforMapper.GetApprovalInfor(id);//获取审批流数据表
       String []ids=approvalProcessSettingListben.getApproval().split(",");//分割人员id

        ArrayList user_name=new ArrayList();//存审批人员姓名
        ArrayList user_id=new ArrayList();//存审批人员id
       Map map=new HashMap();

       map.put("status",approvalProcessSettingListben.getStatus());

       for (int n=0;n<ids.length;n++)
       {
           String dd_user_id=approvalProcessinforMapper.Get_app_user_id(ids[n]).get("dd_user_id").toString();//获取钉钉id
         user_id.add(dd_user_id);
         Map map1=approvalProcessinforMapper.Get_app_user_id(ids[n]);//更加人员id获取人员信息表
         if (map1!=null)
         {
             user_name.add(map1.get("user_name").toString());
         }
       }
       map.put("user_ids",user_id);
       map.put("user_names",user_name);
       return map;
    }

    public Map SubmitApprovalDataLogic(String id, String amountMin,String amountMax,String status,String approval_ids)
    {
        Map map=new HashMap();
        try
        {
            String []dingding_ids=approval_ids.split(",");
            String user_ids="";
            for (int n=0;n<dingding_ids.length;n++)
            {
                if (n==0)
                {
                    user_ids=approvalProcessinforMapper.Get_app_user(dingding_ids[n]).get("id").toString();
                }
                else if (n>0)
                {
                    user_ids=user_ids+","+approvalProcessinforMapper.Get_app_user(dingding_ids[n]).get("id").toString();
                }
            }
            String purchase_approve_id=approvalProcessinforMapper.Get_app_org_buy_channel_approve_middle(id).get("purchase_approve_id").toString();
            String buy_channel_id=approvalProcessinforMapper.Get_app_org_buy_channel_approve_middle(id).get("buy_channel_id").toString();
            approvalProcessinforMapper.Updata_app_purchase_approve(purchase_approve_id,user_ids);//更新审批人列表
            approvalProcessinforMapper.updata_fy_buy_channel(buy_channel_id,status);//更新审批状态表


            URL url = new URL("http://140.249.22.202:8082/ac/deploy?name=1111&processId=testname&processName=test1&orgId=1202851016954982400&buyChannelId="+buy_channel_id);//实例化一个URL对象，用百度有道翻译进行了测试/hello
            URLConnection connection = url.openConnection();//通过URL对象的openConnection()方法得到一个java.net.URLConnection;
            InputStream is = connection.getInputStream();  // 获取输入流
            InputStreamReader isr = new InputStreamReader(is,"utf-8");//在InputStreamReader中指定编码，手动指定为UTF-8
            BufferedReader br = new BufferedReader(isr);//实例化一个BufferedReader对象用来存放转换后的字符
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {  // 读取数据
                builder.append(line+"\n");
            }
            br.close();//关闭流
            isr.close();
            is.close();
            System.out.println(builder.toString());

            map.put("statu",true);
            return map;

        }
        catch (Exception e)
        {
            map.put("statu",false);
            return map;
        }
    }
}

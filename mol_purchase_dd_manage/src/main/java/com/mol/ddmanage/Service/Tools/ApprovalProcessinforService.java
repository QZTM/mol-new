package com.mol.ddmanage.Service.Tools;

import com.mol.ddmanage.Util.HttpCommunication;
import com.mol.ddmanage.client.purchase.PurchaseClient;
import com.mol.ddmanage.mapper.Tools.ApprovalProcessinforMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApprovalProcessinforService
{

    @Autowired
    private PurchaseClient purchaseClient;

    @Resource
    ApprovalProcessinforMapper approvalProcessinforMapper;
    public Map GetApprovalInforLogic(String id)
    {
       Map approvalProcessSettingListben=approvalProcessinforMapper.Get_app_org_buy_channel_approve_middle(id);//获取审批中间表表
        Map Approval=approvalProcessinforMapper.Get_app_purchase_approve(approvalProcessSettingListben.get("purchase_approve_id").toString());//获取审批数据
       String []ids=Approval.get("purchase_approve_list").toString().split(",");//分割人员id

        ArrayList user_name=new ArrayList();//存审批人员姓名
        ArrayList user_id=new ArrayList();//存审批人员id
       Map map=new HashMap();

       map.put("status","1");

       if (!ids[0].equals(""))
       {
           for (int n=0;n<ids.length;n++)
           {
               Map dd_user_id=approvalProcessinforMapper.Get_app_user_id(ids[n]);//获取钉钉id
               user_id.add(dd_user_id.get("dd_user_id").toString());
               Map map1=approvalProcessinforMapper.Get_app_user_id(ids[n]);//更加人员id获取人员信息表
               if (map1!=null)
               {
                   user_name.add(map1.get("user_name").toString());
               }
           }
       }

       map.put("user_ids",user_id);
       map.put("user_names",user_name);
        String purchase_main_person_dd_user_id="";
        String purchase_main_person_user_name="";
        if (Approval.get("purchase_main_person")!=null && !Approval.get("purchase_main_person").equals(""))
        {
            String purchase_main_person=Approval.get("purchase_main_person").toString();//获取本地人员id
            Map Get_app_user=approvalProcessinforMapper.Get_app_user_id(purchase_main_person);//获取人员表
            purchase_main_person_dd_user_id=Get_app_user.get("dd_user_id").toString();//获取钉钉id
            purchase_main_person_user_name=Get_app_user.get("user_name").toString();//获取人员姓名
        }
        map.put("purchase_main_person_dd_user_id",purchase_main_person_dd_user_id);
        map.put("purchase_main_person_user_name",purchase_main_person_user_name);
       return map;
    }

    public Map SubmitApprovalDataLogic(String id, String amountMin,String amountMax,String status,String approval_ids,String Unlock_ids_str)
    {
        Map map=new HashMap();
        try
        {
            String []dingding_ids=approval_ids.split(",");
            String user_ids="";
            for (int n=0;n<dingding_ids.length;n++)
            {
                if (dingding_ids[0].equals(""))
                {
                    break;
                }
                if (n==0)
                {
                    user_ids=approvalProcessinforMapper.Get_app_user(dingding_ids[n]).get("id").toString();
                }
                else if (n>0)
                {
                    user_ids=user_ids+","+approvalProcessinforMapper.Get_app_user(dingding_ids[n]).get("id").toString();
                }
            }
            String app_user_id="";
            if (!Unlock_ids_str.equals(""))
            {
                 app_user_id=approvalProcessinforMapper.Get_app_user(Unlock_ids_str).get("id").toString();
            }

            String purchase_approve_id=approvalProcessinforMapper.Get_app_org_buy_channel_approve_middle(id).get("purchase_approve_id").toString();
            String buy_channel_id=approvalProcessinforMapper.Get_app_org_buy_channel_approve_middle(id).get("buy_channel_id").toString();
            approvalProcessinforMapper.Updata_app_purchase_approve(purchase_approve_id,user_ids,app_user_id);//更新审批人列表
            approvalProcessinforMapper.updata_fy_buy_channel(buy_channel_id,status);//更新审批状态表

           // HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=1111&processId=testname&processName=test1&orgId=1202851016954982400&buyChannelId="+buy_channel_id);
          //String nnn=  HttpCommunication.HttpGet("http://140.249.22.202:8082/ac/deploy?name=n"+String.valueOf(id)+"&processId=process"+ new  IdWorker().nextId() +"&processName=processName"+String.valueOf(id)+"&orgId=1202851016954982400&buyChannelId="+String.valueOf(id));
            String nnn= purchaseClient.deploy("n"+String.valueOf(id),new  IdWorker().nextId()+"","processName"+String.valueOf(id),"1202851016954982400",String.valueOf(id));
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

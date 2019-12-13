package com.mol.ddmanage.Service.DepartmentManagement;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mol.ddmanage.Ben.DepartmentManagement.AddJurisdictionben;
import com.mol.ddmanage.Util.DataUtil;
import com.mol.ddmanage.Util.Dingding_Tools;
import com.mol.ddmanage.mapper.DepartmentMangement.AddJurisdictionMapper;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
public class AddJurisdictionService
{
    @Resource
    AddJurisdictionMapper addJurisdictionMapper;
    public boolean AddJurisdictionLogic(AddJurisdictionben addJurisdictionben,HttpSession httpSession)
    {
        try
        {
            String userid= httpSession.getAttribute("userid").toString();//Token_hand.Review_My_Token(httpSession.getAttribute("token").toString());//获取token里的userid
            addJurisdictionben.setJurisdictionId(DataUtil.GetTimestamp());//id
            addJurisdictionben.setCreatTime(DataUtil.GetNowSytemTime());//获取系统时间
            addJurisdictionben.setCreadStaff(userid);//钉钉人员id
            addJurisdictionMapper.AddJurisdictionMapper(addJurisdictionben);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public String ViewRoles()//查看角色是否有超级管理员
    {
        try {
            Map addJurisdiction=addJurisdictionMapper.select_Super_administrator("超级管理员");
            if (addJurisdiction==null)//如果没有超级管理员，需要创建超级管理员，并且老板为超级管理员的用户
            {
                String dingding_userid="";
               String Getadmin= Dingding_Tools.Get_admin();
                JSONObject jsonObjectGetadmin=JSONObject.parseObject(Getadmin);
                String adminlist=jsonObjectGetadmin.getString("adminList");
                JSONArray jsonArray=JSONObject.parseArray(adminlist);
                for (int n=0 ;n<jsonArray.size();n++)
                {
                    String item= jsonArray.get(n).toString();
                    JSONObject items=JSONObject.parseObject(item);
                    if (items.getString("sys_level").equals("1"))
                    {
                        dingding_userid=items.getString("userid");
                        break;
                    }
                }
               String jurisdictionId=String.valueOf(new IdWorker().nextId());
               addJurisdictionMapper.Insert_admin(jurisdictionId,"超级管理员",DataUtil.GetNowSytemTime(),dingding_userid);//插入一条权限

                addJurisdictionMapper.insert_bac_user_position(String.valueOf(new IdWorker().nextId()),dingding_userid,jurisdictionId);
                return "超级管理员权限注册完毕";
            }
           return "";
        }
        catch (Exception e)
        {
          return "超级管理员注册失败"+e.toString();
        }

    }
}

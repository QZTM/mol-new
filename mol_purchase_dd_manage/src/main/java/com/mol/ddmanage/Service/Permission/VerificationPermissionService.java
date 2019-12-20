package com.mol.ddmanage.Service.Permission;

import com.mol.ddmanage.Ben.Permission.Dataviewingpermissionsben;
import com.mol.ddmanage.mapper.Permission.VerificationPermissionMapper;
import com.mol.ddmanage.mapper.Tools.ApprovalProcessinforMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Map;

@Service
public class VerificationPermissionService//验证登录人权限
{
    @Resource
    VerificationPermissionMapper verificationPermissionMapper;

    @Resource
    ApprovalProcessinforMapper approvalProcessinforMapper;
    public boolean VerificationPermissionLogic(String userid ,String PageName)//验证人员是否有权限访问这个页面
    {
      ArrayList<Map> verifications= verificationPermissionMapper.VerificationPermission(userid);//获取这个用户自身绑定角色的集合
      for (int n=0;n<verifications.size();n++)
      {
          if (verifications.get(n).get(PageName).toString().equals("1"))//集合里有任一一个身份可以访问这个页面就通过
          {
              return true;
          }
      }
      return false;//改用户绑定的所有角色都没有权限访问给定的页面
    }

    /**
     * 查看角色对页面数据的访问权限
     * @param PageName 页面的名称
     * @param httpServletRequest
     * @return 获取dingdingid app_userid 和AuthorityStatus =0代表所有数据可见 AuthorityStatus=1只能可见个人创建的数据
     */
    public Dataviewingpermissionsben DataviewingpermissionsLogic( String PageName , HttpServletRequest httpServletRequest)
    {
        HttpSession httpSession= httpServletRequest.getSession();
        String dingdinguserid=httpSession.getAttribute("userid").toString();//获取钉钉id
        Map app_user=approvalProcessinforMapper.Get_app_user(dingdinguserid);//获取app_user的id
        String userid="123456";
        if (app_user!=null)//如果已经注册，代表app_user里有本人数据
        {
            userid=app_user.get("id").toString();
        }

        ArrayList<Map> verifications= verificationPermissionMapper.Dataviewingpermissions(dingdinguserid);//获取这个用户自身绑定角色的集合
        String status="";
        for (int n=0;n<verifications.size();n++)
        {
            if (verifications.get(n).get(PageName).toString().equals("0"))//集合里有任一一个身份可以访问全部数据
            {
                status= "0";
                break;
            }
            else if (verifications.get(n).get(PageName).toString().equals("1"))
            {
                status="1";
            }
        }
        Dataviewingpermissionsben dataviewingpermissionsben=new Dataviewingpermissionsben();
        dataviewingpermissionsben.setDingding_id(dingdinguserid);
        dataviewingpermissionsben.setApp_userid(userid);
        dataviewingpermissionsben.setAuthorityStatus(status);
        return dataviewingpermissionsben;
    }
}

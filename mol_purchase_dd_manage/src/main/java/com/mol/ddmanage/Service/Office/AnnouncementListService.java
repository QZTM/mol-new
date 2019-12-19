package com.mol.ddmanage.Service.Office;

import com.mol.ddmanage.Ben.Office.AnnouncementEditPageben;
import com.mol.ddmanage.Ben.Permission.Dataviewingpermissionsben;
import com.mol.ddmanage.Service.Permission.VerificationPermissionService;
import com.mol.ddmanage.mapper.Office.AnnouncementListMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnnouncementListService
{
    @Resource
    VerificationPermissionService verificationPermissionService;
    @Resource
    AnnouncementListMapper announcementListMapper;

    public ArrayList<AnnouncementEditPageben> AnnouncementList(HttpServletRequest httpServletRequest)
    {

       Dataviewingpermissionsben dataviewingpermissionsben =verificationPermissionService.DataviewingpermissionsLogic("notice",httpServletRequest);
       ArrayList<AnnouncementEditPageben> announcementEditPageben=announcementListMapper.AnnouncementMessage(dataviewingpermissionsben.getAuthorityStatus(),dataviewingpermissionsben.getDingding_id());
       for (int n=0;n<announcementEditPageben.size();n++)
       {
           announcementEditPageben.get(n).setNumber(String.valueOf(n));
           announcementEditPageben.get(n).setMessageType("消息通知");
       }
      return announcementEditPageben;
    }

    public Map DeleteAnnouncementListLogic(String announcemenId,String messageType)
    {
        Map map=new HashMap();
        try
        {
            announcementListMapper.DeleteAnnouncementList(announcemenId,messageType);
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

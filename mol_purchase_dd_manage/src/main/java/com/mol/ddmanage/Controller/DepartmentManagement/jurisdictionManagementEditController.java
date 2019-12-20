package com.mol.ddmanage.Controller.DepartmentManagement;

import com.mol.ddmanage.Ben.DepartmentManagement.AddJurisdictionben;
import com.mol.ddmanage.Service.DepartmentManagement.JurisdictionManagementEditService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/jurisdictionManagementEditController")
public class jurisdictionManagementEditController
{
    @Resource
    JurisdictionManagementEditService jurisdictionManagementEditService;
    @RequestMapping("/GetJurisdictionPosition")//获取角色原有权限
    public AddJurisdictionben GetJurisdictionPosition(@RequestParam String jurisdictionId)
    {
       return jurisdictionManagementEditService.GetJurisdictionPositionLogic(jurisdictionId);
    }
    @RequestMapping("/UpdateJurisdiction")//更新角色权限
    public Map UpdateJurisdiction(@RequestBody AddJurisdictionben json)
    {
       return jurisdictionManagementEditService.UpdateJurisdictionLogic(json);
    }

    @RequestMapping("/Updatedataviewingpermissions")//更新角色查看数据的权限
    public Map Updatedataviewingpermissions(@RequestBody AddJurisdictionben json)
    {
        return jurisdictionManagementEditService.UpdateDataviewingpermissionsLogic(json);
    }

    @RequestMapping("/GetDataviewingpermissions")
    public AddJurisdictionben GetDataviewingpermissions(@RequestParam String jurisdictionId)//获取角色的查看数据权限
    {
        return jurisdictionManagementEditService.GetDataviewingpermissionsLogic(jurisdictionId);
    }

}

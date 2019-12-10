package com.mol.ddmanage.Controller.Tools;

import com.mol.ddmanage.Ben.Tools.ApprovalProcessSettingListben;
import com.mol.ddmanage.Service.Tools.ApprovalProcessSettingListService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;

@RestController
@RequestMapping("/ApprovalProcessSettingListContraller")
public class ApprovalProcessSettingListContraller
{
    @Resource
    ApprovalProcessSettingListService approvalProcessSettingListService;
    @RequestMapping("/SetapprovalList")//获取审批流程
    public ArrayList<ApprovalProcessSettingListben> SetapprovalList()
    {
      return approvalProcessSettingListService.SetapprovalListLogic();
    }
}

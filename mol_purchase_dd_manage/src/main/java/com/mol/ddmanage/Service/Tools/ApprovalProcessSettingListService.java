package com.mol.ddmanage.Service.Tools;

import com.mol.ddmanage.Ben.Tools.ApprovalProcessSettingListben;
import com.mol.ddmanage.mapper.Tools.ApprovalProcessSettingListMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

@Service
public class ApprovalProcessSettingListService
{
    @Resource
    ApprovalProcessSettingListMapper approvalProcessSettingListMapper;
    public ArrayList<ApprovalProcessSettingListben>SetapprovalListLogic()
    {
        ArrayList<ApprovalProcessSettingListben> approvalProcessSettingListbens=approvalProcessSettingListMapper.SetapprovalList();
        return  approvalProcessSettingListbens;
    }
}

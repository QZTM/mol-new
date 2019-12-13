package com.mol.ddmanage.Controller.Tools;

import com.mol.ddmanage.Service.Tools.ApprovalProcessinforService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/ApprovalProcessinforContraller")
public class ApprovalProcessinforContraller
{
    @Resource
    ApprovalProcessinforService approvalProcessinforService;

    /**
     *  获取审批信息
     * @param id 审批中间表id
     * @return
     */
    @RequestMapping("/GetApprovalInfor")
    public Map GetApprovalInfor(@RequestParam String id)//获取单个审批信息
    {
        return approvalProcessinforService.GetApprovalInforLogic(id);
    }


    /**
     * 插入审配人
     * @param id 审批中间表 id
     * @param amountMin 金额区间小
     * @param amountMax 金额区间大
     * @param status 启用状态
     * @param approval_ids 审批人列表
     * @return
     */
    @RequestMapping("/SubmitApprovalData")
    public Map SubmitApprovalData(@RequestParam String id,@RequestParam String amountMin,@RequestParam String amountMax, @RequestParam String status, @RequestParam String approval_ids,@RequestParam String Unlock_ids_str)
    {
        return approvalProcessinforService.SubmitApprovalDataLogic(id,amountMin,amountMax,status,approval_ids,Unlock_ids_str);
    }
}

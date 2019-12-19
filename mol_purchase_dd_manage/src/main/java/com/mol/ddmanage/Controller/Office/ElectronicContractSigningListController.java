package com.mol.ddmanage.Controller.Office;

import com.mol.ddmanage.Ben.Office.ElectronicContractSigningListben;
import com.mol.ddmanage.Service.Office.ElectronicContractSigningListService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@RequestMapping("/ElectronicContractSigningListController")
public class ElectronicContractSigningListController
{
    @Resource
    ElectronicContractSigningListService electronicContractSigningListService;

    /**
     * 获取订单的合同列表
     * @param Contract_statu
     * @param electronic_contract true启用电子合同
     * @return
     */
    @RequestMapping("/GetElectronicContractSigningList")//获取订单的合同列表
    public ArrayList<ElectronicContractSigningListben> GetElectronicContractSigningList(@RequestParam String Contract_statu, @RequestParam String electronic_contract, HttpServletRequest httpServletRequest)
    {
        return electronicContractSigningListService.GetElectronicContractSigningListLogic(Contract_statu, electronic_contract,httpServletRequest);
    }
}

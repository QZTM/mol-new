package com.mol.ddmanage.Controller.Office;

import com.mol.ddmanage.Ben.PurchasOrderManagement.PurchasOrderinforben;
import com.mol.ddmanage.Service.Office.ElectronicContractSigninginforService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/ElectronicContractSigninginforController")
public class ElectronicContractSigninginforController
{
    @Resource
    ElectronicContractSigninginforService electronicContractSigninginforService;

    @RequestMapping("/GetConfirmSupplier")//获取已经确认的报价和供应商
    public ArrayList<ArrayList<PurchasOrderinforben>> GetConfirmSupplier(@RequestParam String PurchasId)
    {
        return electronicContractSigninginforService.GetConfirmSupplierLogic(PurchasId);
    }

    @RequestMapping("/RegisteredAccount")
    public Map RegisteredAccount()//注册法大大账号
    {
        return electronicContractSigninginforService.RegisteredAccount();
    }

    @RequestMapping("/CertificationAccount")
    public Map CertificationAccount()//此企业在法大大账号的认证
    {
        return electronicContractSigninginforService.CertificationAccountLogic();
    }

    @RequestMapping("/AuthSynchronizeNotity")
    public void AuthSynchronizeNotity(@RequestParam Map map)//认证同步回调地址
    {
        electronicContractSigninginforService.AuthSynchronizeNotityLogic(map);
    }

    @RequestMapping("/AuthAsynchronousNotity")
    public void AuthAsynchronousNotity(@RequestParam Map map)//认证异步回调地址
    {
        electronicContractSigninginforService.AuthAsynchronousNotityLogic(map);
    }

    @RequestMapping("/signContractNotity")//手动签署合同状态回调
    public void signContractNotity(@RequestParam Map map)
    {
       electronicContractSigninginforService.signContractNotityLogic(map);
    }

    @RequestMapping("/Upload_Contract")//上传合同
    public Map Upload_Contract(@RequestParam("file") MultipartFile file ,@RequestParam Map map)
    {
        return electronicContractSigninginforService.Upload_Contract_Logic(file,map);
    }
    @RequestMapping("/signContract")//手动签署合同
    public Map signContract(@RequestParam String purchasId,@RequestParam String supplierid)
    {
        return electronicContractSigninginforService.signContractLogic(purchasId,supplierid);
    }


    @RequestMapping("/SetContract")//查看合同
    public Map SetContract(@RequestParam String purchasId,@RequestParam String supplierid)
    {
        return electronicContractSigninginforService.SetContractLogic(purchasId,supplierid);
    }

}

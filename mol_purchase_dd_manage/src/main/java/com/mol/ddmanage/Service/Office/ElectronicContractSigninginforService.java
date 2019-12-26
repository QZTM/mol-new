package com.mol.ddmanage.Service.Office;

import com.mol.ddmanage.Ben.PurchasOrderManagement.PurchasOrderinforben;
import com.mol.ddmanage.Util.DataUtil;
import com.mol.ddmanage.config.Basic_config;
import com.mol.ddmanage.mapper.Office.ElectronicContractSigninginforMapper;
import com.mol.fadada.handler.ContractHandler;
import com.mol.fadada.handler.RegistAndAuthHandler;
import entity.ServiceResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import util.IdWorker;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class ElectronicContractSigninginforService
{
    @Resource
    ElectronicContractSigninginforMapper electronicContractSigninginforMapper;

    /**
     * 获取已经确认的报价物品对应的供应商
     * @param PurchasId 订单id
     * @return
     */
    public ArrayList<ArrayList<PurchasOrderinforben>> GetConfirmSupplierLogic(String PurchasId)
    {
        ArrayList<ArrayList<PurchasOrderinforben>> Orderss=new ArrayList<>();//一个分组
        ArrayList<PurchasOrderinforben> purchasOrderinforben=electronicContractSigninginforMapper.GetConfirmSupplier(PurchasId);

        Map<String,String> map_corp=new HashMap();
        for (int n=0;n<purchasOrderinforben.size();n++)//遍历出有多少公司报价
        {
            if (purchasOrderinforben.get(n).getCorp_name()!=null)
            {
                if (map_corp.get(purchasOrderinforben.get(n).getCorp_name())==null)
                {
                    map_corp.put(purchasOrderinforben.get(n).getCorp_name(),purchasOrderinforben.get(n).getCorp_name());
                }
            }
            if (purchasOrderinforben.get(n).getSign_status()!=null)//查看供应商合同状态
            {
                if (purchasOrderinforben.get(n).getSign_status().equals("1"))
                {
                    purchasOrderinforben.get(n).setSign_status("未签署合同");
                }
                else if (purchasOrderinforben.get(n).getSign_status().equals("2"))
                {
                    purchasOrderinforben.get(n).setSign_status("等待签署合同");
                }
                else if (purchasOrderinforben.get(n).getSign_status().equals("3"))
                {
                    purchasOrderinforben.get(n).setSign_status("已签署合同");
                }
            }
            else
            {
                purchasOrderinforben.get(n).setSign_status("未上传合同");
            }
        }

        for (String value :map_corp.values())//相同公司的报价归为同一组
        {
            ArrayList<PurchasOrderinforben> Orders=new ArrayList<>();//一个单列
            for (int n=0;n< purchasOrderinforben.size();n++)
            {
                if (value.equals(purchasOrderinforben.get(n).getCorp_name()) && purchasOrderinforben.get(n).getQuote_id()!=null)//有公司名字 并且quote_id不为空代表是已经确定采购这个物品
                {
                    Orders.add(purchasOrderinforben.get(n));
                }
            }
            Orderss.add(Orders);
        }

        ArrayList<PurchasOrderinforben> Orders=new ArrayList<>();//一个单列
        for (int n=0;n< purchasOrderinforben.size();n++)
        {
            if (purchasOrderinforben.get(n).getCorp_name()==null && purchasOrderinforben.get(n).getQuote_id()!=null)//有公司名字
            {
                purchasOrderinforben.get(n).setCorp_name("公司名称未知");
                Orders.add(purchasOrderinforben.get(n));
            }
        }
        if (Orders.size()!=0)
        {
            Orderss.add(Orders);
        }

        for (int n=0;n<Orderss.size();n++)//为分组添加序号
        {
            for (int n_1=0;n_1<Orderss.get(n).size();n_1++)
            {
                Orderss.get(n).get(n_1).setNumber(String.valueOf(n_1));
            }
        }
        return Orderss;
    }

    /**
     * 注册法大大账号
     * @return
     */
     public Map RegisteredAccount()
     {
         Map map =new HashMap();
         ServiceResult serviceResult=RegistAndAuthHandler.regAccount(Basic_config.open_id,"2");
         map.put("statu",serviceResult.isSuccess());
         return map;
     }

    /**
     * 认证法大大账号
     * @return
     */
     public Map CertificationAccountLogic()
     {
         Map map1=new HashMap();
         Map map= electronicContractSigninginforMapper.GetCustomer_id(Basic_config.open_id);
         if (map.get("customer_id")!=null)
         {
             String Auth_id=String.valueOf(new IdWorker().nextId());
             ServiceResult serviceResult=RegistAndAuthHandler.getAuthCompanyurl(map.get("customer_id").toString(),Basic_config.domain_name +"/ElectronicContractSigninginforController/AuthAsynchronousNotity?customer_id="+map.get("customer_id").toString()+"&Auth_id="+Auth_id,Basic_config.domain_name +"/ElectronicContractSigninginforController/AuthSynchronizeNotity?customer_id="+map.get("customer_id").toString()+"&Auth_id="+Auth_id);
             if (serviceResult.isSuccess()==true)//获取认证url
             {
                 String items=serviceResult.getResult().toString().split(",")[1];
                 String url =items.substring(5,items.length()-1);
                 map1.put("url",url);
                 map1.put("statu",true);
                 electronicContractSigninginforMapper.AuthSynchronizeNotity(Auth_id,"","","","","1",DataUtil.GetNowSytemTime(),url);
             }
             else
             {
                 map1.put("statu",false);
             }
         }
         else
         {
            map1.put("statu",false);
         }
         return map1;
     }

    public void AuthSynchronizeNotityLogic(Map map)//认证同步回调地址
    {
        try
        {
            Map map1=map;
            if (map.get("status").toString().equals("0") /*&& electronicContractSigninginforMapper.Setfadada_auth_record(map.get("customer_id"))==null*/)
            {

                electronicContractSigninginforMapper.fadada_auth_record(map.get("Auth_id").toString(),map.get("customer_id").toString(),map.get("transactionNo").toString(),map.get("authenticationType").toString(),map.get("sign").toString(),"1");
                /*electronicContractSigninginforMapper.AuthSynchronizeNotity(String.valueOf(new IdWorker().nextId()),map.get("customer_id").toString(),map.get("transactionNo").toString(),map.get("authenticationType").toString(),map.get("sign").toString(),"1",DataUtil.GetNowSytemTime(),"");*/
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void AuthAsynchronousNotityLogic(Map map)//认证异步回调地址
    {
        try {
            if(map.get("status").toString().equals("4") || map.get("status").toString().equals("6"))
            {
                // electronicContractSigninginforMapper.fadada_auth_record_status(map.get("Auth_id").toString(),"4");
                electronicContractSigninginforMapper.AuthAsynchronousNotity(map.get("customerId").toString(),map.get("serialNo").toString(),map.get("status").toString(),map.get("statusDesc").toString(),DataUtil.GetNowSytemTime());
                ContractHandler.ApplyNumCert(map.get("customerId").toString(),map.get("serialNo").toString());//申请编号证书
                ContractHandler.ApplyCert(map.get("customerId").toString(),map.get("serialNo").toString());//申请实名证书
            }
        }
        catch (Exception e)
        {

        }
    }

    /**
     *  手动签署合同回调
     * @param map
     */
    public void signContractNotityLogic(Map map)
    {
        if ( electronicContractSigninginforMapper.Get_fy_purchase_supplier_contract(map.get("contract_id").toString()).get("sign_status").equals("1"))
        {
            electronicContractSigninginforMapper.SignContractNotity(map.get("contract_id").toString(),"2");
        }
    }
    /**
     *上传合同
     * @param file
     * @param map
     * @return
     */
    public Map Upload_Contract_Logic(MultipartFile file,Map map)
    {
        Map map1=new HashMap();
        try {
            ServiceResult authResult=RegistAndAuthHandler.checkIfAuthed(Basic_config.open_id,"2");
            if (RegistAndAuthHandler.checkIfRegisted(Basic_config.open_id,"2").isSuccess()==false)//查询是否注册
            {
                map1.put("statu","2");//未注册
                return map1;
            }
            else if (authResult.isSuccess()==false)//查询是否认证
            {
                map1.put("statu","3");//未认证
                return map1;
            }
            File toFile = null;//准备上传的合同
            if (file.equals("") || file.getSize() <= 0)
            {
                file = null;
            }
            else
             {
                InputStream ins = null;
                ins = file.getInputStream();
                toFile = new File(file.getOriginalFilename());
                inputStreamToFile(ins, toFile);

                 ServiceResult result =ContractHandler.uploadContract(toFile.getName(),toFile);//调用法大上传合同接口上传
                 if(result.isSuccess()==true)
                 {
                     Map map2= electronicContractSigninginforMapper.GetCustomer_id(Basic_config.open_id);//获取注册记录表
                    // Map map3=electronicContractSigninginforMapper.Setfadada_auth_record(map2.get("customer_id").toString());
                     electronicContractSigninginforMapper.Upload_Contract(String.valueOf(new IdWorker().nextId()) ,result.getResult().toString(), DataUtil.GetNowSytemTime(),map.get("userid").toString());//保存上传合同的信息
                     electronicContractSigninginforMapper.Supplier_Contract(String.valueOf(new IdWorker().nextId()),map.get("purchase_id").toString(),map.get("supplier_id").toString(),result.getResult().toString(),"",DataUtil.GetNowSytemTime(),DataUtil.GetNowSytemTime(),"1");//保存订单对应供应商合同的信息
                   //  ContractHandler.extsign(map2.get("customer_id").toString(),String.valueOf(new  IdWorker().nextId()),result.getResult().toString(),"编写目的","1");//手动签署合同
                 }
                 ins.close();
            }

            map1.put("statu","0");
            return map1;
        }
        catch (Exception e)
        {
           map1.put("statu","1");
           return map1;
        }
    }

    /**
     *  手动签署合同
     * @param purchasId 采购单id
     * @param supplierid 供应商id
     * @return
     */
    public Map signContractLogic(String purchasId,String supplierid)
    {
        Map map=new HashMap();
        try
        {
            Map map1=electronicContractSigninginforMapper.GetContractId(purchasId,supplierid);//获取
            Map map2= electronicContractSigninginforMapper.GetCustomer_id(Basic_config.open_id);//获取注册记录表
            ServiceResult serviceResult=ContractHandler.extsign(map2.get("customer_id").toString(),String.valueOf(new  IdWorker().nextId()),map1.get("contract_id").toString(),"编写目的", Basic_config.domain_name +"/ElectronicContractSigninginforController/signContractNotity?contract_id="+map1.get("contract_id").toString());//手动签署合同
            map.put("statu",serviceResult.isSuccess());
            map.put("url",serviceResult.getResult());
            return map;
        }
        catch (Exception e)
        {
            map.put("statu",false);
            return map;
        }
    }

    /**
     * 查看合同返回url
     * @param purchasId  订单id
     * @param supplierid 供应商id
     * @return 合同地址url
     */
    public Map SetContractLogic(String purchasId,String supplierid)//查看合同url
    {
        Map map=new HashMap();
        try
        {
            Map map1=electronicContractSigninginforMapper.GetContractId(purchasId,supplierid);//获取合同id
            if (map1.get("contract_id")==null)//没有此供应商的合同id
            {
                map.put("statu",false);
                return map;
            }
            ServiceResult serviceResult= RegistAndAuthHandler.SetContract(map1.get("contract_id").toString());
            if (serviceResult.isSuccess()==true)
            {
                map.put("statu",serviceResult.isSuccess());
                map.put("url",serviceResult.getResult().toString());
                return map;
            }
            else
            {
                map.put("statu",serviceResult.isSuccess());
                return map;
            }
        }
        catch (Exception e)
        {
            map.put("statu",false);
            return map;
        }

    }

    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.mol.supplier.controller.microApp;

import com.alibaba.fastjson.JSON;
import com.mol.sms.SendMsmHandler;
import com.mol.supplier.config.MicroAttr;
import com.mol.supplier.entity.MicroApp.Salesman;
import com.mol.supplier.entity.MicroApp.Supplier;
import com.mol.supplier.entity.dingding.Pay.PuiSupplierDeposit;
import com.mol.supplier.entity.thirdPlatform.BdMarbasclass;
import com.mol.supplier.mapper.dingding.Pay.PayMapper;
import com.mol.supplier.mapper.microApp.MicroSalesmanMapper;
import com.mol.supplier.mapper.microApp.MicroSupplierMapper;
import com.mol.supplier.mapper.third.BdMarbasclassMapper;
import com.mol.supplier.service.microApp.MicroAuthService;
import com.mol.supplier.service.microApp.MicroDdJsApiAuthService;
import com.mol.supplier.service.microApp.MicroUserService;
import com.mol.supplier.service.uploadAndDownload.UploadService;
import entity.ServiceResult;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 供应商认证控制器
 */
@Controller
@CrossOrigin
@RequestMapping("/auth")
@Log
public class MicroAuthController {




    @Autowired
    private MicroAuthService microAuthService;

    @Autowired
    private MicroSupplierMapper microSupplierMapper;

    @Autowired
    private MicroUserService microUserService;

    private SendMsmHandler sendMsmHandler = SendMsmHandler.getSendMsmHandler();

    @Autowired
    private BdMarbasclassMapper bdMarbasclassMapper;

    @Autowired
    private MicroDdJsApiAuthService microDdJsApiAuthService;

    @Autowired
    private PayMapper payMapper;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private MicroSalesmanMapper microSalesmanMapper;

    @Autowired
    private MicroAttr microAttr;

    @RequestMapping("/attr")
    public String showAuthChoosePage(HttpSession session) {
        Supplier oldSupplierObj = microUserService.getSupplierFromSession(session);
        Supplier newSupplier = microSupplierMapper.selectByPrimaryKey(oldSupplierObj);
        session.setAttribute("supplier",newSupplier);
        return "authenticate";
    }

    @RequestMapping("/pay/{authType}")
    public String showAuthPayPage(@PathVariable String authType,Model model,HttpServletRequest request,HttpSession session){
        log.info("authType:"+authType);
        Supplier supplier = (Supplier)session.getAttribute("supplier");

        //根据supplierId 和payfor判断支付状态：
        Example example = new Example(PuiSupplierDeposit.class);

        String pageName = "";
        if ("zhanlve".equals(authType)) {
            model.addAttribute("cost",microAttr.getPaySupplierZhanlve());
            example.and().andEqualTo("supplierId",supplier.getPkSupplier()).andEqualTo("payFor",PuiSupplierDeposit.ORDER_PAY_FOR_STRATEGY_SUPPLIER_SERVICE);
            PuiSupplierDeposit puiSupplierDepositGet = payMapper.selectOneByExample(example);
            if(puiSupplierDepositGet != null && puiSupplierDepositGet.getStatus().equals(PuiSupplierDeposit.ORDER_STATUS_SUCCESS)){
                return "pay_success";
            }
            pageName = "authenticate_pay_zhanlve";
        }else if("danyi".equals(authType)){
            model.addAttribute("cost",microAttr.getPaySupplierDanyi());
            example.and().andEqualTo("supplierId",supplier.getPkSupplier()).andEqualTo("payFor",PuiSupplierDeposit.ORDER_PAY_FOR_SINGON_SUPPLIER_SERVICE);
            PuiSupplierDeposit puiSupplierDepositGet = payMapper.selectOneByExample(example);
            if(puiSupplierDepositGet != null && puiSupplierDepositGet.getStatus().equals(PuiSupplierDeposit.ORDER_STATUS_SUCCESS)){
                return "pay_success";
            }
            pageName = "authenticate_pay_danyi";
        }

        model.addAttribute("jsauthmap",microDdJsApiAuthService.getAuthMap(request));

        return pageName;
    }

    /**
     * 显示认证的表单页面
     * @param authType  danyi   zhanlve    jichu
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/attr/{authType}")
    public String showAuthPage(@PathVariable String authType, HttpSession session, Model model) {
        String pageName = "";
        log.info("供应商性质认证---/auth/attr/"+authType);
        Supplier supplierOld = microUserService.getSupplierFromSession(session);
        Supplier supplier = microSupplierMapper.selectByPrimaryKey(supplierOld);
        session.setAttribute("supplier",supplier);
        //获取行业类别（物料分类的第一级）
        List<BdMarbasclass> bdMarbasclassList = bdMarbasclassMapper.findMarbasFirstList();
        log.info("获取到的物料分类第一级list.size:"+bdMarbasclassList.size());
        model.addAttribute("itemTypeList",bdMarbasclassList);
        String paySuccessed = "0";
        Example example = new Example(PuiSupplierDeposit.class);
        if ("jichu".equals(authType)) {
            model.addAttribute("pagenametitlefront", "基础");
            pageName = "authenticate_jichu2";

            System.out.println("supplier.getIfAttrNormal()..."+supplier.getIfAttrNormal());
            System.out.println("supplier.getSupstateNormal()..."+supplier.getSupstateNormal()+",MicroAttr.SUPSTATE_LOADING:"+MicroAttr.SUPSTATE_LOADING);
            //如果正在认证中显示认证中页面
                if(supplier.getSupstateNormal().intValue() == MicroAttr.SUPSTATE_LOADING.intValue()){
                    pageName = "authenticate_shenhe";
                }else if(supplier.getSupstateNormal().intValue() == MicroAttr.SUPSTATE_FAIL.intValue()){
                    pageName = "authenticate_update_jichu";
                }else if(supplier.getSupstateNormal().intValue() == MicroAttr.SUPSTATE_SUCCESS.intValue()){
                    pageName = "authenticate_success";
                }
        } else if ("zhanlve".equals(authType)) {
            model.addAttribute("pagenametitlefront", "战略");
                if(supplier.getSupstateStrategy().intValue() == MicroAttr.SUPSTATE_LOADING.intValue()){
                    pageName = "authenticate_shenhe";
                    return pageName;
                }else if(supplier.getSupstateStrategy().intValue() == MicroAttr.SUPSTATE_FAIL.intValue()){
                    pageName = "authenticate_update_zhanlve";
                    return pageName;
                }else if(supplier.getSupstateStrategy().intValue() == MicroAttr.SUPSTATE_SUCCESS.intValue()){
                    pageName = "authenticate_success";
                    return pageName;
                }
            /**
             * 如果已经认证过了基础供应商，且未在其他状态，那么显示认证页面2，如果没有认证过基础供应商，那么显示认证页面1
             */
            if(supplier.getSupstateNormal().intValue() == MicroAttr.SUPSTATE_SUCCESS.intValue() || supplier.getSupstateStrategy().intValue() == MicroAttr.SUPSTATE_BEFORE_CREATE_PAY){
                pageName = "authenticate_zhanlve2";
            }else{
                pageName = "authenticate_zhanlve";
            }

            //查询支付情况：(根据supplierId和payFor查询)

            example.and().andEqualTo("supplierId",supplier.getPkSupplier()).andEqualTo("payFor",PuiSupplierDeposit.ORDER_PAY_FOR_STRATEGY_SUPPLIER_SERVICE);
            PuiSupplierDeposit puiSupplierDepositGet = payMapper.selectOneByExample(example);
            if(puiSupplierDepositGet != null && puiSupplierDepositGet.getStatus().equals(PuiSupplierDeposit.ORDER_STATUS_SUCCESS)){
                paySuccessed = "1";
            }
        } else if ("danyi".equals(authType)) {
            model.addAttribute("pagenametitlefront", "单一");
                if(supplier.getSupstateSingle().intValue() == MicroAttr.SUPSTATE_LOADING){
                    pageName = "authenticate_shenhe";
                    return pageName;
                }else if(supplier.getSupstateSingle().intValue() == MicroAttr.SUPSTATE_FAIL){
                    pageName = "authenticate_update_danyi";
                    return pageName;
                }else if(supplier.getSupstateSingle().intValue() == MicroAttr.SUPSTATE_SUCCESS){
                    pageName = "authenticate_success";
                    return pageName;
                }
            /**
             * 如果已经认证过了基础供应商，且未在其他状态，那么显示认证页面2，如果没有认证过基础供应商，那么显示认证页面1
             */
            if(supplier.getSupstateNormal().intValue() == MicroAttr.SUPSTATE_SUCCESS || supplier.getSupstateSingle().intValue() == MicroAttr.SUPSTATE_BEFORE_CREATE_PAY){
                pageName = "authenticate_danyi2";
            }else{
                pageName = "authenticate_danyi";
            }
            //查询支付情况：(根据supplierId和payFor查询)
            example.and().andEqualTo("supplierId",supplier.getPkSupplier()).andEqualTo("payFor",PuiSupplierDeposit.ORDER_PAY_FOR_SINGON_SUPPLIER_SERVICE);
            PuiSupplierDeposit puiSupplierDepositGet = payMapper.selectOneByExample(example);
            if(puiSupplierDepositGet != null && puiSupplierDepositGet.getStatus().equals(PuiSupplierDeposit.ORDER_STATUS_SUCCESS)){
                paySuccessed = "1";
            }
        }
        model.addAttribute("payed",paySuccessed);
        log.info("供应商性质认证，，，最终跳转的页面名称为："+ pageName);
        return pageName;
    }


    /**
     * 认证通过后，成功页面的查看认证详情：
     * @return
     */
    @RequestMapping("/detail")
    public String showAuthDetail(String suppliertype,Model model){

        List<BdMarbasclass> bdMarbasclassList = bdMarbasclassMapper.findMarbasFirstList();
        log.info("查看详情页面----获取到的物料分类第一级list.size:"+bdMarbasclassList.size());
        model.addAttribute("itemTypeList",bdMarbasclassList);
        model.addAttribute("pagenametitlefront", suppliertype);
        String finalPageName = "";
        if ("基础".equals(suppliertype)) {
            finalPageName =  "authenticate_detail_jichu";
        }else if("战略".equals(suppliertype)){
            finalPageName =  "authenticate_detail_zhanlve";
        }else if("单一".equals(suppliertype)){
            finalPageName =  "authenticate_detail_danyi";
        }
        log.info("showAuthDetail...suppliertype:"+suppliertype+",,,finalPageName--->"+finalPageName);
        return finalPageName;
    }


    /**
     * 修改
     * @param supplier
     * @param session
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult update(@RequestBody Supplier supplier, HttpSession session, HttpServletResponse response) throws IOException {
        Supplier supplier1 = microUserService.getSupplierFromSession(session);
        System.out.println(supplier);
        supplier.setPkSupplier(supplier1.getPkSupplier());
        try{
            microSupplierMapper.updateByPrimaryKeySelective(supplier);
            Supplier newSupplier = microSupplierMapper.selectByPrimaryKey(supplier);
            session.setAttribute("supplier",newSupplier);
        }catch(Exception e){
            return ServiceResult.failureMsg("系统异常，请稍后再试");
        }
        return ServiceResult.successMsg("修改成功");
    }

    /**
     * 认证相关图片上传并保存入数据库
     *
     * @param file     图片二进制文件（blob)
     * @param whichImg 图片说明，bussinessLicence为营业执照；legalBodyIdFront法人身份证正面；legalBodyIdBack法人身份证反面；
     * @return
     */
    @RequestMapping(value = "/upload/img", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult saveImage(MultipartFile file, String whichImg, HttpSession session) {
        log.info("****供应商认证上传图片****，whichImg:"+whichImg);
        if (file == null) {
            throw new RuntimeException("没有需要保存的图片");
        }
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            throw new RuntimeException("从session中获取用户失败");
        }
        Salesman salesman = (Salesman) userObj;
        String orgId = salesman.getPkSupplier();
        if (StringUtils.isEmpty(orgId)) {
            throw new RuntimeException("从session中未获取到供应商ID");
        }
        Supplier supplier = new Supplier();
        supplier.setPkSupplier(orgId);
        switch (whichImg) {
            case Supplier.WHICHIMAGE_BUSSINESSLICENCEIMG:
                try {
                    supplier.setBusinessLicenceImg(file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Supplier.WHICHIMAGE_LEGALBODYIDFRONTIMG:
                try {
                    supplier.setLegalbodyCardFrontImg(file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Supplier.WHICHIMAGE_LEGALBODYIDBACKIMG:
                try {
                    supplier.setLegalbodyCardBackImg(file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Supplier.WHICHIMAGE_STRATEGY_protocotl:
                log.info("上传战略供应商协议图片：");
                //先本地保存图片，然后把图片路径保存入数据库
                String upload = uploadService.upload(file);

                try {
                    supplier.setLegalbodyCardBackImg(file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Supplier.WHICHIMAGE_SINGLE_protocotl:
//                try {
//                    supplier.setLegalbodyCardBackImg(file.getBytes());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
        }

        //存入数据库：
        int saveResult = microSupplierMapper.updateByPrimaryKeySelective(supplier);
        if (saveResult != 1) {
            throw new RuntimeException("保存照片时出错");
        }
        Supplier newSupplier = microSupplierMapper.selectByPrimaryKey(supplier);
        session.setAttribute("supplier",newSupplier);
        return ServiceResult.success("保存成功");
    }


    @RequestMapping("/upload/file")
    @ResponseBody
    public ServiceResult uploadAuthFile(MultipartFile fileToUpload, String whichFile, HttpSession session) {
        System.out.println("whichFile:" + whichFile);

        if (fileToUpload == null) {
            throw new RuntimeException("没有需要保存的文件");
        }
        Salesman salesman = microUserService.getUserFromSession(session);
        String orgId = salesman.getPkSupplier();
        if (StringUtils.isEmpty(orgId)) {
            throw new RuntimeException("从session中未获取到供应商ID");
        }
        Supplier supplier = new Supplier();
        supplier.setPkSupplier(orgId);
        switch (whichFile) {
            case Supplier.WHICHFILE_PROTOCOL:
                try {
                    supplier.setStrategySupplierProtocol(fileToUpload.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Supplier.WHICHFILE_PROTOCOL_DANYI:
                try {
                    supplier.setSingleSupplierProtocol(fileToUpload.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Supplier.WHICHFILE_AGENTQUALIFICATION1:
                try {
                    supplier.setAdditionalOne(fileToUpload.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Supplier.WHICHFILE_AGENTQUALIFICATION2:
                try {
                    supplier.setAdditionalTwo(fileToUpload.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Supplier.WHICHFILE_AGENTQUALIFICATION3:
                try {
                    supplier.setAdditionalThree(fileToUpload.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Supplier.WHICHFILE_AGENTQUALIFICATION4:
                try {
                    supplier.setAdditionalFour(fileToUpload.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        //存入数据库：
        int saveResult = microSupplierMapper.updateByPrimaryKeySelective(supplier);
        if (saveResult != 1) {
            throw new RuntimeException("保存文件时出错");
        }

        Supplier newSupplier = microSupplierMapper.selectByPrimaryKey(supplier.getPkSupplier());
        session.setAttribute("supplier",newSupplier);
        return ServiceResult.success("保存成功");

    }


    @RequestMapping("/subData")
    @ResponseBody
    public ServiceResult auth(String supplier, @RequestParam String phoneCheckCode, HttpSession session) {

        Supplier supplierAlready = microUserService.getSupplierFromSession(session);
        if (supplierAlready == null) {
            throw new RuntimeException("请先注册后再试");
        }

        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String sessionName = attributeNames.nextElement();
            System.out.println("sessionName:" + sessionName);
        }


        //验证手机验证码
        System.out.println(supplier);
        Supplier supplier1 = JSON.parseObject(supplier, Supplier.class);
        System.out.println(supplier1);
        String phoneNum = supplier1.getAuthPhone();
        boolean phoneCheckResult = sendMsmHandler.checkPhoneCode(phoneNum, phoneCheckCode);
        if (!phoneCheckResult) {
            return ServiceResult.failureMsg("验证码不正确");
        }

        //存入数据库

        Salesman salesman = microUserService.getUserFromSession(session);

        supplier1.setPkSupplier(supplierAlready.getPkSupplier());
        supplier1.setLastUpdateUser(salesman.getId());
        int updated = microSupplierMapper.updateByPrimaryKeySelective(supplier1);
        if (updated == 1) {

            Supplier newSupplier = microSupplierMapper.selectByPrimaryKey(supplierAlready.getPkSupplier());
            session.setAttribute("supplier",newSupplier);

            return ServiceResult.success("提交成功", null);
        } else {
            return ServiceResult.failure("提交失败");
        }

    }


    @RequestMapping("/showPhoneUpdate")
    public String showPhoneUpdate(HttpSession session,Model model){
        Salesman salesman = microUserService.getUserFromSession(session);
        model.addAttribute("user",salesman);
        return "phone_update";
    }


    @RequestMapping(value = "/phoneupdate",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult updatephone(@RequestBody String phone,HttpSession session){
//        Object phoneObj = paraMap.get("phone");
//        if(phoneObj == null) {
//            return ServiceResult.failureMsg("手机号码不能为空！");
//        }
//        String phone = (String)phoneObj;
        log.info("供应商业务员修改手机号码，phone:"+phone);
        Salesman salesman = microUserService.getUserFromSession(session);
        Salesman newS = new Salesman();
        newS.setId(salesman.getId());
        newS.setPhone(phone);
        try {
            microSalesmanMapper.updateByPrimaryKeySelective(newS);
            Salesman salesmanNew = microSalesmanMapper.selectByPrimaryKey(salesman);
            session.setAttribute("user",salesmanNew);
            return ServiceResult.successMsg("修改成功");
        }catch(Exception e) {
            log.warning(e.getMessage());
            return ServiceResult.failureMsg("修改失败，请稍后再试！");
        }
    }
}

package com.mol.supplier.controller.third;

import com.alibaba.fastjson.JSON;
import com.mol.supplier.config.ContractConfig;
import com.mol.supplier.config.OrderStatus;
import com.mol.supplier.entity.dingding.purchase.enquiryPurchaseEntity.PageArray;
import com.mol.supplier.entity.dingding.purchase.enquiryPurchaseEntity.PurchaseArray;
import com.mol.supplier.entity.dingding.purchase.enquiryPurchaseEntity.PurchaseDetail;
import com.mol.supplier.entity.dingding.solr.fyPurchase;
import com.mol.supplier.entity.thirdPlatform.FyQuote;
import com.mol.supplier.service.microApp.MicroDdJsApiAuthService;
import com.mol.supplier.service.microApp.MicroUserService;
import com.mol.supplier.service.third.ScheService;
import com.mol.supplier.service.third.ThirdPlatformService;
import com.mol.supplier.util.StatusScheUtils;
import entity.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import util.BigDecimalUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/sche")
@CrossOrigin
public class ScheController {

    @Autowired
    private ScheService scheService;
    @Autowired
    private ThirdPlatformService platformService;
    @Autowired
    private MicroUserService microUserService;
    @Autowired
    private MicroDdJsApiAuthService microDdJsApiAuthService;


    /**
     * 前往进度页面
     * @return
     */
    @RequestMapping(value = "/findList",method = RequestMethod.GET)
    public String getScheduleList(String status_first, String status_second,String status_thrid,String status_four,int pageNum, int pageSize, ModelMap map, HttpSession session){

        String supplierId = microUserService.getUserFromSession(session).getPkSupplier();


        if (status_first == null || status_first == "") {
            status_first="";
        }
        if (status_second == null || status_second == "") {
            status_second="";
        }
        if (status_thrid == null || status_thrid == "") {
            status_thrid="";
        }
        if (status_four == null || status_four == "") {
            status_four="";
        }
        List<String> purIdList=scheService.findPurIdListBySupplierId(supplierId);
        List<fyPurchase> list=scheService.findListByIdList(status_first,status_second,status_thrid,status_four,purIdList);



        map.addAttribute("list",list);
        map.addAttribute("status",status_first);
        map.addAttribute("pageindex","schedule");
        return "schedule";
    }

    @GetMapping("/findPass")
    public String  getPassList(String status_first,int pageNum,int pageSize,HttpSession session,ModelMap map){
        String supplierId = microUserService.getUserFromSession(session).getPkSupplier();
        List<FyQuote> quoteList=scheService.getList(supplierId,status_first,pageNum,pageSize);
        List<fyPurchase> purList=scheService.getPurListByPurIdList(quoteList);
        //如果订单状态是通过或者拒绝，查询报价表的状态来表示订单状态
        purList=scheService.findFyquoteStatusByPurId(purList,status_first);
//        purList=scheService.changeStatusToChinese(purList);
        map.addAttribute("list",purList);
        map.addAttribute("status",status_first);
        map.addAttribute("pageindex","schedule");
        return "schedule";
    }
    @GetMapping("/findNot")
    public String  getNotList(String status_first,int pageNum,int pageSize,HttpSession session,ModelMap map){
        String supplierId = microUserService.getUserFromSession(session).getPkSupplier();
        List<FyQuote> quoteList=scheService.findNotList(supplierId,status_first,pageNum,pageSize);
        List<fyPurchase> purList=scheService.getPurListByPurIdList(quoteList);
        purList=scheService.findFyquoteStatusByPurId(purList,status_first);

        map.addAttribute("list",purList);
        map.addAttribute("status",status_first);
        map.addAttribute("pageindex","schedule");
        return "schedule";
    }
    /**
     * 进度详情页
     * @param id
     * @param
     * @return
     */
    @RequestMapping(value = "/getScheduleOne",method = RequestMethod.GET)
    public String getScheduleOne(String id, ModelMap modelMap, HttpSession session, HttpServletRequest request){
          String supplierId = microUserService.getUserFromSession(session).getPkSupplier();

        fyPurchase purchase=scheService.selectOneById(id);

        //查询订单相关报价
        List<FyQuote> quoteList=scheService.findQuoteById(id,supplierId);


        //如果状态是7或者8，
        if(OrderStatus.pass.equals(Integer.parseInt(purchase.getStatus())) || OrderStatus.refuse.equals(Integer.parseInt(purchase.getStatus()))){
            int count =0;
            for (FyQuote quote : quoteList) {
                if (Integer.parseInt(quote.getApprovalOverStatus())==OrderStatus.QUOTE_REFUSE){
                    count++;
                }
            }
            if (count==quoteList.size()){
                purchase.setStatus("淘汰");
            }
        }

        purchase= scheService.toChineses(purchase);
        //--------------------------------
        //查询报价商家的数量
        String quoteCounts = purchase.getQuoteCounts();
        modelMap.addAttribute("quoteCount",quoteCounts);

        //定义最后要传递到页面的list
        List<PurchaseArray> purList =new ArrayList<>();
        //解析json字段
        PageArray pageArray = JSON.parseObject(purchase.getGoodsDetail(), PageArray.class);
        System.out.println("pagearrayObj:"+pageArray);
        //json字段中的详情集合
        List<PurchaseArray> purchaseArrayList = pageArray.getPurchaseArray();
        System.out.println("purchaseArray:"+purchaseArrayList);

        for (int i=0;i<quoteList.size();i++){
            for (int j =0;j<purchaseArrayList.size();j++){
                if (quoteList.get(i).getPkMaterialId().equals(purchaseArrayList.get(j).getMaterialId())){
                    PurchaseArray pur=new PurchaseArray();
                    pur.setMaterialId(purchaseArrayList.get(j).getMaterialId());
                    pur.setCount(purchaseArrayList.get(j).getCount());
                    pur.setItemName(purchaseArrayList.get(j).getItemName());
                    pur.setTypeName(purchaseArrayList.get(j).getTypeName());
                    pur.setUnit(purchaseArrayList.get(j).getUnit());
                    pur.setNorms(purchaseArrayList.get(j).getNorms());
                    pur.setQuote(quoteList.get(i).getQuote());
                    pur.setSum(Double.parseDouble(quoteList.get(i).getQuote())*purchaseArrayList.get(j).getCount()+"");
                    pur.setSupplyCycle(quoteList.get(i).getSupplyCycle());
                    modelMap.addAttribute("supplyCycle",quoteList.get(0).getSupplyCycle());
                    purList.add(pur);

                }
            }
        }

        //查询中标情况
        List<PurchaseArray> okList = new ArrayList<>();
        if (purchase.getStatus().equals("通过")){
            //自己公司的报价
            List<FyQuote> fyQuoteList=scheService.findQuoteBySupplierIdAndPurId(supplierId,id);
            List<PurchaseDetail> detailList=new ArrayList<>();
            if (fyQuoteList!=null&&fyQuoteList.size()>0){
                for (FyQuote fyQuote : fyQuoteList) {
                    String quoteId=fyQuote.getId();
                    PurchaseDetail purchaseDetail=scheService.findPurchDetailByPurIdAndQuoteId(id,quoteId);
                    if (purchaseDetail!=null){
                        detailList.add(purchaseDetail);
                    }
                }

            }
            if (detailList!=null && detailList.size()>0){
                for (int i =0;i<detailList.size();i++){
                    for (int j=0;j<purList.size();j++){
                        if (detailList.get(i).getPkMaterial().equals(purList.get(j).getMaterialId())){
                            okList.add(purList.get(j));
                        }
                    }
                }
            }
        }



        //中标的list
        modelMap.addAttribute("okList",okList);
        modelMap.addAttribute("pur",purchase);
        //json字符串
        modelMap.addAttribute("purList",purList);
        //规定申请人的个数
        System.out.println("人数"+pageArray.getQuoteSellerNum());
        modelMap.addAttribute("quoNum",pageArray.getQuoteSellerNum());
        //备注说明
        String remarks = pageArray.getRemarks();
        modelMap.addAttribute("remakes",remarks);
        //截止时间
        String deadLine = pageArray.getDeadLine();
        modelMap.addAttribute("dealLine",deadLine);
        //供货周期
        String supplyCycle = pageArray.getSupplyCycle();
        modelMap.addAttribute("supplyCycle",supplyCycle);
        //支付方式
        String payMent = pageArray.getPayMent();
        modelMap.addAttribute("payMent",payMent);
        //技术支持电话
        String technicalSupportTelephone = pageArray.getTechnicalSupportTelephone();
        modelMap.addAttribute("tst",technicalSupportTelephone);
        //专家评审
        String expertReview = pageArray.getExpertReview();
        modelMap.addAttribute("expertReview",expertReview);
        //专家评审费
        String expertReward = pageArray.getExpertReward();
        modelMap.addAttribute("expertReward",expertReward);

        if ( purchase.getStatus().equals("通过")){
            if (expertReview.equals("true")){
                //查询中标公司，平分专家评审费用
                int winningKidSupplier = scheService.getCountWinningKidSupplier(purchase.getId());

                String expertRewardOne= Math.ceil(BigDecimalUtils.divide(expertReward,winningKidSupplier+""))+"";
                modelMap.addAttribute("expertRewardOne",expertRewardOne);
            }

        }
        modelMap.addAttribute("jsauthmap",microDdJsApiAuthService.getAuthMap(request));

        //电子合同
        String electronicContract = pageArray.getElectronicContract();
        modelMap.addAttribute("electronicContract",electronicContract);
        //合同费用
        BigDecimal contractCost= ContractConfig.CONTRACT_COST;
        modelMap.addAttribute("contractCost",contractCost);
        return "schedule_detail";
    }
}

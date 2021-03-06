package com.mol.supplier.controller.third;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.response.OapiDepartmentGetResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.mol.supplier.config.BuyChannelResource;
import com.mol.supplier.config.ContractConfig;
import com.mol.supplier.config.MicroAttr;
import com.mol.supplier.config.OrderStatus;
import com.mol.supplier.entity.MicroApp.DDDept;
import com.mol.supplier.entity.MicroApp.DDUser;
import com.mol.supplier.entity.MicroApp.Salesman;
import com.mol.supplier.entity.MicroApp.Supplier;
import com.mol.supplier.entity.dingding.login.AppUser;
import com.mol.supplier.entity.dingding.purchase.enquiryPurchaseEntity.PageArray;
import com.mol.supplier.entity.dingding.purchase.enquiryPurchaseEntity.PurchaseArray;
import com.mol.supplier.entity.dingding.purchase.enquiryPurchaseEntity.PurchaseDetail;
import com.mol.supplier.entity.dingding.solr.fyPurchase;
import com.mol.supplier.entity.thirdPlatform.*;
import com.mol.supplier.service.dingding.purchase.EsService;
import com.mol.supplier.service.microApp.MicroGetDDUserInfoService;
import com.mol.supplier.service.microApp.MicroUserService;
import com.mol.supplier.service.third.ScheService;
import com.mol.supplier.service.third.ThirdPlatformService;
import com.taobao.api.internal.toplink.netcat.NetCatOuputWriter;
import entity.PageBean;
import entity.ServiceResult;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import util.TimeUtil;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/index")
@CrossOrigin
@Log
public class ThirdPlatformController {

    @Autowired
    private ThirdPlatformService platformService;

    @Autowired
    private MicroGetDDUserInfoService microGetDDUserInfoService;

    @Resource
    private EsService esService;

    @Autowired
    private MicroUserService microUserService;

    @Autowired
    private ScheService scheService;

    @Autowired
    private MicroAttr microAttr;


    private String htmlName = null;

    @RequestMapping("/findAll")
    public String index(String pageName, String ddId, String purId, ModelMap map, HttpSession session, HttpServletRequest request, HttpServletResponse res) {
        log.info(".../index/findAll");
        if (ddId!=null && purId!=null ){
            log.info("用户从钉钉通知点击进入供应商客户端，ddId:"+ddId+",purId:"+purId);
            //根据钉钉id  查询用户信息，存储到session中
            Salesman man = platformService.findSalesManId(ddId);
            session.setAttribute("user",man);
            Supplier su=platformService.findSupplierByOrgId(man.getPkSupplier());
            session.setAttribute("supplier",su);

            /*获取钉钉用户详情：*/
            OapiUserGetResponse response = microGetDDUserInfoService.getUserDetail(ddId);
            if (response == null) {
                throw new RuntimeException("获取用户信息失败");
            }
            DDUser ddUser = JSONObject.parseObject(JSONObject.toJSONString(response), DDUser.class);
            log.info("获取到的用户详情：");
            log.info(ddUser.toString());


            /*获取部门信息*/
            OapiDepartmentGetResponse departmentGetResponse = microGetDDUserInfoService.getDeptInfo(ddUser.getDepartment().get(0));
            if (departmentGetResponse == null) {
                throw new RuntimeException("获取用户部门信息失败");
            }

            OapiDepartmentGetResponse orgGetResponse = microGetDDUserInfoService.getOrgInfo(-99L);
            if (orgGetResponse == null) {
                throw new RuntimeException("获取组织信息失败");
            }
            DDDept ddDept = JSONObject.parseObject(JSONObject.toJSONString(departmentGetResponse), DDDept.class);
            log.info("获取到的部门信息：");
            log.info(ddDept.toString());

            /*获取组织信息*/
            DDDept org = JSONObject.parseObject(JSONObject.toJSONString(orgGetResponse), DDDept.class);
            log.info("获取到的企业信息：");
            log.info(org.toString());

            session.setAttribute("ddUser", ddUser);
            session.setAttribute("ddDept", ddDept);
            session.setAttribute("ddOrg", org);




//                request.getRequestDispatcher("/index/selectOne?id="+purId).forward(request,res);
//            try {
//                res.sendRedirect("http://"+request.getServerName()+"/index/selectOne?id="+purId);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            //根据订单id查询订单状态
            fyPurchase pur=platformService.findPurById(purId);
            // 由订单状态判断跳转的具体页面
            if(pur!=null){
                if (Integer.parseInt(pur.getStatus())==OrderStatus.waitingQuote){
                    //报价 /index/selectOne?id
                    return "forward:/index/selectOne?id="+purId;
                }
                if (Integer.parseInt(pur.getStatus())==OrderStatus.pass || Integer.parseInt(pur.getStatus())==OrderStatus.refuse){
                    //通过或淘汰 /sche/getScheduleOne?id
                    return "forward:/sche/getScheduleOne?id="+purId;
                }
            }


        }
        Object salesmanObj = session.getAttribute("user");
        if(salesmanObj == null){
            System.out.println("session中没有用户信息");
        }else{
            System.out.println("session中有用户信息");
        }


        //获取第三方轮播图信息
        log.info("获取第三方轮播图信息-------");
        List<Lunbo> lunboList = platformService.findLunBo();
        map.addAttribute("lunboList", lunboList);
        System.out.println(lunboList);
        log.info("获取企业排版信息-------");
        //获取企业排版信息
        List<Enter> enterList = platformService.findAll();
        System.out.println(enterList);
        map.addAttribute("enterList", enterList);
        //询价采购需求
        //采购渠道
        String buyId = "";
        //返回的页面
        if (pageName == null) {
            pageName = "询价采购";
        }
        switch (pageName) {
            //添加没个页面的序号和对应的页面
            case "询价采购":
                buyId = 4 + "";
                break;
            case "战略采购":
                buyId = 3 + "";
                break;
            case "单一来源":
                buyId = 5 + "";
                break;
            case "加工维修":
                buyId = 6 + "";
                break;
        }
        //查询五条最新公告
        List<fyPurchase> passPurList = platformService.findPassPurchByStatus(OrderStatus.pass + "", 1, 5);
        map.addAttribute("notice",passPurList);


        //获取最新的五个采购
        List<fyPurchase> newEstPurList = platformService.findPur(OrderStatus.waitingQuote+"",1,5);
        //获取公司的名称
        newEstPurList=platformService.getAppOrgNameByPurId(newEstPurList);

        map.addAttribute("newEst",newEstPurList);


        List<fyPurchase> orderList = platformService.findList(buyId, 1, 5);
        map.addAttribute("orderList", orderList);
        map.addAttribute("pageName", pageName);
        map.addAttribute("index", true);
        map.addAttribute("pageindex","index");
        map.addAttribute("corpid",microAttr.getCorpId());
        return "index";
    }

    /**
     * 获取首页的询价list
     */
    @RequestMapping("/getOrderList")
    @ResponseBody
    public List<fyPurchase> getOrderList(String buyId, String status) {
        //List<fyPurchase> orderList = platformService.findList(buyId, 1, 5);
        List<fyPurchase> orderList = platformService.findListByStatusAndId(buyId, 1, 8, status);
        orderList=platformService.PurchaseToChinese(orderList);
        return orderList;
    }

    /**
     * 获取采购内容List
     */
    @RequestMapping(value = "/findList", method = RequestMethod.GET)
    public String findList(String pageName, int pageNumber, int pageSize, String keyword, String status, String goodsType, String tickert,HttpSession session, ModelMap map) {
        Supplier supplier = microUserService.getSupplierFromSession(session);

        System.out.println("ti:" + tickert);
        //采购渠道
        String buyId = "";
        //返回的页面

        switch (pageName) {
            //添加没个页面的序号和对应的页面
            case "询价采购":
                buyId = 4 + "";
                htmlName = "index_xjcg";
                break;
            case "战略采购":
                buyId = 3 + "";
                htmlName = "index_zlcg";
                break;
            case "单一来源":
                buyId = 5 + "";
                htmlName = "index_dyly";
                break;
            case "加工维修":
                buyId = 6 + "";
                htmlName = "index_jgwx";
                break;
            case "中标公告":
                htmlName="index_zbgg";
                break;
            case "茉尔资讯":
                htmlName="index_mezx";
                break;
            default:
                htmlName="error_enter";
                break;
        }
        //判断页面
        if (htmlName=="error_enter"){
            return htmlName;
        }
        List<fyPurchase> list = new ArrayList<>();
        int count = 0;

        //进入茉尔资讯
        if (htmlName == "index_mezx") {
            log.info("查询茉尔资讯");
            List<SuppNews> newsList=platformService.getNewsList(pageNumber,pageSize);
            map.addAttribute("newsList",newsList);
            log.info("查询茉尔资讯的list："+newsList);
            return htmlName;
        }
        //进入中标公告
        if (htmlName == "index_zbgg") {
            log.info("查询中标公告");
            List<fyPurchase> lists =platformService.findPassPurchByStatus(OrderStatus.pass+"",pageNumber,pageSize);
            lists =platformService.findPassSupplierCountOfPassPur(lists);
            //将订单状态改为中文

            for (fyPurchase pur : lists) {
                fyPurchase p = platformService.getPurStatusToChinese(pur);
                list.add(p);
            }
            count=platformService.findPassCountByStatus(OrderStatus.pass);
            log.info("中标公告的list"+list);
            log.info("中标公告的数量"+count);
        } else {
            //单一，询价，战略，维修加工，
            if (keyword == null || keyword == "") {
                keyword = "";
            }
            if (status == null || status == "") {
                status = "";
            }
            if (goodsType == null || goodsType == "") {
                goodsType = "";
            }

            PageBean pb = new PageBean();


            if (keyword != null && keyword != "") {
                pb = esService.list(pageNumber, pageSize, keyword);
                list = pb.getList();
                count = pb.getTotalCount();
            } else {
                //获取采购内容的list
                list = platformService.findLIstByStatusAndGoodsTypeAndBuyChannelId(buyId, status, goodsType, pageNumber, pageSize);
                count = platformService.findCount(buyId, status, goodsType);
            }
            //单一页面  将公司id切换为中文显示
            if (buyId==5+""){
                list=platformService.getPkSupplierToCHinese(list);
            }

            pb.setTotalCount(count);
            pb.setPageSize(pageSize);
            pb.setTotalPage(pb.getTotalCount() % pb.getPageSize() == 0 ? pb.getTotalCount() / pb.getPageSize() : pb.getTotalCount() / pb.getPageSize() + 1);
            pb.setCurrentPage(pageNumber);



            map.addAttribute("searchVal", "");
            map.addAttribute("keyword", keyword);
            map.addAttribute("status", status);
        }
        //获取物料第一级分类
        List<BdMarbasclass> marbasFirstList = platformService.findMarbasClassFirstList();
        map.addAttribute("marList", marbasFirstList);

        map.addAttribute("list", list);
        map.addAttribute("count", count);
        return htmlName;
    }

    /**
     * 采购类型 行业类别
     *
     * @param status
     * @param goodsType
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping("/getMarbasClassNameList")
    @ResponseBody
    public List<fyPurchase> getMarbasClassNameList(String buyChannelId, String status, String goodsType, Integer pageNumber, Integer pageSize) {
        //获取行业类别
        //行业类别
        log.info("按照物料分类查询，当前状态："+status);
        List<fyPurchase> list = platformService.findLIstByStatusAndGoodsTypeAndBuyChannelId(buyChannelId, status, goodsType, pageNumber, pageSize);
        if(status!="" && Integer.parseInt(status)==OrderStatus.pass){
            log.info("按照物料分类查询，需要查询中标公司数量，当前状态："+status);
            list =platformService.findPassSupplierCountOfPassPur(list);
        }
        return list;
    }

    /**
     * 数量
     * @param buyChannelId
     * @param status
     * @param goodsType
     * @return
     */
    @RequestMapping("/getMarbasClassNameList/getCount")
    @ResponseBody
    public Integer getCount(String buyChannelId, String status, String goodsType) {
        return platformService.findCount(buyChannelId, status, goodsType);
    }


    /**
     * 询价采购，
     * 详情页
     *
     * @param id
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/selectOne", method = RequestMethod.GET)
    public String selectOne(String id, ModelMap modelMap, HttpSession session) {

        log.info("查看订单详情，订单id："+id);
        fyPurchase purchase = platformService.selectOneById(id);
        purchase=platformService.ToChineseString(purchase);
        //查询报价商家的数量
        String quoteCounts = purchase.getQuoteCounts();
        modelMap.addAttribute("quoteCount", quoteCounts);
        //--------------------------------
        //定义最后要传递到页面的list
        List<PurchaseArray> purList = new ArrayList<>();
        //解析json字段
        PageArray pageArray = JSON.parseObject(purchase.getGoodsDetail(), PageArray.class);
        System.out.println("pagearrayObj:" + pageArray);
        //json字段中的详情集合
        List<PurchaseArray> purchaseArrayList = pageArray.getPurchaseArray();
        System.out.println("purchaseArray:" + purchaseArrayList);
        for (int i = 0; i < purchaseArrayList.size(); i++) {
            PurchaseArray pur = new PurchaseArray();
            pur.setMaterialId(purchaseArrayList.get(i).getMaterialId());
            pur.setCount(purchaseArrayList.get(i).getCount());
            pur.setItemName(purchaseArrayList.get(i).getItemName());
            pur.setTypeName(purchaseArrayList.get(i).getTypeName());
            pur.setUnit(purchaseArrayList.get(i).getUnit());
            pur.setNorms(purchaseArrayList.get(i).getNorms());
            purList.add(pur);
        }

        //判断登录人所在公司是否已经报价
        boolean quoteOrNot = false;
        Supplier supplier = microUserService.getSupplierFromSession(session);
        if (supplier != null) {

            String supplierId = supplier.getPkSupplier();
            quoteOrNot = platformService.isQuote(id, supplierId);
        } else {
            quoteOrNot = true;
        }


        modelMap.addAttribute("quoteOrNot", quoteOrNot);
        modelMap.addAttribute("pur", purchase);
        //json字符串
        modelMap.addAttribute("purList", purList);
        //规定申请人的个数
        System.out.println("人数" + pageArray.getQuoteSellerNum());
        modelMap.addAttribute("quoNum", pageArray.getQuoteSellerNum());
        //备注说明
        String remarks = pageArray.getRemarks();
        modelMap.addAttribute("remakes", remarks);
        //截止时间
        String deadLine = pageArray.getDeadLine();
        modelMap.addAttribute("dealLine", deadLine);
        //供货周期
        String supplyCycle = pageArray.getSupplyCycle();
        modelMap.addAttribute("supplyCycle", supplyCycle);
        //支付方式
        String payMent = pageArray.getPayMent();
        modelMap.addAttribute("payMent", payMent);
        //技术支持电话
        String technicalSupportTelephone = pageArray.getTechnicalSupportTelephone();
        modelMap.addAttribute("tst", technicalSupportTelephone);
        //专家评审
        String expertReview = pageArray.getExpertReview();
        modelMap.addAttribute("expertReview",expertReview);
        //专家评审费
        String expertReward = pageArray.getExpertReward();
        modelMap.addAttribute("expertReward",expertReward);


        if (purchase.getBuyChannelId() == 4) {
            htmlName = "xjcg_detail";
        }
        if (purchase.getBuyChannelId() == 3) {
            htmlName = "zlcg_detail";
        }
        if (purchase.getBuyChannelId() == 5) {
            htmlName = "dyly_detail";
        }
        if (purchase.getBuyChannelId() == 6) {
            htmlName = "jgwx_detail";
        }
        return htmlName;
    }

    /**
     * 前往中标详情页面
     * @param id
     * @return
     */
    @GetMapping("/selecOnePassPur")
    public String selectOnePassPur(String id,ModelMap modelMap ,HttpSession session){

        //String supplierId = microUserService.getUserFromSession(session).getPkSupplier();

        fyPurchase purchase=scheService.selectOneById(id);

        //查询订单相关报价
       // List<FyQuote> quoteList=scheService.findQuoteById(id,null);

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


        //获取最终选中的报价
        List<FyQuote> quoteList=null;

        //公司的list
        List<Supplier> supplierList=new ArrayList<>();
        quoteList=scheService.findPassQuoteByPurId(purchase.getId());


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
                    pur.setSupplier(quoteList.get(i).getPkSupplierId());
                    modelMap.addAttribute("supplyCycle",quoteList.get(0).getSupplyCycle());
                    purList.add(pur);

                }
            }
        }


        for (int i =0;i<quoteList.size();i++){
            if (supplierList.size()>0){
                for (int j=0;j<supplierList.size();j++){
                    if (supplierList.get(j).getPkSupplier().equals(quoteList.get(i).getPkSupplierId())){
                        break;
                    }
                    if (j==supplierList.size()-1){
                        Supplier supplier=scheService.findSupplierById(quoteList.get(i).getPkSupplierId());
                        supplierList.add(supplier);
                    }
                }
            }else {
                Supplier supplier=scheService.findSupplierById(quoteList.get(i).getPkSupplierId());
//                Supplier supplier=scheService.getSupplierById(detailList.get(i).getPkSupplierId());
                supplierList.add(supplier);
            }


        }
        modelMap.put("supplier",supplierList);
        //中标的list
        //modelMap.addAttribute("okList",okList);
        modelMap.addAttribute("pur",purchase);
        //json字符串
        modelMap.addAttribute("purList",purList);//所有报价
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

        return "zbgg_detail";
    }

    /**
     * 前往新闻详情页面
     * @param id 新闻表主键ID
     * @return
     */
    @GetMapping("/findnewsDetail")
    public String findNewsDetail(String id ,ModelMap map,HttpSession session){
        Salesman sale = microUserService.getUserFromSession(session);
        int pageNum=1;
        int pageSize=10;
        log.info("前往新闻详情页面："+id);
        SuppNews news=platformService.findNewsDetail(id);
        log.info("查询资讯详情完成："+news);
        //中间表插入阅读记录
        int inert = platformService.saveReadHistory(sale, news);
        log.info("增加阅读记录："+inert);
        //将id的新闻，阅读人数加一
        int i=platformService.addNewsNumOfReaders(news);
        log.info("查询资讯详情修改阅读人数完成："+i);
        //添加10条推荐（不包含次条）
        List<SuppNews> list=platformService.getNewListWithOutThisId(pageNum,pageSize);
        //判断是否已读
        list=platformService.getIfRead(pageNum++,pageSize,sale,list);
        map.addAttribute("list",list);
        map.addAttribute("news",news);
        return "mezx_detail";
    }


    /**
     * 前往报价页面，获取当前页面的物品id
     */
    @RequestMapping("/quote")
    public String toGetQuote(String id, ModelMap map, HttpSession session) {
        String supplierId = microUserService.getUserFromSession(session).getPkSupplier();
        Supplier su=platformService.getSupplierById(supplierId);


        if (su.getSupstateNormal()!=1){
            //供应商不是基础供应商
            return "error_quote";
        }

        fyPurchase purchase = platformService.selectOneById(id);
        if (Integer.parseInt(BuyChannelResource.STRATEGY)==purchase.getBuyChannelId()) {
            //判断供应商是不是战略供应商，不是则跳转报错页面；
            if (su.getSupstateStrategy()!=1){
                return "error_quote";
            }
        }
        if (Integer.parseInt(BuyChannelResource.SINGLESOURCE)==purchase.getBuyChannelId()) {
            //判断供应商是不是单一供应商，不是则跳转；
            String pkSupplier = purchase.getPkSupplier();
            if (pkSupplier==null || !pkSupplier.equals(supplierId)){
                return "error_quote";
            }
        }
        purchase=platformService.ToChineseString(purchase);
        //定义最后要传递到页面的list
        List<PurchaseArray> purList = new ArrayList<>();
        //解析json字段
        PageArray pageArray = JSON.parseObject(purchase.getGoodsDetail(), PageArray.class);
        System.out.println("pagearrayObj:" + pageArray);
        //json字段中的详情集合
        List<PurchaseArray> purchaseArrayList = pageArray.getPurchaseArray();
        System.out.println("purchaseArray:" + purchaseArrayList);
        for (int i = 0; i < purchaseArrayList.size(); i++) {
            PurchaseArray pur = new PurchaseArray();
            pur.setFyPurchaseId(id);
            pur.setMaterialId(purchaseArrayList.get(i).getMaterialId());
            pur.setCount(purchaseArrayList.get(i).getCount());
            pur.setItemName(purchaseArrayList.get(i).getItemName());
            pur.setTypeName(purchaseArrayList.get(i).getTypeName());
            pur.setUnit(purchaseArrayList.get(i).getUnit());
            pur.setNorms(purchaseArrayList.get(i).getNorms());
            //上次价格
            Double avgPrice = platformService.getAvgPrice(supplierId, purchaseArrayList.get(i).getMaterialId());
            pur.setAvgPriceHistory(avgPrice);
            log.info("物品"+pur.getItemName()+"上次报价"+avgPrice);
            purList.add(pur);
        }
        map.addAttribute("purList", purList);
        map.addAttribute("purId", id);
        return "index_detail_quote";
    }

    /**
     * 提交报价信息
     */
    @RequestMapping(value = "/getQuoteFromForm", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult getQuoteFromForm(QuoteModel quotes, HttpSession session) {

        String supplierId = microUserService.getUserFromSession(session).getPkSupplier();


        Salesman salesman = (Salesman) session.getAttribute("user");
        if (salesman==null){
            return ServiceResult.failureMsg("服务器异常，请稍后重试！");
        }
        //判断订单报价状态
        //1.status
        //2.dealTime
        String purId = quotes.getQuotes().get(0).getFyPurchaseId();
        fyPurchase pur=platformService.selectOneById(purId);
        String nowTime = TimeUtil.getNowWitchOutSecond();
        String deadLineTime = pur.getDeadLine();

        long l=0L;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date nowT = sdf.parse(nowTime);
            long nowTimel = nowT.getTime();
            //long nowTimel = sdf.parse(nowTime).getTime();
            log.info(nowTime+"现在时间："+nowTimel);
            //long dealTimeL = sdf.parse(deadLineTime).getTime();
            Date deT = sdf.parse(deadLineTime);
            long dealTimeL = deT.getTime();
            log.info(deadLineTime+"截止时间："+dealTimeL);
            l =dealTimeL- nowTimel;
            log.info("时间结果："+l);
        }catch (Exception e){
            e.printStackTrace();
        }
        int st=Integer.parseInt(pur.getStatus());
        if ( l<0){
            log.info("阻止报价操作：超过订单报价截止日期，");
            log.info("时间："+l);
            return ServiceResult.failureMsg("本次订单报价已经截止了");
        }
        if (st != OrderStatus.waitingQuote){
            log.info("阻止报价操作：不符合订单报价状态");
            return ServiceResult.failureMsg("本次订单报价已经截止了");
        }

        if (quotes == null) {
            return null;
        }

        try{
            platformService.saveQuote(quotes, supplierId, salesman);
        }catch (Exception e){
            return ServiceResult.failureMsg("发生异常,请重新报价");
        }
        //添加报价记录
        platformService.addQuoteCountsByPkMaterialId(quotes.getQuotes().get(0).getFyPurchaseId());
        //return "redirect:/index/findAll";
        return ServiceResult.successMsg("报价成功");

    }


    /**
     * 进度页面上拉刷新
     *
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    @ResponseBody
    public List<fyPurchase> refresh(String buyId, String status, int pageNum, int pageSize, String keyword, String goodsType) {
        System.out.println("刷新页面...");
        if (buyId == null && status == "" && buyId == "") {
            return null;
        }
        List<fyPurchase> fList = null;
        PageBean pb = null;
        if (keyword != null && keyword != "") {
            pb = esService.list(pageNum, pageSize, keyword);
            fList = pb.getList();
        } else {
            fList = platformService.findLIstByStatusAndGoodsTypeAndBuyChannelId(buyId, status, goodsType, pageNum, pageSize);

        }
        log.info("刷新加载的页面list容量："+fList.size());
        return fList;
    }


}

package com.mol.purchase.service.dingding.workBean;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.mol.purchase.config.OrderStatus;
import com.mol.purchase.entity.*;
import com.mol.purchase.entity.activiti.ActHiProcinst;
import com.mol.purchase.entity.activiti.ActReProcdef;
import com.mol.purchase.entity.dingding.login.AppUser;
import com.mol.purchase.entity.dingding.purchase.enquiryPurchaseEntity.PurchaseDetail;
import com.mol.purchase.entity.dingding.purchase.workBench.BigDataStar;
import com.mol.purchase.entity.dingding.purchase.workBench.Ucharts;
import com.mol.purchase.entity.dingding.purchase.workBench.UchartsSeries;
import com.mol.purchase.entity.dingding.purchase.workBench.toBeNegotiated.MaterIdToSupplierId;
import com.mol.purchase.entity.dingding.purchase.workBench.toBeNegotiated.NegotiatIng;
import com.mol.purchase.entity.dingding.purchase.workBench.toBeNegotiated.SupplierIdToExpertId;
import com.mol.purchase.entity.dingding.solr.fyPurchase;
import com.mol.purchase.mapper.newMysql.*;
import com.mol.purchase.mapper.newMysql.dingding.activiti.ActHiProcinstMapper;
import com.mol.purchase.mapper.newMysql.dingding.activiti.ActReProcdefMapper;
import com.mol.purchase.mapper.newMysql.dingding.purchase.BdSupplierMapper;
import com.mol.purchase.mapper.newMysql.dingding.purchase.fyPurchaseDetailMapper;
import com.mol.purchase.mapper.newMysql.dingding.purchase.fyPurchaseMapper;
import com.mol.purchase.mapper.newMysql.dingding.user.AppUserMapper;
import com.mol.purchase.mapper.newMysql.dingding.workBench.PoOrderMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import util.TimeUtil;

import javax.swing.plaf.SeparatorUI;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:TobeNegotiatedService
 * Package:com.purchase.service.dingding.workBean
 * Description
 *      E应用待议价service
 * @date:2019/8/12 13:27
 * @author:yangjiangyan
 */
@Service
@Log
public class TobeNegotiatedService {

    @Autowired
    private fyPurchaseMapper fyPurchaseMapper;
    @Autowired
    private FyQuoteMapper fyQuoteMapper;
    @Autowired
    private BdSupplierMapper bdSupplierMapper;
    @Autowired
    private fyPurchaseDetailMapper fyPurchaseDetailMapper;
    @Autowired
    private AppUserMapper appUserMapper;
    @Autowired
    private PoOrderMapper poOrderMapper;
    @Autowired
    private ExpertRecommendMapper expertRecommendMapper;
    @Autowired
    private ExpertUserMapper expertUserMapper;
    @Autowired
    private ActHiProcinstMapper actHiProcinstMapper;
    @Autowired
    private ActReProcdefMapper actProRecdefMapper;
    @Autowired
    private AppPurchaseApproveMapper appPurchaseApproveMapper;
    @Autowired
    AppOrgBuyChannelApproveMiddleMapper appOrgBuyChannelApproveMiddleMapper;


    public List<String> getListBelongsSupplier(String supplierId) {
        return fyQuoteMapper.getListBySupplier(supplierId);
    }

    public List<fyPurchase> findListByIdListAndStatus(String status, List<String> quoteIdList) {
        return fyPurchaseMapper.findListByIdlistAndStatus(status,null,null,quoteIdList);
    }

    public List<fyPurchase> findListByOrgId(String orgId, String status,String secondStatus,int pageNum,int pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        return fyPurchaseMapper.findListByOrgIdAndStatus(orgId,status,secondStatus);
    }

    public fyPurchase findFypurchaseById(String id) {
        fyPurchase pur = fyPurchaseMapper.findOneById(id);
        return pur;
    }

    public  fyPurchase getStaffName(fyPurchase pur){
        if (pur!=null){
            AppUser appUser = new AppUser();
            appUser.setId(pur.getStaffId());
            appUser= appUserMapper.selectOne(appUser);
            if(appUser!=null){
                pur.setStaffId(appUser.getUserName());
            }
        }
        return pur;
    }

    public Map<String,List> findQuoteById(String id) {
        Map<String,List> quoteMap=new HashMap<>();
        //根据订单id查询报价公司id
        List<String> supplierIdsList=fyQuoteMapper.findFypurchaseIdListById(id);

        //根据订单id，公司id 查询
        for (String supplierId : supplierIdsList) {
            List<FyQuote> fyQuotesList=fyQuoteMapper.findQuoteBySupplierIdAndPurchaseId(id,supplierId);
            //便利fyquotelist获取物料id，查询上次该物料的报价
            for (FyQuote quote : fyQuotesList) {
//                Example e = new Example(FyQuote.class);
//                Example.Criteria criteria = e.createCriteria();
//                criteria.andEqualTo("pkSupplierId",quote.getPkSupplierId());
//                criteria.andEqualTo("pkMaterialId",quote.getPkMaterialId());
//                e.setOrderByClause("creationtime desc");
//                List<FyQuote> quotesList = fyQuoteMapper.selectByExample(e);
                List<BigDataStar> bigDataBySuppliedAndpkMaterialId = fyQuoteMapper.getBigDataBySuppliedAndpkMaterialId(quote.getPkSupplierId(), quote.getPkMaterialId(),quote.getCreationtime());
                log.info("查询到公司"+quote.getPkSupplierId()+"上次对"+quote.getPkMaterialId()+"的报价是list："+bigDataBySuppliedAndpkMaterialId);
                if (bigDataBySuppliedAndpkMaterialId.size()>0){
                    log.info("查询到公司"+quote.getPkSupplierId()+"上次对"+quote.getPkMaterialId()+"的报价是"+bigDataBySuppliedAndpkMaterialId.get(0).getNorigprice());
                    quote.setLastQuotePrice(bigDataBySuppliedAndpkMaterialId.get(0).getNorigprice()+"");
                }
            }
            //根据公司id查询公司名称
            //String supplierName = bdSupplierMapper.getSupplierNameById(supplierId);
            quoteMap.put(supplierId,fyQuotesList);
        }
        return quoteMap;
    }

    public Integer updataAppUserListById(String id, String [] callId) {
        List<AppUser> list= new ArrayList<>();
        for (String dduserId : callId) {
            AppUser appUser= new AppUser();
            appUser.setDdUserId(dduserId);
            appUser= appUserMapper.selectOne(appUser);
            if (appUser!=null){
                list.add(appUser);
            }
        }
        //fyPurchase pur = fyPurchaseMapper.findOneById(id);
        if (list.size()>0){
            String arr = JSON.toJSONString(list);
            log.info("查询到的议价人员："+arr.length());
            return fyPurchaseMapper.updateAppUserListById(id, arr);
        }

        return 0;
    }

    public List<AppUser> findAppUserListById(String id) {
        String appUserString=fyPurchaseMapper.findAppUserListById(id);
//        List<AppUser>list = (List<AppUser>) JSON.parse(appUserString);
        List<AppUser>list= JSON.parseArray(appUserString,AppUser.class);

        return list;
    }

    /**
     * 查询该公司待审核的订单list，选择userid有权限查看的
     * @param orgId
     * @param status
     * @param userId
     * @return
     */
    public List<fyPurchase> findListIfOk(String orgId, String status,String secondStatus, String userId,int pageNum,int pageSize) {

        List<fyPurchase> overList=new ArrayList<>();
        PageHelper.startPage(pageNum,pageSize);
        List<fyPurchase> list = fyPurchaseMapper.findListByOrgIdAndStatus(orgId, status,secondStatus);
        if(list.size()>0){
            for (fyPurchase fyPurchase : list) {
                String negotiatePerson = fyPurchase.getNegotiatePerson();
                if (negotiatePerson!=null&&negotiatePerson.length()>0){
                    List appuserlist=JSON.parseObject(negotiatePerson, List.class);
                    for (int i =0 ;i<appuserlist.size();i++){
                        Object o = appuserlist.get(i);
                        String s = JSON.toJSONString(o);
                        AppUser appUser = JSON.parseObject(s, AppUser.class);
                        if (appUser.getId().equals(userId)){
                            overList.add(fyPurchase);
                        }
                    }
                }
            }
        }

        return overList;
    }

    public Ucharts getBigData(String supplierId, String pkMaterialId,String time) {

        List<BigDataStar> bdList = new ArrayList<>();
        if (supplierId==null){
            log.info("查询ERP");
            //查询ERP数据
            bdList=poOrderMapper.getBigDataBySuppliedAndpkMaterialId(supplierId,pkMaterialId);
            log.info("查询大数据 查询ERP数据物料价格list："+bdList);
        }else {
            //查询历史报价数据
            bdList=fyQuoteMapper.getBigDataBySuppliedAndpkMaterialId(supplierId,pkMaterialId,time);
            log.info("查询大数据 查询该公司"+supplierId+"物料"+pkMaterialId+"价格list："+bdList);
        }
        if (bdList.size()==0){
            return new Ucharts();
        }
        Ucharts u=new Ucharts();
        UchartsSeries us=new UchartsSeries();
        String [] caArr=new String [bdList.size()];
        Double [] daArr= new Double[bdList.size()];
        for(int i =0;i<bdList.size();i++){
            //x轴时间
            caArr[i]=bdList.get(i).getDmakedate().split(" ")[0];
            //价格
            daArr[i]=bdList.get(i).getNorigprice();
            //name
        }
        us.setName(bdList.get(0).getName());
        us.setData(daArr);
        List<UchartsSeries> ulist=new ArrayList<>();
        ulist.add(us);

        u.setCategories(caArr);
        u.setSeries(ulist);

        return u;
    }

    // 保存待议价界面提交的对应数据
    @Transactional
    public void save(NegotiatIng negotiatIng) {
        //订单id
        String purId = negotiatIng.getPurId();
        //负责人说明
        String explain = negotiatIng.getExplain();
        //保存负责人说明
        fyPurchaseMapper.updateExplainById(purId,explain);
        //更改订单状态为等待审核结果
        fyPurchaseMapper.updateStatusById(purId, OrderStatus.END_OF_BARGAINING+"");
        //对应关系
        List<MaterIdToSupplierId> mts = negotiatIng.getMaterIdToSupplierId();

        List<SupplierIdToExpertId> ste = negotiatIng.getSupplierToExpert();

        //订单的所有设置为淘汰
        int refuse=fyQuoteMapper.updataApprovalOverRefuse(OrderStatus.QUOTE_REFUSE+"",TimeUtil.getNow(),OrderStatus.QUOTE_STATUS_NOUSERD+"",purId);
        log.info("设置全部，数量为："+refuse);

        //将返回的物料对应信息保存到报价表
        if(mts!=null && mts.size()>0){
            for (MaterIdToSupplierId mt : mts) {
                fyQuoteMapper.updataApprovalOver(OrderStatus.QUOTE_PASS+"", TimeUtil.getNow(),OrderStatus.QUOTE_STATUS_NOUSERD+"",purId,mt.getMaterId(),mt.getSupplierId());
            }
        }

        //合并
        for(int k=0;k<mts.size();k++){
            String idString="";
            if (ste!=null){
                log.info("订单有专家参与推荐。。。");
                for (int v=0;v<ste.size();v++){
                    if (mts.get(k).getSupplierId().equals(ste.get(v).getSuId())){
                        //专家id 字符串
                        //List<ExpertUser> expertList = ste.get(v).getExpertList();
                        String eId = ste.get(v).getExId();
                        idString+=eId+",";
                    }
                }
            }

            String materId = mts.get(k).getMaterId();
            String supplierId = mts.get(k).getSupplierId();
            //保存物料对应关系
            //根据物料，订单，公司查询报价记录id
            String quoteId=fyQuoteMapper.findIdByPurIdAndPkMatIdAndSupplierId(purId,materId,supplierId);
            //根据订单id，物料id，保存选中的报价id
            fyPurchaseDetailMapper.updataQuoteIdAndExpertIdByPurIdAndPkMatId(purId,materId,quoteId,idString);
        }

    }

    //查询订单详情表中具体物料选中的具体公司
    public List<PurchaseDetail> findFyPurchaseDetailById(String id) {
         return fyPurchaseDetailMapper.findPurchaseDetailList(id);
    }

    //保存待审批界面审批人建议以及订单是否通过审批的状态
    public void saveApproverProlosalAndStatus(String purId, String passOrNot, String approverProposal) {
        fyPurchaseMapper.updateStatusAndApproverProposalById(purId,passOrNot,approverProposal);
    }
    //通过业务id集合 查询待审批订单集合
    public List<fyPurchase> findFyPurchaseByIdArr(List<String> arr) {
         return fyPurchaseMapper.findPurchaseByIdList(arr);
    }

    //查询订单中供应商的推荐专家
    public List<ExpertRecommend> findExpertList(String purId, String supplierId) {
        Example o = new Example(ExpertRecommend.class);
        o.and().andEqualTo("purchaseId",purId).andEqualTo("supplierId",supplierId);
        List<ExpertRecommend> expertRecommends = expertRecommendMapper.selectByExample(o);
        return expertRecommends;
    }

    public List<ExpertUser> findExpertUserList(List<ExpertRecommend> erList) {
        List<ExpertUser> list = new ArrayList<>();
        for (ExpertRecommend er : erList) {
//            ExpertUser t = new ExpertUser();
//            t.setId(er.getExpertId());
            ExpertUser t2 = expertUserMapper.findExpertUserById(er.getExpertId());
            t2.setRecommendReason(er.getRecommendReason());

            list.add(t2);
        }
        return list;
    }

    public List<FyQuote> getFyQuoteByPurIdAndSupplierId(String purId, String supplierId) {
        FyQuote t = new FyQuote();
        t.setFyPurchaseId(purId);
        t.setPkSupplierId(supplierId);
        return fyQuoteMapper.select(t);
    }

    public PurchaseDetail getPurchaseDetailByPurIdAndQuoteId(String purId, List<FyQuote > quote) {
        PurchaseDetail pd=null;
        if (quote!=null &&quote.size()>0){
            for (int i=0;i<quote.size();i++){
                PurchaseDetail t = new PurchaseDetail();
                t.setFyPurchaseId(purId);
                t.setQuoteId(quote.get(i).getId());
                PurchaseDetail purchaseDetail = fyPurchaseDetailMapper.selectOne(t);
                if (purchaseDetail!=null){
                    return purchaseDetail;
                }else {
                    if (i==quote.size()-1){
                        return pd;
                    }
                }
            }
        }else {
            return pd;
        }
        return pd;
    }


    public List<fyPurchase> findListByOrgIdAndBuychannelAndStatus(String orgId, String status,String statusSecond, List<String> buyChannelList,int pageNum,int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return fyPurchaseMapper.findListByOrgIdAndBuychannelAndStatus(orgId,status, statusSecond,buyChannelList);
    }

    public ActHiProcinst findActHiProcinstByPurId(String purId) {
        return actHiProcinstMapper.getProByPurId(purId);
    }

    public ActReProcdef findActReProcdefByActId(String procDefId) {
        return actProRecdefMapper.findActReProcdefByActId(procDefId);
    }

    public AppPurchaseApprove findAppPurchaseApproveByKey(String key) {
        AppPurchaseApprove t =new AppPurchaseApprove();
        t.setPurchaseActiveKey(key);
        return appPurchaseApproveMapper.selectOne(t);
    }

    public AppUser findAppUserById(String appUserId) {
        AppUser t = new AppUser();
        t.setId(appUserId);
        return appUserMapper.selectOne(t);
    }

    public Supplier findSupplierByQuoteId(String quoteId) {
        if (quoteId!=null){
            return bdSupplierMapper.findSupplierByQuoteId(quoteId);
        }
        return null;
    }

    public List<AppOrgBuyChannelApproveMiddle> findAppOrgBuyChannelApproveMiddleByOrgIdAndMainPersonId(String orgId, String userId) {
        List<AppOrgBuyChannelApproveMiddle> appOrgBuyChannelApproveMiddleByOrgIdAndPurchaseMainPersonId = appOrgBuyChannelApproveMiddleMapper.findAppOrgBuyChannelApproveMiddleByOrgIdAndPurchaseMainPersonId(orgId, userId);
        log.info("根据orgID，userID 查询到的appOrgBuyChannelApproveMiddle的list长度："+appOrgBuyChannelApproveMiddleByOrgIdAndPurchaseMainPersonId.size());
        return appOrgBuyChannelApproveMiddleByOrgIdAndPurchaseMainPersonId;
    }

    public List<fyPurchase> findFyPurchaseByBuyChannelAndOrgId(List<String> buychanneList, String orgId,int pageNum,int pageSize) {
//        PageHelper.startPage(pageNum,pageSize);
        Example e =new Example(fyPurchase.class);
        Example.Criteria criteria = e.createCriteria();
//        criteria.andEqualTo("buyChannelId",buyChannelId);
        criteria.andIn("buyChannelId",buychanneList);
        criteria.andEqualTo("orgId",orgId);
        criteria.andGreaterThan("status",OrderStatus.UNDER_REVIEW);
        e.setOrderByClause("create_time desc");
        return fyPurchaseMapper.selectByExample(e);
    }

    public List<fyPurchase> findFyPurchaseByNegotiatePersonNotNullAndOrgId(String orgId) {
        return fyPurchaseMapper.findFyPurchaseByNegotiatePersonNotNullAndOrgId(orgId);
    }

    public List<fyPurchase> findNegotiatePersonNotNullInnerUser(List<fyPurchase> list, String userId) {
        if (list.size()==0){
            return list;
        }
        if (userId==null){
            return null;
        }
        List <fyPurchase> overlist=new ArrayList<>();
        for (fyPurchase pur : list) {
            String negotiatePerson = pur.getNegotiatePerson();
            if (negotiatePerson!=null&&negotiatePerson.length()>0){
                List appuserlist=JSON.parseObject(negotiatePerson, List.class);
                for (int i =0 ;i<appuserlist.size();i++){
                    Object o = appuserlist.get(i);
                    String s = JSON.toJSONString(o);
                    AppUser appUser = JSON.parseObject(s, AppUser.class);
                    if (appUser.getId().equals(userId)){
                        overlist.add(pur);
                    }
                }
            }
        }
        return overlist;
    }

    public List<FyQuote> findSupplierIdByPurId(String id) {
        if (id!=null){
//            Example e = new Example(FyQuote.class);
//            e.and().andEqualTo("fyPurchaseId",id);
//            List<FyQuote> quoteList = fyQuoteMapper.selectByExample(e);

            return fyQuoteMapper.findFyQuoteByPurid(id);

        }
        return null;
    }

    public List<Supplier> findSupplierByQuoteList(List<FyQuote> quoteList) {
        if (quoteList!=null && quoteList.size()>0){
            List<Supplier> supplierList = new ArrayList<>();
            for (FyQuote fyQuote : quoteList) {
                Example t = new Example(Supplier.class);
                t.and().andEqualTo("pkSupplier",fyQuote.getPkSupplierId());
                Supplier supplier = bdSupplierMapper.selectOneByExample(t);
                supplierList.add(supplier);
            }
            return supplierList;
        }
        return null;
    }

    public List<FyQuote> findQuoteByPurIdAndSupplierId(String supplierId, String purId) {
        if (supplierId!=null && purId!=null){
            Example o = new Example(FyQuote.class);
            o.and().andEqualTo("fyPurchaseId",purId).andEqualTo("pkSupplierId",supplierId);
            List<FyQuote> quoteList = fyQuoteMapper.selectByExample(o);
            return quoteList;
        }else {
            return null;
        }
    }
}

package com.mol.purchase.controller.dingding.shcedule;

import com.alipay.api.domain.Sale;
import com.github.pagehelper.PageInfo;
import com.mol.oos.OOSConfig;
import com.mol.purchase.config.OrderStatus;
import com.mol.purchase.entity.*;
import com.mol.purchase.entity.activiti.*;
import com.mol.purchase.entity.dingding.login.AppAuthOrg;
import com.mol.purchase.entity.dingding.login.AppUser;
import com.mol.purchase.entity.dingding.solr.fyPurchase;
import com.mol.purchase.service.dingding.schedule.SchedulerRepairService;
import entity.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:SchedulerRepairController
 * Package:com.purchase.controller.dingding.shcedule
 * Description
 *  E应用进度（第二次更改）
 * @date:2019/9/11 14:42
 * @author:yangjiangyan
 */
@RestController
@CrossOrigin
@RequestMapping("/scheRe")
public class SchedulerRepairController {

    @Autowired
    private SchedulerRepairService schedulerRepairService;

    private static final Logger log= LoggerFactory.getLogger(SchedulerRepairController.class);


    @RequestMapping(value = "/getList",method = RequestMethod.GET)
    public ServiceResult getList(String orgId, String userId,int pageNum,int pageSize){
        List<fyPurchase> purList=schedulerRepairService.getList(orgId,userId,pageNum,pageSize);
        log.info("E应用进度页面 查询List:"+purList);
        return ServiceResult.success(purList);
    }

    @RequestMapping(value = "/getPur",method = RequestMethod.GET)
    public ServiceResult getPur(String id ){
        Map<String,Object> map=new HashMap<>();
        //查询订单
        fyPurchase pur=schedulerRepairService.getPurById(id);
        map.put("pur",pur);
        //获取最终选中的报价
        List<FyQuote> detailList=null;

        //公司的list
        List<Supplier> supplierList=new ArrayList<>();
        if (pur.getStatus().equals(OrderStatus.PASS+"") ){
            detailList=schedulerRepairService.checkQuoteList(id);

            for (int i =0;i<detailList.size();i++){
                if (supplierList.size()>0){
                    for (int j=0;j<supplierList.size();j++){
                        if (supplierList.get(j).getPkSupplier().equals(detailList.get(i).getPkSupplierId())){
                            break;
                        }
                        if (j==supplierList.size()-1){
                            Supplier supplier=schedulerRepairService.getSupplierById(detailList.get(i).getPkSupplierId());
                            supplierList.add(supplier);
                        }
                    }
                }else {
                    Supplier supplier=schedulerRepairService.getSupplierById(detailList.get(i).getPkSupplierId());
                    supplierList.add(supplier);
                }


            }
        }
        map.put("quote",detailList);
        map.put("supplier",supplierList);

        return ServiceResult.success(map);
    }

    @GetMapping("/getComment")
    public ServiceResult getComment(@RequestParam String purId){
        ActHiProcinst pro =schedulerRepairService.getComment(purId);
        if (pro!=null){
            List<ActHiComment> commentList=schedulerRepairService.getuserIdAndCommentByprocInstId(pro.getProcInstId());
            return ServiceResult.success(commentList);
        }else {
            return ServiceResult.failure("还没有进行审批！");
        }
    }

    @GetMapping("/getApproveList")
    public ServiceResult getApproveList(String purId){
        log.info("查询订单审批人，参数purid:"+purId);
        if (purId==null){
            return ServiceResult.failureMsg("订单id传递失败！");
        }

        //查询ActHiProcinst
        ActHiProcinst ahp=schedulerRepairService.findActHiProcinstByBusinessKey(purId);
        //act_hi_varinst  查询结果

        List<ActHiActinst> list=new ArrayList<>();
        List<AppUser> userList=new ArrayList<>();


        list=schedulerRepairService.getActHiActinstByPurId(purId);
        log.info("查询订单的审批人员id："+list);
        userList=schedulerRepairService.getAppUserByList(list);

        ActHiVarinst ahv=schedulerRepairService.findActHiVarinstByProcInstId(ahp.getProcInstId());

        if (userList==null){
            log.info("E应用查看的订单还没有进入审批流程，暂无审批人员");
            return ServiceResult.success(userList);
        }
        if (ahv==null){
            log.info("act_hi_varinst  查询结果为null,订单开始审批，首位审批人员还未操作");
            return ServiceResult.success(userList);
        }
        if (ahv!=null && "pass".equals(ahv.getText())){
            //分两种，一种是中途，一种是结束
//            ActReProcdef arp=schedulerRepairService.findActReProcdefById(ahp.getProcDefId());
//            AppPurchaseApprove apa=schedulerRepairService.findAppPurchaseApproveByKey(arp.getKey());
//            int length = apa.getPurchaseApproveList().split(",").length;
//            if (userList.size()==length){
//
//            }
            fyPurchase pur = schedulerRepairService.getPurById(purId);
            if (OrderStatus.PASS==Integer.parseInt(pur.getStatus())){
                log.info("订单状态已修改为通过，所有审批人员全部操作完成");
                for (AppUser appUser : userList) {
                    appUser.setIfPass(true);
                }
            }else {
                log.info("订单状态未修改为通过，部分审批人员没有操作完成");
                for(int i=0;i<userList.size();i++){
                    if (i!=userList.size()-1){
                        userList.get(i).setIfPass(true);
                    }
                }
            }
        }else {
            log.info("订单已被拒绝");
            for (int i=0;i<userList.size();i++){
                if (i==userList.size()-1){
                    userList.get(i).setIfPass(false);
                }else {
                    userList.get(i).setIfPass(true);
                }
            }
        }
        return ServiceResult.success(userList);


    }

    /**
     * 联系供应商
     * @param purId
     * @param supplierId
     * @return
     */
    @GetMapping("/getSaleMan")
    public ServiceResult getSaleman(String purId,String supplierId){
        if (purId ==null || supplierId==null){
            log.info("联系供应商 参数异常");
            return ServiceResult.failure("查询失败！");
        }
        String id =schedulerRepairService.getSalemanId(purId,supplierId);
        log.info("联系供应商  查询到报价人员id"+id);
        SupplierSalesman man=schedulerRepairService.getSaleManById(id);
        log.info("联系供应商  查询到报价人员信息"+man);
        return ServiceResult.success(man);
    }

    /**
     * 供应商支付专家推荐费用情况
     * @param supplierId
     * @param purId
     * @return
     */
    @GetMapping("/getPayresult")
    public ServiceResult getPayR(String supplierId,String purId){
        QuotePayresult qp=schedulerRepairService.getPayExpertResult(supplierId,purId);
        log.info("查询供应商支付订单前期费用 :"+qp);
        if (qp ==null){
            log.info("查询供应商支付订单前期费用 信息为空");
            return ServiceResult.failureMsg("查询失败！");
        }
        return ServiceResult.success(qp);
    }

    /**
     * 保存 合同图片
     * @param file 图片
     * @param purId 订单Id
     * @param orgId 公司id
     * @return
     */
    @PostMapping("/upLoadContractPictures")
    public ServiceResult upLoadContractPictures(@RequestParam("file") MultipartFile file, String purId,String orgId){

        if (file==null){
            return ServiceResult.failureMsg("文件接收失败");
        }
        schedulerRepairService.upload(file,orgId,purId);
        return ServiceResult.successMsg("上传成功");
    }

    /**
     * 下载合同
     * @param bucket 桶名称
     * @param key   文件名称
     * @return
     */
    @GetMapping("/downLoadContractPictures")
    public ServiceResult downLoadOOSConTractPictures(String bucket,String key){

        schedulerRepairService.downFromOOSImg(bucket,key);
        return null;
    }
}

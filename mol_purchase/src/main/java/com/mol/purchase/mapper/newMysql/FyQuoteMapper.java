package com.mol.purchase.mapper.newMysql;

import com.mol.base.BaseMapper;
import com.mol.purchase.entity.FyQuote;
import com.mol.purchase.entity.dingding.purchase.workBench.BigDataStar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * ClassName:FyQuoteMapper
 * Package:com.purchase.mapper.newMysql.third
 * Description
 *  报价表
 * @date:2019/8/1 9:37
 * @author:yangjiangyan
 */
@Mapper
public interface FyQuoteMapper extends BaseMapper<FyQuote> {

    //保存报价信息
    void saveQuote(FyQuote fyQuote);

    //获取公司参与的订单
    List<String> getListBySupplier(String supplierId);

    List<String> findFypurchaseIdListById(String id);

    List<FyQuote> findQuoteBySupplierIdAndPurchaseId(String id, String supplierId);

    String findIdByPurIdAndPkMatIdAndSupplierId(String purId, String materId, String supplierId);

    List<String> findFypurchaseIdListBySupplierId(String id);

    FyQuote findQuoteByid(String quoteId);

    List<FyQuote> findSupplierIdListByPurId(String id);

    List<BigDataStar> getBigDataBySuppliedAndpkMaterialId(String supplierId,String pkMaterialId,String time);

    List<FyQuote> findQuteByPurIdAndIdNotEuqal(@Param("id") String purId,@Param("list") Set<String> supplierSet);

    int updataApprovalOverRefuse(String quoteStatus, String time, String quoteStatusUesdOrNo, String purId);

    int updataApprovalOver(String quoteStatus, String time, String quoteStatusUesdOrNo, String purId, String materId, String supplierId);

    void updataApprovalStatusEnableByPurId(String purId, String time, String quoteStatusUesdOrNo);

    void updataApprovalOverStatusAndTimeAndEnableByPurId(String quoteStatus, String time, String quoteStatusUesdOrNo);

    List<FyQuote> findFyQuoteByPurid(String purId);
}

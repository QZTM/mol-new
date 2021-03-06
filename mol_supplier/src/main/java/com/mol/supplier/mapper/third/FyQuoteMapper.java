package com.mol.supplier.mapper.third;

import com.mol.base.BaseMapper;
import com.mol.supplier.entity.thirdPlatform.FyQuote;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName:FyQuoteMapper
 * Package:com.mol.supplier.mapper.newMysql.third
 * Description
 *  报价表
 * @date:2019/8/1 9:37
 * @author:yangjiangyan
 */
@Mapper
public interface FyQuoteMapper extends BaseMapper<FyQuote> {

    //保存报价信息
    //void saveQuote(FyQuote fyQuote);

    //获取公司参与的订单
    List<String> getListBySupplier(String supplierId);

    List<String> findFypurchaseIdListById(String id);

    List<FyQuote> findQuoteBySupplierIdAndPurchaseId(String id, String supplierId);

    String findIdByPurIdAndPkMatIdAndSupplierId(String purId, String materId, String supplierId);

    List<String> findFypurchaseIdListBySupplierId(String id);

    FyQuote findQuoteByid(String quoteId);

    List<FyQuote> findPassQuoteByPurId(String id);

    int findCountWinningKidSupplierByPurId(String id);

    List<FyQuote> findQuoteBySupplierIdAndApprovalOverStatus(String supplierId, String status);

    List<FyQuote> findPurAllRefuseBySupplierIdAndStatus(String supplierId, String status);

    List<FyQuote> findPriceBySupplierIdAndMaterialId(String supplierId, String materId);
}

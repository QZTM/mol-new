package com.mol.quartz.mapper;

import com.mol.base.BaseMapper;
import com.mol.quartz.entity.Purchase;

import java.util.Map;

public interface PurchaseMapper extends BaseMapper<Purchase> {

    public Integer compareQuoteSellerNumAndSellerCountById(String id);

    String getPurchaseMainPersonDDIdByOrgAndChannel(Map paraMap);

}

package com.mol.purchase.service.dingding.my;

import com.github.pagehelper.PageHelper;
import com.mol.purchase.entity.dingding.solr.fyPurchase;
import com.mol.purchase.mapper.newMysql.dingding.purchase.fyPurchaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyAppService {

    @Autowired
    fyPurchaseMapper purchaseMapper;

    public List<fyPurchase> findPurListByOrgIdAndUserIdAndBetweenStatusAndStatusScond(String orgId, String userId, String status, String status_second, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return purchaseMapper.findListByOrgIdAndUserIdAndStatus(orgId,userId,status,status_second);
    }
}

package com.mol.purchase.service.dingding.winningannouncement;

import com.github.pagehelper.PageHelper;
import com.mol.purchase.entity.dingding.solr.fyPurchase;
import com.mol.purchase.mapper.newMysql.dingding.purchase.fyPurchaseDetailMapper;
import com.mol.purchase.mapper.newMysql.dingding.purchase.fyPurchaseMapper;
import org.apache.commons.collections.iterators.CollatingIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class WinnAnnouncementService {

    @Autowired
    fyPurchaseMapper purchaseMapper;
    @Autowired
    fyPurchaseDetailMapper purchaseDetailMapper;

    public List<fyPurchase> getWinAnnounceList(String orgId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Example e = new Example(fyPurchase.class);
        Example.Criteria criteria = e.createCriteria();
        criteria.andEqualTo("orgId",orgId);
        criteria.andBetween("status",7,8);
        e.setOrderByClause("approval_end_time desc");
        return purchaseMapper.selectByExample(e);
    }

    public List<fyPurchase> findPassSupplierCountOfPassPur(List<fyPurchase> list) {
        if (list!=null && list.size()>0){
            for (fyPurchase purchase : list) {
                int i=purchaseDetailMapper.findPassSupplierOfPassPurByPurId(purchase.getId());
                purchase.setPassSupplierCount(i);
            }
        }
        return list;
    }
}

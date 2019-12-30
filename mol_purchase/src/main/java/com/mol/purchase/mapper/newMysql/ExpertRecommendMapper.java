package com.mol.purchase.mapper.newMysql;

import com.mol.base.BaseMapper;
import com.mol.purchase.entity.ExpertRecommend;
import com.mol.purchase.entity.dingding.solr.fyPurchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ExpertRecommendMapper extends BaseMapper<ExpertRecommend> {
    void updataAdoptByPurIdAndExpertId(String purId, String expertId);

    void updataAdoptNotChecked(String purId);

    Integer updataComissionMoneyByPurIdAndExpertId(String moneyOne, String nowTime, String purId, String expertId);

    List<ExpertRecommend> findExpertRecommendByPurIdAndExpertIdNotIn(@Param("id") String purId,@Param("list") Set<String> expetSet);
}

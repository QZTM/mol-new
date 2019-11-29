package com.mol.expert.mapper.newMysql.third;

import com.mol.expert.entity.dingding.solr.fyPurchase;
import com.mol.expert.entity.thirdPlatform.Enter;
import com.mol.expert.entity.thirdPlatform.Lunbo;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface TpMapper {

    List<Enter> findAll();
    List<Lunbo> findLunBo();
    //询价采购
    List<fyPurchase> findList(String id);
    //查询数据数量
    int findCount(String id);
    //查询采购类型的数量
    List<String> findTypeList();
    //id查询单个记录
    fyPurchase selectOneById(String id);
}

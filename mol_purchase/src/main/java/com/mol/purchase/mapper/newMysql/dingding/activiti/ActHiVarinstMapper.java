package com.mol.purchase.mapper.newMysql.dingding.activiti;

import com.mol.purchase.entity.activiti.ActHiVarinst;

public interface ActHiVarinstMapper {
    ActHiVarinst findActHiVarinstByProcInstId(String procInstId);
}

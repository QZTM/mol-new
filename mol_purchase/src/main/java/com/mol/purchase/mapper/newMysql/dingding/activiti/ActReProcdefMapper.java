package com.mol.purchase.mapper.newMysql.dingding.activiti;

import com.mol.base.BaseMapper;
import com.mol.purchase.entity.activiti.ActReProcdef;

public interface ActReProcdefMapper extends BaseMapper<ActReProcdef> {
    ActReProcdef findActReProcdefByActId(String procDefId);
}

package com.mol.quartz.mapper;

import com.mol.base.BaseMapper;
import com.mol.quartz.entity.ExpertUser;

import java.util.List;

public interface ExpertUserMapper extends BaseMapper<ExpertUser> {

    List<String> getExpertDDIdListByMarId(String marId);

}

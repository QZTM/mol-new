package com.mol.purchase.mapper.newMysql;

import com.mol.base.BaseMapper;
import com.mol.purchase.entity.AppOrgBuyChannelApproveMiddle;

import java.util.List;

public interface AppOrgBuyChannelApproveMiddleMapper extends BaseMapper<AppOrgBuyChannelApproveMiddle> {
    List<AppOrgBuyChannelApproveMiddle> findAppOrgBuyChannelApproveMiddleByOrgIdAndPurchaseMainPersonId(String orgId, String userId);
}

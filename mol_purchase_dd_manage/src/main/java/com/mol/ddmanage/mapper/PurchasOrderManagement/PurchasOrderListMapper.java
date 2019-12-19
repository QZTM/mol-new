package com.mol.ddmanage.mapper.PurchasOrderManagement;

import com.mol.ddmanage.Ben.PurchasOrderManagement.PurchasOrderListben;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

public interface PurchasOrderListMapper
{
    ArrayList<PurchasOrderListben> PurchasOrderListShow(@Param(value = "AuthorityStatus") String AuthorityStatus, @Param(value = "userid") String userid,@Param(value = "buy_channel_id") String buy_channel_id);
}

package com.mol.purchase.entity;

import lombok.Data;

import javax.persistence.Id;

@Data
public class AppOrgBuyChannelApproveMiddle {

    @Id
    private String id;
    private String appOrgId;
    private String buyChannelId;
    private String purchaseApproveId;

}

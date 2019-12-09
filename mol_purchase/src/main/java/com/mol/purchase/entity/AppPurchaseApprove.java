package com.mol.purchase.entity;

import lombok.Data;

import javax.persistence.Id;

@Data
public class AppPurchaseApprove {
    @Id
    private String  id;
    private String purchaseMainPerson;
    private String purchaseApproveLeader;
    private String purchaseApproveList;
    private String purchaseActiveKey;
}

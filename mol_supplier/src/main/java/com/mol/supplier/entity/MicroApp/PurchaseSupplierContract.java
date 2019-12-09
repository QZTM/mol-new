package com.mol.supplier.entity.MicroApp;

import lombok.Data;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name = "fy_purchase_supplier_contract")
public class PurchaseSupplierContract implements Serializable {

    public static final String 采购方供应商都未签署 = "1";
    public static final String 采购方已签署供应商未签署 = "2";
    public static final String 采购方供应商都已签署 = "3";
    public static final String 合同已归档 = "9";

    @Id
    private String id;
    private String purchaseId;
    private String signStatus;
    private String startSignTime;
    private String createTime;
    private String templateId;
    private String contractId;
    private String supplierId;
}

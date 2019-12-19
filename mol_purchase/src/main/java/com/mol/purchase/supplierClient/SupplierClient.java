package com.mol.purchase.supplierClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "mol-supplier")
public interface SupplierClient {

    @GetMapping(value = "/molsupplier/getToken", produces = { "application/json;charset=UTF-8" })
    public String getToken();
}

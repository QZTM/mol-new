package com.mol.ddmanage.Ben.Workench;


import java.util.ArrayList;

public class GetNeedQuoteAlertben
{
     private String purchaseId;//采购id
     private String supplierId;//供应商id
     private String pkMaterialId;//物料id
     private String quote;//本次报价
     private String last_quote;//上一次报价
    private String material_name;//物料名称
    private String supplier_name;//供应商名称
     private String quote_difference;//报价差
    private String creationtime;//报价时间
    private String floating_ratio;//上浮比例
    private String reason;//高于报价的原因

    private ArrayList<String> times;//X轴时间字符传
    private ArrayList<Double> quotes;//物料的历史价格


    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getPkMaterialId() {
        return pkMaterialId;
    }

    public void setPkMaterialId(String pkMaterialId) {
        this.pkMaterialId = pkMaterialId;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getLast_quote() {
        return last_quote;
    }

    public void setLast_quote(String last_quote) {
        this.last_quote = last_quote;
    }

    public String getQuote_difference() {
        return quote_difference;
    }

    public void setQuote_difference(String quote_difference) {
        this.quote_difference = quote_difference;
    }

    public String getFloating_ratio() {
        return floating_ratio;
    }

    public void setFloating_ratio(String floating_ratio) {
        this.floating_ratio = floating_ratio;
    }

    public String getCreationtime() {
        return creationtime;
    }

    public void setCreationtime(String creationtime) {
        this.creationtime = creationtime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMaterial_name() {
        return material_name;
    }

    public void setMaterial_name(String material_name) {
        this.material_name = material_name;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public ArrayList<String> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<String> times) {
        this.times = times;
    }

    public ArrayList<Double> getQuotes() {
        return quotes;
    }

    public void setQuotes(ArrayList<Double> quotes) {
        this.quotes = quotes;
    }
}

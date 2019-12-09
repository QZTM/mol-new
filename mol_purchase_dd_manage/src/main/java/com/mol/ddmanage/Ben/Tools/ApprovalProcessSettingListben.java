package com.mol.ddmanage.Ben.Tools;

public class ApprovalProcessSettingListben //审批流实体类
{
    private String id;//审批id
   private String status;//是否开启
   private String amountMin;//金额小
   private String amountMax;//金额大
   private String name;//审批类型
     private String approval;//审批人员
    private String unlock;//解锁人员
    private String person;//抄送人员



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmountMin() {
        return amountMin;
    }

    public void setAmountMin(String amountMin) {
        this.amountMin = amountMin;
    }

    public String getAmountMax() {
        return amountMax;
    }

    public void setAmountMax(String amountMax) {
        this.amountMax = amountMax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getUnlock() {
        return unlock;
    }

    public void setUnlock(String unlock) {
        this.unlock = unlock;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

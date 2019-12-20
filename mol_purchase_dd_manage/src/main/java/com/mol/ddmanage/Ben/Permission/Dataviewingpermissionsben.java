package com.mol.ddmanage.Ben.Permission;

public class Dataviewingpermissionsben
{
    private String dingding_id;
    private String app_userid;
    private String AuthorityStatus;//0部门都能访问 ，1只能访问个人数据

    public String getDingding_id() {
        return dingding_id;
    }

    public void setDingding_id(String dingding_id) {
        this.dingding_id = dingding_id;
    }

    public String getApp_userid() {
        return app_userid;
    }

    public void setApp_userid(String app_userid) {
        this.app_userid = app_userid;
    }

    public String getAuthorityStatus() {
        return AuthorityStatus;
    }

    public void setAuthorityStatus(String authorityStatus) {
        AuthorityStatus = authorityStatus;
    }
}

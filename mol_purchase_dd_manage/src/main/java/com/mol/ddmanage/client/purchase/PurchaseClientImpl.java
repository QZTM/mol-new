package com.mol.ddmanage.client.purchase;

import lombok.extern.java.Log;

@Log
public class PurchaseClientImpl implements PurchaseClient {

    @Override
    public String deploy(String name, String processId, String processName, String orgId, String buyChannelId) {
        throw new RuntimeException("调用采购端部署工作流出现了异常！name:"+name+",processId:"+processId+",processName:"+processName+",orgId:"+orgId+",buyChannelId:"+buyChannelId);
    }
}

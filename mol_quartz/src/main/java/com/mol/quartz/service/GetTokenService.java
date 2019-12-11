package com.mol.quartz.service;

import com.mol.quartz.eurekaexpertclient.ExpertClient;
import com.mol.quartz.eurekapurchaseclient.PurchaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;

@Service
public class GetTokenService {

    @Autowired
    private PurchaseClient purchaseClient;

    @Autowired
    private ExpertClient expertClient;

    @Async
    public ListenableFuture<String> getPurchaseToken(){
        return new AsyncResult<>(purchaseClient.getToken());
    }

    @Async
    public ListenableFuture<String> getExpertToken(){
        return new AsyncResult<>(expertClient.getToken());
    }



}

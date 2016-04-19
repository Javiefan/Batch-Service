package com.bwts.batchservice.service;

public abstract class BatchCallback {

    public void exceedMaxRetry(){};

    public void afterRetry(){};
}

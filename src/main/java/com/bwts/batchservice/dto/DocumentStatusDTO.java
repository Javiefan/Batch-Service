package com.bwts.batchservice.dto;

import java.util.Date;
import java.util.UUID;

public class DocumentStatusDTO {
    private UUID documentId;
    private UUID tenantId;
    private int currentStatus;
    private Date updateTime;
    private String ublData;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public String getUblData() {
        return ublData;
    }

    public void setUblData(String ublData) {
        this.ublData = ublData;
    }
}

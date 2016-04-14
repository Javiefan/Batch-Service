package com.bwts.batchservice.dto;


import com.bwts.batchservice.entity.DocumentStatus;

import java.util.Date;
import java.util.UUID;

public class DocumentStatusDTO {
    private UUID documentId;
    private UUID tenantId;
    private DocumentStatus flowStatus;
    private Date updateTime;
    private String ublData;

    public Date getUpdateTime() {
        return new Date(updateTime.getTime());
    }

    public void setUpdateTime(Date updateTime) {
        if(updateTime == null) {
            this.updateTime = new Date();
        } else {
            this.updateTime = new Date(updateTime.getTime());
        }
    }

    public DocumentStatus getFlowStatus() {
        return flowStatus;
    }

    public void setFlowStatus(DocumentStatus flowStatus) {
        this.flowStatus = flowStatus;
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

    @Override
    public int hashCode() {
        return documentId.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if(object == null || !(object instanceof DocumentStatusDTO)) {
            return false;
        }

        DocumentStatusDTO documentStatusDTO = (DocumentStatusDTO) object;
        if(documentStatusDTO.getDocumentId() == null) {
            return false;
        }
        return documentStatusDTO.getDocumentId().equals(documentId);
    }
}
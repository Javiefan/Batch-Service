package com.bwts.batchservice.dto;


import java.util.Date;
import java.util.UUID;

public class DocumentStatusDTO {
    private String resourceId;
    private UUID tenantId;
    private String resourceType;
    private String status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String flowStatus) {
        this.status = flowStatus;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public int hashCode() {
        return resourceId.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if(object == null || !(object instanceof DocumentStatusDTO)) {
            return false;
        }

        DocumentStatusDTO documentStatusDTO = (DocumentStatusDTO) object;
        if(documentStatusDTO.getResourceId() == null) {
            return false;
        }
        return documentStatusDTO.getResourceId().equals(resourceId);
    }
}
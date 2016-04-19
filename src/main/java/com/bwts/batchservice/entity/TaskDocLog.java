package com.bwts.batchservice.entity;

import java.util.Date;
import java.util.UUID;

public class TaskDocLog {
    private Long id;

    private UUID tenantId;

    private String resourceId;

    private String resourceType;

    private String phase;

    private Integer retryTime;

    private String actionResult;

    private String message;

    private Date actionTimestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase == null ? null : phase.trim();
    }

    public Integer getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(Integer retryTime) {
        this.retryTime = retryTime;
    }

    public String getActionResult() {
        return actionResult;
    }

    public void setActionResult(String actionResult) {
        this.actionResult = actionResult == null ? null : actionResult.trim();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public Date getActionTimestamp() {
        return new Date(actionTimestamp.getTime());
    }

    public void setActionTimestamp(Date actionTimestamp) {
        this.actionTimestamp = new Date(actionTimestamp.getTime());
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}

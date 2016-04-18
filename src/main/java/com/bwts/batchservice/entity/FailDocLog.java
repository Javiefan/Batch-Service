package com.bwts.batchservice.entity;

import java.util.Date;
import java.util.UUID;

public class FailDocLog {
    private Long id;

    private UUID tenantId;

    private UUID resourceId;

    private String resoureType;

    private String phase;

    private Integer taskId;

    private String message;

    private Date failTimestamp;

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

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase == null ? null : phase.trim();
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public Date getFailTimestamp() {
        return new Date(failTimestamp.getTime());
    }

    public void setFailTimestamp(Date failTimestamp) {
        this.failTimestamp = new Date(failTimestamp.getTime());
    }

    public String getType() {
        return resoureType;
    }

    public void setType(String resoureType) {
        this.resoureType = resoureType;
    }
}

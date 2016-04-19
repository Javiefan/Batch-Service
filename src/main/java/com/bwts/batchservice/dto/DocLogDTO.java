package com.bwts.batchservice.dto;

import scala.util.parsing.combinator.testing.Str;

import java.util.Date;
import java.util.UUID;

public class DocLogDTO {
    private UUID tenantId;

    private String resourceId;

    private String resourceType;

    private String phase;

    private String payload;

    private String actionResult;

    private Date throwTime;

    private int retryTimes;

    private String message;

    private Integer taskId;

    public UUID getTenantId() {
        return tenantId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getPhase() {
        return phase;
    }

    public String getPayload() {
        return payload;
    }

    public String getMessage() {
        return message;
    }

    public String getActionResult() {
        return actionResult;
    }

    public Date getThrowTime() {
        return new Date(throwTime.getTime());
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setActionResult(String actionResult) {
        this.actionResult = actionResult;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public void setThrowTime(Date throwTime) {
        this.throwTime = new Date(throwTime.getTime());
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public static class Builder {
        private DocLogDTO docLogDTO = new DocLogDTO();

        public Builder setTenantId(UUID tenantId) {
            docLogDTO.setTenantId(tenantId);
            return this;
        }

        public Builder setResourceId(String resourceId) {
            docLogDTO.setResourceId(resourceId);
            return this;
        }

        public Builder setPhase(String phase) {
            docLogDTO.setPhase(phase);
            return this;
        }

        public Builder setPayload(String payload) {
            docLogDTO.setPayload(payload);
            return this;
        }

        public Builder setActionResult(String actionResult) {
            docLogDTO.setActionResult(actionResult);
            return this;
        }

        public Builder setRetryTimes(int retryTimes) {
            docLogDTO.setRetryTimes(retryTimes);
            return this;
        }

        public Builder setThrowTime(Date throwTime) {
            docLogDTO.setThrowTime(throwTime);
            return this;
        }

        public Builder setMessage(String message) {
            docLogDTO.setMessage(message);
            return this;
        }

        public Builder setTaskId(Integer taskId) {
            docLogDTO.setTaskId(taskId);
            return this;
        }

        public Builder setResourceType(String resourceType) {
            docLogDTO.setResourceType(resourceType);
            return  this;
        }

        public DocLogDTO build() {
            return docLogDTO;
        }
    }
}

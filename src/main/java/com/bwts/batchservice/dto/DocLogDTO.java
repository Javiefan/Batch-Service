package com.bwts.batchservice.dto;

import java.util.Date;
import java.util.UUID;

public class DocLogDTO {
    private UUID tenantId;

    private UUID documentId;

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

    public UUID getDocumentId() {
        return documentId;
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
        return throwTime;
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

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
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
        this.throwTime = throwTime;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public static class Builder {
        private DocLogDTO docLogDTO = new DocLogDTO();

        public Builder setTenantId(UUID tenantId) {
            docLogDTO.setTenantId(tenantId);
            return this;
        }

        public Builder setDocumentId(UUID documentId) {
            docLogDTO.setDocumentId(documentId);
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

        public DocLogDTO build() {
            return docLogDTO;
        }
    }
}

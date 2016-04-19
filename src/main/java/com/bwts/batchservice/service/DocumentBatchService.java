package com.bwts.batchservice.service;


import com.bwts.batchservice.constants.StatusCode;
import com.bwts.batchservice.dto.DocLogDTO;
import com.bwts.batchservice.dto.DocumentStatusDTO;
import com.bwts.batchservice.dto.DocumentStatusList;
import com.bwts.batchservice.dto.DocumentStatusListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class DocumentBatchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentBatchService.class);

    @Autowired
    private RemoteDocumentService documentService;
    @Autowired
    private BatchService batchService;

    private static String OWNER_SENDER = "SENDER";
    private static String OWNER_RECEIVER = "RECEIVER";

    @Value("${page.size}")
    private int pageSize;

    public void processFailedDocumentForSender() {
        int pageNum = 1;
        while (true) {
            DocumentStatusListDTO documentStatusListDTO = documentService.getFailedJobList(OWNER_SENDER, pageNum++, pageSize);
            DocumentStatusList senderStatus = documentService.retryDocument(OWNER_SENDER, documentStatusListDTO);

            if (documentStatusListDTO == null || documentStatusListDTO.getTotalCount() <= 0 || documentStatusListDTO.getItems() == null) {
                return;
            }
            String type = documentStatusListDTO.getItems().get(0).getResourceType();

            for (DocumentStatusDTO status : senderStatus.getItems()) {
                status.setResourceType(type);
                final DocLogDTO docLogDTO = convertToDocLogDTO(status);

                batchService.processTask(docLogDTO, new BatchCallback() {
                    @Override
                    public void exceedMaxRetry() {
                        documentService.updateDocumentStatus(status, StatusCode.RETRY_FAILED);
                    }
                });
            }
        }
    }

    public void processFailedDocumentForReceiver() {
        int pageNum = 1;
        while (true) {
            DocumentStatusListDTO documentStatusListDTO = documentService.getFailedJobList(OWNER_RECEIVER, pageNum++, pageSize);
            DocumentStatusList senderStatus = documentService.retryDocument(OWNER_RECEIVER, documentStatusListDTO);

            if (documentStatusListDTO == null || documentStatusListDTO.getTotalCount() <= 0 || documentStatusListDTO.getItems() == null) {
                return;
            }
            String type = documentStatusListDTO.getItems().get(0).getResourceType();

            for (DocumentStatusDTO status : senderStatus.getItems()) {
                status.setResourceType(type);
                final DocLogDTO docLogDTO = convertToDocLogDTO(status);

                batchService.processTask(docLogDTO, new BatchCallback() {
                    @Override
                    public void exceedMaxRetry() {
                        documentService.updateDocumentStatus(status, StatusCode.MIGRATION_RETRY_FAILED);
                    }
                });
            }
        }
    }

    private DocLogDTO convertToDocLogDTO(DocumentStatusDTO status) {
        return new DocLogDTO.Builder()
                            .withTenantId(status.getTenantId())
                            .withResourceId(status.getResourceId())
                            .withResourceType(status.getResourceType())
                            .withPayload(status.getUblData())
                            .withThrowTime(new Date())
                            .withActionResult(status.getStatus().toString())
                            .withPhase("Unknown")
                            .build();
    }

    public void updateDocumentStatistics() {
        documentService.updateDocumentStatistics();
    }

}

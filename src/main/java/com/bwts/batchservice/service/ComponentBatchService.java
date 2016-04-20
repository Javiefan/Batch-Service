package com.bwts.batchservice.service;


import com.bwts.batchservice.constants.StatusCode;
import com.bwts.batchservice.dto.DocLogDTO;
import com.bwts.batchservice.dto.DocumentStatusDTO;
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
public class ComponentBatchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentBatchService.class);

    @Autowired
    private RemoteComponentService componentService;
    @Autowired
    private BatchService batchService;

    @Value("${batch.page.size}")
    private int pageSize;

    public void processFailedComponent() {
        int pageNum = 1;
        while (true) {
            DocumentStatusListDTO documentStatusListDTO = componentService.getNotBackupedComponents(pageNum++, pageSize);
            componentService.retryComponent(documentStatusListDTO);

            if (documentStatusListDTO == null || documentStatusListDTO.getTotalCount() <= 0 || documentStatusListDTO.getItems().size() == 0) {
                return;
            }
            String type = documentStatusListDTO.getItems().get(0).getResourceType();

            for (DocumentStatusDTO status : documentStatusListDTO.getItems()) {
                status.setResourceType(type);
                final DocLogDTO docLogDTO = convertToDocLogDTO(status);

                batchService.processTask(docLogDTO, new BatchCallback() {
                    @Override
                    public void exceedMaxRetry() {
                        componentService.updateComponentStatus(status, StatusCode.RETRY_FAILED);
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
                .withActionResult("Not Backup")
                .withPhase("Unknown")
                .build();
    }

}

package com.bwts.batchservice.service;


import com.bwts.batchservice.constants.StatusCode;
import com.bwts.batchservice.dto.DocumentStatusDTO;
import com.bwts.batchservice.dto.DocumentStatusListDTO;
import com.bwts.common.rest.client.RestClient;
import com.bwts.common.security.DefaultTenants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

@Service
@Transactional
public class RemoteComponentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteComponentService.class);

    private static final String HEADER_USER_ID = "X-BWTS-UserId";
    private static final String HEADER_TENANT_ID = "X-BWTS-TenantId";

    @Autowired
    private RestClient.DocumentApiRestClient documentApiRestClient;


    public DocumentStatusListDTO getNotBackupedComponents (int pageNum, int pageSize) {
        return documentApiRestClient.getResource()
                .path("components/notBackuped")
                .queryParam("pageNum", String.valueOf(pageNum))
                .queryParam("pageSize", String.valueOf(pageSize))
                .request()
                .header(HEADER_USER_ID, DefaultTenants.SYSTEM_USER.toString())
                .header(HEADER_TENANT_ID, DefaultTenants.SUPER_ADMIN_TENANT.toString())
                .get(DocumentStatusListDTO.class);
    }

    public void retryComponent(DocumentStatusListDTO documentStatusListDTO) {
        if (documentStatusListDTO == null) {
            LOGGER.info("documentStatusListDTO is illegal");
            return;
        }

        LOGGER.info("Begin retry components, the size is {}", documentStatusListDTO.getItems().size());

        JSONArray items = new JSONArray();
        for (DocumentStatusDTO documentStatusDTO : documentStatusListDTO.getItems()) {
            items.add(documentStatusDTO.getResourceId());
        }
        Entity<JSONArray> entity = Entity.entity(items, MediaType.APPLICATION_JSON);
        documentApiRestClient.getResource()
                .path("components/backup")
                .request()
                .header(HEADER_USER_ID, DefaultTenants.SYSTEM_USER.toString())
                .header(HEADER_TENANT_ID, DefaultTenants.SUPER_ADMIN_TENANT.toString())
                .post(entity);
    }

    public void updateComponentStatus(DocumentStatusDTO documentStatusDTO, String Status) {
        Entity<String> entity = Entity.entity(StatusCode.RETRY_FAILED.toString(), MediaType.TEXT_PLAIN);
        documentApiRestClient.getResource()
                .path("components")
                .path(documentStatusDTO.getResourceId().toString())
                .path("backupStatus")
                .request()
                .header(HEADER_USER_ID, DefaultTenants.SYSTEM_USER.toString())
                .header(HEADER_TENANT_ID, DefaultTenants.SUPER_ADMIN_TENANT.toString())
                .put(entity);
    }

}

package com.bwts.batchservice.service;


import com.bwts.batchservice.constants.StatusCode;
import com.bwts.batchservice.dto.DocumentStatusDTO;
import com.bwts.batchservice.dto.DocumentStatusList;
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
import java.util.Date;

@Service
@Transactional
public class RemoteDocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDocumentService.class);

    private static final String HEADER_USER_ID = "X-BWTS-UserId";
    private static final String HEADER_TENANT_ID = "X-BWTS-TenantId";

    @Autowired
    private RestClient.DocumentApiRestClient documentApiRestClient;


    public DocumentStatusListDTO getFailedJobList(String owner, int pageNum, int pageSize) {
        return documentApiRestClient.getResource()
                .path("documents/status/failed")
                .queryParam("pageNum", String.valueOf(pageNum))
                .queryParam("pageSize", String.valueOf(pageSize))
                .queryParam("from", owner)
                .request()
                .header(HEADER_USER_ID, DefaultTenants.SYSTEM_USER.toString())
                .header(HEADER_TENANT_ID, DefaultTenants.SUPER_ADMIN_TENANT.toString())
                .get(DocumentStatusListDTO.class);
    }

    public DocumentStatusList retryDocument(String owner, DocumentStatusListDTO documentStatusListDTO) {
        if (documentStatusListDTO == null) {
            LOGGER.info("documentStatusListDTO is illegal");
            return null;
        }

        LOGGER.info("Begin retry {} documents, the size is {}", owner, documentStatusListDTO.getItems().size());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("documentOwner", owner);

        JSONArray items = new JSONArray();
        for (DocumentStatusDTO documentStatusDTO : documentStatusListDTO.getItems()) {
            JSONObject status = new JSONObject();
            status.put("resourceId", documentStatusDTO.getResourceId().toString());
            status.put("tenantId", documentStatusDTO.getTenantId().toString());
            items.add(status);
        }
        jsonObject.put("items", items);
        Entity<JSONObject> entity = Entity.entity(jsonObject, MediaType.APPLICATION_JSON);
        return documentApiRestClient.getResource()
                .path("documents")
                .request()
                .header(HEADER_USER_ID, DefaultTenants.SYSTEM_USER.toString())
                .header(HEADER_TENANT_ID, DefaultTenants.SUPER_ADMIN_TENANT.toString())
                .put(entity, DocumentStatusList.class);
    }

    public void updateDocumentStatus(DocumentStatusDTO documentStatusDTO, String Status) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("flowStatus", Status);

        Entity<JSONObject> entity = Entity.entity(jsonObject, MediaType.APPLICATION_JSON);
        documentApiRestClient.getResource()
                .path("documents")
                .path(documentStatusDTO.getResourceId().toString())
                .path("status")
                .request()
                .header(HEADER_USER_ID, DefaultTenants.SYSTEM_USER.toString())
                .header(HEADER_TENANT_ID, DefaultTenants.SUPER_ADMIN_TENANT.toString())
                .put(entity);
    }

    public void updateDocumentStatistics() {
        Entity<String> entity = Entity.entity("", MediaType.APPLICATION_JSON);
        documentApiRestClient.getResource()
                .path("admin/documents/statistics")
                .request()
                .header(HEADER_USER_ID, DefaultTenants.SYSTEM_USER.toString())
                .header(HEADER_TENANT_ID, DefaultTenants.SUPER_ADMIN_TENANT.toString())
                .put(entity);
    }

}

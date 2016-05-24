package com.bwts.batchservice.service;


import com.bwts.batchservice.dao.DB2DAO;
import com.bwts.batchservice.dao.PostgreSQLDAO;
import com.bwts.batchservice.entity.Company;
import com.bwts.batchservice.entity.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class BatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchService.class);

    @Autowired
    private PostgreSQLDAO postgreSQLDAO;
    @Autowired
    private DB2DAO db2DAO;

    public void copyData(Timestamp startTime, Timestamp endTime) {
        List<Document> documentList = postgreSQLDAO.getDocumentList(startTime, endTime);
        List<Company> companyList = postgreSQLDAO.getCompanyList(startTime, endTime);
        db2DAO.insertInvoiceDetailInfo(documentList);

        if (db2DAO.checkInvoiceInfoExists()) {
            db2DAO.updateInvoiceInfo(documentList.size());
        } else {
            try {
                db2DAO.insertInvoiceInfo(documentList.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Company company : companyList) {
            if (db2DAO.checkCompanyExists(company)) {
                db2DAO.updateComInvoiceInfo(company);
            } else {
                try {
                    db2DAO.insertComInvoiceInfo(company);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

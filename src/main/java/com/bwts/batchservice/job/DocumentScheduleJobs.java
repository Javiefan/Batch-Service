package com.bwts.batchservice.job;


import com.bwts.batchservice.service.DocumentBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DocumentScheduleJobs {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentScheduleJobs.class);

    @Autowired
    private DocumentBatchService documentBatchService;

    @Scheduled(fixedRateString = "${schedule.sender.regenerate.einvoice.interval}", initialDelay = 10 * 1000)
    public void processFailedDocumentForSender() {
        long beginTime = new Date().getTime();
        documentBatchService.processFailedDocumentForSender();
        long endTime = new Date().getTime();
        LOGGER.info("Batch JOB: processFailedDocumentForSender take {} seconds to process", (endTime - beginTime) / 1000);
    }

    @Scheduled(fixedRateString = "${schedule.receiver.migrated.einvoice.interval}", initialDelay = 15 * 1000)
    public void migrateFailedDocumentForReceiver() {
        long beginTime = new Date().getTime();
        documentBatchService.processFailedDocumentForReceiver();
        long endTime = new Date().getTime();
        LOGGER.info("Batch JOB: migrateFailedDocumentForReceiver take {} seconds to process", (endTime - beginTime) / 1000);
    }

    @Scheduled(cron = "${schedule.report.monthly.statistics.cron}")
    public void updateDocumentStatistics() {
        long beginTime = new Date().getTime();
        documentBatchService.updateDocumentStatistics();
        long endTime = new Date().getTime();
        LOGGER.info("Batch JOB: updateDocumentStatistics take {} seconds to process", (endTime - beginTime) / 1000);
    }

}

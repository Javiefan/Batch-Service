package com.bwts.batchservice.job;


import com.bwts.batchservice.service.BatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Component
public class DocumentScheduleJobs {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentScheduleJobs.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Value("${file.location}")
    private String path;

    @Autowired
    private BatchService batchService;

    @Scheduled(fixedRateString = "${schedule.interval}", initialDelay = 2 * 1000)
    public void migrateDate() {
        LOGGER.info("starting batch job!");
        Timestamp startTime = null;
        Timestamp endTime = null;
        try {
            startTime = readTime();
            LOGGER.info("start time = " + startTime);
            endTime = new Timestamp(System.currentTimeMillis());
            LOGGER.info("end time = " + endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        batchService.copyData(startTime, endTime);
        try {
            saveTime(endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Timestamp readTime() throws Exception {
        String time = new String(Files.readAllBytes(Paths.get(path)));
        return new Timestamp(dateFormat.parse(time).getTime());
    }

    private void saveTime(Timestamp time) throws Exception {
        FileOutputStream writer = new FileOutputStream(Paths.get(path).toFile());
        writer.write(dateFormat.format(time).getBytes());
        writer.close();
    }

}

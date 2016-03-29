package com.bwts.batchservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.bwts.batchservice")
public class BootStrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Batch-Service!");
        SpringApplication.run(BootStrap.class);
    }
}

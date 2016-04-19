package com.bwts.batchservice;

import com.bwts.common.kafka.KafkaProducerConfig;
import com.bwts.common.rest.client.RestClient.DocumentApiRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.bwts.batchservice")
@EnableScheduling
@Import({KafkaProducerConfig.class})
public class BootStrap extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BootStrap.class);
    }

    @Bean
    public DocumentApiRestClient documentApiRestClient(@Value("${documentApi.host}") String host) {
        return new DocumentApiRestClient(host);
    }

    public static void main(String[] args) {
        LOGGER.info("Starting Batch-Service!");
        new BootStrap()
                .configure(new SpringApplicationBuilder(BootStrap.class))
                .run(args);
    }
}

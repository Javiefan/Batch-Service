package com.bwts.batchservice.consumer;

import com.bwts.batchservice.dto.DocLogDTO;
import com.bwts.batchservice.service.DocBatchService;
import com.bwts.common.batch.BatchMessage;
import com.bwts.common.kafka.consumer.ConsumerAbstractHandler;
import com.bwts.common.security.UserContext;
import com.bwts.common.security.utils.UserContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

public class DocLogConsumer extends ConsumerAbstractHandler<BatchMessage> {

        private static final Logger LOGGER = LoggerFactory.getLogger(DocLogConsumer.class);

        private final DocBatchService docBatchService;

        @Autowired
        public DocLogConsumer(DocBatchService docBatchService,
                              @Value("#{'${kafka.consumer.topic.batch.name}'}") String topic,
                              @Value("#{'${kafka.consumer.topic.batch.threads}'}") Integer threadCount) {
            super(topic, threadCount);
            this.docBatchService = docBatchService;
        }

        @Override
        public void process(BatchMessage message) {
            LOGGER.info("consume batch message: " + message.toString());

            DocLogDTO docLogDTO = new DocLogDTO.Builder()
                    .setTenantId(message.getTenantId())
                    .setDocumentId(message.getDocumentId())
                    .setPayload(message.getPayload())
                    .setPhase(message.getPhase())
                    .setThrowTime(message.getThrowTime())
                    .build();

            // for extensibility
            docLogDTO.setMessage("");
            docLogDTO.setActionResult("Failure");

            docLogDTO.setRetryTimes(docBatchService.getRetriedTimes(docLogDTO));

            SecurityContextHolder.setContext(new UserContext(null, docLogDTO.getTenantId()));

            docBatchService.processDocWithRetry(docLogDTO);
        }
}

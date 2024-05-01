package io.github.calvary.erp.queue;

/*-
 * Copyright © 2023 - 2024 Calvary ERP Contributors (mailnjeru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionEntryProducer implements Messenger<TransactionEntryMessage> {

    @Value("${queue.transaction-entry.topic}")
    private String topicName;

    private static final Logger log = LoggerFactory.getLogger(TransactionEntryProducer.class);

    private final KafkaTemplate<String, TransactionEntryMessage> kafkaTemplate;

    public TransactionEntryProducer(
        @Qualifier("transactionEntryMessageKafkaTemplate") KafkaTemplate<String, TransactionEntryMessage> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(TransactionEntryMessage transactionEntryMessage) {
        kafkaTemplate.send(topicName, transactionEntryMessage);
    }
}

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

import io.github.calvary.erp.BalanceSheetUpdateService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BsItemConsumer {

    private static final Logger log = LoggerFactory.getLogger(BsItemConsumer.class);

    private final BalanceSheetUpdateService balanceSheetUpdateService;

    public BsItemConsumer(BalanceSheetUpdateService balanceSheetUpdateService) {
        this.balanceSheetUpdateService = balanceSheetUpdateService;
    }

    @KafkaListener(topics = "transaction-entry-topic", groupId = "calvary-erp-reports")
    public void processDepreciationJobMessages(List<TransactionEntryMessage> messages) {
        // Process the batch of depreciation job messages
        for (TransactionEntryMessage message : messages) {
            // Extract the necessary details from the message

            log.debug("Received message for entry-id id {}", message.getId());

            // TODO Check why update service is not working
            balanceSheetUpdateService.update(message);

            log.debug("BS Update for transaction-entry-id id {} complete, sequence status update begins...", message.getId());
        }
    }
}

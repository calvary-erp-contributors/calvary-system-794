package io.github.calvary.erp.emails;

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

import io.github.calvary.erp.internal.InternalReceiptEmailRequestService;
import io.github.calvary.erp.internal.InternalSalesReceiptService;
import io.github.calvary.service.dto.ReceiptEmailRequestDTO;
import io.github.calvary.service.dto.SalesReceiptDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class SalesReceiptEmailNotificationJobImpl implements SalesReceiptEmailNotificationJob {

    private final InternalSalesReceiptService internalSalesReceiptService;
    private final InternalReceiptEmailRequestService internalReceiptEmailRequestService;
    private final SalesReceiptEmailService salesReceiptEmailService;

    public SalesReceiptEmailNotificationJobImpl(
        InternalSalesReceiptService internalSalesReceiptService,
        InternalReceiptEmailRequestService internalReceiptEmailRequestService,
        SalesReceiptEmailService salesReceiptEmailService
    ) {
        this.internalSalesReceiptService = internalSalesReceiptService;
        this.internalReceiptEmailRequestService = internalReceiptEmailRequestService;
        this.salesReceiptEmailService = salesReceiptEmailService;
    }

    @Override
    @Async
    public void runNotificationJob(ReceiptEmailRequestDTO requisition) {
        Optional<List<SalesReceiptDTO>> notificationList = internalSalesReceiptService.findSalesReceiptsPendingEmailNotification();

        notificationList.ifPresent(salesReceiptList -> {
            salesReceiptList.forEach(salesReceiptEmailService::sendEmailNotification);

            salesReceiptList.forEach(internalSalesReceiptService::hasBeenEmailed);

            requisition.setNumberOfUpdates(salesReceiptList.size());
        });

        requisition.setUploadComplete(true);

        internalReceiptEmailRequestService.save(requisition);
    }
}

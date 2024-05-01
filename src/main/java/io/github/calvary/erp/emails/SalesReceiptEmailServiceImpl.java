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

import io.github.calvary.erp.internal.InternalDealerService;
import io.github.calvary.erp.internal.InternalTransactionItemEntryService;
import io.github.calvary.erp.internal.InternalTransferItemEntryService;
import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.dto.SalesReceiptDTO;
import io.github.calvary.service.dto.TransactionItemEntryDTO;
import io.github.calvary.service.dto.TransferItemEntryDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class SalesReceiptEmailServiceImpl implements SalesReceiptEmailService {

    private final InternalDealerService internalDealerService;
    private final InternalTransactionItemEntryService internalTransactionItemEntryService;
    private final InternalTransferItemEntryService internalTransferItemEntryService;

    private final MailNotification mailNotification;

    public SalesReceiptEmailServiceImpl(
        InternalDealerService internalDealerService,
        InternalTransactionItemEntryService internalTransactionItemEntryService,
        InternalTransferItemEntryService internalTransferItemEntryService,
        MailNotification mailNotification
    ) {
        this.internalDealerService = internalDealerService;
        this.internalTransactionItemEntryService = internalTransactionItemEntryService;
        this.internalTransferItemEntryService = internalTransferItemEntryService;
        this.mailNotification = mailNotification;
    }

    @Override
    public void sendEmailNotification(SalesReceiptDTO salesReceipt) {
        Optional<DealerDTO> dealer = internalDealerService.findOne(salesReceipt.getDealer().getId());

        Optional<List<TransactionItemEntryDTO>> transactionItemEntries = internalTransactionItemEntryService.findItemsRelatedToSalesReceipt(
            salesReceipt
        );

        Optional<List<TransferItemEntryDTO>> transferItemEntries = internalTransferItemEntryService.findItemsRelatedToSalesReceipt(
            salesReceipt
        );

        dealer.ifPresent(recipient -> {
            transactionItemEntries.ifPresent(transactionItems -> {
                transferItemEntries.ifPresent(transferItems -> {
                    mailNotification.sendEmailSalesReceiptNotification(recipient, transactionItems, transferItems);
                });
            });
        });
    }
}

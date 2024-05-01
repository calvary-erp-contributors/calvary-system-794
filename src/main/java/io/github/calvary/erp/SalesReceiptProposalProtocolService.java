package io.github.calvary.erp;

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

import io.github.calvary.domain.TransactionItemEntry;
import io.github.calvary.domain.TransferItemEntry;
import io.github.calvary.erp.repository.InternalTransactionItemEntryRepository;
import io.github.calvary.erp.repository.InternalTransferItemEntryRepository;
import io.github.calvary.service.SalesReceiptService;
import io.github.calvary.service.dto.SalesReceiptProposalDTO;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SalesReceiptProposalProtocolService {

    private final SalesReceiptService salesReceiptService;

    private final InternalTransferItemEntryRepository transferItemEntryRepository;

    private final InternalTransactionItemEntryRepository internalTransactionItemEntryRepository;

    public SalesReceiptProposalProtocolService(
        SalesReceiptService salesReceiptService,
        InternalTransferItemEntryRepository transferItemEntryRepository,
        InternalTransactionItemEntryRepository internalTransactionItemEntryRepository
    ) {
        this.salesReceiptService = salesReceiptService;
        this.transferItemEntryRepository = transferItemEntryRepository;
        this.internalTransactionItemEntryRepository = internalTransactionItemEntryRepository;
    }

    public long salesReceiptPosting(SalesReceiptProposalDTO salesReceiptProposal) {
        long postedReceipts = salesReceiptService
            .findAll(Pageable.ofSize(Integer.MAX_VALUE))
            .stream()
            .filter(unfiltered -> !unfiltered.getHasBeenProposed())
            .filter(unfilteredReceipt -> {
                Optional<BigDecimal> transferAmount = transferItemEntryRepository
                    .findAllBySalesReceiptId(unfilteredReceipt.getId())
                    .stream()
                    .map(entries -> entries.stream().map(TransferItemEntry::getItemAmount).reduce(BigDecimal::add))
                    .map(Optional::get)
                    .reduce(BigDecimal::add);

                Optional<BigDecimal> transactionAmount = internalTransactionItemEntryRepository
                    .findAllBySalesReceiptId(unfilteredReceipt.getId())
                    .stream()
                    .map(entries -> entries.stream().map(TransactionItemEntry::getItemAmount).reduce(BigDecimal::add))
                    .map(Optional::get)
                    .reduce(BigDecimal::add);

                if (transactionAmount.isPresent() && transferAmount.isPresent()) {
                    return transactionAmount.get().intValue() == transferAmount.get().intValue();
                } else {
                    return false;
                }
            })
            .peek(receipt -> receipt.setHasBeenProposed(true))
            .peek(salesReceiptService::save)
            .count();

        return postedReceipts;
    }
}

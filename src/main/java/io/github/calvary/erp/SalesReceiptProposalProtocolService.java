package io.github.calvary.erp;

import io.github.calvary.domain.TransactionItemEntry;
import io.github.calvary.domain.TransferItemEntry;
import io.github.calvary.repository.TransactionItemEntryRepository;
import io.github.calvary.repository.TransferItemEntryRepository;
import io.github.calvary.service.SalesReceiptService;
import io.github.calvary.service.dto.SalesReceiptProposalDTO;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SalesReceiptProposalProtocolService {

    private final SalesReceiptService salesReceiptService;

    private final TransactionItemEntryRepository transactionItemEntryRepository;
    private final TransferItemEntryRepository transferItemEntryRepository;

    public SalesReceiptProposalProtocolService(
        SalesReceiptService salesReceiptService,
        TransactionItemEntryRepository transactionItemEntryRepository,
        TransferItemEntryRepository transferItemEntryRepository
    ) {
        this.salesReceiptService = salesReceiptService;
        this.transactionItemEntryRepository = transactionItemEntryRepository;
        this.transferItemEntryRepository = transferItemEntryRepository;
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
                    .map(TransferItemEntry::getItemAmount)
                    .reduce(BigDecimal::add);

                Optional<BigDecimal> transactionAmount = transactionItemEntryRepository
                    .findAllBySalesReceiptId(unfilteredReceipt.getId())
                    .stream()
                    .map(TransactionItemEntry::getItemAmount)
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

package io.github.calvary.erp.emails;

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

    public SalesReceiptEmailServiceImpl(
        InternalDealerService internalDealerService,
        InternalTransactionItemEntryService internalTransactionItemEntryService,
        InternalTransferItemEntryService internalTransferItemEntryService
    ) {
        this.internalDealerService = internalDealerService;
        this.internalTransactionItemEntryService = internalTransactionItemEntryService;
        this.internalTransferItemEntryService = internalTransferItemEntryService;
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
        // TODO Compile emaills
    }
}

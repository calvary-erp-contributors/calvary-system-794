package io.github.calvary.erp.emails;

import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.dto.TransactionItemEntryDTO;
import io.github.calvary.service.dto.TransferItemEntryDTO;
import java.util.List;

public interface MailNotification {
    void sendEmailSalesReceiptNotification(
        DealerDTO recipient,
        List<TransactionItemEntryDTO> transactionItems,
        List<TransferItemEntryDTO> transferItems
    );
}

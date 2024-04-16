package io.github.calvary.erp.emails;

import io.github.calvary.service.dto.SalesReceiptDTO;
import java.util.List;

/**
 * This service sends emails to the donors of the sales-receipt items provided
 *
 */
public interface SalesReceiptEmailService {
    void sendEmailNotification(SalesReceiptDTO salesReceipt);
}

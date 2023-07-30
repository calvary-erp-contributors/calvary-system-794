package io.github.calvary.erp;

import io.github.calvary.erp.queue.TransactionEntryMessage;

public interface BalanceSheetUpdateService {

    /**
     * Updates the message items for the balance sheet report
     *
     * @param message
     */
    void update(TransactionEntryMessage message);
}

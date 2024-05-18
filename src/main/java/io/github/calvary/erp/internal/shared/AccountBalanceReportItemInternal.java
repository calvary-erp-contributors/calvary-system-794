package io.github.calvary.erp.internal.shared;

import java.math.BigDecimal;

public interface AccountBalanceReportItemInternal {
    Long getId();

    String getAccountNumber();

    String getAccountName();

    BigDecimal getAccountBalance();
}

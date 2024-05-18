package io.github.calvary.erp.internal.shared;

import io.github.calvary.domain.AccountBalanceReportItem;
import io.github.calvary.service.dto.AccountBalanceReportItemDTO;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/**
 * Mapper for the entity {@link AccountBalanceReportItem} and its DTO {@link AccountBalanceReportItemDTO}.
 */
@Component
public class AccountBalanceReportItemInternalMapper implements Mapping<AccountBalanceReportItemInternal, AccountBalanceReportItemDTO> {

    @Override
    public AccountBalanceReportItemDTO mapToV2(AccountBalanceReportItemInternal v1) {
        AccountBalanceReportItemDTO dto = new AccountBalanceReportItemDTO();
        dto.setId(v1.getId());
        dto.setAccountName(v1.getAccountName());
        dto.setAccountNumber(v1.getAccountNumber());
        dto.setAccountBalance(v1.getAccountBalance());

        return dto;
    }

    @Override
    public AccountBalanceReportItemInternal mapToV1(AccountBalanceReportItemDTO v2) {
        return new AccountBalanceReportItemInternal() {
            @Override
            public Long getId() {
                return v2.getId();
            }

            @Override
            public String getAccountNumber() {
                return v2.getAccountNumber();
            }

            @Override
            public String getAccountName() {
                return v2.getAccountName();
            }

            @Override
            public BigDecimal getAccountBalance() {
                return v2.getAccountBalance();
            }
        };
    }
}

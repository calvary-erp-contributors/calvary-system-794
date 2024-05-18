package io.github.calvary.erp.internal.shared;

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

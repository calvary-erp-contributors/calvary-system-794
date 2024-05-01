package io.github.calvary.service.dto;

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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.TransactionAccount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionAccountDTO implements Serializable {

    private Long id;

    @NotNull
    private String accountName;

    private String accountNumber;

    private BigDecimal openingBalance;

    private TransactionAccountDTO parentAccount;

    private TransactionAccountTypeDTO transactionAccountType;

    private TransactionCurrencyDTO transactionCurrency;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public TransactionAccountDTO getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(TransactionAccountDTO parentAccount) {
        this.parentAccount = parentAccount;
    }

    public TransactionAccountTypeDTO getTransactionAccountType() {
        return transactionAccountType;
    }

    public void setTransactionAccountType(TransactionAccountTypeDTO transactionAccountType) {
        this.transactionAccountType = transactionAccountType;
    }

    public TransactionCurrencyDTO getTransactionCurrency() {
        return transactionCurrency;
    }

    public void setTransactionCurrency(TransactionCurrencyDTO transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionAccountDTO)) {
            return false;
        }

        TransactionAccountDTO transactionAccountDTO = (TransactionAccountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionAccountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionAccountDTO{" +
            "id=" + getId() +
            ", accountName='" + getAccountName() + "'" +
            ", accountNumber='" + getAccountNumber() + "'" +
            ", openingBalance=" + getOpeningBalance() +
            ", parentAccount=" + getParentAccount() +
            ", transactionAccountType=" + getTransactionAccountType() +
            ", transactionCurrency=" + getTransactionCurrency() +
            "}";
    }
}

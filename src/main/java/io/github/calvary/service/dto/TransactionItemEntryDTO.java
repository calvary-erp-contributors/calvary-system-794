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

/**
 * A DTO for the {@link io.github.calvary.domain.TransactionItemEntry} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionItemEntryDTO implements Serializable {

    private Long id;

    private String description;

    private BigDecimal itemAmount;

    private TransactionItemDTO transactionItem;

    private SalesReceiptDTO salesReceipt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(BigDecimal itemAmount) {
        this.itemAmount = itemAmount;
    }

    public TransactionItemDTO getTransactionItem() {
        return transactionItem;
    }

    public void setTransactionItem(TransactionItemDTO transactionItem) {
        this.transactionItem = transactionItem;
    }

    public SalesReceiptDTO getSalesReceipt() {
        return salesReceipt;
    }

    public void setSalesReceipt(SalesReceiptDTO salesReceipt) {
        this.salesReceipt = salesReceipt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionItemEntryDTO)) {
            return false;
        }

        TransactionItemEntryDTO transactionItemEntryDTO = (TransactionItemEntryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionItemEntryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionItemEntryDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", itemAmount=" + getItemAmount() +
            ", transactionItem=" + getTransactionItem() +
            ", salesReceipt=" + getSalesReceipt() +
            "}";
    }
}

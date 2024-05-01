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
 * A DTO for the {@link io.github.calvary.domain.TransferItemEntry} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransferItemEntryDTO implements Serializable {

    private Long id;

    private String description;

    @NotNull
    private BigDecimal itemAmount;

    private SalesReceiptDTO salesReceipt;

    private TransferItemDTO transferItem;

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

    public SalesReceiptDTO getSalesReceipt() {
        return salesReceipt;
    }

    public void setSalesReceipt(SalesReceiptDTO salesReceipt) {
        this.salesReceipt = salesReceipt;
    }

    public TransferItemDTO getTransferItem() {
        return transferItem;
    }

    public void setTransferItem(TransferItemDTO transferItem) {
        this.transferItem = transferItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransferItemEntryDTO)) {
            return false;
        }

        TransferItemEntryDTO transferItemEntryDTO = (TransferItemEntryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transferItemEntryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransferItemEntryDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", itemAmount=" + getItemAmount() +
            ", salesReceipt=" + getSalesReceipt() +
            ", transferItem=" + getTransferItem() +
            "}";
    }
}

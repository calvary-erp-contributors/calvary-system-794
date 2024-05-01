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
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.BalanceSheetItemType} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BalanceSheetItemTypeDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer itemSequence;

    @NotNull
    private String itemNumber;

    private String shortDescription;

    private TransactionAccountDTO transactionAccount;

    private BalanceSheetItemTypeDTO parentItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getItemSequence() {
        return itemSequence;
    }

    public void setItemSequence(Integer itemSequence) {
        this.itemSequence = itemSequence;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public TransactionAccountDTO getTransactionAccount() {
        return transactionAccount;
    }

    public void setTransactionAccount(TransactionAccountDTO transactionAccount) {
        this.transactionAccount = transactionAccount;
    }

    public BalanceSheetItemTypeDTO getParentItem() {
        return parentItem;
    }

    public void setParentItem(BalanceSheetItemTypeDTO parentItem) {
        this.parentItem = parentItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BalanceSheetItemTypeDTO)) {
            return false;
        }

        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO = (BalanceSheetItemTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, balanceSheetItemTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BalanceSheetItemTypeDTO{" +
            "id=" + getId() +
            ", itemSequence=" + getItemSequence() +
            ", itemNumber='" + getItemNumber() + "'" +
            ", shortDescription='" + getShortDescription() + "'" +
            ", transactionAccount=" + getTransactionAccount() +
            ", parentItem=" + getParentItem() +
            "}";
    }
}

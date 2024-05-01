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
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.BalanceSheetItemValue} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BalanceSheetItemValueDTO implements Serializable {

    private Long id;

    private String shortDescription;

    @NotNull
    private LocalDate effectiveDate;

    @NotNull
    private BigDecimal itemAmount;

    private BalanceSheetItemTypeDTO itemType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public BigDecimal getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(BigDecimal itemAmount) {
        this.itemAmount = itemAmount;
    }

    public BalanceSheetItemTypeDTO getItemType() {
        return itemType;
    }

    public void setItemType(BalanceSheetItemTypeDTO itemType) {
        this.itemType = itemType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BalanceSheetItemValueDTO)) {
            return false;
        }

        BalanceSheetItemValueDTO balanceSheetItemValueDTO = (BalanceSheetItemValueDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, balanceSheetItemValueDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BalanceSheetItemValueDTO{" +
            "id=" + getId() +
            ", shortDescription='" + getShortDescription() + "'" +
            ", effectiveDate='" + getEffectiveDate() + "'" +
            ", itemAmount=" + getItemAmount() +
            ", itemType=" + getItemType() +
            "}";
    }
}

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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.Dealer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DealerDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String mainEmail;

    private UUID dealerReference;

    private DealerTypeDTO dealerType;

    private Set<SalesReceiptEmailPersonaDTO> salesReceiptEmailPersonas = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainEmail() {
        return mainEmail;
    }

    public void setMainEmail(String mainEmail) {
        this.mainEmail = mainEmail;
    }

    public UUID getDealerReference() {
        return dealerReference;
    }

    public void setDealerReference(UUID dealerReference) {
        this.dealerReference = dealerReference;
    }

    public DealerTypeDTO getDealerType() {
        return dealerType;
    }

    public void setDealerType(DealerTypeDTO dealerType) {
        this.dealerType = dealerType;
    }

    public Set<SalesReceiptEmailPersonaDTO> getSalesReceiptEmailPersonas() {
        return salesReceiptEmailPersonas;
    }

    public void setSalesReceiptEmailPersonas(Set<SalesReceiptEmailPersonaDTO> salesReceiptEmailPersonas) {
        this.salesReceiptEmailPersonas = salesReceiptEmailPersonas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DealerDTO)) {
            return false;
        }

        DealerDTO dealerDTO = (DealerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, dealerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DealerDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", mainEmail='" + getMainEmail() + "'" +
            ", dealerReference='" + getDealerReference() + "'" +
            ", dealerType=" + getDealerType() +
            ", salesReceiptEmailPersonas=" + getSalesReceiptEmailPersonas() +
            "}";
    }
}

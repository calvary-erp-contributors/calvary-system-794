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
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.AccountTransaction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AccountTransactionDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalDate transactionDate;

    private String description;

    private String referenceNumber;

    private Boolean wasProposed;

    private Boolean wasPosted;

    private Boolean wasDeleted;

    private Boolean wasApproved;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Boolean getWasProposed() {
        return wasProposed;
    }

    public void setWasProposed(Boolean wasProposed) {
        this.wasProposed = wasProposed;
    }

    public Boolean getWasPosted() {
        return wasPosted;
    }

    public void setWasPosted(Boolean wasPosted) {
        this.wasPosted = wasPosted;
    }

    public Boolean getWasDeleted() {
        return wasDeleted;
    }

    public void setWasDeleted(Boolean wasDeleted) {
        this.wasDeleted = wasDeleted;
    }

    public Boolean getWasApproved() {
        return wasApproved;
    }

    public void setWasApproved(Boolean wasApproved) {
        this.wasApproved = wasApproved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountTransactionDTO)) {
            return false;
        }

        AccountTransactionDTO accountTransactionDTO = (AccountTransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, accountTransactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccountTransactionDTO{" +
            "id=" + getId() +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", referenceNumber='" + getReferenceNumber() + "'" +
            ", wasProposed='" + getWasProposed() + "'" +
            ", wasPosted='" + getWasPosted() + "'" +
            ", wasDeleted='" + getWasDeleted() + "'" +
            ", wasApproved='" + getWasApproved() + "'" +
            "}";
    }
}

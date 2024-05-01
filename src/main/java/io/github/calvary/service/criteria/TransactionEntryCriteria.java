package io.github.calvary.service.criteria;

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

import io.github.calvary.domain.enumeration.TransactionEntryTypes;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.TransactionEntry} entity. This class is used
 * in {@link io.github.calvary.web.rest.TransactionEntryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transaction-entries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionEntryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TransactionEntryTypes
     */
    public static class TransactionEntryTypesFilter extends Filter<TransactionEntryTypes> {

        public TransactionEntryTypesFilter() {}

        public TransactionEntryTypesFilter(TransactionEntryTypesFilter filter) {
            super(filter);
        }

        @Override
        public TransactionEntryTypesFilter copy() {
            return new TransactionEntryTypesFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter entryAmount;

    private TransactionEntryTypesFilter transactionEntryType;

    private StringFilter description;

    private BooleanFilter wasProposed;

    private BooleanFilter wasPosted;

    private BooleanFilter wasDeleted;

    private BooleanFilter wasApproved;

    private LongFilter transactionAccountId;

    private LongFilter accountTransactionId;

    private Boolean distinct;

    public TransactionEntryCriteria() {}

    public TransactionEntryCriteria(TransactionEntryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.entryAmount = other.entryAmount == null ? null : other.entryAmount.copy();
        this.transactionEntryType = other.transactionEntryType == null ? null : other.transactionEntryType.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.wasProposed = other.wasProposed == null ? null : other.wasProposed.copy();
        this.wasPosted = other.wasPosted == null ? null : other.wasPosted.copy();
        this.wasDeleted = other.wasDeleted == null ? null : other.wasDeleted.copy();
        this.wasApproved = other.wasApproved == null ? null : other.wasApproved.copy();
        this.transactionAccountId = other.transactionAccountId == null ? null : other.transactionAccountId.copy();
        this.accountTransactionId = other.accountTransactionId == null ? null : other.accountTransactionId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransactionEntryCriteria copy() {
        return new TransactionEntryCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public BigDecimalFilter getEntryAmount() {
        return entryAmount;
    }

    public BigDecimalFilter entryAmount() {
        if (entryAmount == null) {
            entryAmount = new BigDecimalFilter();
        }
        return entryAmount;
    }

    public void setEntryAmount(BigDecimalFilter entryAmount) {
        this.entryAmount = entryAmount;
    }

    public TransactionEntryTypesFilter getTransactionEntryType() {
        return transactionEntryType;
    }

    public TransactionEntryTypesFilter transactionEntryType() {
        if (transactionEntryType == null) {
            transactionEntryType = new TransactionEntryTypesFilter();
        }
        return transactionEntryType;
    }

    public void setTransactionEntryType(TransactionEntryTypesFilter transactionEntryType) {
        this.transactionEntryType = transactionEntryType;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public BooleanFilter getWasProposed() {
        return wasProposed;
    }

    public BooleanFilter wasProposed() {
        if (wasProposed == null) {
            wasProposed = new BooleanFilter();
        }
        return wasProposed;
    }

    public void setWasProposed(BooleanFilter wasProposed) {
        this.wasProposed = wasProposed;
    }

    public BooleanFilter getWasPosted() {
        return wasPosted;
    }

    public BooleanFilter wasPosted() {
        if (wasPosted == null) {
            wasPosted = new BooleanFilter();
        }
        return wasPosted;
    }

    public void setWasPosted(BooleanFilter wasPosted) {
        this.wasPosted = wasPosted;
    }

    public BooleanFilter getWasDeleted() {
        return wasDeleted;
    }

    public BooleanFilter wasDeleted() {
        if (wasDeleted == null) {
            wasDeleted = new BooleanFilter();
        }
        return wasDeleted;
    }

    public void setWasDeleted(BooleanFilter wasDeleted) {
        this.wasDeleted = wasDeleted;
    }

    public BooleanFilter getWasApproved() {
        return wasApproved;
    }

    public BooleanFilter wasApproved() {
        if (wasApproved == null) {
            wasApproved = new BooleanFilter();
        }
        return wasApproved;
    }

    public void setWasApproved(BooleanFilter wasApproved) {
        this.wasApproved = wasApproved;
    }

    public LongFilter getTransactionAccountId() {
        return transactionAccountId;
    }

    public LongFilter transactionAccountId() {
        if (transactionAccountId == null) {
            transactionAccountId = new LongFilter();
        }
        return transactionAccountId;
    }

    public void setTransactionAccountId(LongFilter transactionAccountId) {
        this.transactionAccountId = transactionAccountId;
    }

    public LongFilter getAccountTransactionId() {
        return accountTransactionId;
    }

    public LongFilter accountTransactionId() {
        if (accountTransactionId == null) {
            accountTransactionId = new LongFilter();
        }
        return accountTransactionId;
    }

    public void setAccountTransactionId(LongFilter accountTransactionId) {
        this.accountTransactionId = accountTransactionId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionEntryCriteria that = (TransactionEntryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(entryAmount, that.entryAmount) &&
            Objects.equals(transactionEntryType, that.transactionEntryType) &&
            Objects.equals(description, that.description) &&
            Objects.equals(wasProposed, that.wasProposed) &&
            Objects.equals(wasPosted, that.wasPosted) &&
            Objects.equals(wasDeleted, that.wasDeleted) &&
            Objects.equals(wasApproved, that.wasApproved) &&
            Objects.equals(transactionAccountId, that.transactionAccountId) &&
            Objects.equals(accountTransactionId, that.accountTransactionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            entryAmount,
            transactionEntryType,
            description,
            wasProposed,
            wasPosted,
            wasDeleted,
            wasApproved,
            transactionAccountId,
            accountTransactionId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionEntryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (entryAmount != null ? "entryAmount=" + entryAmount + ", " : "") +
            (transactionEntryType != null ? "transactionEntryType=" + transactionEntryType + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (wasProposed != null ? "wasProposed=" + wasProposed + ", " : "") +
            (wasPosted != null ? "wasPosted=" + wasPosted + ", " : "") +
            (wasDeleted != null ? "wasDeleted=" + wasDeleted + ", " : "") +
            (wasApproved != null ? "wasApproved=" + wasApproved + ", " : "") +
            (transactionAccountId != null ? "transactionAccountId=" + transactionAccountId + ", " : "") +
            (accountTransactionId != null ? "accountTransactionId=" + accountTransactionId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

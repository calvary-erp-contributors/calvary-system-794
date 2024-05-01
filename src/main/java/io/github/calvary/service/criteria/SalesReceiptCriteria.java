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

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.SalesReceipt} entity. This class is used
 * in {@link io.github.calvary.web.rest.SalesReceiptResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sales-receipts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private LocalDateFilter transactionDate;

    private BooleanFilter hasBeenEmailed;

    private BooleanFilter hasBeenProposed;

    private BooleanFilter shouldBeEmailed;

    private LongFilter transactionClassId;

    private LongFilter dealerId;

    private LongFilter salesReceiptTitleId;

    private LongFilter transactionItemEntryId;

    private LongFilter transferItemEntryId;

    private Boolean distinct;

    public SalesReceiptCriteria() {}

    public SalesReceiptCriteria(SalesReceiptCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.transactionDate = other.transactionDate == null ? null : other.transactionDate.copy();
        this.hasBeenEmailed = other.hasBeenEmailed == null ? null : other.hasBeenEmailed.copy();
        this.hasBeenProposed = other.hasBeenProposed == null ? null : other.hasBeenProposed.copy();
        this.shouldBeEmailed = other.shouldBeEmailed == null ? null : other.shouldBeEmailed.copy();
        this.transactionClassId = other.transactionClassId == null ? null : other.transactionClassId.copy();
        this.dealerId = other.dealerId == null ? null : other.dealerId.copy();
        this.salesReceiptTitleId = other.salesReceiptTitleId == null ? null : other.salesReceiptTitleId.copy();
        this.transactionItemEntryId = other.transactionItemEntryId == null ? null : other.transactionItemEntryId.copy();
        this.transferItemEntryId = other.transferItemEntryId == null ? null : other.transferItemEntryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SalesReceiptCriteria copy() {
        return new SalesReceiptCriteria(this);
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

    public LocalDateFilter getTransactionDate() {
        return transactionDate;
    }

    public LocalDateFilter transactionDate() {
        if (transactionDate == null) {
            transactionDate = new LocalDateFilter();
        }
        return transactionDate;
    }

    public void setTransactionDate(LocalDateFilter transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BooleanFilter getHasBeenEmailed() {
        return hasBeenEmailed;
    }

    public BooleanFilter hasBeenEmailed() {
        if (hasBeenEmailed == null) {
            hasBeenEmailed = new BooleanFilter();
        }
        return hasBeenEmailed;
    }

    public void setHasBeenEmailed(BooleanFilter hasBeenEmailed) {
        this.hasBeenEmailed = hasBeenEmailed;
    }

    public BooleanFilter getHasBeenProposed() {
        return hasBeenProposed;
    }

    public BooleanFilter hasBeenProposed() {
        if (hasBeenProposed == null) {
            hasBeenProposed = new BooleanFilter();
        }
        return hasBeenProposed;
    }

    public void setHasBeenProposed(BooleanFilter hasBeenProposed) {
        this.hasBeenProposed = hasBeenProposed;
    }

    public BooleanFilter getShouldBeEmailed() {
        return shouldBeEmailed;
    }

    public BooleanFilter shouldBeEmailed() {
        if (shouldBeEmailed == null) {
            shouldBeEmailed = new BooleanFilter();
        }
        return shouldBeEmailed;
    }

    public void setShouldBeEmailed(BooleanFilter shouldBeEmailed) {
        this.shouldBeEmailed = shouldBeEmailed;
    }

    public LongFilter getTransactionClassId() {
        return transactionClassId;
    }

    public LongFilter transactionClassId() {
        if (transactionClassId == null) {
            transactionClassId = new LongFilter();
        }
        return transactionClassId;
    }

    public void setTransactionClassId(LongFilter transactionClassId) {
        this.transactionClassId = transactionClassId;
    }

    public LongFilter getDealerId() {
        return dealerId;
    }

    public LongFilter dealerId() {
        if (dealerId == null) {
            dealerId = new LongFilter();
        }
        return dealerId;
    }

    public void setDealerId(LongFilter dealerId) {
        this.dealerId = dealerId;
    }

    public LongFilter getSalesReceiptTitleId() {
        return salesReceiptTitleId;
    }

    public LongFilter salesReceiptTitleId() {
        if (salesReceiptTitleId == null) {
            salesReceiptTitleId = new LongFilter();
        }
        return salesReceiptTitleId;
    }

    public void setSalesReceiptTitleId(LongFilter salesReceiptTitleId) {
        this.salesReceiptTitleId = salesReceiptTitleId;
    }

    public LongFilter getTransactionItemEntryId() {
        return transactionItemEntryId;
    }

    public LongFilter transactionItemEntryId() {
        if (transactionItemEntryId == null) {
            transactionItemEntryId = new LongFilter();
        }
        return transactionItemEntryId;
    }

    public void setTransactionItemEntryId(LongFilter transactionItemEntryId) {
        this.transactionItemEntryId = transactionItemEntryId;
    }

    public LongFilter getTransferItemEntryId() {
        return transferItemEntryId;
    }

    public LongFilter transferItemEntryId() {
        if (transferItemEntryId == null) {
            transferItemEntryId = new LongFilter();
        }
        return transferItemEntryId;
    }

    public void setTransferItemEntryId(LongFilter transferItemEntryId) {
        this.transferItemEntryId = transferItemEntryId;
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
        final SalesReceiptCriteria that = (SalesReceiptCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(transactionDate, that.transactionDate) &&
            Objects.equals(hasBeenEmailed, that.hasBeenEmailed) &&
            Objects.equals(hasBeenProposed, that.hasBeenProposed) &&
            Objects.equals(shouldBeEmailed, that.shouldBeEmailed) &&
            Objects.equals(transactionClassId, that.transactionClassId) &&
            Objects.equals(dealerId, that.dealerId) &&
            Objects.equals(salesReceiptTitleId, that.salesReceiptTitleId) &&
            Objects.equals(transactionItemEntryId, that.transactionItemEntryId) &&
            Objects.equals(transferItemEntryId, that.transferItemEntryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            description,
            transactionDate,
            hasBeenEmailed,
            hasBeenProposed,
            shouldBeEmailed,
            transactionClassId,
            dealerId,
            salesReceiptTitleId,
            transactionItemEntryId,
            transferItemEntryId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (transactionDate != null ? "transactionDate=" + transactionDate + ", " : "") +
            (hasBeenEmailed != null ? "hasBeenEmailed=" + hasBeenEmailed + ", " : "") +
            (hasBeenProposed != null ? "hasBeenProposed=" + hasBeenProposed + ", " : "") +
            (shouldBeEmailed != null ? "shouldBeEmailed=" + shouldBeEmailed + ", " : "") +
            (transactionClassId != null ? "transactionClassId=" + transactionClassId + ", " : "") +
            (dealerId != null ? "dealerId=" + dealerId + ", " : "") +
            (salesReceiptTitleId != null ? "salesReceiptTitleId=" + salesReceiptTitleId + ", " : "") +
            (transactionItemEntryId != null ? "transactionItemEntryId=" + transactionItemEntryId + ", " : "") +
            (transferItemEntryId != null ? "transferItemEntryId=" + transferItemEntryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

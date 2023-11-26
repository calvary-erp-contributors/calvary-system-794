package io.github.calvary.service.criteria;

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

    private StringFilter salesReceiptTitle;

    private StringFilter description;

    private LongFilter transactionClassId;

    private LongFilter dealerId;

    private LongFilter transactionItemAmountId;

    private Boolean distinct;

    public SalesReceiptCriteria() {}

    public SalesReceiptCriteria(SalesReceiptCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.salesReceiptTitle = other.salesReceiptTitle == null ? null : other.salesReceiptTitle.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.transactionClassId = other.transactionClassId == null ? null : other.transactionClassId.copy();
        this.dealerId = other.dealerId == null ? null : other.dealerId.copy();
        this.transactionItemAmountId = other.transactionItemAmountId == null ? null : other.transactionItemAmountId.copy();
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

    public StringFilter getSalesReceiptTitle() {
        return salesReceiptTitle;
    }

    public StringFilter salesReceiptTitle() {
        if (salesReceiptTitle == null) {
            salesReceiptTitle = new StringFilter();
        }
        return salesReceiptTitle;
    }

    public void setSalesReceiptTitle(StringFilter salesReceiptTitle) {
        this.salesReceiptTitle = salesReceiptTitle;
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

    public LongFilter getTransactionItemAmountId() {
        return transactionItemAmountId;
    }

    public LongFilter transactionItemAmountId() {
        if (transactionItemAmountId == null) {
            transactionItemAmountId = new LongFilter();
        }
        return transactionItemAmountId;
    }

    public void setTransactionItemAmountId(LongFilter transactionItemAmountId) {
        this.transactionItemAmountId = transactionItemAmountId;
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
            Objects.equals(salesReceiptTitle, that.salesReceiptTitle) &&
            Objects.equals(description, that.description) &&
            Objects.equals(transactionClassId, that.transactionClassId) &&
            Objects.equals(dealerId, that.dealerId) &&
            Objects.equals(transactionItemAmountId, that.transactionItemAmountId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, salesReceiptTitle, description, transactionClassId, dealerId, transactionItemAmountId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (salesReceiptTitle != null ? "salesReceiptTitle=" + salesReceiptTitle + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (transactionClassId != null ? "transactionClassId=" + transactionClassId + ", " : "") +
            (dealerId != null ? "dealerId=" + dealerId + ", " : "") +
            (transactionItemAmountId != null ? "transactionItemAmountId=" + transactionItemAmountId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

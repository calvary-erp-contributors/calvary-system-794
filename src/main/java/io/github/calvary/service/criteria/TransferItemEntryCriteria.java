package io.github.calvary.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.TransferItemEntry} entity. This class is used
 * in {@link io.github.calvary.web.rest.TransferItemEntryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transfer-item-entries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransferItemEntryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private BigDecimalFilter itemAmount;

    private LongFilter salesReceiptId;

    private LongFilter transferItemId;

    private Boolean distinct;

    public TransferItemEntryCriteria() {}

    public TransferItemEntryCriteria(TransferItemEntryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.itemAmount = other.itemAmount == null ? null : other.itemAmount.copy();
        this.salesReceiptId = other.salesReceiptId == null ? null : other.salesReceiptId.copy();
        this.transferItemId = other.transferItemId == null ? null : other.transferItemId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransferItemEntryCriteria copy() {
        return new TransferItemEntryCriteria(this);
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

    public BigDecimalFilter getItemAmount() {
        return itemAmount;
    }

    public BigDecimalFilter itemAmount() {
        if (itemAmount == null) {
            itemAmount = new BigDecimalFilter();
        }
        return itemAmount;
    }

    public void setItemAmount(BigDecimalFilter itemAmount) {
        this.itemAmount = itemAmount;
    }

    public LongFilter getSalesReceiptId() {
        return salesReceiptId;
    }

    public LongFilter salesReceiptId() {
        if (salesReceiptId == null) {
            salesReceiptId = new LongFilter();
        }
        return salesReceiptId;
    }

    public void setSalesReceiptId(LongFilter salesReceiptId) {
        this.salesReceiptId = salesReceiptId;
    }

    public LongFilter getTransferItemId() {
        return transferItemId;
    }

    public LongFilter transferItemId() {
        if (transferItemId == null) {
            transferItemId = new LongFilter();
        }
        return transferItemId;
    }

    public void setTransferItemId(LongFilter transferItemId) {
        this.transferItemId = transferItemId;
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
        final TransferItemEntryCriteria that = (TransferItemEntryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(itemAmount, that.itemAmount) &&
            Objects.equals(salesReceiptId, that.salesReceiptId) &&
            Objects.equals(transferItemId, that.transferItemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, itemAmount, salesReceiptId, transferItemId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransferItemEntryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (itemAmount != null ? "itemAmount=" + itemAmount + ", " : "") +
            (salesReceiptId != null ? "salesReceiptId=" + salesReceiptId + ", " : "") +
            (transferItemId != null ? "transferItemId=" + transferItemId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

package io.github.calvary.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.TransactionItemEntry} entity. This class is used
 * in {@link io.github.calvary.web.rest.TransactionItemEntryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transaction-item-entries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionItemEntryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private BigDecimalFilter itemAmount;

    private LongFilter transactionItemId;

    private Boolean distinct;

    public TransactionItemEntryCriteria() {}

    public TransactionItemEntryCriteria(TransactionItemEntryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.itemAmount = other.itemAmount == null ? null : other.itemAmount.copy();
        this.transactionItemId = other.transactionItemId == null ? null : other.transactionItemId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransactionItemEntryCriteria copy() {
        return new TransactionItemEntryCriteria(this);
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

    public LongFilter getTransactionItemId() {
        return transactionItemId;
    }

    public LongFilter transactionItemId() {
        if (transactionItemId == null) {
            transactionItemId = new LongFilter();
        }
        return transactionItemId;
    }

    public void setTransactionItemId(LongFilter transactionItemId) {
        this.transactionItemId = transactionItemId;
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
        final TransactionItemEntryCriteria that = (TransactionItemEntryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(itemAmount, that.itemAmount) &&
            Objects.equals(transactionItemId, that.transactionItemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, itemAmount, transactionItemId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionItemEntryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (itemAmount != null ? "itemAmount=" + itemAmount + ", " : "") +
            (transactionItemId != null ? "transactionItemId=" + transactionItemId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

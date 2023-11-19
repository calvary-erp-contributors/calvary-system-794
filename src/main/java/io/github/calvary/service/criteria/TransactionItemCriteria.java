package io.github.calvary.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.TransactionItem} entity. This class is used
 * in {@link io.github.calvary.web.rest.TransactionItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transaction-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter itemName;

    private StringFilter description;

    private LongFilter transactionClassId;

    private LongFilter transactionAccountId;

    private Boolean distinct;

    public TransactionItemCriteria() {}

    public TransactionItemCriteria(TransactionItemCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.itemName = other.itemName == null ? null : other.itemName.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.transactionClassId = other.transactionClassId == null ? null : other.transactionClassId.copy();
        this.transactionAccountId = other.transactionAccountId == null ? null : other.transactionAccountId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransactionItemCriteria copy() {
        return new TransactionItemCriteria(this);
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

    public StringFilter getItemName() {
        return itemName;
    }

    public StringFilter itemName() {
        if (itemName == null) {
            itemName = new StringFilter();
        }
        return itemName;
    }

    public void setItemName(StringFilter itemName) {
        this.itemName = itemName;
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
        final TransactionItemCriteria that = (TransactionItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(itemName, that.itemName) &&
            Objects.equals(description, that.description) &&
            Objects.equals(transactionClassId, that.transactionClassId) &&
            Objects.equals(transactionAccountId, that.transactionAccountId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemName, description, transactionClassId, transactionAccountId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionItemCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (itemName != null ? "itemName=" + itemName + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (transactionClassId != null ? "transactionClassId=" + transactionClassId + ", " : "") +
            (transactionAccountId != null ? "transactionAccountId=" + transactionAccountId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

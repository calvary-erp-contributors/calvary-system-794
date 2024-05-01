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
 * Criteria class for the {@link io.github.calvary.domain.TransactionItemAmount} entity. This class is used
 * in {@link io.github.calvary.web.rest.TransactionItemAmountResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transaction-item-amounts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionItemAmountCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter transactionItemAmount;

    private LongFilter transactionItemId;

    private Boolean distinct;

    public TransactionItemAmountCriteria() {}

    public TransactionItemAmountCriteria(TransactionItemAmountCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.transactionItemAmount = other.transactionItemAmount == null ? null : other.transactionItemAmount.copy();
        this.transactionItemId = other.transactionItemId == null ? null : other.transactionItemId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransactionItemAmountCriteria copy() {
        return new TransactionItemAmountCriteria(this);
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

    public BigDecimalFilter getTransactionItemAmount() {
        return transactionItemAmount;
    }

    public BigDecimalFilter transactionItemAmount() {
        if (transactionItemAmount == null) {
            transactionItemAmount = new BigDecimalFilter();
        }
        return transactionItemAmount;
    }

    public void setTransactionItemAmount(BigDecimalFilter transactionItemAmount) {
        this.transactionItemAmount = transactionItemAmount;
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
        final TransactionItemAmountCriteria that = (TransactionItemAmountCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(transactionItemAmount, that.transactionItemAmount) &&
            Objects.equals(transactionItemId, that.transactionItemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transactionItemAmount, transactionItemId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionItemAmountCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (transactionItemAmount != null ? "transactionItemAmount=" + transactionItemAmount + ", " : "") +
            (transactionItemId != null ? "transactionItemId=" + transactionItemId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

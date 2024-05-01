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
 * Criteria class for the {@link io.github.calvary.domain.SalesReceiptTitle} entity. This class is used
 * in {@link io.github.calvary.web.rest.SalesReceiptTitleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sales-receipt-titles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptTitleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter receiptTitle;

    private Boolean distinct;

    public SalesReceiptTitleCriteria() {}

    public SalesReceiptTitleCriteria(SalesReceiptTitleCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.receiptTitle = other.receiptTitle == null ? null : other.receiptTitle.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SalesReceiptTitleCriteria copy() {
        return new SalesReceiptTitleCriteria(this);
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

    public StringFilter getReceiptTitle() {
        return receiptTitle;
    }

    public StringFilter receiptTitle() {
        if (receiptTitle == null) {
            receiptTitle = new StringFilter();
        }
        return receiptTitle;
    }

    public void setReceiptTitle(StringFilter receiptTitle) {
        this.receiptTitle = receiptTitle;
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
        final SalesReceiptTitleCriteria that = (SalesReceiptTitleCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(receiptTitle, that.receiptTitle) && Objects.equals(distinct, that.distinct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiptTitle, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptTitleCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (receiptTitle != null ? "receiptTitle=" + receiptTitle + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

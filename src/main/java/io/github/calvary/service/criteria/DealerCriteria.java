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
 * Criteria class for the {@link io.github.calvary.domain.Dealer} entity. This class is used
 * in {@link io.github.calvary.web.rest.DealerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /dealers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DealerCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter mainEmail;

    private UUIDFilter dealerReference;

    private LongFilter dealerTypeId;

    private LongFilter salesReceiptEmailPersonaId;

    private Boolean distinct;

    public DealerCriteria() {}

    public DealerCriteria(DealerCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.mainEmail = other.mainEmail == null ? null : other.mainEmail.copy();
        this.dealerReference = other.dealerReference == null ? null : other.dealerReference.copy();
        this.dealerTypeId = other.dealerTypeId == null ? null : other.dealerTypeId.copy();
        this.salesReceiptEmailPersonaId = other.salesReceiptEmailPersonaId == null ? null : other.salesReceiptEmailPersonaId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DealerCriteria copy() {
        return new DealerCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getMainEmail() {
        return mainEmail;
    }

    public StringFilter mainEmail() {
        if (mainEmail == null) {
            mainEmail = new StringFilter();
        }
        return mainEmail;
    }

    public void setMainEmail(StringFilter mainEmail) {
        this.mainEmail = mainEmail;
    }

    public UUIDFilter getDealerReference() {
        return dealerReference;
    }

    public UUIDFilter dealerReference() {
        if (dealerReference == null) {
            dealerReference = new UUIDFilter();
        }
        return dealerReference;
    }

    public void setDealerReference(UUIDFilter dealerReference) {
        this.dealerReference = dealerReference;
    }

    public LongFilter getDealerTypeId() {
        return dealerTypeId;
    }

    public LongFilter dealerTypeId() {
        if (dealerTypeId == null) {
            dealerTypeId = new LongFilter();
        }
        return dealerTypeId;
    }

    public void setDealerTypeId(LongFilter dealerTypeId) {
        this.dealerTypeId = dealerTypeId;
    }

    public LongFilter getSalesReceiptEmailPersonaId() {
        return salesReceiptEmailPersonaId;
    }

    public LongFilter salesReceiptEmailPersonaId() {
        if (salesReceiptEmailPersonaId == null) {
            salesReceiptEmailPersonaId = new LongFilter();
        }
        return salesReceiptEmailPersonaId;
    }

    public void setSalesReceiptEmailPersonaId(LongFilter salesReceiptEmailPersonaId) {
        this.salesReceiptEmailPersonaId = salesReceiptEmailPersonaId;
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
        final DealerCriteria that = (DealerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(mainEmail, that.mainEmail) &&
            Objects.equals(dealerReference, that.dealerReference) &&
            Objects.equals(dealerTypeId, that.dealerTypeId) &&
            Objects.equals(salesReceiptEmailPersonaId, that.salesReceiptEmailPersonaId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, mainEmail, dealerReference, dealerTypeId, salesReceiptEmailPersonaId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DealerCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (mainEmail != null ? "mainEmail=" + mainEmail + ", " : "") +
            (dealerReference != null ? "dealerReference=" + dealerReference + ", " : "") +
            (dealerTypeId != null ? "dealerTypeId=" + dealerTypeId + ", " : "") +
            (salesReceiptEmailPersonaId != null ? "salesReceiptEmailPersonaId=" + salesReceiptEmailPersonaId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

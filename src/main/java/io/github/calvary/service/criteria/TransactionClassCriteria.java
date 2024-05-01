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
 * Criteria class for the {@link io.github.calvary.domain.TransactionClass} entity. This class is used
 * in {@link io.github.calvary.web.rest.TransactionClassResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transaction-classes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionClassCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter className;

    private Boolean distinct;

    public TransactionClassCriteria() {}

    public TransactionClassCriteria(TransactionClassCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.className = other.className == null ? null : other.className.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransactionClassCriteria copy() {
        return new TransactionClassCriteria(this);
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

    public StringFilter getClassName() {
        return className;
    }

    public StringFilter className() {
        if (className == null) {
            className = new StringFilter();
        }
        return className;
    }

    public void setClassName(StringFilter className) {
        this.className = className;
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
        final TransactionClassCriteria that = (TransactionClassCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(className, that.className) && Objects.equals(distinct, that.distinct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, className, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionClassCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (className != null ? "className=" + className + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

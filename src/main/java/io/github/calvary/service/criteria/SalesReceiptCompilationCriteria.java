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
 * Criteria class for the {@link io.github.calvary.domain.SalesReceiptCompilation} entity. This class is used
 * in {@link io.github.calvary.web.rest.SalesReceiptCompilationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sales-receipt-compilations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptCompilationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter timeOfCompilation;

    private UUIDFilter compilationIdentifier;

    private IntegerFilter receiptsCompiled;

    private LongFilter compiledById;

    private Boolean distinct;

    public SalesReceiptCompilationCriteria() {}

    public SalesReceiptCompilationCriteria(SalesReceiptCompilationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.timeOfCompilation = other.timeOfCompilation == null ? null : other.timeOfCompilation.copy();
        this.compilationIdentifier = other.compilationIdentifier == null ? null : other.compilationIdentifier.copy();
        this.receiptsCompiled = other.receiptsCompiled == null ? null : other.receiptsCompiled.copy();
        this.compiledById = other.compiledById == null ? null : other.compiledById.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SalesReceiptCompilationCriteria copy() {
        return new SalesReceiptCompilationCriteria(this);
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

    public InstantFilter getTimeOfCompilation() {
        return timeOfCompilation;
    }

    public InstantFilter timeOfCompilation() {
        if (timeOfCompilation == null) {
            timeOfCompilation = new InstantFilter();
        }
        return timeOfCompilation;
    }

    public void setTimeOfCompilation(InstantFilter timeOfCompilation) {
        this.timeOfCompilation = timeOfCompilation;
    }

    public UUIDFilter getCompilationIdentifier() {
        return compilationIdentifier;
    }

    public UUIDFilter compilationIdentifier() {
        if (compilationIdentifier == null) {
            compilationIdentifier = new UUIDFilter();
        }
        return compilationIdentifier;
    }

    public void setCompilationIdentifier(UUIDFilter compilationIdentifier) {
        this.compilationIdentifier = compilationIdentifier;
    }

    public IntegerFilter getReceiptsCompiled() {
        return receiptsCompiled;
    }

    public IntegerFilter receiptsCompiled() {
        if (receiptsCompiled == null) {
            receiptsCompiled = new IntegerFilter();
        }
        return receiptsCompiled;
    }

    public void setReceiptsCompiled(IntegerFilter receiptsCompiled) {
        this.receiptsCompiled = receiptsCompiled;
    }

    public LongFilter getCompiledById() {
        return compiledById;
    }

    public LongFilter compiledById() {
        if (compiledById == null) {
            compiledById = new LongFilter();
        }
        return compiledById;
    }

    public void setCompiledById(LongFilter compiledById) {
        this.compiledById = compiledById;
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
        final SalesReceiptCompilationCriteria that = (SalesReceiptCompilationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(timeOfCompilation, that.timeOfCompilation) &&
            Objects.equals(compilationIdentifier, that.compilationIdentifier) &&
            Objects.equals(receiptsCompiled, that.receiptsCompiled) &&
            Objects.equals(compiledById, that.compiledById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeOfCompilation, compilationIdentifier, receiptsCompiled, compiledById, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptCompilationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (timeOfCompilation != null ? "timeOfCompilation=" + timeOfCompilation + ", " : "") +
            (compilationIdentifier != null ? "compilationIdentifier=" + compilationIdentifier + ", " : "") +
            (receiptsCompiled != null ? "receiptsCompiled=" + receiptsCompiled + ", " : "") +
            (compiledById != null ? "compiledById=" + compiledById + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

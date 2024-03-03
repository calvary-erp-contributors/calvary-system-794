package io.github.calvary.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.ReceiptEmailRequest} entity. This class is used
 * in {@link io.github.calvary.web.rest.ReceiptEmailRequestResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /receipt-email-requests?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReceiptEmailRequestCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter timeOfRequisition;

    private BooleanFilter uploadComplete;

    private IntegerFilter numberOfUpdates;

    private LongFilter requestedById;

    private Boolean distinct;

    public ReceiptEmailRequestCriteria() {}

    public ReceiptEmailRequestCriteria(ReceiptEmailRequestCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.timeOfRequisition = other.timeOfRequisition == null ? null : other.timeOfRequisition.copy();
        this.uploadComplete = other.uploadComplete == null ? null : other.uploadComplete.copy();
        this.numberOfUpdates = other.numberOfUpdates == null ? null : other.numberOfUpdates.copy();
        this.requestedById = other.requestedById == null ? null : other.requestedById.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ReceiptEmailRequestCriteria copy() {
        return new ReceiptEmailRequestCriteria(this);
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

    public ZonedDateTimeFilter getTimeOfRequisition() {
        return timeOfRequisition;
    }

    public ZonedDateTimeFilter timeOfRequisition() {
        if (timeOfRequisition == null) {
            timeOfRequisition = new ZonedDateTimeFilter();
        }
        return timeOfRequisition;
    }

    public void setTimeOfRequisition(ZonedDateTimeFilter timeOfRequisition) {
        this.timeOfRequisition = timeOfRequisition;
    }

    public BooleanFilter getUploadComplete() {
        return uploadComplete;
    }

    public BooleanFilter uploadComplete() {
        if (uploadComplete == null) {
            uploadComplete = new BooleanFilter();
        }
        return uploadComplete;
    }

    public void setUploadComplete(BooleanFilter uploadComplete) {
        this.uploadComplete = uploadComplete;
    }

    public IntegerFilter getNumberOfUpdates() {
        return numberOfUpdates;
    }

    public IntegerFilter numberOfUpdates() {
        if (numberOfUpdates == null) {
            numberOfUpdates = new IntegerFilter();
        }
        return numberOfUpdates;
    }

    public void setNumberOfUpdates(IntegerFilter numberOfUpdates) {
        this.numberOfUpdates = numberOfUpdates;
    }

    public LongFilter getRequestedById() {
        return requestedById;
    }

    public LongFilter requestedById() {
        if (requestedById == null) {
            requestedById = new LongFilter();
        }
        return requestedById;
    }

    public void setRequestedById(LongFilter requestedById) {
        this.requestedById = requestedById;
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
        final ReceiptEmailRequestCriteria that = (ReceiptEmailRequestCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(timeOfRequisition, that.timeOfRequisition) &&
            Objects.equals(uploadComplete, that.uploadComplete) &&
            Objects.equals(numberOfUpdates, that.numberOfUpdates) &&
            Objects.equals(requestedById, that.requestedById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeOfRequisition, uploadComplete, numberOfUpdates, requestedById, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReceiptEmailRequestCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (timeOfRequisition != null ? "timeOfRequisition=" + timeOfRequisition + ", " : "") +
            (uploadComplete != null ? "uploadComplete=" + uploadComplete + ", " : "") +
            (numberOfUpdates != null ? "numberOfUpdates=" + numberOfUpdates + ", " : "") +
            (requestedById != null ? "requestedById=" + requestedById + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

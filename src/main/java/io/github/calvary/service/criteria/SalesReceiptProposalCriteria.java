package io.github.calvary.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.SalesReceiptProposal} entity. This class is used
 * in {@link io.github.calvary.web.rest.SalesReceiptProposalResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sales-receipt-proposals?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptProposalCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter timeOfPosting;

    private UUIDFilter postingIdentifier;

    private IntegerFilter numberOfReceiptsPosted;

    private LongFilter proposedById;

    private Boolean distinct;

    public SalesReceiptProposalCriteria() {}

    public SalesReceiptProposalCriteria(SalesReceiptProposalCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.timeOfPosting = other.timeOfPosting == null ? null : other.timeOfPosting.copy();
        this.postingIdentifier = other.postingIdentifier == null ? null : other.postingIdentifier.copy();
        this.numberOfReceiptsPosted = other.numberOfReceiptsPosted == null ? null : other.numberOfReceiptsPosted.copy();
        this.proposedById = other.proposedById == null ? null : other.proposedById.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SalesReceiptProposalCriteria copy() {
        return new SalesReceiptProposalCriteria(this);
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

    public ZonedDateTimeFilter getTimeOfPosting() {
        return timeOfPosting;
    }

    public ZonedDateTimeFilter timeOfPosting() {
        if (timeOfPosting == null) {
            timeOfPosting = new ZonedDateTimeFilter();
        }
        return timeOfPosting;
    }

    public void setTimeOfPosting(ZonedDateTimeFilter timeOfPosting) {
        this.timeOfPosting = timeOfPosting;
    }

    public UUIDFilter getPostingIdentifier() {
        return postingIdentifier;
    }

    public UUIDFilter postingIdentifier() {
        if (postingIdentifier == null) {
            postingIdentifier = new UUIDFilter();
        }
        return postingIdentifier;
    }

    public void setPostingIdentifier(UUIDFilter postingIdentifier) {
        this.postingIdentifier = postingIdentifier;
    }

    public IntegerFilter getNumberOfReceiptsPosted() {
        return numberOfReceiptsPosted;
    }

    public IntegerFilter numberOfReceiptsPosted() {
        if (numberOfReceiptsPosted == null) {
            numberOfReceiptsPosted = new IntegerFilter();
        }
        return numberOfReceiptsPosted;
    }

    public void setNumberOfReceiptsPosted(IntegerFilter numberOfReceiptsPosted) {
        this.numberOfReceiptsPosted = numberOfReceiptsPosted;
    }

    public LongFilter getProposedById() {
        return proposedById;
    }

    public LongFilter proposedById() {
        if (proposedById == null) {
            proposedById = new LongFilter();
        }
        return proposedById;
    }

    public void setProposedById(LongFilter proposedById) {
        this.proposedById = proposedById;
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
        final SalesReceiptProposalCriteria that = (SalesReceiptProposalCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(timeOfPosting, that.timeOfPosting) &&
            Objects.equals(postingIdentifier, that.postingIdentifier) &&
            Objects.equals(numberOfReceiptsPosted, that.numberOfReceiptsPosted) &&
            Objects.equals(proposedById, that.proposedById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeOfPosting, postingIdentifier, numberOfReceiptsPosted, proposedById, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptProposalCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (timeOfPosting != null ? "timeOfPosting=" + timeOfPosting + ", " : "") +
            (postingIdentifier != null ? "postingIdentifier=" + postingIdentifier + ", " : "") +
            (numberOfReceiptsPosted != null ? "numberOfReceiptsPosted=" + numberOfReceiptsPosted + ", " : "") +
            (proposedById != null ? "proposedById=" + proposedById + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}

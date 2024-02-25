package io.github.calvary.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.SalesReceiptProposal} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptProposalDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime timeOfPosting;

    @NotNull
    private UUID postingIdentifier;

    private Integer numberOfReceiptsPosted;

    private ApplicationUserDTO proposedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getTimeOfPosting() {
        return timeOfPosting;
    }

    public void setTimeOfPosting(ZonedDateTime timeOfPosting) {
        this.timeOfPosting = timeOfPosting;
    }

    public UUID getPostingIdentifier() {
        return postingIdentifier;
    }

    public void setPostingIdentifier(UUID postingIdentifier) {
        this.postingIdentifier = postingIdentifier;
    }

    public Integer getNumberOfReceiptsPosted() {
        return numberOfReceiptsPosted;
    }

    public void setNumberOfReceiptsPosted(Integer numberOfReceiptsPosted) {
        this.numberOfReceiptsPosted = numberOfReceiptsPosted;
    }

    public ApplicationUserDTO getProposedBy() {
        return proposedBy;
    }

    public void setProposedBy(ApplicationUserDTO proposedBy) {
        this.proposedBy = proposedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesReceiptProposalDTO)) {
            return false;
        }

        SalesReceiptProposalDTO salesReceiptProposalDTO = (SalesReceiptProposalDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, salesReceiptProposalDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptProposalDTO{" +
            "id=" + getId() +
            ", timeOfPosting='" + getTimeOfPosting() + "'" +
            ", postingIdentifier='" + getPostingIdentifier() + "'" +
            ", numberOfReceiptsPosted=" + getNumberOfReceiptsPosted() +
            ", proposedBy=" + getProposedBy() +
            "}";
    }
}

package io.github.calvary.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SalesReceiptProposal.
 */
@Entity
@Table(name = "sales_receipt_proposal")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "salesreceiptproposal")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptProposal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "time_of_posting", nullable = false)
    private ZonedDateTime timeOfPosting;

    @NotNull
    @Column(name = "posting_identifier", nullable = false, unique = true)
    private UUID postingIdentifier;

    @Column(name = "number_of_receipts_posted")
    private Integer numberOfReceiptsPosted;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SalesReceiptProposal id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getTimeOfPosting() {
        return this.timeOfPosting;
    }

    public SalesReceiptProposal timeOfPosting(ZonedDateTime timeOfPosting) {
        this.setTimeOfPosting(timeOfPosting);
        return this;
    }

    public void setTimeOfPosting(ZonedDateTime timeOfPosting) {
        this.timeOfPosting = timeOfPosting;
    }

    public UUID getPostingIdentifier() {
        return this.postingIdentifier;
    }

    public SalesReceiptProposal postingIdentifier(UUID postingIdentifier) {
        this.setPostingIdentifier(postingIdentifier);
        return this;
    }

    public void setPostingIdentifier(UUID postingIdentifier) {
        this.postingIdentifier = postingIdentifier;
    }

    public Integer getNumberOfReceiptsPosted() {
        return this.numberOfReceiptsPosted;
    }

    public SalesReceiptProposal numberOfReceiptsPosted(Integer numberOfReceiptsPosted) {
        this.setNumberOfReceiptsPosted(numberOfReceiptsPosted);
        return this;
    }

    public void setNumberOfReceiptsPosted(Integer numberOfReceiptsPosted) {
        this.numberOfReceiptsPosted = numberOfReceiptsPosted;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesReceiptProposal)) {
            return false;
        }
        return id != null && id.equals(((SalesReceiptProposal) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptProposal{" +
            "id=" + getId() +
            ", timeOfPosting='" + getTimeOfPosting() + "'" +
            ", postingIdentifier='" + getPostingIdentifier() + "'" +
            ", numberOfReceiptsPosted=" + getNumberOfReceiptsPosted() +
            "}";
    }
}

package io.github.calvary.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.SalesReceipt} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptDTO implements Serializable {

    private Long id;

    private String salesReceiptTitle;

    private String description;

    @NotNull
    private LocalDate transactionDate;

    private Boolean hasBeenEmailed;

    private Boolean hasBeenProposed;

    private TransactionClassDTO transactionClass;

    private DealerDTO dealer;

    private Set<TransactionItemEntryDTO> transactionItemEntries = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSalesReceiptTitle() {
        return salesReceiptTitle;
    }

    public void setSalesReceiptTitle(String salesReceiptTitle) {
        this.salesReceiptTitle = salesReceiptTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Boolean getHasBeenEmailed() {
        return hasBeenEmailed;
    }

    public void setHasBeenEmailed(Boolean hasBeenEmailed) {
        this.hasBeenEmailed = hasBeenEmailed;
    }

    public Boolean getHasBeenProposed() {
        return hasBeenProposed;
    }

    public void setHasBeenProposed(Boolean hasBeenProposed) {
        this.hasBeenProposed = hasBeenProposed;
    }

    public TransactionClassDTO getTransactionClass() {
        return transactionClass;
    }

    public void setTransactionClass(TransactionClassDTO transactionClass) {
        this.transactionClass = transactionClass;
    }

    public DealerDTO getDealer() {
        return dealer;
    }

    public void setDealer(DealerDTO dealer) {
        this.dealer = dealer;
    }

    public Set<TransactionItemEntryDTO> getTransactionItemEntries() {
        return transactionItemEntries;
    }

    public void setTransactionItemEntries(Set<TransactionItemEntryDTO> transactionItemEntries) {
        this.transactionItemEntries = transactionItemEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesReceiptDTO)) {
            return false;
        }

        SalesReceiptDTO salesReceiptDTO = (SalesReceiptDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, salesReceiptDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptDTO{" +
            "id=" + getId() +
            ", salesReceiptTitle='" + getSalesReceiptTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", hasBeenEmailed='" + getHasBeenEmailed() + "'" +
            ", hasBeenProposed='" + getHasBeenProposed() + "'" +
            ", transactionClass=" + getTransactionClass() +
            ", dealer=" + getDealer() +
            ", transactionItemEntries=" + getTransactionItemEntries() +
            "}";
    }
}

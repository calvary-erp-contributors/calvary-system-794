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

    private String description;

    @NotNull
    private LocalDate transactionDate;

    private Boolean hasBeenEmailed;

    private Boolean hasBeenProposed;

    private Boolean shouldBeEmailed;

    private TransactionClassDTO transactionClass;

    private DealerDTO dealer;

    private Set<TransactionItemEntryDTO> transactionItemEntries = new HashSet<>();

    private SalesReceiptTitleDTO salesReceiptTitle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getShouldBeEmailed() {
        return shouldBeEmailed;
    }

    public void setShouldBeEmailed(Boolean shouldBeEmailed) {
        this.shouldBeEmailed = shouldBeEmailed;
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

    public SalesReceiptTitleDTO getSalesReceiptTitle() {
        return salesReceiptTitle;
    }

    public void setSalesReceiptTitle(SalesReceiptTitleDTO salesReceiptTitle) {
        this.salesReceiptTitle = salesReceiptTitle;
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
            ", description='" + getDescription() + "'" +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", hasBeenEmailed='" + getHasBeenEmailed() + "'" +
            ", hasBeenProposed='" + getHasBeenProposed() + "'" +
            ", shouldBeEmailed='" + getShouldBeEmailed() + "'" +
            ", transactionClass=" + getTransactionClass() +
            ", dealer=" + getDealer() +
            ", transactionItemEntries=" + getTransactionItemEntries() +
            ", salesReceiptTitle=" + getSalesReceiptTitle() +
            "}";
    }
}

package io.github.calvary.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SalesReceipt.
 */
@Entity
@Table(name = "sales_receipt")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "salesreceipt")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceipt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "has_been_emailed")
    private Boolean hasBeenEmailed;

    @Column(name = "has_been_proposed")
    private Boolean hasBeenProposed;

    @Column(name = "should_be_emailed")
    private Boolean shouldBeEmailed;

    @ManyToOne
    private TransactionClass transactionClass;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "dealerType" }, allowSetters = true)
    private Dealer dealer;

    @ManyToOne(optional = false)
    @NotNull
    private SalesReceiptTitle salesReceiptTitle;

    @OneToMany(mappedBy = "salesReceipt")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "transactionItem", "salesReceipt" }, allowSetters = true)
    private Set<TransactionItemEntry> transactionItemEntries = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SalesReceipt id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public SalesReceipt description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTransactionDate() {
        return this.transactionDate;
    }

    public SalesReceipt transactionDate(LocalDate transactionDate) {
        this.setTransactionDate(transactionDate);
        return this;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Boolean getHasBeenEmailed() {
        return this.hasBeenEmailed;
    }

    public SalesReceipt hasBeenEmailed(Boolean hasBeenEmailed) {
        this.setHasBeenEmailed(hasBeenEmailed);
        return this;
    }

    public void setHasBeenEmailed(Boolean hasBeenEmailed) {
        this.hasBeenEmailed = hasBeenEmailed;
    }

    public Boolean getHasBeenProposed() {
        return this.hasBeenProposed;
    }

    public SalesReceipt hasBeenProposed(Boolean hasBeenProposed) {
        this.setHasBeenProposed(hasBeenProposed);
        return this;
    }

    public void setHasBeenProposed(Boolean hasBeenProposed) {
        this.hasBeenProposed = hasBeenProposed;
    }

    public Boolean getShouldBeEmailed() {
        return this.shouldBeEmailed;
    }

    public SalesReceipt shouldBeEmailed(Boolean shouldBeEmailed) {
        this.setShouldBeEmailed(shouldBeEmailed);
        return this;
    }

    public void setShouldBeEmailed(Boolean shouldBeEmailed) {
        this.shouldBeEmailed = shouldBeEmailed;
    }

    public TransactionClass getTransactionClass() {
        return this.transactionClass;
    }

    public void setTransactionClass(TransactionClass transactionClass) {
        this.transactionClass = transactionClass;
    }

    public SalesReceipt transactionClass(TransactionClass transactionClass) {
        this.setTransactionClass(transactionClass);
        return this;
    }

    public Dealer getDealer() {
        return this.dealer;
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    public SalesReceipt dealer(Dealer dealer) {
        this.setDealer(dealer);
        return this;
    }

    public SalesReceiptTitle getSalesReceiptTitle() {
        return this.salesReceiptTitle;
    }

    public void setSalesReceiptTitle(SalesReceiptTitle salesReceiptTitle) {
        this.salesReceiptTitle = salesReceiptTitle;
    }

    public SalesReceipt salesReceiptTitle(SalesReceiptTitle salesReceiptTitle) {
        this.setSalesReceiptTitle(salesReceiptTitle);
        return this;
    }

    public Set<TransactionItemEntry> getTransactionItemEntries() {
        return this.transactionItemEntries;
    }

    public void setTransactionItemEntries(Set<TransactionItemEntry> transactionItemEntries) {
        if (this.transactionItemEntries != null) {
            this.transactionItemEntries.forEach(i -> i.setSalesReceipt(null));
        }
        if (transactionItemEntries != null) {
            transactionItemEntries.forEach(i -> i.setSalesReceipt(this));
        }
        this.transactionItemEntries = transactionItemEntries;
    }

    public SalesReceipt transactionItemEntries(Set<TransactionItemEntry> transactionItemEntries) {
        this.setTransactionItemEntries(transactionItemEntries);
        return this;
    }

    public SalesReceipt addTransactionItemEntry(TransactionItemEntry transactionItemEntry) {
        this.transactionItemEntries.add(transactionItemEntry);
        transactionItemEntry.setSalesReceipt(this);
        return this;
    }

    public SalesReceipt removeTransactionItemEntry(TransactionItemEntry transactionItemEntry) {
        this.transactionItemEntries.remove(transactionItemEntry);
        transactionItemEntry.setSalesReceipt(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesReceipt)) {
            return false;
        }
        return id != null && id.equals(((SalesReceipt) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceipt{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", hasBeenEmailed='" + getHasBeenEmailed() + "'" +
            ", hasBeenProposed='" + getHasBeenProposed() + "'" +
            ", shouldBeEmailed='" + getShouldBeEmailed() + "'" +
            "}";
    }
}

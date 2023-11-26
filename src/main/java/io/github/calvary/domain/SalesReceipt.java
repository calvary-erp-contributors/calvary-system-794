package io.github.calvary.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
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

    @Column(name = "sales_receipt_title")
    private String salesReceiptTitle;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private TransactionClass transactionClass;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "dealerType" }, allowSetters = true)
    private Dealer dealer;

    @ManyToMany
    @NotNull
    @JoinTable(
        name = "rel_sales_receipt__transaction_item_entry",
        joinColumns = @JoinColumn(name = "sales_receipt_id"),
        inverseJoinColumns = @JoinColumn(name = "transaction_item_entry_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "transactionItem" }, allowSetters = true)
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

    public String getSalesReceiptTitle() {
        return this.salesReceiptTitle;
    }

    public SalesReceipt salesReceiptTitle(String salesReceiptTitle) {
        this.setSalesReceiptTitle(salesReceiptTitle);
        return this;
    }

    public void setSalesReceiptTitle(String salesReceiptTitle) {
        this.salesReceiptTitle = salesReceiptTitle;
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

    public Set<TransactionItemEntry> getTransactionItemEntries() {
        return this.transactionItemEntries;
    }

    public void setTransactionItemEntries(Set<TransactionItemEntry> transactionItemEntries) {
        this.transactionItemEntries = transactionItemEntries;
    }

    public SalesReceipt transactionItemEntries(Set<TransactionItemEntry> transactionItemEntries) {
        this.setTransactionItemEntries(transactionItemEntries);
        return this;
    }

    public SalesReceipt addTransactionItemEntry(TransactionItemEntry transactionItemEntry) {
        this.transactionItemEntries.add(transactionItemEntry);
        return this;
    }

    public SalesReceipt removeTransactionItemEntry(TransactionItemEntry transactionItemEntry) {
        this.transactionItemEntries.remove(transactionItemEntry);
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
            ", salesReceiptTitle='" + getSalesReceiptTitle() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}

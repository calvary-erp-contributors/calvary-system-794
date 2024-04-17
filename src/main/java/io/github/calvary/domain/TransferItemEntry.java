package io.github.calvary.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TransferItemEntry.
 */
@Entity
@Table(name = "transfer_item_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "transferitementry")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransferItemEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "item_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal itemAmount;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "transactionClass", "dealer", "salesReceiptTitle", "transactionItemEntries", "transferItemEntries" },
        allowSetters = true
    )
    private SalesReceipt salesReceipt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "transactionClass", "transactionAccount" }, allowSetters = true)
    private TransferItem transferItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TransferItemEntry id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public TransferItemEntry description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getItemAmount() {
        return this.itemAmount;
    }

    public TransferItemEntry itemAmount(BigDecimal itemAmount) {
        this.setItemAmount(itemAmount);
        return this;
    }

    public void setItemAmount(BigDecimal itemAmount) {
        this.itemAmount = itemAmount;
    }

    public SalesReceipt getSalesReceipt() {
        return this.salesReceipt;
    }

    public void setSalesReceipt(SalesReceipt salesReceipt) {
        this.salesReceipt = salesReceipt;
    }

    public TransferItemEntry salesReceipt(SalesReceipt salesReceipt) {
        this.setSalesReceipt(salesReceipt);
        return this;
    }

    public TransferItem getTransferItem() {
        return this.transferItem;
    }

    public void setTransferItem(TransferItem transferItem) {
        this.transferItem = transferItem;
    }

    public TransferItemEntry transferItem(TransferItem transferItem) {
        this.setTransferItem(transferItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransferItemEntry)) {
            return false;
        }
        return id != null && id.equals(((TransferItemEntry) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransferItemEntry{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", itemAmount=" + getItemAmount() +
            "}";
    }
}

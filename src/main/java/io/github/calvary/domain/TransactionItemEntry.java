package io.github.calvary.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TransactionItemEntry.
 */
@Entity
@Table(name = "transaction_item_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "transactionitementry")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionItemEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "item_amount", precision = 21, scale = 2)
    private BigDecimal itemAmount;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "transactionClass", "transactionAccount" }, allowSetters = true)
    private TransactionItem transactionItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TransactionItemEntry id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public TransactionItemEntry description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getItemAmount() {
        return this.itemAmount;
    }

    public TransactionItemEntry itemAmount(BigDecimal itemAmount) {
        this.setItemAmount(itemAmount);
        return this;
    }

    public void setItemAmount(BigDecimal itemAmount) {
        this.itemAmount = itemAmount;
    }

    public TransactionItem getTransactionItem() {
        return this.transactionItem;
    }

    public void setTransactionItem(TransactionItem transactionItem) {
        this.transactionItem = transactionItem;
    }

    public TransactionItemEntry transactionItem(TransactionItem transactionItem) {
        this.setTransactionItem(transactionItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionItemEntry)) {
            return false;
        }
        return id != null && id.equals(((TransactionItemEntry) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionItemEntry{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", itemAmount=" + getItemAmount() +
            "}";
    }
}

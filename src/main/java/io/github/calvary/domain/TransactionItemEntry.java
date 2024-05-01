package io.github.calvary.domain;

/*-
 * Copyright © 2023 - 2024 Calvary ERP Contributors (mailnjeru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
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

    @ManyToOne
    @JsonIgnoreProperties(value = { "transactionClass", "transactionAccount" }, allowSetters = true)
    private TransactionItem transactionItem;

    @ManyToOne
    @JsonIgnoreProperties(
        value = { "transactionClass", "dealer", "salesReceiptTitle", "transactionItemEntries", "transferItemEntries" },
        allowSetters = true
    )
    private SalesReceipt salesReceipt;

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

    public SalesReceipt getSalesReceipt() {
        return this.salesReceipt;
    }

    public void setSalesReceipt(SalesReceipt salesReceipt) {
        this.salesReceipt = salesReceipt;
    }

    public TransactionItemEntry salesReceipt(SalesReceipt salesReceipt) {
        this.setSalesReceipt(salesReceipt);
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

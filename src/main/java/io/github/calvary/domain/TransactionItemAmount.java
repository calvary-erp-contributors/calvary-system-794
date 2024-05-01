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
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TransactionItemAmount.
 */
@Entity
@Table(name = "transaction_item_amount")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "transactionitemamount")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionItemAmount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "transaction_item_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal transactionItemAmount;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "transactionClass", "transactionAccount" }, allowSetters = true)
    private TransactionItem transactionItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TransactionItemAmount id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTransactionItemAmount() {
        return this.transactionItemAmount;
    }

    public TransactionItemAmount transactionItemAmount(BigDecimal transactionItemAmount) {
        this.setTransactionItemAmount(transactionItemAmount);
        return this;
    }

    public void setTransactionItemAmount(BigDecimal transactionItemAmount) {
        this.transactionItemAmount = transactionItemAmount;
    }

    public TransactionItem getTransactionItem() {
        return this.transactionItem;
    }

    public void setTransactionItem(TransactionItem transactionItem) {
        this.transactionItem = transactionItem;
    }

    public TransactionItemAmount transactionItem(TransactionItem transactionItem) {
        this.setTransactionItem(transactionItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionItemAmount)) {
            return false;
        }
        return id != null && id.equals(((TransactionItemAmount) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionItemAmount{" +
            "id=" + getId() +
            ", transactionItemAmount=" + getTransactionItemAmount() +
            "}";
    }
}

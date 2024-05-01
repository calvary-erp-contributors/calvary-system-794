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
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TransactionItem.
 */
@Entity
@Table(name = "transaction_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "transactionitem")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "item_name", nullable = false, unique = true)
    private String itemName;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private TransactionClass transactionClass;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "parentAccount", "transactionAccountType", "transactionCurrency" }, allowSetters = true)
    private TransactionAccount transactionAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TransactionItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return this.itemName;
    }

    public TransactionItem itemName(String itemName) {
        this.setItemName(itemName);
        return this;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return this.description;
    }

    public TransactionItem description(String description) {
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

    public TransactionItem transactionClass(TransactionClass transactionClass) {
        this.setTransactionClass(transactionClass);
        return this;
    }

    public TransactionAccount getTransactionAccount() {
        return this.transactionAccount;
    }

    public void setTransactionAccount(TransactionAccount transactionAccount) {
        this.transactionAccount = transactionAccount;
    }

    public TransactionItem transactionAccount(TransactionAccount transactionAccount) {
        this.setTransactionAccount(transactionAccount);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionItem)) {
            return false;
        }
        return id != null && id.equals(((TransactionItem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionItem{" +
            "id=" + getId() +
            ", itemName='" + getItemName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}

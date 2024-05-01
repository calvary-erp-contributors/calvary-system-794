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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Dealer.
 */
@Entity
@Table(name = "dealer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "dealer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Dealer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "main_email")
    private String mainEmail;

    @Column(name = "dealer_reference")
    private UUID dealerReference;

    @ManyToOne(optional = false)
    @NotNull
    private DealerType dealerType;

    @ManyToMany
    @JoinTable(
        name = "rel_dealer__sales_receipt_email_persona",
        joinColumns = @JoinColumn(name = "dealer_id"),
        inverseJoinColumns = @JoinColumn(name = "sales_receipt_email_persona_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "createdBy", "lastModifiedBy", "contributor" }, allowSetters = true)
    private Set<SalesReceiptEmailPersona> salesReceiptEmailPersonas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Dealer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Dealer name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainEmail() {
        return this.mainEmail;
    }

    public Dealer mainEmail(String mainEmail) {
        this.setMainEmail(mainEmail);
        return this;
    }

    public void setMainEmail(String mainEmail) {
        this.mainEmail = mainEmail;
    }

    public UUID getDealerReference() {
        return this.dealerReference;
    }

    public Dealer dealerReference(UUID dealerReference) {
        this.setDealerReference(dealerReference);
        return this;
    }

    public void setDealerReference(UUID dealerReference) {
        this.dealerReference = dealerReference;
    }

    public DealerType getDealerType() {
        return this.dealerType;
    }

    public void setDealerType(DealerType dealerType) {
        this.dealerType = dealerType;
    }

    public Dealer dealerType(DealerType dealerType) {
        this.setDealerType(dealerType);
        return this;
    }

    public Set<SalesReceiptEmailPersona> getSalesReceiptEmailPersonas() {
        return this.salesReceiptEmailPersonas;
    }

    public void setSalesReceiptEmailPersonas(Set<SalesReceiptEmailPersona> salesReceiptEmailPersonas) {
        this.salesReceiptEmailPersonas = salesReceiptEmailPersonas;
    }

    public Dealer salesReceiptEmailPersonas(Set<SalesReceiptEmailPersona> salesReceiptEmailPersonas) {
        this.setSalesReceiptEmailPersonas(salesReceiptEmailPersonas);
        return this;
    }

    public Dealer addSalesReceiptEmailPersona(SalesReceiptEmailPersona salesReceiptEmailPersona) {
        this.salesReceiptEmailPersonas.add(salesReceiptEmailPersona);
        return this;
    }

    public Dealer removeSalesReceiptEmailPersona(SalesReceiptEmailPersona salesReceiptEmailPersona) {
        this.salesReceiptEmailPersonas.remove(salesReceiptEmailPersona);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dealer)) {
            return false;
        }
        return id != null && id.equals(((Dealer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dealer{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", mainEmail='" + getMainEmail() + "'" +
            ", dealerReference='" + getDealerReference() + "'" +
            "}";
    }
}

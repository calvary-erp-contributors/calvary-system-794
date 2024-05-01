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
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ReceiptEmailRequest.
 */
@Entity
@Table(name = "receipt_email_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "receiptemailrequest")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReceiptEmailRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "time_of_requisition", nullable = false)
    private ZonedDateTime timeOfRequisition;

    @Column(name = "upload_complete")
    private Boolean uploadComplete;

    @Column(name = "number_of_updates")
    private Integer numberOfUpdates;

    @ManyToOne
    @JsonIgnoreProperties(value = { "createdBy", "lastModifiedBy", "systemIdentity" }, allowSetters = true)
    private ApplicationUser requestedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReceiptEmailRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getTimeOfRequisition() {
        return this.timeOfRequisition;
    }

    public ReceiptEmailRequest timeOfRequisition(ZonedDateTime timeOfRequisition) {
        this.setTimeOfRequisition(timeOfRequisition);
        return this;
    }

    public void setTimeOfRequisition(ZonedDateTime timeOfRequisition) {
        this.timeOfRequisition = timeOfRequisition;
    }

    public Boolean getUploadComplete() {
        return this.uploadComplete;
    }

    public ReceiptEmailRequest uploadComplete(Boolean uploadComplete) {
        this.setUploadComplete(uploadComplete);
        return this;
    }

    public void setUploadComplete(Boolean uploadComplete) {
        this.uploadComplete = uploadComplete;
    }

    public Integer getNumberOfUpdates() {
        return this.numberOfUpdates;
    }

    public ReceiptEmailRequest numberOfUpdates(Integer numberOfUpdates) {
        this.setNumberOfUpdates(numberOfUpdates);
        return this;
    }

    public void setNumberOfUpdates(Integer numberOfUpdates) {
        this.numberOfUpdates = numberOfUpdates;
    }

    public ApplicationUser getRequestedBy() {
        return this.requestedBy;
    }

    public void setRequestedBy(ApplicationUser applicationUser) {
        this.requestedBy = applicationUser;
    }

    public ReceiptEmailRequest requestedBy(ApplicationUser applicationUser) {
        this.setRequestedBy(applicationUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReceiptEmailRequest)) {
            return false;
        }
        return id != null && id.equals(((ReceiptEmailRequest) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReceiptEmailRequest{" +
            "id=" + getId() +
            ", timeOfRequisition='" + getTimeOfRequisition() + "'" +
            ", uploadComplete='" + getUploadComplete() + "'" +
            ", numberOfUpdates=" + getNumberOfUpdates() +
            "}";
    }
}

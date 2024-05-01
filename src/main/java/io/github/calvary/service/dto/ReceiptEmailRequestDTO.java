package io.github.calvary.service.dto;

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

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.ReceiptEmailRequest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReceiptEmailRequestDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime timeOfRequisition;

    private Boolean uploadComplete;

    private Integer numberOfUpdates;

    private ApplicationUserDTO requestedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getTimeOfRequisition() {
        return timeOfRequisition;
    }

    public void setTimeOfRequisition(ZonedDateTime timeOfRequisition) {
        this.timeOfRequisition = timeOfRequisition;
    }

    public Boolean getUploadComplete() {
        return uploadComplete;
    }

    public void setUploadComplete(Boolean uploadComplete) {
        this.uploadComplete = uploadComplete;
    }

    public Integer getNumberOfUpdates() {
        return numberOfUpdates;
    }

    public void setNumberOfUpdates(Integer numberOfUpdates) {
        this.numberOfUpdates = numberOfUpdates;
    }

    public ApplicationUserDTO getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(ApplicationUserDTO requestedBy) {
        this.requestedBy = requestedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReceiptEmailRequestDTO)) {
            return false;
        }

        ReceiptEmailRequestDTO receiptEmailRequestDTO = (ReceiptEmailRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, receiptEmailRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReceiptEmailRequestDTO{" +
            "id=" + getId() +
            ", timeOfRequisition='" + getTimeOfRequisition() + "'" +
            ", uploadComplete='" + getUploadComplete() + "'" +
            ", numberOfUpdates=" + getNumberOfUpdates() +
            ", requestedBy=" + getRequestedBy() +
            "}";
    }
}

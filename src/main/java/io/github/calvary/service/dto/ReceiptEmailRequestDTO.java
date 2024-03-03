package io.github.calvary.service.dto;

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

package io.github.calvary.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.SalesReceiptTitle} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptTitleDTO implements Serializable {

    private Long id;

    @NotNull
    private String receiptTitle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiptTitle() {
        return receiptTitle;
    }

    public void setReceiptTitle(String receiptTitle) {
        this.receiptTitle = receiptTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesReceiptTitleDTO)) {
            return false;
        }

        SalesReceiptTitleDTO salesReceiptTitleDTO = (SalesReceiptTitleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, salesReceiptTitleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptTitleDTO{" +
            "id=" + getId() +
            ", receiptTitle='" + getReceiptTitle() + "'" +
            "}";
    }
}

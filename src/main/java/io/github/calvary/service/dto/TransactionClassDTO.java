package io.github.calvary.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.TransactionClass} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionClassDTO implements Serializable {

    private Long id;

    @NotNull
    private String className;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionClassDTO)) {
            return false;
        }

        TransactionClassDTO transactionClassDTO = (TransactionClassDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionClassDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionClassDTO{" +
            "id=" + getId() +
            ", className='" + getClassName() + "'" +
            "}";
    }
}

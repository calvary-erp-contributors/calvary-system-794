package io.github.calvary.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.TransactionItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionItemDTO implements Serializable {

    private Long id;

    @NotNull
    private String itemName;

    private String description;

    private TransactionClassDTO transactionClass;

    private TransactionAccountDTO transactionAccount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionClassDTO getTransactionClass() {
        return transactionClass;
    }

    public void setTransactionClass(TransactionClassDTO transactionClass) {
        this.transactionClass = transactionClass;
    }

    public TransactionAccountDTO getTransactionAccount() {
        return transactionAccount;
    }

    public void setTransactionAccount(TransactionAccountDTO transactionAccount) {
        this.transactionAccount = transactionAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionItemDTO)) {
            return false;
        }

        TransactionItemDTO transactionItemDTO = (TransactionItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionItemDTO{" +
            "id=" + getId() +
            ", itemName='" + getItemName() + "'" +
            ", description='" + getDescription() + "'" +
            ", transactionClass=" + getTransactionClass() +
            ", transactionAccount=" + getTransactionAccount() +
            "}";
    }
}

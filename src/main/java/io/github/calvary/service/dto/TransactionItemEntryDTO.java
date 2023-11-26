package io.github.calvary.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.TransactionItemEntry} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionItemEntryDTO implements Serializable {

    private Long id;

    private String description;

    private BigDecimal itemAmount;

    private TransactionItemDTO transactionItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(BigDecimal itemAmount) {
        this.itemAmount = itemAmount;
    }

    public TransactionItemDTO getTransactionItem() {
        return transactionItem;
    }

    public void setTransactionItem(TransactionItemDTO transactionItem) {
        this.transactionItem = transactionItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionItemEntryDTO)) {
            return false;
        }

        TransactionItemEntryDTO transactionItemEntryDTO = (TransactionItemEntryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionItemEntryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionItemEntryDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", itemAmount=" + getItemAmount() +
            ", transactionItem=" + getTransactionItem() +
            "}";
    }
}

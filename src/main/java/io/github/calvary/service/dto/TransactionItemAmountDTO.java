package io.github.calvary.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.TransactionItemAmount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionItemAmountDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal transactionItemAmount;

    private TransactionItemDTO transactionItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTransactionItemAmount() {
        return transactionItemAmount;
    }

    public void setTransactionItemAmount(BigDecimal transactionItemAmount) {
        this.transactionItemAmount = transactionItemAmount;
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
        if (!(o instanceof TransactionItemAmountDTO)) {
            return false;
        }

        TransactionItemAmountDTO transactionItemAmountDTO = (TransactionItemAmountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionItemAmountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionItemAmountDTO{" +
            "id=" + getId() +
            ", transactionItemAmount=" + getTransactionItemAmount() +
            ", transactionItem=" + getTransactionItem() +
            "}";
    }
}

package io.github.calvary.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.TransferItemEntry} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransferItemEntryDTO implements Serializable {

    private Long id;

    private String description;

    @NotNull
    private BigDecimal itemAmount;

    private SalesReceiptDTO salesReceipt;

    private TransferItemDTO transferItem;

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

    public SalesReceiptDTO getSalesReceipt() {
        return salesReceipt;
    }

    public void setSalesReceipt(SalesReceiptDTO salesReceipt) {
        this.salesReceipt = salesReceipt;
    }

    public TransferItemDTO getTransferItem() {
        return transferItem;
    }

    public void setTransferItem(TransferItemDTO transferItem) {
        this.transferItem = transferItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransferItemEntryDTO)) {
            return false;
        }

        TransferItemEntryDTO transferItemEntryDTO = (TransferItemEntryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transferItemEntryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransferItemEntryDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", itemAmount=" + getItemAmount() +
            ", salesReceipt=" + getSalesReceipt() +
            ", transferItem=" + getTransferItem() +
            "}";
    }
}

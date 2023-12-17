package io.github.calvary.service.mapper;

import io.github.calvary.domain.SalesReceipt;
import io.github.calvary.domain.TransactionItem;
import io.github.calvary.domain.TransferItemEntry;
import io.github.calvary.service.dto.SalesReceiptDTO;
import io.github.calvary.service.dto.TransactionItemDTO;
import io.github.calvary.service.dto.TransferItemEntryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransferItemEntry} and its DTO {@link TransferItemEntryDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransferItemEntryMapper extends EntityMapper<TransferItemEntryDTO, TransferItemEntry> {
    @Mapping(target = "transactionItem", source = "transactionItem", qualifiedByName = "transactionItemItemName")
    @Mapping(target = "salesReceipt", source = "salesReceipt", qualifiedByName = "salesReceiptId")
    TransferItemEntryDTO toDto(TransferItemEntry s);

    @Named("transactionItemItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "itemName", source = "itemName")
    TransactionItemDTO toDtoTransactionItemItemName(TransactionItem transactionItem);

    @Named("salesReceiptId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SalesReceiptDTO toDtoSalesReceiptId(SalesReceipt salesReceipt);
}

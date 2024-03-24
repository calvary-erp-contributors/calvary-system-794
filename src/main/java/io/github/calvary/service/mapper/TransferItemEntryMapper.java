package io.github.calvary.service.mapper;

import io.github.calvary.domain.SalesReceipt;
import io.github.calvary.domain.TransferItem;
import io.github.calvary.domain.TransferItemEntry;
import io.github.calvary.service.dto.SalesReceiptDTO;
import io.github.calvary.service.dto.TransferItemDTO;
import io.github.calvary.service.dto.TransferItemEntryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransferItemEntry} and its DTO {@link TransferItemEntryDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransferItemEntryMapper extends EntityMapper<TransferItemEntryDTO, TransferItemEntry> {
    @Mapping(target = "salesReceipt", source = "salesReceipt", qualifiedByName = "salesReceiptId")
    @Mapping(target = "transferItem", source = "transferItem", qualifiedByName = "transferItemItemName")
    TransferItemEntryDTO toDto(TransferItemEntry s);

    @Named("salesReceiptId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SalesReceiptDTO toDtoSalesReceiptId(SalesReceipt salesReceipt);

    @Named("transferItemItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "itemName", source = "itemName")
    TransferItemDTO toDtoTransferItemItemName(TransferItem transferItem);
}

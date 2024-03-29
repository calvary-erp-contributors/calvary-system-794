package io.github.calvary.service.mapper;

import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.domain.TransactionClass;
import io.github.calvary.domain.TransferItem;
import io.github.calvary.service.dto.TransactionAccountDTO;
import io.github.calvary.service.dto.TransactionClassDTO;
import io.github.calvary.service.dto.TransferItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransferItem} and its DTO {@link TransferItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransferItemMapper extends EntityMapper<TransferItemDTO, TransferItem> {
    @Mapping(target = "transactionClass", source = "transactionClass", qualifiedByName = "transactionClassClassName")
    @Mapping(target = "transactionAccount", source = "transactionAccount", qualifiedByName = "transactionAccountAccountName")
    TransferItemDTO toDto(TransferItem s);

    @Named("transactionClassClassName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "className", source = "className")
    TransactionClassDTO toDtoTransactionClassClassName(TransactionClass transactionClass);

    @Named("transactionAccountAccountName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "accountName", source = "accountName")
    TransactionAccountDTO toDtoTransactionAccountAccountName(TransactionAccount transactionAccount);
}

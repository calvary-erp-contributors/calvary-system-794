package io.github.calvary.service.mapper;

import io.github.calvary.domain.TransactionItem;
import io.github.calvary.domain.TransactionItemAmount;
import io.github.calvary.service.dto.TransactionItemAmountDTO;
import io.github.calvary.service.dto.TransactionItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransactionItemAmount} and its DTO {@link TransactionItemAmountDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionItemAmountMapper extends EntityMapper<TransactionItemAmountDTO, TransactionItemAmount> {
    @Mapping(target = "transactionItem", source = "transactionItem", qualifiedByName = "transactionItemItemName")
    TransactionItemAmountDTO toDto(TransactionItemAmount s);

    @Named("transactionItemItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "itemName", source = "itemName")
    TransactionItemDTO toDtoTransactionItemItemName(TransactionItem transactionItem);
}

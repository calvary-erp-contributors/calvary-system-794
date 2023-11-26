package io.github.calvary.service.mapper;

import io.github.calvary.domain.Dealer;
import io.github.calvary.domain.SalesReceipt;
import io.github.calvary.domain.TransactionClass;
import io.github.calvary.domain.TransactionItemAmount;
import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.dto.SalesReceiptDTO;
import io.github.calvary.service.dto.TransactionClassDTO;
import io.github.calvary.service.dto.TransactionItemAmountDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SalesReceipt} and its DTO {@link SalesReceiptDTO}.
 */
@Mapper(componentModel = "spring")
public interface SalesReceiptMapper extends EntityMapper<SalesReceiptDTO, SalesReceipt> {
    @Mapping(target = "transactionClass", source = "transactionClass", qualifiedByName = "transactionClassClassName")
    @Mapping(target = "dealer", source = "dealer", qualifiedByName = "dealerName")
    @Mapping(
        target = "transactionItemAmounts",
        source = "transactionItemAmounts",
        qualifiedByName = "transactionItemAmountTransactionItemAmountSet"
    )
    SalesReceiptDTO toDto(SalesReceipt s);

    @Mapping(target = "removeTransactionItemAmount", ignore = true)
    SalesReceipt toEntity(SalesReceiptDTO salesReceiptDTO);

    @Named("transactionClassClassName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "className", source = "className")
    TransactionClassDTO toDtoTransactionClassClassName(TransactionClass transactionClass);

    @Named("dealerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DealerDTO toDtoDealerName(Dealer dealer);

    @Named("transactionItemAmountTransactionItemAmount")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "transactionItemAmount", source = "transactionItemAmount")
    TransactionItemAmountDTO toDtoTransactionItemAmountTransactionItemAmount(TransactionItemAmount transactionItemAmount);

    @Named("transactionItemAmountTransactionItemAmountSet")
    default Set<TransactionItemAmountDTO> toDtoTransactionItemAmountTransactionItemAmountSet(
        Set<TransactionItemAmount> transactionItemAmount
    ) {
        return transactionItemAmount.stream().map(this::toDtoTransactionItemAmountTransactionItemAmount).collect(Collectors.toSet());
    }
}

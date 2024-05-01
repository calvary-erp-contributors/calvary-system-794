package io.github.calvary.service.mapper;

/*-
 * Copyright © 2023 - 2024 Calvary ERP Contributors (mailnjeru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.github.calvary.domain.Dealer;
import io.github.calvary.domain.SalesReceipt;
import io.github.calvary.domain.SalesReceiptTitle;
import io.github.calvary.domain.TransactionClass;
import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.dto.SalesReceiptDTO;
import io.github.calvary.service.dto.SalesReceiptTitleDTO;
import io.github.calvary.service.dto.TransactionClassDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SalesReceipt} and its DTO {@link SalesReceiptDTO}.
 */
@Mapper(componentModel = "spring")
public interface SalesReceiptMapper extends EntityMapper<SalesReceiptDTO, SalesReceipt> {
    @Mapping(target = "transactionClass", source = "transactionClass", qualifiedByName = "transactionClassClassName")
    @Mapping(target = "dealer", source = "dealer", qualifiedByName = "dealerName")
    @Mapping(target = "salesReceiptTitle", source = "salesReceiptTitle", qualifiedByName = "salesReceiptTitleReceiptTitle")
    SalesReceiptDTO toDto(SalesReceipt s);

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

    @Named("salesReceiptTitleReceiptTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "receiptTitle", source = "receiptTitle")
    SalesReceiptTitleDTO toDtoSalesReceiptTitleReceiptTitle(SalesReceiptTitle salesReceiptTitle);
}

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

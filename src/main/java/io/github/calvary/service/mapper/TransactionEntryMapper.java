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

import io.github.calvary.domain.AccountTransaction;
import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.domain.TransactionEntry;
import io.github.calvary.service.dto.AccountTransactionDTO;
import io.github.calvary.service.dto.TransactionAccountDTO;
import io.github.calvary.service.dto.TransactionEntryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransactionEntry} and its DTO {@link TransactionEntryDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionEntryMapper extends EntityMapper<TransactionEntryDTO, TransactionEntry> {
    @Mapping(target = "transactionAccount", source = "transactionAccount", qualifiedByName = "transactionAccountAccountName")
    @Mapping(target = "accountTransaction", source = "accountTransaction", qualifiedByName = "accountTransactionReferenceNumber")
    TransactionEntryDTO toDto(TransactionEntry s);

    @Named("transactionAccountAccountName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "accountName", source = "accountName")
    TransactionAccountDTO toDtoTransactionAccountAccountName(TransactionAccount transactionAccount);

    @Named("accountTransactionReferenceNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "referenceNumber", source = "referenceNumber")
    AccountTransactionDTO toDtoAccountTransactionReferenceNumber(AccountTransaction accountTransaction);
}

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

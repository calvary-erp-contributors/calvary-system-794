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

import io.github.calvary.domain.AccountingEvent;
import io.github.calvary.domain.Dealer;
import io.github.calvary.domain.EventType;
import io.github.calvary.service.dto.AccountingEventDTO;
import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.dto.EventTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AccountingEvent} and its DTO {@link AccountingEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface AccountingEventMapper extends EntityMapper<AccountingEventDTO, AccountingEvent> {
    @Mapping(target = "eventType", source = "eventType", qualifiedByName = "eventTypeName")
    @Mapping(target = "dealer", source = "dealer", qualifiedByName = "dealerName")
    AccountingEventDTO toDto(AccountingEvent s);

    @Named("eventTypeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    EventTypeDTO toDtoEventTypeName(EventType eventType);

    @Named("dealerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DealerDTO toDtoDealerName(Dealer dealer);
}

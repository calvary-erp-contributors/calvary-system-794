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

import io.github.calvary.domain.TransactionClass;
import io.github.calvary.service.dto.TransactionClassDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransactionClass} and its DTO {@link TransactionClassDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionClassMapper extends EntityMapper<TransactionClassDTO, TransactionClass> {}

package io.github.calvary.erp;

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
import io.github.calvary.erp.queue.TransactionEntryMessage;
import io.github.calvary.repository.BalanceSheetItemTypeRepository;
import io.github.calvary.repository.TransactionAccountRepository;
import io.github.calvary.service.BalanceSheetItemValueService;
import io.github.calvary.service.TransactionEntryService;
import io.github.calvary.service.dto.BalanceSheetItemValueDTO;
import io.github.calvary.service.mapper.BalanceSheetItemTypeMapper;
import java.time.LocalDate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BalanceSheetItemUpdateService implements BalanceSheetUpdateService {

    private final TransactionEntryService transactionEntryService;
    private final BalanceSheetItemValueService balanceSheetItemValueService;
    private final TransactionAccountRepository transactionAccountRepository;
    private final BalanceSheetItemTypeRepository balanceSheetItemTypeRepository;
    private final BalanceSheetItemTypeMapper balanceSheetItemTypeMapper;

    public BalanceSheetItemUpdateService(
        TransactionEntryService transactionEntryService,
        BalanceSheetItemValueService balanceSheetItemValueService,
        TransactionAccountRepository transactionAccountRepository,
        BalanceSheetItemTypeRepository balanceSheetItemTypeRepository,
        BalanceSheetItemTypeMapper balanceSheetItemTypeMapper
    ) {
        this.transactionEntryService = transactionEntryService;
        this.balanceSheetItemValueService = balanceSheetItemValueService;
        this.transactionAccountRepository = transactionAccountRepository;
        this.balanceSheetItemTypeRepository = balanceSheetItemTypeRepository;
        this.balanceSheetItemTypeMapper = balanceSheetItemTypeMapper;
    }

    @Async
    @Override
    public void update(TransactionEntryMessage message) {
        //        transactionEntryService.findOne(message.getId()).ifPresent(transactionEntryDTO -> {
        //
        //            transactionAccountRepository
        //                .findById(message.getTransactionAccountId())
        //                .flatMap(balanceSheetItemTypeRepository::findBalanceSheetItemTypeByTransactionAccountEquals)
        //                .ifPresent(itemType -> {
        //
        //                BalanceSheetItemValueDTO itemValueDTO = new BalanceSheetItemValueDTO();
        //
        //                itemValueDTO.setShortDescription(transactionEntryDTO.getDescription());
        //                // TODO Add date to entries
        //                itemValueDTO.setEffectiveDate(LocalDate.now());
        //                // TODO update account posting
        //                itemValueDTO.setItemAmount(transactionEntryDTO.getEntryAmount());
        //
        //                itemValueDTO.setItemType(balanceSheetItemTypeMapper.toDto(itemType));
        //
        //                balanceSheetItemValueService.save(itemValueDTO);
        //            });
        //
        //        });
    }
}

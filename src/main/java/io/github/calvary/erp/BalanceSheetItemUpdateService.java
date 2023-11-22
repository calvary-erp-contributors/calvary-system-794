package io.github.calvary.erp;

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

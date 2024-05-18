package io.github.calvary.erp.internal;

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

import io.github.calvary.domain.AccountBalanceReportItem;
import io.github.calvary.erp.internal.shared.AccountBalanceReportItemInternal;
import io.github.calvary.erp.internal.shared.Mapping;
import io.github.calvary.erp.repository.InternalAccountBalanceReportItemRepository;
import io.github.calvary.repository.search.AccountBalanceReportItemSearchRepository;
import io.github.calvary.service.dto.AccountBalanceReportItemDTO;
import io.github.calvary.service.mapper.AccountBalanceReportItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AccountBalanceReportItem}.
 */
@Service
@Transactional
public class InternalAccountBalanceReportItemServiceImpl implements InternalAccountBalanceReportItemService {

    private final Logger log = LoggerFactory.getLogger(InternalAccountBalanceReportItemServiceImpl.class);

    private final InternalAccountBalanceReportItemRepository accountBalanceReportItemRepository;

    private final AccountBalanceReportItemMapper accountBalanceReportItemMapper;

    private final Mapping<AccountBalanceReportItemInternal, AccountBalanceReportItemDTO> accountBalanceReportItemInternalMapping;

    private final AccountBalanceReportItemSearchRepository accountBalanceReportItemSearchRepository;

    public InternalAccountBalanceReportItemServiceImpl(
        InternalAccountBalanceReportItemRepository accountBalanceReportItemRepository,
        AccountBalanceReportItemMapper accountBalanceReportItemMapper,
        Mapping<AccountBalanceReportItemInternal, AccountBalanceReportItemDTO> accountBalanceReportItemInternalMapping,
        AccountBalanceReportItemSearchRepository accountBalanceReportItemSearchRepository
    ) {
        this.accountBalanceReportItemRepository = accountBalanceReportItemRepository;
        this.accountBalanceReportItemMapper = accountBalanceReportItemMapper;
        this.accountBalanceReportItemInternalMapping = accountBalanceReportItemInternalMapping;
        this.accountBalanceReportItemSearchRepository = accountBalanceReportItemSearchRepository;
    }

    @Override
    public AccountBalanceReportItemDTO save(AccountBalanceReportItemDTO accountBalanceReportItemDTO) {
        log.debug("Request to save AccountBalanceReportItem : {}", accountBalanceReportItemDTO);
        AccountBalanceReportItem accountBalanceReportItem = accountBalanceReportItemMapper.toEntity(accountBalanceReportItemDTO);
        accountBalanceReportItem = accountBalanceReportItemRepository.save(accountBalanceReportItem);
        AccountBalanceReportItemDTO result = accountBalanceReportItemMapper.toDto(accountBalanceReportItem);
        accountBalanceReportItemSearchRepository.index(accountBalanceReportItem);
        return result;
    }

    @Override
    public AccountBalanceReportItemDTO update(AccountBalanceReportItemDTO accountBalanceReportItemDTO) {
        log.debug("Request to update AccountBalanceReportItem : {}", accountBalanceReportItemDTO);
        AccountBalanceReportItem accountBalanceReportItem = accountBalanceReportItemMapper.toEntity(accountBalanceReportItemDTO);
        accountBalanceReportItem = accountBalanceReportItemRepository.save(accountBalanceReportItem);
        AccountBalanceReportItemDTO result = accountBalanceReportItemMapper.toDto(accountBalanceReportItem);
        accountBalanceReportItemSearchRepository.index(accountBalanceReportItem);
        return result;
    }

    @Override
    public Optional<AccountBalanceReportItemDTO> partialUpdate(AccountBalanceReportItemDTO accountBalanceReportItemDTO) {
        log.debug("Request to partially update AccountBalanceReportItem : {}", accountBalanceReportItemDTO);

        return accountBalanceReportItemRepository
            .findById(accountBalanceReportItemDTO.getId())
            .map(existingAccountBalanceReportItem -> {
                accountBalanceReportItemMapper.partialUpdate(existingAccountBalanceReportItem, accountBalanceReportItemDTO);

                return existingAccountBalanceReportItem;
            })
            .map(accountBalanceReportItemRepository::save)
            .map(savedAccountBalanceReportItem -> {
                accountBalanceReportItemSearchRepository.save(savedAccountBalanceReportItem);

                return savedAccountBalanceReportItem;
            })
            .map(accountBalanceReportItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountBalanceReportItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AccountBalanceReportItems");
        return accountBalanceReportItemRepository.findAll(pageable).map(accountBalanceReportItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccountBalanceReportItemDTO> findOne(Long id) {
        log.debug("Request to get AccountBalanceReportItem : {}", id);
        return accountBalanceReportItemRepository.findById(id).map(accountBalanceReportItemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete AccountBalanceReportItem : {}", id);
        accountBalanceReportItemRepository.deleteById(id);
        accountBalanceReportItemSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountBalanceReportItemDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of AccountBalanceReportItems for query {}", query);
        return accountBalanceReportItemSearchRepository.search(query, pageable).map(accountBalanceReportItemMapper::toDto);
    }

    /**
     * Runs the item list for account balances report
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    public Page<AccountBalanceReportItemDTO> listAccountBalanceItems(Pageable pageable) {
        return accountBalanceReportItemRepository.listAccountBalanceItems(pageable).map(accountBalanceReportItemInternalMapping::mapToV2);
    }
}

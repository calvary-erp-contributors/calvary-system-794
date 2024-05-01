package io.github.calvary.service.impl;

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

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.github.calvary.domain.TransactionItem;
import io.github.calvary.repository.TransactionItemRepository;
import io.github.calvary.repository.search.TransactionItemSearchRepository;
import io.github.calvary.service.TransactionItemService;
import io.github.calvary.service.dto.TransactionItemDTO;
import io.github.calvary.service.mapper.TransactionItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransactionItem}.
 */
@Service
@Transactional
public class TransactionItemServiceImpl implements TransactionItemService {

    private final Logger log = LoggerFactory.getLogger(TransactionItemServiceImpl.class);

    private final TransactionItemRepository transactionItemRepository;

    private final TransactionItemMapper transactionItemMapper;

    private final TransactionItemSearchRepository transactionItemSearchRepository;

    public TransactionItemServiceImpl(
        TransactionItemRepository transactionItemRepository,
        TransactionItemMapper transactionItemMapper,
        TransactionItemSearchRepository transactionItemSearchRepository
    ) {
        this.transactionItemRepository = transactionItemRepository;
        this.transactionItemMapper = transactionItemMapper;
        this.transactionItemSearchRepository = transactionItemSearchRepository;
    }

    @Override
    public TransactionItemDTO save(TransactionItemDTO transactionItemDTO) {
        log.debug("Request to save TransactionItem : {}", transactionItemDTO);
        TransactionItem transactionItem = transactionItemMapper.toEntity(transactionItemDTO);
        transactionItem = transactionItemRepository.save(transactionItem);
        TransactionItemDTO result = transactionItemMapper.toDto(transactionItem);
        transactionItemSearchRepository.index(transactionItem);
        return result;
    }

    @Override
    public TransactionItemDTO update(TransactionItemDTO transactionItemDTO) {
        log.debug("Request to update TransactionItem : {}", transactionItemDTO);
        TransactionItem transactionItem = transactionItemMapper.toEntity(transactionItemDTO);
        transactionItem = transactionItemRepository.save(transactionItem);
        TransactionItemDTO result = transactionItemMapper.toDto(transactionItem);
        transactionItemSearchRepository.index(transactionItem);
        return result;
    }

    @Override
    public Optional<TransactionItemDTO> partialUpdate(TransactionItemDTO transactionItemDTO) {
        log.debug("Request to partially update TransactionItem : {}", transactionItemDTO);

        return transactionItemRepository
            .findById(transactionItemDTO.getId())
            .map(existingTransactionItem -> {
                transactionItemMapper.partialUpdate(existingTransactionItem, transactionItemDTO);

                return existingTransactionItem;
            })
            .map(transactionItemRepository::save)
            .map(savedTransactionItem -> {
                transactionItemSearchRepository.save(savedTransactionItem);

                return savedTransactionItem;
            })
            .map(transactionItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransactionItems");
        return transactionItemRepository.findAll(pageable).map(transactionItemMapper::toDto);
    }

    public Page<TransactionItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return transactionItemRepository.findAllWithEagerRelationships(pageable).map(transactionItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionItemDTO> findOne(Long id) {
        log.debug("Request to get TransactionItem : {}", id);
        return transactionItemRepository.findOneWithEagerRelationships(id).map(transactionItemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TransactionItem : {}", id);
        transactionItemRepository.deleteById(id);
        transactionItemSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionItemDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransactionItems for query {}", query);
        return transactionItemSearchRepository.search(query, pageable).map(transactionItemMapper::toDto);
    }
}

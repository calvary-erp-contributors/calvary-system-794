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

import io.github.calvary.domain.TransactionCurrency;
import io.github.calvary.repository.TransactionCurrencyRepository;
import io.github.calvary.repository.search.TransactionCurrencySearchRepository;
import io.github.calvary.service.TransactionCurrencyService;
import io.github.calvary.service.dto.TransactionCurrencyDTO;
import io.github.calvary.service.mapper.TransactionCurrencyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransactionCurrency}.
 */
@Service
@Transactional
public class TransactionCurrencyServiceImpl implements TransactionCurrencyService {

    private final Logger log = LoggerFactory.getLogger(TransactionCurrencyServiceImpl.class);

    private final TransactionCurrencyRepository transactionCurrencyRepository;

    private final TransactionCurrencyMapper transactionCurrencyMapper;

    private final TransactionCurrencySearchRepository transactionCurrencySearchRepository;

    public TransactionCurrencyServiceImpl(
        TransactionCurrencyRepository transactionCurrencyRepository,
        TransactionCurrencyMapper transactionCurrencyMapper,
        TransactionCurrencySearchRepository transactionCurrencySearchRepository
    ) {
        this.transactionCurrencyRepository = transactionCurrencyRepository;
        this.transactionCurrencyMapper = transactionCurrencyMapper;
        this.transactionCurrencySearchRepository = transactionCurrencySearchRepository;
    }

    @Override
    public TransactionCurrencyDTO save(TransactionCurrencyDTO transactionCurrencyDTO) {
        log.debug("Request to save TransactionCurrency : {}", transactionCurrencyDTO);
        TransactionCurrency transactionCurrency = transactionCurrencyMapper.toEntity(transactionCurrencyDTO);
        transactionCurrency = transactionCurrencyRepository.save(transactionCurrency);
        TransactionCurrencyDTO result = transactionCurrencyMapper.toDto(transactionCurrency);
        transactionCurrencySearchRepository.index(transactionCurrency);
        return result;
    }

    @Override
    public TransactionCurrencyDTO update(TransactionCurrencyDTO transactionCurrencyDTO) {
        log.debug("Request to update TransactionCurrency : {}", transactionCurrencyDTO);
        TransactionCurrency transactionCurrency = transactionCurrencyMapper.toEntity(transactionCurrencyDTO);
        transactionCurrency = transactionCurrencyRepository.save(transactionCurrency);
        TransactionCurrencyDTO result = transactionCurrencyMapper.toDto(transactionCurrency);
        transactionCurrencySearchRepository.index(transactionCurrency);
        return result;
    }

    @Override
    public Optional<TransactionCurrencyDTO> partialUpdate(TransactionCurrencyDTO transactionCurrencyDTO) {
        log.debug("Request to partially update TransactionCurrency : {}", transactionCurrencyDTO);

        return transactionCurrencyRepository
            .findById(transactionCurrencyDTO.getId())
            .map(existingTransactionCurrency -> {
                transactionCurrencyMapper.partialUpdate(existingTransactionCurrency, transactionCurrencyDTO);

                return existingTransactionCurrency;
            })
            .map(transactionCurrencyRepository::save)
            .map(savedTransactionCurrency -> {
                transactionCurrencySearchRepository.save(savedTransactionCurrency);

                return savedTransactionCurrency;
            })
            .map(transactionCurrencyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionCurrencyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransactionCurrencies");
        return transactionCurrencyRepository.findAll(pageable).map(transactionCurrencyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionCurrencyDTO> findOne(Long id) {
        log.debug("Request to get TransactionCurrency : {}", id);
        return transactionCurrencyRepository.findById(id).map(transactionCurrencyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TransactionCurrency : {}", id);
        transactionCurrencyRepository.deleteById(id);
        transactionCurrencySearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionCurrencyDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransactionCurrencies for query {}", query);
        return transactionCurrencySearchRepository.search(query, pageable).map(transactionCurrencyMapper::toDto);
    }
}

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

import io.github.calvary.domain.TransferItemEntry;
import io.github.calvary.repository.TransferItemEntryRepository;
import io.github.calvary.repository.search.TransferItemEntrySearchRepository;
import io.github.calvary.service.TransferItemEntryService;
import io.github.calvary.service.dto.TransferItemEntryDTO;
import io.github.calvary.service.mapper.TransferItemEntryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransferItemEntry}.
 */
@Service
@Transactional
public class TransferItemEntryServiceImpl implements TransferItemEntryService {

    private final Logger log = LoggerFactory.getLogger(TransferItemEntryServiceImpl.class);

    private final TransferItemEntryRepository transferItemEntryRepository;

    private final TransferItemEntryMapper transferItemEntryMapper;

    private final TransferItemEntrySearchRepository transferItemEntrySearchRepository;

    public TransferItemEntryServiceImpl(
        TransferItemEntryRepository transferItemEntryRepository,
        TransferItemEntryMapper transferItemEntryMapper,
        TransferItemEntrySearchRepository transferItemEntrySearchRepository
    ) {
        this.transferItemEntryRepository = transferItemEntryRepository;
        this.transferItemEntryMapper = transferItemEntryMapper;
        this.transferItemEntrySearchRepository = transferItemEntrySearchRepository;
    }

    @Override
    public TransferItemEntryDTO save(TransferItemEntryDTO transferItemEntryDTO) {
        log.debug("Request to save TransferItemEntry : {}", transferItemEntryDTO);
        TransferItemEntry transferItemEntry = transferItemEntryMapper.toEntity(transferItemEntryDTO);
        transferItemEntry = transferItemEntryRepository.save(transferItemEntry);
        TransferItemEntryDTO result = transferItemEntryMapper.toDto(transferItemEntry);
        transferItemEntrySearchRepository.index(transferItemEntry);
        return result;
    }

    @Override
    public TransferItemEntryDTO update(TransferItemEntryDTO transferItemEntryDTO) {
        log.debug("Request to update TransferItemEntry : {}", transferItemEntryDTO);
        TransferItemEntry transferItemEntry = transferItemEntryMapper.toEntity(transferItemEntryDTO);
        transferItemEntry = transferItemEntryRepository.save(transferItemEntry);
        TransferItemEntryDTO result = transferItemEntryMapper.toDto(transferItemEntry);
        transferItemEntrySearchRepository.index(transferItemEntry);
        return result;
    }

    @Override
    public Optional<TransferItemEntryDTO> partialUpdate(TransferItemEntryDTO transferItemEntryDTO) {
        log.debug("Request to partially update TransferItemEntry : {}", transferItemEntryDTO);

        return transferItemEntryRepository
            .findById(transferItemEntryDTO.getId())
            .map(existingTransferItemEntry -> {
                transferItemEntryMapper.partialUpdate(existingTransferItemEntry, transferItemEntryDTO);

                return existingTransferItemEntry;
            })
            .map(transferItemEntryRepository::save)
            .map(savedTransferItemEntry -> {
                transferItemEntrySearchRepository.save(savedTransferItemEntry);

                return savedTransferItemEntry;
            })
            .map(transferItemEntryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferItemEntryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransferItemEntries");
        return transferItemEntryRepository.findAll(pageable).map(transferItemEntryMapper::toDto);
    }

    public Page<TransferItemEntryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return transferItemEntryRepository.findAllWithEagerRelationships(pageable).map(transferItemEntryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransferItemEntryDTO> findOne(Long id) {
        log.debug("Request to get TransferItemEntry : {}", id);
        return transferItemEntryRepository.findOneWithEagerRelationships(id).map(transferItemEntryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TransferItemEntry : {}", id);
        transferItemEntryRepository.deleteById(id);
        transferItemEntrySearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferItemEntryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransferItemEntries for query {}", query);
        return transferItemEntrySearchRepository.search(query, pageable).map(transferItemEntryMapper::toDto);
    }
}

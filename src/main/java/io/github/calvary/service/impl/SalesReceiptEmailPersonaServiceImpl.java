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

import io.github.calvary.domain.SalesReceiptEmailPersona;
import io.github.calvary.repository.SalesReceiptEmailPersonaRepository;
import io.github.calvary.repository.search.SalesReceiptEmailPersonaSearchRepository;
import io.github.calvary.service.SalesReceiptEmailPersonaService;
import io.github.calvary.service.dto.SalesReceiptEmailPersonaDTO;
import io.github.calvary.service.mapper.SalesReceiptEmailPersonaMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SalesReceiptEmailPersona}.
 */
@Service
@Transactional
public class SalesReceiptEmailPersonaServiceImpl implements SalesReceiptEmailPersonaService {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptEmailPersonaServiceImpl.class);

    private final SalesReceiptEmailPersonaRepository salesReceiptEmailPersonaRepository;

    private final SalesReceiptEmailPersonaMapper salesReceiptEmailPersonaMapper;

    private final SalesReceiptEmailPersonaSearchRepository salesReceiptEmailPersonaSearchRepository;

    public SalesReceiptEmailPersonaServiceImpl(
        SalesReceiptEmailPersonaRepository salesReceiptEmailPersonaRepository,
        SalesReceiptEmailPersonaMapper salesReceiptEmailPersonaMapper,
        SalesReceiptEmailPersonaSearchRepository salesReceiptEmailPersonaSearchRepository
    ) {
        this.salesReceiptEmailPersonaRepository = salesReceiptEmailPersonaRepository;
        this.salesReceiptEmailPersonaMapper = salesReceiptEmailPersonaMapper;
        this.salesReceiptEmailPersonaSearchRepository = salesReceiptEmailPersonaSearchRepository;
    }

    @Override
    public SalesReceiptEmailPersonaDTO save(SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO) {
        log.debug("Request to save SalesReceiptEmailPersona : {}", salesReceiptEmailPersonaDTO);
        SalesReceiptEmailPersona salesReceiptEmailPersona = salesReceiptEmailPersonaMapper.toEntity(salesReceiptEmailPersonaDTO);
        salesReceiptEmailPersona = salesReceiptEmailPersonaRepository.save(salesReceiptEmailPersona);
        SalesReceiptEmailPersonaDTO result = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);
        salesReceiptEmailPersonaSearchRepository.index(salesReceiptEmailPersona);
        return result;
    }

    @Override
    public SalesReceiptEmailPersonaDTO update(SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO) {
        log.debug("Request to update SalesReceiptEmailPersona : {}", salesReceiptEmailPersonaDTO);
        SalesReceiptEmailPersona salesReceiptEmailPersona = salesReceiptEmailPersonaMapper.toEntity(salesReceiptEmailPersonaDTO);
        salesReceiptEmailPersona = salesReceiptEmailPersonaRepository.save(salesReceiptEmailPersona);
        SalesReceiptEmailPersonaDTO result = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);
        salesReceiptEmailPersonaSearchRepository.index(salesReceiptEmailPersona);
        return result;
    }

    @Override
    public Optional<SalesReceiptEmailPersonaDTO> partialUpdate(SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO) {
        log.debug("Request to partially update SalesReceiptEmailPersona : {}", salesReceiptEmailPersonaDTO);

        return salesReceiptEmailPersonaRepository
            .findById(salesReceiptEmailPersonaDTO.getId())
            .map(existingSalesReceiptEmailPersona -> {
                salesReceiptEmailPersonaMapper.partialUpdate(existingSalesReceiptEmailPersona, salesReceiptEmailPersonaDTO);

                return existingSalesReceiptEmailPersona;
            })
            .map(salesReceiptEmailPersonaRepository::save)
            .map(savedSalesReceiptEmailPersona -> {
                salesReceiptEmailPersonaSearchRepository.save(savedSalesReceiptEmailPersona);

                return savedSalesReceiptEmailPersona;
            })
            .map(salesReceiptEmailPersonaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesReceiptEmailPersonaDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SalesReceiptEmailPersonas");
        return salesReceiptEmailPersonaRepository.findAll(pageable).map(salesReceiptEmailPersonaMapper::toDto);
    }

    public Page<SalesReceiptEmailPersonaDTO> findAllWithEagerRelationships(Pageable pageable) {
        return salesReceiptEmailPersonaRepository.findAllWithEagerRelationships(pageable).map(salesReceiptEmailPersonaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SalesReceiptEmailPersonaDTO> findOne(Long id) {
        log.debug("Request to get SalesReceiptEmailPersona : {}", id);
        return salesReceiptEmailPersonaRepository.findOneWithEagerRelationships(id).map(salesReceiptEmailPersonaMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SalesReceiptEmailPersona : {}", id);
        salesReceiptEmailPersonaRepository.deleteById(id);
        salesReceiptEmailPersonaSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesReceiptEmailPersonaDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SalesReceiptEmailPersonas for query {}", query);
        return salesReceiptEmailPersonaSearchRepository.search(query, pageable).map(salesReceiptEmailPersonaMapper::toDto);
    }
}

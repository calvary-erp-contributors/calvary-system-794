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

import io.github.calvary.domain.BalanceSheetItemValue;
import io.github.calvary.repository.BalanceSheetItemValueRepository;
import io.github.calvary.repository.search.BalanceSheetItemValueSearchRepository;
import io.github.calvary.service.BalanceSheetItemValueService;
import io.github.calvary.service.dto.BalanceSheetItemValueDTO;
import io.github.calvary.service.mapper.BalanceSheetItemValueMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BalanceSheetItemValue}.
 */
@Service
@Transactional
public class BalanceSheetItemValueServiceImpl implements BalanceSheetItemValueService {

    private final Logger log = LoggerFactory.getLogger(BalanceSheetItemValueServiceImpl.class);

    private final BalanceSheetItemValueRepository balanceSheetItemValueRepository;

    private final BalanceSheetItemValueMapper balanceSheetItemValueMapper;

    private final BalanceSheetItemValueSearchRepository balanceSheetItemValueSearchRepository;

    public BalanceSheetItemValueServiceImpl(
        BalanceSheetItemValueRepository balanceSheetItemValueRepository,
        BalanceSheetItemValueMapper balanceSheetItemValueMapper,
        BalanceSheetItemValueSearchRepository balanceSheetItemValueSearchRepository
    ) {
        this.balanceSheetItemValueRepository = balanceSheetItemValueRepository;
        this.balanceSheetItemValueMapper = balanceSheetItemValueMapper;
        this.balanceSheetItemValueSearchRepository = balanceSheetItemValueSearchRepository;
    }

    @Override
    public BalanceSheetItemValueDTO save(BalanceSheetItemValueDTO balanceSheetItemValueDTO) {
        log.debug("Request to save BalanceSheetItemValue : {}", balanceSheetItemValueDTO);
        BalanceSheetItemValue balanceSheetItemValue = balanceSheetItemValueMapper.toEntity(balanceSheetItemValueDTO);
        balanceSheetItemValue = balanceSheetItemValueRepository.save(balanceSheetItemValue);
        BalanceSheetItemValueDTO result = balanceSheetItemValueMapper.toDto(balanceSheetItemValue);
        balanceSheetItemValueSearchRepository.index(balanceSheetItemValue);
        return result;
    }

    @Override
    public BalanceSheetItemValueDTO update(BalanceSheetItemValueDTO balanceSheetItemValueDTO) {
        log.debug("Request to update BalanceSheetItemValue : {}", balanceSheetItemValueDTO);
        BalanceSheetItemValue balanceSheetItemValue = balanceSheetItemValueMapper.toEntity(balanceSheetItemValueDTO);
        balanceSheetItemValue = balanceSheetItemValueRepository.save(balanceSheetItemValue);
        BalanceSheetItemValueDTO result = balanceSheetItemValueMapper.toDto(balanceSheetItemValue);
        balanceSheetItemValueSearchRepository.index(balanceSheetItemValue);
        return result;
    }

    @Override
    public Optional<BalanceSheetItemValueDTO> partialUpdate(BalanceSheetItemValueDTO balanceSheetItemValueDTO) {
        log.debug("Request to partially update BalanceSheetItemValue : {}", balanceSheetItemValueDTO);

        return balanceSheetItemValueRepository
            .findById(balanceSheetItemValueDTO.getId())
            .map(existingBalanceSheetItemValue -> {
                balanceSheetItemValueMapper.partialUpdate(existingBalanceSheetItemValue, balanceSheetItemValueDTO);

                return existingBalanceSheetItemValue;
            })
            .map(balanceSheetItemValueRepository::save)
            .map(savedBalanceSheetItemValue -> {
                balanceSheetItemValueSearchRepository.save(savedBalanceSheetItemValue);

                return savedBalanceSheetItemValue;
            })
            .map(balanceSheetItemValueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BalanceSheetItemValueDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BalanceSheetItemValues");
        return balanceSheetItemValueRepository.findAll(pageable).map(balanceSheetItemValueMapper::toDto);
    }

    public Page<BalanceSheetItemValueDTO> findAllWithEagerRelationships(Pageable pageable) {
        return balanceSheetItemValueRepository.findAllWithEagerRelationships(pageable).map(balanceSheetItemValueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BalanceSheetItemValueDTO> findOne(Long id) {
        log.debug("Request to get BalanceSheetItemValue : {}", id);
        return balanceSheetItemValueRepository.findOneWithEagerRelationships(id).map(balanceSheetItemValueMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete BalanceSheetItemValue : {}", id);
        balanceSheetItemValueRepository.deleteById(id);
        balanceSheetItemValueSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BalanceSheetItemValueDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of BalanceSheetItemValues for query {}", query);
        return balanceSheetItemValueSearchRepository.search(query, pageable).map(balanceSheetItemValueMapper::toDto);
    }
}

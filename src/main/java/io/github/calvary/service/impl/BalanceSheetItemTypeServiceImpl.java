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

import io.github.calvary.domain.BalanceSheetItemType;
import io.github.calvary.repository.BalanceSheetItemTypeRepository;
import io.github.calvary.repository.search.BalanceSheetItemTypeSearchRepository;
import io.github.calvary.service.BalanceSheetItemTypeService;
import io.github.calvary.service.dto.BalanceSheetItemTypeDTO;
import io.github.calvary.service.mapper.BalanceSheetItemTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BalanceSheetItemType}.
 */
@Service
@Transactional
public class BalanceSheetItemTypeServiceImpl implements BalanceSheetItemTypeService {

    private final Logger log = LoggerFactory.getLogger(BalanceSheetItemTypeServiceImpl.class);

    private final BalanceSheetItemTypeRepository balanceSheetItemTypeRepository;

    private final BalanceSheetItemTypeMapper balanceSheetItemTypeMapper;

    private final BalanceSheetItemTypeSearchRepository balanceSheetItemTypeSearchRepository;

    public BalanceSheetItemTypeServiceImpl(
        BalanceSheetItemTypeRepository balanceSheetItemTypeRepository,
        BalanceSheetItemTypeMapper balanceSheetItemTypeMapper,
        BalanceSheetItemTypeSearchRepository balanceSheetItemTypeSearchRepository
    ) {
        this.balanceSheetItemTypeRepository = balanceSheetItemTypeRepository;
        this.balanceSheetItemTypeMapper = balanceSheetItemTypeMapper;
        this.balanceSheetItemTypeSearchRepository = balanceSheetItemTypeSearchRepository;
    }

    @Override
    public BalanceSheetItemTypeDTO save(BalanceSheetItemTypeDTO balanceSheetItemTypeDTO) {
        log.debug("Request to save BalanceSheetItemType : {}", balanceSheetItemTypeDTO);
        BalanceSheetItemType balanceSheetItemType = balanceSheetItemTypeMapper.toEntity(balanceSheetItemTypeDTO);
        balanceSheetItemType = balanceSheetItemTypeRepository.save(balanceSheetItemType);
        BalanceSheetItemTypeDTO result = balanceSheetItemTypeMapper.toDto(balanceSheetItemType);
        balanceSheetItemTypeSearchRepository.index(balanceSheetItemType);
        return result;
    }

    @Override
    public BalanceSheetItemTypeDTO update(BalanceSheetItemTypeDTO balanceSheetItemTypeDTO) {
        log.debug("Request to update BalanceSheetItemType : {}", balanceSheetItemTypeDTO);
        BalanceSheetItemType balanceSheetItemType = balanceSheetItemTypeMapper.toEntity(balanceSheetItemTypeDTO);
        balanceSheetItemType = balanceSheetItemTypeRepository.save(balanceSheetItemType);
        BalanceSheetItemTypeDTO result = balanceSheetItemTypeMapper.toDto(balanceSheetItemType);
        balanceSheetItemTypeSearchRepository.index(balanceSheetItemType);
        return result;
    }

    @Override
    public Optional<BalanceSheetItemTypeDTO> partialUpdate(BalanceSheetItemTypeDTO balanceSheetItemTypeDTO) {
        log.debug("Request to partially update BalanceSheetItemType : {}", balanceSheetItemTypeDTO);

        return balanceSheetItemTypeRepository
            .findById(balanceSheetItemTypeDTO.getId())
            .map(existingBalanceSheetItemType -> {
                balanceSheetItemTypeMapper.partialUpdate(existingBalanceSheetItemType, balanceSheetItemTypeDTO);

                return existingBalanceSheetItemType;
            })
            .map(balanceSheetItemTypeRepository::save)
            .map(savedBalanceSheetItemType -> {
                balanceSheetItemTypeSearchRepository.save(savedBalanceSheetItemType);

                return savedBalanceSheetItemType;
            })
            .map(balanceSheetItemTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BalanceSheetItemTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BalanceSheetItemTypes");
        return balanceSheetItemTypeRepository.findAll(pageable).map(balanceSheetItemTypeMapper::toDto);
    }

    public Page<BalanceSheetItemTypeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return balanceSheetItemTypeRepository.findAllWithEagerRelationships(pageable).map(balanceSheetItemTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BalanceSheetItemTypeDTO> findOne(Long id) {
        log.debug("Request to get BalanceSheetItemType : {}", id);
        return balanceSheetItemTypeRepository.findOneWithEagerRelationships(id).map(balanceSheetItemTypeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete BalanceSheetItemType : {}", id);
        balanceSheetItemTypeRepository.deleteById(id);
        balanceSheetItemTypeSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BalanceSheetItemTypeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of BalanceSheetItemTypes for query {}", query);
        return balanceSheetItemTypeSearchRepository.search(query, pageable).map(balanceSheetItemTypeMapper::toDto);
    }
}

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

import io.github.calvary.domain.DealerType;
import io.github.calvary.repository.DealerTypeRepository;
import io.github.calvary.repository.search.DealerTypeSearchRepository;
import io.github.calvary.service.DealerTypeService;
import io.github.calvary.service.dto.DealerTypeDTO;
import io.github.calvary.service.mapper.DealerTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link DealerType}.
 */
@Service
@Transactional
public class DealerTypeServiceImpl implements DealerTypeService {

    private final Logger log = LoggerFactory.getLogger(DealerTypeServiceImpl.class);

    private final DealerTypeRepository dealerTypeRepository;

    private final DealerTypeMapper dealerTypeMapper;

    private final DealerTypeSearchRepository dealerTypeSearchRepository;

    public DealerTypeServiceImpl(
        DealerTypeRepository dealerTypeRepository,
        DealerTypeMapper dealerTypeMapper,
        DealerTypeSearchRepository dealerTypeSearchRepository
    ) {
        this.dealerTypeRepository = dealerTypeRepository;
        this.dealerTypeMapper = dealerTypeMapper;
        this.dealerTypeSearchRepository = dealerTypeSearchRepository;
    }

    @Override
    public DealerTypeDTO save(DealerTypeDTO dealerTypeDTO) {
        log.debug("Request to save DealerType : {}", dealerTypeDTO);
        DealerType dealerType = dealerTypeMapper.toEntity(dealerTypeDTO);
        dealerType = dealerTypeRepository.save(dealerType);
        DealerTypeDTO result = dealerTypeMapper.toDto(dealerType);
        dealerTypeSearchRepository.index(dealerType);
        return result;
    }

    @Override
    public DealerTypeDTO update(DealerTypeDTO dealerTypeDTO) {
        log.debug("Request to update DealerType : {}", dealerTypeDTO);
        DealerType dealerType = dealerTypeMapper.toEntity(dealerTypeDTO);
        dealerType = dealerTypeRepository.save(dealerType);
        DealerTypeDTO result = dealerTypeMapper.toDto(dealerType);
        dealerTypeSearchRepository.index(dealerType);
        return result;
    }

    @Override
    public Optional<DealerTypeDTO> partialUpdate(DealerTypeDTO dealerTypeDTO) {
        log.debug("Request to partially update DealerType : {}", dealerTypeDTO);

        return dealerTypeRepository
            .findById(dealerTypeDTO.getId())
            .map(existingDealerType -> {
                dealerTypeMapper.partialUpdate(existingDealerType, dealerTypeDTO);

                return existingDealerType;
            })
            .map(dealerTypeRepository::save)
            .map(savedDealerType -> {
                dealerTypeSearchRepository.save(savedDealerType);

                return savedDealerType;
            })
            .map(dealerTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DealerTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all DealerTypes");
        return dealerTypeRepository.findAll(pageable).map(dealerTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DealerTypeDTO> findOne(Long id) {
        log.debug("Request to get DealerType : {}", id);
        return dealerTypeRepository.findById(id).map(dealerTypeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete DealerType : {}", id);
        dealerTypeRepository.deleteById(id);
        dealerTypeSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DealerTypeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of DealerTypes for query {}", query);
        return dealerTypeSearchRepository.search(query, pageable).map(dealerTypeMapper::toDto);
    }
}

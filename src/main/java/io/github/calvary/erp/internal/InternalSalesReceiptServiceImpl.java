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

import io.github.calvary.domain.SalesReceipt;
import io.github.calvary.erp.repository.InternalSalesReceiptRepository;
import io.github.calvary.repository.SalesReceiptRepository;
import io.github.calvary.repository.search.SalesReceiptSearchRepository;
import io.github.calvary.service.SalesReceiptService;
import io.github.calvary.service.dto.SalesReceiptDTO;
import io.github.calvary.service.mapper.SalesReceiptMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SalesReceipt}.
 */
@Service
@Transactional
public class InternalSalesReceiptServiceImpl implements InternalSalesReceiptService {

    private final Logger log = LoggerFactory.getLogger(InternalSalesReceiptServiceImpl.class);

    private final InternalSalesReceiptRepository salesReceiptRepository;

    private final SalesReceiptMapper salesReceiptMapper;

    private final SalesReceiptSearchRepository salesReceiptSearchRepository;

    public InternalSalesReceiptServiceImpl(
        InternalSalesReceiptRepository salesReceiptRepository,
        SalesReceiptMapper salesReceiptMapper,
        SalesReceiptSearchRepository salesReceiptSearchRepository
    ) {
        this.salesReceiptRepository = salesReceiptRepository;
        this.salesReceiptMapper = salesReceiptMapper;
        this.salesReceiptSearchRepository = salesReceiptSearchRepository;
    }

    @Override
    public SalesReceiptDTO save(SalesReceiptDTO salesReceiptDTO) {
        log.debug("Request to save SalesReceipt : {}", salesReceiptDTO);
        SalesReceipt salesReceipt = salesReceiptMapper.toEntity(salesReceiptDTO);
        salesReceipt = salesReceiptRepository.save(salesReceipt);
        SalesReceiptDTO result = salesReceiptMapper.toDto(salesReceipt);
        salesReceiptSearchRepository.index(salesReceipt);
        return result;
    }

    @Override
    public SalesReceiptDTO update(SalesReceiptDTO salesReceiptDTO) {
        log.debug("Request to update SalesReceipt : {}", salesReceiptDTO);
        SalesReceipt salesReceipt = salesReceiptMapper.toEntity(salesReceiptDTO);
        salesReceipt = salesReceiptRepository.save(salesReceipt);
        SalesReceiptDTO result = salesReceiptMapper.toDto(salesReceipt);
        salesReceiptSearchRepository.index(salesReceipt);
        return result;
    }

    @Override
    public Optional<SalesReceiptDTO> partialUpdate(SalesReceiptDTO salesReceiptDTO) {
        log.debug("Request to partially update SalesReceipt : {}", salesReceiptDTO);

        return salesReceiptRepository
            .findById(salesReceiptDTO.getId())
            .map(existingSalesReceipt -> {
                salesReceiptMapper.partialUpdate(existingSalesReceipt, salesReceiptDTO);

                return existingSalesReceipt;
            })
            .map(salesReceiptRepository::save)
            .map(savedSalesReceipt -> {
                salesReceiptSearchRepository.save(savedSalesReceipt);

                return savedSalesReceipt;
            })
            .map(salesReceiptMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesReceiptDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SalesReceipts");
        return salesReceiptRepository.findAll(pageable).map(salesReceiptMapper::toDto);
    }

    public Page<SalesReceiptDTO> findAllWithEagerRelationships(Pageable pageable) {
        return salesReceiptRepository.findAllWithEagerRelationships(pageable).map(salesReceiptMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SalesReceiptDTO> findOne(Long id) {
        log.debug("Request to get SalesReceipt : {}", id);
        return salesReceiptRepository.findOneWithEagerRelationships(id).map(salesReceiptMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SalesReceipt : {}", id);
        salesReceiptRepository.deleteById(id);
        salesReceiptSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesReceiptDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SalesReceipts for query {}", query);
        return salesReceiptSearchRepository.search(query, pageable).map(salesReceiptMapper::toDto);
    }

    /**
     * Fetch receipts that are pending for notification
     *
     * @return List of notifiable sales-receipt items
     */
    @Override
    public Optional<List<SalesReceiptDTO>> findSalesReceiptsPendingEmailNotification() {
        return salesReceiptRepository.findSalesReceiptDueForNotification().map(salesReceiptMapper::toDto);
    }

    /**
     * Set the sales-receipt has having been notified
     *
     * @param salesReceiptDTO
     * @return
     */
    @Override
    public SalesReceiptDTO hasBeenEmailed(SalesReceiptDTO salesReceiptDTO) {
        salesReceiptDTO.setHasBeenEmailed(true);
        return save(salesReceiptDTO);
    }
}

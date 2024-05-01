package io.github.calvary.service;

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

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.TransferItem;
import io.github.calvary.repository.TransferItemRepository;
import io.github.calvary.repository.search.TransferItemSearchRepository;
import io.github.calvary.service.criteria.TransferItemCriteria;
import io.github.calvary.service.dto.TransferItemDTO;
import io.github.calvary.service.mapper.TransferItemMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TransferItem} entities in the database.
 * The main input is a {@link TransferItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TransferItemDTO} or a {@link Page} of {@link TransferItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransferItemQueryService extends QueryService<TransferItem> {

    private final Logger log = LoggerFactory.getLogger(TransferItemQueryService.class);

    private final TransferItemRepository transferItemRepository;

    private final TransferItemMapper transferItemMapper;

    private final TransferItemSearchRepository transferItemSearchRepository;

    public TransferItemQueryService(
        TransferItemRepository transferItemRepository,
        TransferItemMapper transferItemMapper,
        TransferItemSearchRepository transferItemSearchRepository
    ) {
        this.transferItemRepository = transferItemRepository;
        this.transferItemMapper = transferItemMapper;
        this.transferItemSearchRepository = transferItemSearchRepository;
    }

    /**
     * Return a {@link List} of {@link TransferItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TransferItemDTO> findByCriteria(TransferItemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TransferItem> specification = createSpecification(criteria);
        return transferItemMapper.toDto(transferItemRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TransferItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransferItemDTO> findByCriteria(TransferItemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransferItem> specification = createSpecification(criteria);
        return transferItemRepository.findAll(specification, page).map(transferItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransferItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TransferItem> specification = createSpecification(criteria);
        return transferItemRepository.count(specification);
    }

    /**
     * Function to convert {@link TransferItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransferItem> createSpecification(TransferItemCriteria criteria) {
        Specification<TransferItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TransferItem_.id));
            }
            if (criteria.getItemName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getItemName(), TransferItem_.itemName));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), TransferItem_.description));
            }
            if (criteria.getTransactionClassId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionClassId(),
                            root -> root.join(TransferItem_.transactionClass, JoinType.LEFT).get(TransactionClass_.id)
                        )
                    );
            }
            if (criteria.getTransactionAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionAccountId(),
                            root -> root.join(TransferItem_.transactionAccount, JoinType.LEFT).get(TransactionAccount_.id)
                        )
                    );
            }
        }
        return specification;
    }
}

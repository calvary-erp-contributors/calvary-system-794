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
import io.github.calvary.domain.TransactionItem;
import io.github.calvary.repository.TransactionItemRepository;
import io.github.calvary.repository.search.TransactionItemSearchRepository;
import io.github.calvary.service.criteria.TransactionItemCriteria;
import io.github.calvary.service.dto.TransactionItemDTO;
import io.github.calvary.service.mapper.TransactionItemMapper;
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
 * Service for executing complex queries for {@link TransactionItem} entities in the database.
 * The main input is a {@link TransactionItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TransactionItemDTO} or a {@link Page} of {@link TransactionItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransactionItemQueryService extends QueryService<TransactionItem> {

    private final Logger log = LoggerFactory.getLogger(TransactionItemQueryService.class);

    private final TransactionItemRepository transactionItemRepository;

    private final TransactionItemMapper transactionItemMapper;

    private final TransactionItemSearchRepository transactionItemSearchRepository;

    public TransactionItemQueryService(
        TransactionItemRepository transactionItemRepository,
        TransactionItemMapper transactionItemMapper,
        TransactionItemSearchRepository transactionItemSearchRepository
    ) {
        this.transactionItemRepository = transactionItemRepository;
        this.transactionItemMapper = transactionItemMapper;
        this.transactionItemSearchRepository = transactionItemSearchRepository;
    }

    /**
     * Return a {@link List} of {@link TransactionItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TransactionItemDTO> findByCriteria(TransactionItemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TransactionItem> specification = createSpecification(criteria);
        return transactionItemMapper.toDto(transactionItemRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TransactionItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransactionItemDTO> findByCriteria(TransactionItemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransactionItem> specification = createSpecification(criteria);
        return transactionItemRepository.findAll(specification, page).map(transactionItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransactionItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TransactionItem> specification = createSpecification(criteria);
        return transactionItemRepository.count(specification);
    }

    /**
     * Function to convert {@link TransactionItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransactionItem> createSpecification(TransactionItemCriteria criteria) {
        Specification<TransactionItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TransactionItem_.id));
            }
            if (criteria.getItemName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getItemName(), TransactionItem_.itemName));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), TransactionItem_.description));
            }
            if (criteria.getTransactionClassId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionClassId(),
                            root -> root.join(TransactionItem_.transactionClass, JoinType.LEFT).get(TransactionClass_.id)
                        )
                    );
            }
            if (criteria.getTransactionAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionAccountId(),
                            root -> root.join(TransactionItem_.transactionAccount, JoinType.LEFT).get(TransactionAccount_.id)
                        )
                    );
            }
        }
        return specification;
    }
}

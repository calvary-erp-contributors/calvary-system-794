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
import io.github.calvary.domain.TransactionItemAmount;
import io.github.calvary.repository.TransactionItemAmountRepository;
import io.github.calvary.repository.search.TransactionItemAmountSearchRepository;
import io.github.calvary.service.criteria.TransactionItemAmountCriteria;
import io.github.calvary.service.dto.TransactionItemAmountDTO;
import io.github.calvary.service.mapper.TransactionItemAmountMapper;
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
 * Service for executing complex queries for {@link TransactionItemAmount} entities in the database.
 * The main input is a {@link TransactionItemAmountCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TransactionItemAmountDTO} or a {@link Page} of {@link TransactionItemAmountDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransactionItemAmountQueryService extends QueryService<TransactionItemAmount> {

    private final Logger log = LoggerFactory.getLogger(TransactionItemAmountQueryService.class);

    private final TransactionItemAmountRepository transactionItemAmountRepository;

    private final TransactionItemAmountMapper transactionItemAmountMapper;

    private final TransactionItemAmountSearchRepository transactionItemAmountSearchRepository;

    public TransactionItemAmountQueryService(
        TransactionItemAmountRepository transactionItemAmountRepository,
        TransactionItemAmountMapper transactionItemAmountMapper,
        TransactionItemAmountSearchRepository transactionItemAmountSearchRepository
    ) {
        this.transactionItemAmountRepository = transactionItemAmountRepository;
        this.transactionItemAmountMapper = transactionItemAmountMapper;
        this.transactionItemAmountSearchRepository = transactionItemAmountSearchRepository;
    }

    /**
     * Return a {@link List} of {@link TransactionItemAmountDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TransactionItemAmountDTO> findByCriteria(TransactionItemAmountCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TransactionItemAmount> specification = createSpecification(criteria);
        return transactionItemAmountMapper.toDto(transactionItemAmountRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TransactionItemAmountDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransactionItemAmountDTO> findByCriteria(TransactionItemAmountCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransactionItemAmount> specification = createSpecification(criteria);
        return transactionItemAmountRepository.findAll(specification, page).map(transactionItemAmountMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransactionItemAmountCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TransactionItemAmount> specification = createSpecification(criteria);
        return transactionItemAmountRepository.count(specification);
    }

    /**
     * Function to convert {@link TransactionItemAmountCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransactionItemAmount> createSpecification(TransactionItemAmountCriteria criteria) {
        Specification<TransactionItemAmount> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TransactionItemAmount_.id));
            }
            if (criteria.getTransactionItemAmount() != null) {
                specification =
                    specification.and(
                        buildRangeSpecification(criteria.getTransactionItemAmount(), TransactionItemAmount_.transactionItemAmount)
                    );
            }
            if (criteria.getTransactionItemId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionItemId(),
                            root -> root.join(TransactionItemAmount_.transactionItem, JoinType.LEFT).get(TransactionItem_.id)
                        )
                    );
            }
        }
        return specification;
    }
}

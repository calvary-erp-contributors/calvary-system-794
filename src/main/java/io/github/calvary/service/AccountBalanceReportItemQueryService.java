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
import io.github.calvary.domain.AccountBalanceReportItem;
import io.github.calvary.repository.AccountBalanceReportItemRepository;
import io.github.calvary.repository.search.AccountBalanceReportItemSearchRepository;
import io.github.calvary.service.criteria.AccountBalanceReportItemCriteria;
import io.github.calvary.service.dto.AccountBalanceReportItemDTO;
import io.github.calvary.service.mapper.AccountBalanceReportItemMapper;
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
 * Service for executing complex queries for {@link AccountBalanceReportItem} entities in the database.
 * The main input is a {@link AccountBalanceReportItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AccountBalanceReportItemDTO} or a {@link Page} of {@link AccountBalanceReportItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AccountBalanceReportItemQueryService extends QueryService<AccountBalanceReportItem> {

    private final Logger log = LoggerFactory.getLogger(AccountBalanceReportItemQueryService.class);

    private final AccountBalanceReportItemRepository accountBalanceReportItemRepository;

    private final AccountBalanceReportItemMapper accountBalanceReportItemMapper;

    private final AccountBalanceReportItemSearchRepository accountBalanceReportItemSearchRepository;

    public AccountBalanceReportItemQueryService(
        AccountBalanceReportItemRepository accountBalanceReportItemRepository,
        AccountBalanceReportItemMapper accountBalanceReportItemMapper,
        AccountBalanceReportItemSearchRepository accountBalanceReportItemSearchRepository
    ) {
        this.accountBalanceReportItemRepository = accountBalanceReportItemRepository;
        this.accountBalanceReportItemMapper = accountBalanceReportItemMapper;
        this.accountBalanceReportItemSearchRepository = accountBalanceReportItemSearchRepository;
    }

    /**
     * Return a {@link List} of {@link AccountBalanceReportItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AccountBalanceReportItemDTO> findByCriteria(AccountBalanceReportItemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<AccountBalanceReportItem> specification = createSpecification(criteria);
        return accountBalanceReportItemMapper.toDto(accountBalanceReportItemRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AccountBalanceReportItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AccountBalanceReportItemDTO> findByCriteria(AccountBalanceReportItemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AccountBalanceReportItem> specification = createSpecification(criteria);
        return accountBalanceReportItemRepository.findAll(specification, page).map(accountBalanceReportItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AccountBalanceReportItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<AccountBalanceReportItem> specification = createSpecification(criteria);
        return accountBalanceReportItemRepository.count(specification);
    }

    /**
     * Function to convert {@link AccountBalanceReportItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AccountBalanceReportItem> createSpecification(AccountBalanceReportItemCriteria criteria) {
        Specification<AccountBalanceReportItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AccountBalanceReportItem_.id));
            }
            if (criteria.getAccountNumber() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getAccountNumber(), AccountBalanceReportItem_.accountNumber));
            }
            if (criteria.getAccountName() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getAccountName(), AccountBalanceReportItem_.accountName));
            }
            if (criteria.getAccountBalance() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getAccountBalance(), AccountBalanceReportItem_.accountBalance));
            }
        }
        return specification;
    }
}

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
import io.github.calvary.domain.SalesReceiptProposal;
import io.github.calvary.repository.SalesReceiptProposalRepository;
import io.github.calvary.repository.search.SalesReceiptProposalSearchRepository;
import io.github.calvary.service.criteria.SalesReceiptProposalCriteria;
import io.github.calvary.service.dto.SalesReceiptProposalDTO;
import io.github.calvary.service.mapper.SalesReceiptProposalMapper;
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
 * Service for executing complex queries for {@link SalesReceiptProposal} entities in the database.
 * The main input is a {@link SalesReceiptProposalCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SalesReceiptProposalDTO} or a {@link Page} of {@link SalesReceiptProposalDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SalesReceiptProposalQueryService extends QueryService<SalesReceiptProposal> {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptProposalQueryService.class);

    private final SalesReceiptProposalRepository salesReceiptProposalRepository;

    private final SalesReceiptProposalMapper salesReceiptProposalMapper;

    private final SalesReceiptProposalSearchRepository salesReceiptProposalSearchRepository;

    public SalesReceiptProposalQueryService(
        SalesReceiptProposalRepository salesReceiptProposalRepository,
        SalesReceiptProposalMapper salesReceiptProposalMapper,
        SalesReceiptProposalSearchRepository salesReceiptProposalSearchRepository
    ) {
        this.salesReceiptProposalRepository = salesReceiptProposalRepository;
        this.salesReceiptProposalMapper = salesReceiptProposalMapper;
        this.salesReceiptProposalSearchRepository = salesReceiptProposalSearchRepository;
    }

    /**
     * Return a {@link List} of {@link SalesReceiptProposalDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SalesReceiptProposalDTO> findByCriteria(SalesReceiptProposalCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SalesReceiptProposal> specification = createSpecification(criteria);
        return salesReceiptProposalMapper.toDto(salesReceiptProposalRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SalesReceiptProposalDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SalesReceiptProposalDTO> findByCriteria(SalesReceiptProposalCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SalesReceiptProposal> specification = createSpecification(criteria);
        return salesReceiptProposalRepository.findAll(specification, page).map(salesReceiptProposalMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SalesReceiptProposalCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SalesReceiptProposal> specification = createSpecification(criteria);
        return salesReceiptProposalRepository.count(specification);
    }

    /**
     * Function to convert {@link SalesReceiptProposalCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SalesReceiptProposal> createSpecification(SalesReceiptProposalCriteria criteria) {
        Specification<SalesReceiptProposal> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SalesReceiptProposal_.id));
            }
            if (criteria.getTimeOfPosting() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getTimeOfPosting(), SalesReceiptProposal_.timeOfPosting));
            }
            if (criteria.getPostingIdentifier() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getPostingIdentifier(), SalesReceiptProposal_.postingIdentifier));
            }
            if (criteria.getNumberOfReceiptsPosted() != null) {
                specification =
                    specification.and(
                        buildRangeSpecification(criteria.getNumberOfReceiptsPosted(), SalesReceiptProposal_.numberOfReceiptsPosted)
                    );
            }
            if (criteria.getProposedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProposedById(),
                            root -> root.join(SalesReceiptProposal_.proposedBy, JoinType.LEFT).get(ApplicationUser_.id)
                        )
                    );
            }
        }
        return specification;
    }
}

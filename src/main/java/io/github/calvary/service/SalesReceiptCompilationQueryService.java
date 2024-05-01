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
import io.github.calvary.domain.SalesReceiptCompilation;
import io.github.calvary.repository.SalesReceiptCompilationRepository;
import io.github.calvary.repository.search.SalesReceiptCompilationSearchRepository;
import io.github.calvary.service.criteria.SalesReceiptCompilationCriteria;
import io.github.calvary.service.dto.SalesReceiptCompilationDTO;
import io.github.calvary.service.mapper.SalesReceiptCompilationMapper;
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
 * Service for executing complex queries for {@link SalesReceiptCompilation} entities in the database.
 * The main input is a {@link SalesReceiptCompilationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SalesReceiptCompilationDTO} or a {@link Page} of {@link SalesReceiptCompilationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SalesReceiptCompilationQueryService extends QueryService<SalesReceiptCompilation> {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptCompilationQueryService.class);

    private final SalesReceiptCompilationRepository salesReceiptCompilationRepository;

    private final SalesReceiptCompilationMapper salesReceiptCompilationMapper;

    private final SalesReceiptCompilationSearchRepository salesReceiptCompilationSearchRepository;

    public SalesReceiptCompilationQueryService(
        SalesReceiptCompilationRepository salesReceiptCompilationRepository,
        SalesReceiptCompilationMapper salesReceiptCompilationMapper,
        SalesReceiptCompilationSearchRepository salesReceiptCompilationSearchRepository
    ) {
        this.salesReceiptCompilationRepository = salesReceiptCompilationRepository;
        this.salesReceiptCompilationMapper = salesReceiptCompilationMapper;
        this.salesReceiptCompilationSearchRepository = salesReceiptCompilationSearchRepository;
    }

    /**
     * Return a {@link List} of {@link SalesReceiptCompilationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SalesReceiptCompilationDTO> findByCriteria(SalesReceiptCompilationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SalesReceiptCompilation> specification = createSpecification(criteria);
        return salesReceiptCompilationMapper.toDto(salesReceiptCompilationRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SalesReceiptCompilationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SalesReceiptCompilationDTO> findByCriteria(SalesReceiptCompilationCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SalesReceiptCompilation> specification = createSpecification(criteria);
        return salesReceiptCompilationRepository.findAll(specification, page).map(salesReceiptCompilationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SalesReceiptCompilationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SalesReceiptCompilation> specification = createSpecification(criteria);
        return salesReceiptCompilationRepository.count(specification);
    }

    /**
     * Function to convert {@link SalesReceiptCompilationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SalesReceiptCompilation> createSpecification(SalesReceiptCompilationCriteria criteria) {
        Specification<SalesReceiptCompilation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SalesReceiptCompilation_.id));
            }
            if (criteria.getTimeOfCompilation() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getTimeOfCompilation(), SalesReceiptCompilation_.timeOfCompilation));
            }
            if (criteria.getCompilationIdentifier() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCompilationIdentifier(), SalesReceiptCompilation_.compilationIdentifier)
                    );
            }
            if (criteria.getReceiptsCompiled() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getReceiptsCompiled(), SalesReceiptCompilation_.receiptsCompiled));
            }
            if (criteria.getCompiledById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCompiledById(),
                            root -> root.join(SalesReceiptCompilation_.compiledBy, JoinType.LEFT).get(ApplicationUser_.id)
                        )
                    );
            }
        }
        return specification;
    }
}

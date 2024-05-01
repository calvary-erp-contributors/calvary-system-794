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
import io.github.calvary.domain.ApplicationUser;
import io.github.calvary.repository.ApplicationUserRepository;
import io.github.calvary.repository.search.ApplicationUserSearchRepository;
import io.github.calvary.service.criteria.ApplicationUserCriteria;
import io.github.calvary.service.dto.ApplicationUserDTO;
import io.github.calvary.service.mapper.ApplicationUserMapper;
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
 * Service for executing complex queries for {@link ApplicationUser} entities in the database.
 * The main input is a {@link ApplicationUserCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ApplicationUserDTO} or a {@link Page} of {@link ApplicationUserDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ApplicationUserQueryService extends QueryService<ApplicationUser> {

    private final Logger log = LoggerFactory.getLogger(ApplicationUserQueryService.class);

    private final ApplicationUserRepository applicationUserRepository;

    private final ApplicationUserMapper applicationUserMapper;

    private final ApplicationUserSearchRepository applicationUserSearchRepository;

    public ApplicationUserQueryService(
        ApplicationUserRepository applicationUserRepository,
        ApplicationUserMapper applicationUserMapper,
        ApplicationUserSearchRepository applicationUserSearchRepository
    ) {
        this.applicationUserRepository = applicationUserRepository;
        this.applicationUserMapper = applicationUserMapper;
        this.applicationUserSearchRepository = applicationUserSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ApplicationUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ApplicationUserDTO> findByCriteria(ApplicationUserCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ApplicationUser> specification = createSpecification(criteria);
        return applicationUserMapper.toDto(applicationUserRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ApplicationUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ApplicationUserDTO> findByCriteria(ApplicationUserCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ApplicationUser> specification = createSpecification(criteria);
        return applicationUserRepository.findAll(specification, page).map(applicationUserMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ApplicationUserCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ApplicationUser> specification = createSpecification(criteria);
        return applicationUserRepository.count(specification);
    }

    /**
     * Function to convert {@link ApplicationUserCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ApplicationUser> createSpecification(ApplicationUserCriteria criteria) {
        Specification<ApplicationUser> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ApplicationUser_.id));
            }
            if (criteria.getApplicationIdentity() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getApplicationIdentity(), ApplicationUser_.applicationIdentity));
            }
            if (criteria.getLastLoginTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastLoginTime(), ApplicationUser_.lastLoginTime));
            }
            if (criteria.getTimeOfCreation() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimeOfCreation(), ApplicationUser_.timeOfCreation));
            }
            if (criteria.getLastTimeOfModification() != null) {
                specification =
                    specification.and(
                        buildRangeSpecification(criteria.getLastTimeOfModification(), ApplicationUser_.lastTimeOfModification)
                    );
            }
            if (criteria.getUserIdentifier() != null) {
                specification = specification.and(buildSpecification(criteria.getUserIdentifier(), ApplicationUser_.userIdentifier));
            }
            if (criteria.getCreatedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCreatedById(),
                            root -> root.join(ApplicationUser_.createdBy, JoinType.LEFT).get(ApplicationUser_.id)
                        )
                    );
            }
            if (criteria.getLastModifiedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLastModifiedById(),
                            root -> root.join(ApplicationUser_.lastModifiedBy, JoinType.LEFT).get(ApplicationUser_.id)
                        )
                    );
            }
            if (criteria.getSystemIdentityId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSystemIdentityId(),
                            root -> root.join(ApplicationUser_.systemIdentity, JoinType.LEFT).get(User_.id)
                        )
                    );
            }
        }
        return specification;
    }
}

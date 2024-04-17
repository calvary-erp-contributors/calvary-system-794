package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.SalesReceiptEmailPersona;
import io.github.calvary.repository.SalesReceiptEmailPersonaRepository;
import io.github.calvary.repository.search.SalesReceiptEmailPersonaSearchRepository;
import io.github.calvary.service.criteria.SalesReceiptEmailPersonaCriteria;
import io.github.calvary.service.dto.SalesReceiptEmailPersonaDTO;
import io.github.calvary.service.mapper.SalesReceiptEmailPersonaMapper;
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
 * Service for executing complex queries for {@link SalesReceiptEmailPersona} entities in the database.
 * The main input is a {@link SalesReceiptEmailPersonaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SalesReceiptEmailPersonaDTO} or a {@link Page} of {@link SalesReceiptEmailPersonaDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SalesReceiptEmailPersonaQueryService extends QueryService<SalesReceiptEmailPersona> {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptEmailPersonaQueryService.class);

    private final SalesReceiptEmailPersonaRepository salesReceiptEmailPersonaRepository;

    private final SalesReceiptEmailPersonaMapper salesReceiptEmailPersonaMapper;

    private final SalesReceiptEmailPersonaSearchRepository salesReceiptEmailPersonaSearchRepository;

    public SalesReceiptEmailPersonaQueryService(
        SalesReceiptEmailPersonaRepository salesReceiptEmailPersonaRepository,
        SalesReceiptEmailPersonaMapper salesReceiptEmailPersonaMapper,
        SalesReceiptEmailPersonaSearchRepository salesReceiptEmailPersonaSearchRepository
    ) {
        this.salesReceiptEmailPersonaRepository = salesReceiptEmailPersonaRepository;
        this.salesReceiptEmailPersonaMapper = salesReceiptEmailPersonaMapper;
        this.salesReceiptEmailPersonaSearchRepository = salesReceiptEmailPersonaSearchRepository;
    }

    /**
     * Return a {@link List} of {@link SalesReceiptEmailPersonaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SalesReceiptEmailPersonaDTO> findByCriteria(SalesReceiptEmailPersonaCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SalesReceiptEmailPersona> specification = createSpecification(criteria);
        return salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersonaRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SalesReceiptEmailPersonaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SalesReceiptEmailPersonaDTO> findByCriteria(SalesReceiptEmailPersonaCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SalesReceiptEmailPersona> specification = createSpecification(criteria);
        return salesReceiptEmailPersonaRepository.findAll(specification, page).map(salesReceiptEmailPersonaMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SalesReceiptEmailPersonaCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SalesReceiptEmailPersona> specification = createSpecification(criteria);
        return salesReceiptEmailPersonaRepository.count(specification);
    }

    /**
     * Function to convert {@link SalesReceiptEmailPersonaCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SalesReceiptEmailPersona> createSpecification(SalesReceiptEmailPersonaCriteria criteria) {
        Specification<SalesReceiptEmailPersona> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SalesReceiptEmailPersona_.id));
            }
            if (criteria.getEmailIdentifier() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getEmailIdentifier(), SalesReceiptEmailPersona_.emailIdentifier));
            }
            if (criteria.getMainEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMainEmail(), SalesReceiptEmailPersona_.mainEmail));
            }
            if (criteria.getClearCopyEmail() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getClearCopyEmail(), SalesReceiptEmailPersona_.clearCopyEmail));
            }
            if (criteria.getBlindCopyEmail() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getBlindCopyEmail(), SalesReceiptEmailPersona_.blindCopyEmail));
            }
            if (criteria.getLanguageKeyCode() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getLanguageKeyCode(), SalesReceiptEmailPersona_.languageKeyCode));
            }
            if (criteria.getPreferredGreeting() != null) {
                specification =
                    specification.and(
                        buildStringSpecification(criteria.getPreferredGreeting(), SalesReceiptEmailPersona_.preferredGreeting)
                    );
            }
            if (criteria.getPreferredGreetingDesignation() != null) {
                specification =
                    specification.and(
                        buildStringSpecification(
                            criteria.getPreferredGreetingDesignation(),
                            SalesReceiptEmailPersona_.preferredGreetingDesignation
                        )
                    );
            }
            if (criteria.getPreferredPrefix() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getPreferredPrefix(), SalesReceiptEmailPersona_.preferredPrefix));
            }
            if (criteria.getPreferredSuffix() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getPreferredSuffix(), SalesReceiptEmailPersona_.preferredSuffix));
            }
            if (criteria.getTimeBasedGreetings() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getTimeBasedGreetings(), SalesReceiptEmailPersona_.timeBasedGreetings));
            }
            if (criteria.getSloganBasedGreeting() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getSloganBasedGreeting(), SalesReceiptEmailPersona_.sloganBasedGreeting));
            }
            if (criteria.getAddPrefix() != null) {
                specification = specification.and(buildSpecification(criteria.getAddPrefix(), SalesReceiptEmailPersona_.addPrefix));
            }
            if (criteria.getAddSuffix() != null) {
                specification = specification.and(buildSpecification(criteria.getAddSuffix(), SalesReceiptEmailPersona_.addSuffix));
            }
            if (criteria.getPreferredSignature() != null) {
                specification =
                    specification.and(
                        buildStringSpecification(criteria.getPreferredSignature(), SalesReceiptEmailPersona_.preferredSignature)
                    );
            }
            if (criteria.getPreferredSignatureDesignation() != null) {
                specification =
                    specification.and(
                        buildStringSpecification(
                            criteria.getPreferredSignatureDesignation(),
                            SalesReceiptEmailPersona_.preferredSignatureDesignation
                        )
                    );
            }
            if (criteria.getIncludeServiceDetails() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getIncludeServiceDetails(), SalesReceiptEmailPersona_.includeServiceDetails)
                    );
            }
            if (criteria.getIncludeMessageOfTheDay() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getIncludeMessageOfTheDay(), SalesReceiptEmailPersona_.includeMessageOfTheDay)
                    );
            }
            if (criteria.getIncludeTreasuryQuote() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getIncludeTreasuryQuote(), SalesReceiptEmailPersona_.includeTreasuryQuote)
                    );
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), SalesReceiptEmailPersona_.createdAt));
            }
            if (criteria.getLastModifedAt() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getLastModifedAt(), SalesReceiptEmailPersona_.lastModifedAt));
            }
            if (criteria.getCreatedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCreatedById(),
                            root -> root.join(SalesReceiptEmailPersona_.createdBy, JoinType.LEFT).get(ApplicationUser_.id)
                        )
                    );
            }
            if (criteria.getLastModifiedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLastModifiedById(),
                            root -> root.join(SalesReceiptEmailPersona_.lastModifiedBy, JoinType.LEFT).get(ApplicationUser_.id)
                        )
                    );
            }
            if (criteria.getContributorId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getContributorId(),
                            root -> root.join(SalesReceiptEmailPersona_.contributor, JoinType.LEFT).get(Dealer_.id)
                        )
                    );
            }
        }
        return specification;
    }
}

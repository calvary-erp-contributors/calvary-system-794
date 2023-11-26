package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.SalesReceiptTitle;
import io.github.calvary.repository.SalesReceiptTitleRepository;
import io.github.calvary.repository.search.SalesReceiptTitleSearchRepository;
import io.github.calvary.service.criteria.SalesReceiptTitleCriteria;
import io.github.calvary.service.dto.SalesReceiptTitleDTO;
import io.github.calvary.service.mapper.SalesReceiptTitleMapper;
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
 * Service for executing complex queries for {@link SalesReceiptTitle} entities in the database.
 * The main input is a {@link SalesReceiptTitleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SalesReceiptTitleDTO} or a {@link Page} of {@link SalesReceiptTitleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SalesReceiptTitleQueryService extends QueryService<SalesReceiptTitle> {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptTitleQueryService.class);

    private final SalesReceiptTitleRepository salesReceiptTitleRepository;

    private final SalesReceiptTitleMapper salesReceiptTitleMapper;

    private final SalesReceiptTitleSearchRepository salesReceiptTitleSearchRepository;

    public SalesReceiptTitleQueryService(
        SalesReceiptTitleRepository salesReceiptTitleRepository,
        SalesReceiptTitleMapper salesReceiptTitleMapper,
        SalesReceiptTitleSearchRepository salesReceiptTitleSearchRepository
    ) {
        this.salesReceiptTitleRepository = salesReceiptTitleRepository;
        this.salesReceiptTitleMapper = salesReceiptTitleMapper;
        this.salesReceiptTitleSearchRepository = salesReceiptTitleSearchRepository;
    }

    /**
     * Return a {@link List} of {@link SalesReceiptTitleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SalesReceiptTitleDTO> findByCriteria(SalesReceiptTitleCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SalesReceiptTitle> specification = createSpecification(criteria);
        return salesReceiptTitleMapper.toDto(salesReceiptTitleRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SalesReceiptTitleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SalesReceiptTitleDTO> findByCriteria(SalesReceiptTitleCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SalesReceiptTitle> specification = createSpecification(criteria);
        return salesReceiptTitleRepository.findAll(specification, page).map(salesReceiptTitleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SalesReceiptTitleCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SalesReceiptTitle> specification = createSpecification(criteria);
        return salesReceiptTitleRepository.count(specification);
    }

    /**
     * Function to convert {@link SalesReceiptTitleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SalesReceiptTitle> createSpecification(SalesReceiptTitleCriteria criteria) {
        Specification<SalesReceiptTitle> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SalesReceiptTitle_.id));
            }
            if (criteria.getReceiptTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReceiptTitle(), SalesReceiptTitle_.receiptTitle));
            }
        }
        return specification;
    }
}

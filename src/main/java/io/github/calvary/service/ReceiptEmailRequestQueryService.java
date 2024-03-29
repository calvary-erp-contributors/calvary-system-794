package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.ReceiptEmailRequest;
import io.github.calvary.repository.ReceiptEmailRequestRepository;
import io.github.calvary.repository.search.ReceiptEmailRequestSearchRepository;
import io.github.calvary.service.criteria.ReceiptEmailRequestCriteria;
import io.github.calvary.service.dto.ReceiptEmailRequestDTO;
import io.github.calvary.service.mapper.ReceiptEmailRequestMapper;
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
 * Service for executing complex queries for {@link ReceiptEmailRequest} entities in the database.
 * The main input is a {@link ReceiptEmailRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ReceiptEmailRequestDTO} or a {@link Page} of {@link ReceiptEmailRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReceiptEmailRequestQueryService extends QueryService<ReceiptEmailRequest> {

    private final Logger log = LoggerFactory.getLogger(ReceiptEmailRequestQueryService.class);

    private final ReceiptEmailRequestRepository receiptEmailRequestRepository;

    private final ReceiptEmailRequestMapper receiptEmailRequestMapper;

    private final ReceiptEmailRequestSearchRepository receiptEmailRequestSearchRepository;

    public ReceiptEmailRequestQueryService(
        ReceiptEmailRequestRepository receiptEmailRequestRepository,
        ReceiptEmailRequestMapper receiptEmailRequestMapper,
        ReceiptEmailRequestSearchRepository receiptEmailRequestSearchRepository
    ) {
        this.receiptEmailRequestRepository = receiptEmailRequestRepository;
        this.receiptEmailRequestMapper = receiptEmailRequestMapper;
        this.receiptEmailRequestSearchRepository = receiptEmailRequestSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ReceiptEmailRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ReceiptEmailRequestDTO> findByCriteria(ReceiptEmailRequestCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ReceiptEmailRequest> specification = createSpecification(criteria);
        return receiptEmailRequestMapper.toDto(receiptEmailRequestRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ReceiptEmailRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReceiptEmailRequestDTO> findByCriteria(ReceiptEmailRequestCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ReceiptEmailRequest> specification = createSpecification(criteria);
        return receiptEmailRequestRepository.findAll(specification, page).map(receiptEmailRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReceiptEmailRequestCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ReceiptEmailRequest> specification = createSpecification(criteria);
        return receiptEmailRequestRepository.count(specification);
    }

    /**
     * Function to convert {@link ReceiptEmailRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ReceiptEmailRequest> createSpecification(ReceiptEmailRequestCriteria criteria) {
        Specification<ReceiptEmailRequest> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ReceiptEmailRequest_.id));
            }
            if (criteria.getTimeOfRequisition() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getTimeOfRequisition(), ReceiptEmailRequest_.timeOfRequisition));
            }
            if (criteria.getUploadComplete() != null) {
                specification = specification.and(buildSpecification(criteria.getUploadComplete(), ReceiptEmailRequest_.uploadComplete));
            }
            if (criteria.getNumberOfUpdates() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getNumberOfUpdates(), ReceiptEmailRequest_.numberOfUpdates));
            }
            if (criteria.getRequestedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getRequestedById(),
                            root -> root.join(ReceiptEmailRequest_.requestedBy, JoinType.LEFT).get(ApplicationUser_.id)
                        )
                    );
            }
        }
        return specification;
    }
}

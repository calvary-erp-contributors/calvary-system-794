package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.TransactionClass;
import io.github.calvary.repository.TransactionClassRepository;
import io.github.calvary.repository.search.TransactionClassSearchRepository;
import io.github.calvary.service.criteria.TransactionClassCriteria;
import io.github.calvary.service.dto.TransactionClassDTO;
import io.github.calvary.service.mapper.TransactionClassMapper;
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
 * Service for executing complex queries for {@link TransactionClass} entities in the database.
 * The main input is a {@link TransactionClassCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TransactionClassDTO} or a {@link Page} of {@link TransactionClassDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransactionClassQueryService extends QueryService<TransactionClass> {

    private final Logger log = LoggerFactory.getLogger(TransactionClassQueryService.class);

    private final TransactionClassRepository transactionClassRepository;

    private final TransactionClassMapper transactionClassMapper;

    private final TransactionClassSearchRepository transactionClassSearchRepository;

    public TransactionClassQueryService(
        TransactionClassRepository transactionClassRepository,
        TransactionClassMapper transactionClassMapper,
        TransactionClassSearchRepository transactionClassSearchRepository
    ) {
        this.transactionClassRepository = transactionClassRepository;
        this.transactionClassMapper = transactionClassMapper;
        this.transactionClassSearchRepository = transactionClassSearchRepository;
    }

    /**
     * Return a {@link List} of {@link TransactionClassDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TransactionClassDTO> findByCriteria(TransactionClassCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TransactionClass> specification = createSpecification(criteria);
        return transactionClassMapper.toDto(transactionClassRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TransactionClassDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransactionClassDTO> findByCriteria(TransactionClassCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransactionClass> specification = createSpecification(criteria);
        return transactionClassRepository.findAll(specification, page).map(transactionClassMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransactionClassCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TransactionClass> specification = createSpecification(criteria);
        return transactionClassRepository.count(specification);
    }

    /**
     * Function to convert {@link TransactionClassCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransactionClass> createSpecification(TransactionClassCriteria criteria) {
        Specification<TransactionClass> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TransactionClass_.id));
            }
            if (criteria.getClassName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getClassName(), TransactionClass_.className));
            }
        }
        return specification;
    }
}

package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.TransactionItemEntry;
import io.github.calvary.repository.TransactionItemEntryRepository;
import io.github.calvary.repository.search.TransactionItemEntrySearchRepository;
import io.github.calvary.service.criteria.TransactionItemEntryCriteria;
import io.github.calvary.service.dto.TransactionItemEntryDTO;
import io.github.calvary.service.mapper.TransactionItemEntryMapper;
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
 * Service for executing complex queries for {@link TransactionItemEntry} entities in the database.
 * The main input is a {@link TransactionItemEntryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TransactionItemEntryDTO} or a {@link Page} of {@link TransactionItemEntryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransactionItemEntryQueryService extends QueryService<TransactionItemEntry> {

    private final Logger log = LoggerFactory.getLogger(TransactionItemEntryQueryService.class);

    private final TransactionItemEntryRepository transactionItemEntryRepository;

    private final TransactionItemEntryMapper transactionItemEntryMapper;

    private final TransactionItemEntrySearchRepository transactionItemEntrySearchRepository;

    public TransactionItemEntryQueryService(
        TransactionItemEntryRepository transactionItemEntryRepository,
        TransactionItemEntryMapper transactionItemEntryMapper,
        TransactionItemEntrySearchRepository transactionItemEntrySearchRepository
    ) {
        this.transactionItemEntryRepository = transactionItemEntryRepository;
        this.transactionItemEntryMapper = transactionItemEntryMapper;
        this.transactionItemEntrySearchRepository = transactionItemEntrySearchRepository;
    }

    /**
     * Return a {@link List} of {@link TransactionItemEntryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TransactionItemEntryDTO> findByCriteria(TransactionItemEntryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TransactionItemEntry> specification = createSpecification(criteria);
        return transactionItemEntryMapper.toDto(transactionItemEntryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TransactionItemEntryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransactionItemEntryDTO> findByCriteria(TransactionItemEntryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransactionItemEntry> specification = createSpecification(criteria);
        return transactionItemEntryRepository.findAll(specification, page).map(transactionItemEntryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransactionItemEntryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TransactionItemEntry> specification = createSpecification(criteria);
        return transactionItemEntryRepository.count(specification);
    }

    /**
     * Function to convert {@link TransactionItemEntryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransactionItemEntry> createSpecification(TransactionItemEntryCriteria criteria) {
        Specification<TransactionItemEntry> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TransactionItemEntry_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), TransactionItemEntry_.description));
            }
            if (criteria.getItemAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getItemAmount(), TransactionItemEntry_.itemAmount));
            }
            if (criteria.getTransactionItemId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionItemId(),
                            root -> root.join(TransactionItemEntry_.transactionItem, JoinType.LEFT).get(TransactionItem_.id)
                        )
                    );
            }
        }
        return specification;
    }
}

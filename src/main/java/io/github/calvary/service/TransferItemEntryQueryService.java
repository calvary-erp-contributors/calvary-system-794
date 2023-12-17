package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.TransferItemEntry;
import io.github.calvary.repository.TransferItemEntryRepository;
import io.github.calvary.repository.search.TransferItemEntrySearchRepository;
import io.github.calvary.service.criteria.TransferItemEntryCriteria;
import io.github.calvary.service.dto.TransferItemEntryDTO;
import io.github.calvary.service.mapper.TransferItemEntryMapper;
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
 * Service for executing complex queries for {@link TransferItemEntry} entities in the database.
 * The main input is a {@link TransferItemEntryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TransferItemEntryDTO} or a {@link Page} of {@link TransferItemEntryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransferItemEntryQueryService extends QueryService<TransferItemEntry> {

    private final Logger log = LoggerFactory.getLogger(TransferItemEntryQueryService.class);

    private final TransferItemEntryRepository transferItemEntryRepository;

    private final TransferItemEntryMapper transferItemEntryMapper;

    private final TransferItemEntrySearchRepository transferItemEntrySearchRepository;

    public TransferItemEntryQueryService(
        TransferItemEntryRepository transferItemEntryRepository,
        TransferItemEntryMapper transferItemEntryMapper,
        TransferItemEntrySearchRepository transferItemEntrySearchRepository
    ) {
        this.transferItemEntryRepository = transferItemEntryRepository;
        this.transferItemEntryMapper = transferItemEntryMapper;
        this.transferItemEntrySearchRepository = transferItemEntrySearchRepository;
    }

    /**
     * Return a {@link List} of {@link TransferItemEntryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TransferItemEntryDTO> findByCriteria(TransferItemEntryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TransferItemEntry> specification = createSpecification(criteria);
        return transferItemEntryMapper.toDto(transferItemEntryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TransferItemEntryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransferItemEntryDTO> findByCriteria(TransferItemEntryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransferItemEntry> specification = createSpecification(criteria);
        return transferItemEntryRepository.findAll(specification, page).map(transferItemEntryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransferItemEntryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TransferItemEntry> specification = createSpecification(criteria);
        return transferItemEntryRepository.count(specification);
    }

    /**
     * Function to convert {@link TransferItemEntryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransferItemEntry> createSpecification(TransferItemEntryCriteria criteria) {
        Specification<TransferItemEntry> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TransferItemEntry_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), TransferItemEntry_.description));
            }
            if (criteria.getItemAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getItemAmount(), TransferItemEntry_.itemAmount));
            }
            if (criteria.getTransactionItemId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionItemId(),
                            root -> root.join(TransferItemEntry_.transactionItem, JoinType.LEFT).get(TransactionItem_.id)
                        )
                    );
            }
            if (criteria.getSalesReceiptId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSalesReceiptId(),
                            root -> root.join(TransferItemEntry_.salesReceipt, JoinType.LEFT).get(SalesReceipt_.id)
                        )
                    );
            }
        }
        return specification;
    }
}

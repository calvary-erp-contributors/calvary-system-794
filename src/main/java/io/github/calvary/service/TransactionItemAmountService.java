package io.github.calvary.service;

import io.github.calvary.service.dto.TransactionItemAmountDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.TransactionItemAmount}.
 */
public interface TransactionItemAmountService {
    /**
     * Save a transactionItemAmount.
     *
     * @param transactionItemAmountDTO the entity to save.
     * @return the persisted entity.
     */
    TransactionItemAmountDTO save(TransactionItemAmountDTO transactionItemAmountDTO);

    /**
     * Updates a transactionItemAmount.
     *
     * @param transactionItemAmountDTO the entity to update.
     * @return the persisted entity.
     */
    TransactionItemAmountDTO update(TransactionItemAmountDTO transactionItemAmountDTO);

    /**
     * Partially updates a transactionItemAmount.
     *
     * @param transactionItemAmountDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransactionItemAmountDTO> partialUpdate(TransactionItemAmountDTO transactionItemAmountDTO);

    /**
     * Get all the transactionItemAmounts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionItemAmountDTO> findAll(Pageable pageable);

    /**
     * Get all the transactionItemAmounts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionItemAmountDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" transactionItemAmount.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransactionItemAmountDTO> findOne(Long id);

    /**
     * Delete the "id" transactionItemAmount.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the transactionItemAmount corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionItemAmountDTO> search(String query, Pageable pageable);
}

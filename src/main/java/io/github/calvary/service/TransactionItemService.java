package io.github.calvary.service;

import io.github.calvary.service.dto.TransactionItemDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.TransactionItem}.
 */
public interface TransactionItemService {
    /**
     * Save a transactionItem.
     *
     * @param transactionItemDTO the entity to save.
     * @return the persisted entity.
     */
    TransactionItemDTO save(TransactionItemDTO transactionItemDTO);

    /**
     * Updates a transactionItem.
     *
     * @param transactionItemDTO the entity to update.
     * @return the persisted entity.
     */
    TransactionItemDTO update(TransactionItemDTO transactionItemDTO);

    /**
     * Partially updates a transactionItem.
     *
     * @param transactionItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransactionItemDTO> partialUpdate(TransactionItemDTO transactionItemDTO);

    /**
     * Get all the transactionItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionItemDTO> findAll(Pageable pageable);

    /**
     * Get all the transactionItems with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionItemDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" transactionItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransactionItemDTO> findOne(Long id);

    /**
     * Delete the "id" transactionItem.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the transactionItem corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionItemDTO> search(String query, Pageable pageable);
}

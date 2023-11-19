package io.github.calvary.service;

import io.github.calvary.service.dto.TransactionClassDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.TransactionClass}.
 */
public interface TransactionClassService {
    /**
     * Save a transactionClass.
     *
     * @param transactionClassDTO the entity to save.
     * @return the persisted entity.
     */
    TransactionClassDTO save(TransactionClassDTO transactionClassDTO);

    /**
     * Updates a transactionClass.
     *
     * @param transactionClassDTO the entity to update.
     * @return the persisted entity.
     */
    TransactionClassDTO update(TransactionClassDTO transactionClassDTO);

    /**
     * Partially updates a transactionClass.
     *
     * @param transactionClassDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransactionClassDTO> partialUpdate(TransactionClassDTO transactionClassDTO);

    /**
     * Get all the transactionClasses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionClassDTO> findAll(Pageable pageable);

    /**
     * Get the "id" transactionClass.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransactionClassDTO> findOne(Long id);

    /**
     * Delete the "id" transactionClass.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the transactionClass corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionClassDTO> search(String query, Pageable pageable);
}

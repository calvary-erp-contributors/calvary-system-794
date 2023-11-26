package io.github.calvary.service;

import io.github.calvary.service.dto.SalesReceiptDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.SalesReceipt}.
 */
public interface SalesReceiptService {
    /**
     * Save a salesReceipt.
     *
     * @param salesReceiptDTO the entity to save.
     * @return the persisted entity.
     */
    SalesReceiptDTO save(SalesReceiptDTO salesReceiptDTO);

    /**
     * Updates a salesReceipt.
     *
     * @param salesReceiptDTO the entity to update.
     * @return the persisted entity.
     */
    SalesReceiptDTO update(SalesReceiptDTO salesReceiptDTO);

    /**
     * Partially updates a salesReceipt.
     *
     * @param salesReceiptDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SalesReceiptDTO> partialUpdate(SalesReceiptDTO salesReceiptDTO);

    /**
     * Get all the salesReceipts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SalesReceiptDTO> findAll(Pageable pageable);

    /**
     * Get all the salesReceipts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SalesReceiptDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" salesReceipt.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SalesReceiptDTO> findOne(Long id);

    /**
     * Delete the "id" salesReceipt.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the salesReceipt corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SalesReceiptDTO> search(String query, Pageable pageable);
}

package io.github.calvary.service;

import io.github.calvary.service.dto.ReceiptEmailRequestDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.ReceiptEmailRequest}.
 */
public interface ReceiptEmailRequestService {
    /**
     * Save a receiptEmailRequest.
     *
     * @param receiptEmailRequestDTO the entity to save.
     * @return the persisted entity.
     */
    ReceiptEmailRequestDTO save(ReceiptEmailRequestDTO receiptEmailRequestDTO);

    /**
     * Updates a receiptEmailRequest.
     *
     * @param receiptEmailRequestDTO the entity to update.
     * @return the persisted entity.
     */
    ReceiptEmailRequestDTO update(ReceiptEmailRequestDTO receiptEmailRequestDTO);

    /**
     * Partially updates a receiptEmailRequest.
     *
     * @param receiptEmailRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ReceiptEmailRequestDTO> partialUpdate(ReceiptEmailRequestDTO receiptEmailRequestDTO);

    /**
     * Get all the receiptEmailRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReceiptEmailRequestDTO> findAll(Pageable pageable);

    /**
     * Get all the receiptEmailRequests with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReceiptEmailRequestDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" receiptEmailRequest.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ReceiptEmailRequestDTO> findOne(Long id);

    /**
     * Delete the "id" receiptEmailRequest.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the receiptEmailRequest corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReceiptEmailRequestDTO> search(String query, Pageable pageable);
}

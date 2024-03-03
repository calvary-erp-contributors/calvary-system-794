package io.github.calvary.service;

import io.github.calvary.service.dto.TransferItemDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.TransferItem}.
 */
public interface TransferItemService {
    /**
     * Save a transferItem.
     *
     * @param transferItemDTO the entity to save.
     * @return the persisted entity.
     */
    TransferItemDTO save(TransferItemDTO transferItemDTO);

    /**
     * Updates a transferItem.
     *
     * @param transferItemDTO the entity to update.
     * @return the persisted entity.
     */
    TransferItemDTO update(TransferItemDTO transferItemDTO);

    /**
     * Partially updates a transferItem.
     *
     * @param transferItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransferItemDTO> partialUpdate(TransferItemDTO transferItemDTO);

    /**
     * Get all the transferItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransferItemDTO> findAll(Pageable pageable);

    /**
     * Get all the transferItems with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransferItemDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" transferItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransferItemDTO> findOne(Long id);

    /**
     * Delete the "id" transferItem.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the transferItem corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransferItemDTO> search(String query, Pageable pageable);
}

package io.github.calvary.service;

import io.github.calvary.service.dto.TransferItemEntryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.TransferItemEntry}.
 */
public interface TransferItemEntryService {
    /**
     * Save a transferItemEntry.
     *
     * @param transferItemEntryDTO the entity to save.
     * @return the persisted entity.
     */
    TransferItemEntryDTO save(TransferItemEntryDTO transferItemEntryDTO);

    /**
     * Updates a transferItemEntry.
     *
     * @param transferItemEntryDTO the entity to update.
     * @return the persisted entity.
     */
    TransferItemEntryDTO update(TransferItemEntryDTO transferItemEntryDTO);

    /**
     * Partially updates a transferItemEntry.
     *
     * @param transferItemEntryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransferItemEntryDTO> partialUpdate(TransferItemEntryDTO transferItemEntryDTO);

    /**
     * Get all the transferItemEntries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransferItemEntryDTO> findAll(Pageable pageable);

    /**
     * Get all the transferItemEntries with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransferItemEntryDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" transferItemEntry.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransferItemEntryDTO> findOne(Long id);

    /**
     * Delete the "id" transferItemEntry.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the transferItemEntry corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransferItemEntryDTO> search(String query, Pageable pageable);
}

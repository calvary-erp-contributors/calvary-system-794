package io.github.calvary.service;

import io.github.calvary.service.dto.SalesReceiptEmailPersonaDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.SalesReceiptEmailPersona}.
 */
public interface SalesReceiptEmailPersonaService {
    /**
     * Save a salesReceiptEmailPersona.
     *
     * @param salesReceiptEmailPersonaDTO the entity to save.
     * @return the persisted entity.
     */
    SalesReceiptEmailPersonaDTO save(SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO);

    /**
     * Updates a salesReceiptEmailPersona.
     *
     * @param salesReceiptEmailPersonaDTO the entity to update.
     * @return the persisted entity.
     */
    SalesReceiptEmailPersonaDTO update(SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO);

    /**
     * Partially updates a salesReceiptEmailPersona.
     *
     * @param salesReceiptEmailPersonaDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SalesReceiptEmailPersonaDTO> partialUpdate(SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO);

    /**
     * Get all the salesReceiptEmailPersonas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SalesReceiptEmailPersonaDTO> findAll(Pageable pageable);

    /**
     * Get all the salesReceiptEmailPersonas with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SalesReceiptEmailPersonaDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" salesReceiptEmailPersona.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SalesReceiptEmailPersonaDTO> findOne(Long id);

    /**
     * Delete the "id" salesReceiptEmailPersona.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the salesReceiptEmailPersona corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SalesReceiptEmailPersonaDTO> search(String query, Pageable pageable);
}

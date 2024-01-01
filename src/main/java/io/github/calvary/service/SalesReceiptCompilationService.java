package io.github.calvary.service;

import io.github.calvary.service.dto.SalesReceiptCompilationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.SalesReceiptCompilation}.
 */
public interface SalesReceiptCompilationService {
    /**
     * Save a salesReceiptCompilation.
     *
     * @param salesReceiptCompilationDTO the entity to save.
     * @return the persisted entity.
     */
    SalesReceiptCompilationDTO save(SalesReceiptCompilationDTO salesReceiptCompilationDTO);

    /**
     * Updates a salesReceiptCompilation.
     *
     * @param salesReceiptCompilationDTO the entity to update.
     * @return the persisted entity.
     */
    SalesReceiptCompilationDTO update(SalesReceiptCompilationDTO salesReceiptCompilationDTO);

    /**
     * Partially updates a salesReceiptCompilation.
     *
     * @param salesReceiptCompilationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SalesReceiptCompilationDTO> partialUpdate(SalesReceiptCompilationDTO salesReceiptCompilationDTO);

    /**
     * Get all the salesReceiptCompilations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SalesReceiptCompilationDTO> findAll(Pageable pageable);

    /**
     * Get all the salesReceiptCompilations with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SalesReceiptCompilationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" salesReceiptCompilation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SalesReceiptCompilationDTO> findOne(Long id);

    /**
     * Delete the "id" salesReceiptCompilation.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the salesReceiptCompilation corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SalesReceiptCompilationDTO> search(String query, Pageable pageable);
}

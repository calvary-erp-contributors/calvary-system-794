package io.github.calvary.service;

import io.github.calvary.service.dto.SalesReceiptProposalDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.SalesReceiptProposal}.
 */
public interface SalesReceiptProposalService {
    /**
     * Save a salesReceiptProposal.
     *
     * @param salesReceiptProposalDTO the entity to save.
     * @return the persisted entity.
     */
    SalesReceiptProposalDTO save(SalesReceiptProposalDTO salesReceiptProposalDTO);

    /**
     * Updates a salesReceiptProposal.
     *
     * @param salesReceiptProposalDTO the entity to update.
     * @return the persisted entity.
     */
    SalesReceiptProposalDTO update(SalesReceiptProposalDTO salesReceiptProposalDTO);

    /**
     * Partially updates a salesReceiptProposal.
     *
     * @param salesReceiptProposalDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SalesReceiptProposalDTO> partialUpdate(SalesReceiptProposalDTO salesReceiptProposalDTO);

    /**
     * Get all the salesReceiptProposals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SalesReceiptProposalDTO> findAll(Pageable pageable);

    /**
     * Get the "id" salesReceiptProposal.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SalesReceiptProposalDTO> findOne(Long id);

    /**
     * Delete the "id" salesReceiptProposal.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the salesReceiptProposal corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SalesReceiptProposalDTO> search(String query, Pageable pageable);
}

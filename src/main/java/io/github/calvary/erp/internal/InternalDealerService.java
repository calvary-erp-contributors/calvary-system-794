package io.github.calvary.erp.internal;

import io.github.calvary.service.dto.DealerDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.Dealer}.
 */
public interface InternalDealerService {
    /**
     * Save a dealer.
     *
     * @param dealerDTO the entity to save.
     * @return the persisted entity.
     */
    DealerDTO save(DealerDTO dealerDTO);

    /**
     * Updates a dealer.
     *
     * @param dealerDTO the entity to update.
     * @return the persisted entity.
     */
    DealerDTO update(DealerDTO dealerDTO);

    /**
     * Partially updates a dealer.
     *
     * @param dealerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DealerDTO> partialUpdate(DealerDTO dealerDTO);

    /**
     * Get all the dealers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DealerDTO> findAll(Pageable pageable);

    /**
     * Get all the dealers with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DealerDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" dealer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DealerDTO> findOne(Long id);

    /**
     * Delete the "id" dealer.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the dealer corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DealerDTO> search(String query, Pageable pageable);
}

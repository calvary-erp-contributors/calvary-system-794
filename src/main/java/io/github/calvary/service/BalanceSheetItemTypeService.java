package io.github.calvary.service;

/*-
 * Copyright © 2023 - 2024 Calvary ERP Contributors (mailnjeru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.github.calvary.service.dto.BalanceSheetItemTypeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.BalanceSheetItemType}.
 */
public interface BalanceSheetItemTypeService {
    /**
     * Save a balanceSheetItemType.
     *
     * @param balanceSheetItemTypeDTO the entity to save.
     * @return the persisted entity.
     */
    BalanceSheetItemTypeDTO save(BalanceSheetItemTypeDTO balanceSheetItemTypeDTO);

    /**
     * Updates a balanceSheetItemType.
     *
     * @param balanceSheetItemTypeDTO the entity to update.
     * @return the persisted entity.
     */
    BalanceSheetItemTypeDTO update(BalanceSheetItemTypeDTO balanceSheetItemTypeDTO);

    /**
     * Partially updates a balanceSheetItemType.
     *
     * @param balanceSheetItemTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BalanceSheetItemTypeDTO> partialUpdate(BalanceSheetItemTypeDTO balanceSheetItemTypeDTO);

    /**
     * Get all the balanceSheetItemTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BalanceSheetItemTypeDTO> findAll(Pageable pageable);

    /**
     * Get all the balanceSheetItemTypes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BalanceSheetItemTypeDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" balanceSheetItemType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BalanceSheetItemTypeDTO> findOne(Long id);

    /**
     * Delete the "id" balanceSheetItemType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the balanceSheetItemType corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BalanceSheetItemTypeDTO> search(String query, Pageable pageable);
}

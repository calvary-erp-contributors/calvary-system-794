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

import io.github.calvary.service.dto.BalanceSheetItemValueDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.BalanceSheetItemValue}.
 */
public interface BalanceSheetItemValueService {
    /**
     * Save a balanceSheetItemValue.
     *
     * @param balanceSheetItemValueDTO the entity to save.
     * @return the persisted entity.
     */
    BalanceSheetItemValueDTO save(BalanceSheetItemValueDTO balanceSheetItemValueDTO);

    /**
     * Updates a balanceSheetItemValue.
     *
     * @param balanceSheetItemValueDTO the entity to update.
     * @return the persisted entity.
     */
    BalanceSheetItemValueDTO update(BalanceSheetItemValueDTO balanceSheetItemValueDTO);

    /**
     * Partially updates a balanceSheetItemValue.
     *
     * @param balanceSheetItemValueDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BalanceSheetItemValueDTO> partialUpdate(BalanceSheetItemValueDTO balanceSheetItemValueDTO);

    /**
     * Get all the balanceSheetItemValues.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BalanceSheetItemValueDTO> findAll(Pageable pageable);

    /**
     * Get all the balanceSheetItemValues with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BalanceSheetItemValueDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" balanceSheetItemValue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BalanceSheetItemValueDTO> findOne(Long id);

    /**
     * Delete the "id" balanceSheetItemValue.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the balanceSheetItemValue corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BalanceSheetItemValueDTO> search(String query, Pageable pageable);
}

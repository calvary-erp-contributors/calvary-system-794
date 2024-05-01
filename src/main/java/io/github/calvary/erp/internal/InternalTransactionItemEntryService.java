package io.github.calvary.erp.internal;

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

import io.github.calvary.service.dto.SalesReceiptDTO;
import io.github.calvary.service.dto.TransactionItemEntryDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.TransactionItemEntry}.
 */
public interface InternalTransactionItemEntryService {
    /**
     * Save a transactionItemEntry.
     *
     * @param transactionItemEntryDTO the entity to save.
     * @return the persisted entity.
     */
    TransactionItemEntryDTO save(TransactionItemEntryDTO transactionItemEntryDTO);

    /**
     * Updates a transactionItemEntry.
     *
     * @param transactionItemEntryDTO the entity to update.
     * @return the persisted entity.
     */
    TransactionItemEntryDTO update(TransactionItemEntryDTO transactionItemEntryDTO);

    /**
     * Partially updates a transactionItemEntry.
     *
     * @param transactionItemEntryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransactionItemEntryDTO> partialUpdate(TransactionItemEntryDTO transactionItemEntryDTO);

    /**
     * Get all the transactionItemEntries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionItemEntryDTO> findAll(Pageable pageable);

    /**
     * Get all the transactionItemEntries with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionItemEntryDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" transactionItemEntry.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransactionItemEntryDTO> findOne(Long id);

    /**
     * Delete the "id" transactionItemEntry.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the transactionItemEntry corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionItemEntryDTO> search(String query, Pageable pageable);

    /**
     * Get list of transaction-items related to a given sales-receipt item
     *
     * @param salesReceipt Parent owning the transaction-items we seek
     * @return Optional list of related transaction-items
     */
    Optional<List<TransactionItemEntryDTO>> findItemsRelatedToSalesReceipt(SalesReceiptDTO salesReceipt);
}

package io.github.calvary.erp.rest;

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

import io.github.calvary.erp.internal.InternalTransactionItemEntryService;
import io.github.calvary.repository.TransactionItemEntryRepository;
import io.github.calvary.service.TransactionItemEntryQueryService;
import io.github.calvary.service.TransactionItemEntryService;
import io.github.calvary.service.criteria.TransactionItemEntryCriteria;
import io.github.calvary.service.dto.TransactionItemEntryDTO;
import io.github.calvary.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.github.calvary.domain.TransactionItemEntry}.
 */
@RestController
@RequestMapping("/api/app")
public class TransactionItemEntryResourceProd {

    private final Logger log = LoggerFactory.getLogger(TransactionItemEntryResourceProd.class);

    private static final String ENTITY_NAME = "transactionItemEntry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InternalTransactionItemEntryService transactionItemEntryService;

    private final TransactionItemEntryRepository transactionItemEntryRepository;

    private final TransactionItemEntryQueryService transactionItemEntryQueryService;

    public TransactionItemEntryResourceProd(
        InternalTransactionItemEntryService transactionItemEntryService,
        TransactionItemEntryRepository transactionItemEntryRepository,
        TransactionItemEntryQueryService transactionItemEntryQueryService
    ) {
        this.transactionItemEntryService = transactionItemEntryService;
        this.transactionItemEntryRepository = transactionItemEntryRepository;
        this.transactionItemEntryQueryService = transactionItemEntryQueryService;
    }

    /**
     * {@code POST  /transaction-item-entries} : Create a new transactionItemEntry.
     *
     * @param transactionItemEntryDTO the transactionItemEntryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionItemEntryDTO, or with status {@code 400 (Bad Request)} if the transactionItemEntry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transaction-item-entries")
    public ResponseEntity<TransactionItemEntryDTO> createTransactionItemEntry(@RequestBody TransactionItemEntryDTO transactionItemEntryDTO)
        throws URISyntaxException {
        log.debug("REST request to save TransactionItemEntry : {}", transactionItemEntryDTO);
        if (transactionItemEntryDTO.getId() != null) {
            throw new BadRequestAlertException("A new transactionItemEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransactionItemEntryDTO result = transactionItemEntryService.save(transactionItemEntryDTO);
        return ResponseEntity
            .created(new URI("/api/transaction-item-entries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transaction-item-entries/:id} : Updates an existing transactionItemEntry.
     *
     * @param id the id of the transactionItemEntryDTO to save.
     * @param transactionItemEntryDTO the transactionItemEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionItemEntryDTO,
     * or with status {@code 400 (Bad Request)} if the transactionItemEntryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionItemEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transaction-item-entries/{id}")
    public ResponseEntity<TransactionItemEntryDTO> updateTransactionItemEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TransactionItemEntryDTO transactionItemEntryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TransactionItemEntry : {}, {}", id, transactionItemEntryDTO);
        if (transactionItemEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionItemEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionItemEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransactionItemEntryDTO result = transactionItemEntryService.update(transactionItemEntryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transactionItemEntryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /transaction-item-entries/:id} : Partial updates given fields of an existing transactionItemEntry, field will ignore if it is null
     *
     * @param id the id of the transactionItemEntryDTO to save.
     * @param transactionItemEntryDTO the transactionItemEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionItemEntryDTO,
     * or with status {@code 400 (Bad Request)} if the transactionItemEntryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transactionItemEntryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transactionItemEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transaction-item-entries/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionItemEntryDTO> partialUpdateTransactionItemEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TransactionItemEntryDTO transactionItemEntryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TransactionItemEntry partially : {}, {}", id, transactionItemEntryDTO);
        if (transactionItemEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionItemEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionItemEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionItemEntryDTO> result = transactionItemEntryService.partialUpdate(transactionItemEntryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transactionItemEntryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transaction-item-entries} : get all the transactionItemEntries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactionItemEntries in body.
     */
    @GetMapping("/transaction-item-entries")
    public ResponseEntity<List<TransactionItemEntryDTO>> getAllTransactionItemEntries(
        TransactionItemEntryCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TransactionItemEntries by criteria: {}", criteria);
        Page<TransactionItemEntryDTO> page = transactionItemEntryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transaction-item-entries/count} : count all the transactionItemEntries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/transaction-item-entries/count")
    public ResponseEntity<Long> countTransactionItemEntries(TransactionItemEntryCriteria criteria) {
        log.debug("REST request to count TransactionItemEntries by criteria: {}", criteria);
        return ResponseEntity.ok().body(transactionItemEntryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /transaction-item-entries/:id} : get the "id" transactionItemEntry.
     *
     * @param id the id of the transactionItemEntryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionItemEntryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transaction-item-entries/{id}")
    public ResponseEntity<TransactionItemEntryDTO> getTransactionItemEntry(@PathVariable Long id) {
        log.debug("REST request to get TransactionItemEntry : {}", id);
        Optional<TransactionItemEntryDTO> transactionItemEntryDTO = transactionItemEntryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionItemEntryDTO);
    }

    /**
     * {@code DELETE  /transaction-item-entries/:id} : delete the "id" transactionItemEntry.
     *
     * @param id the id of the transactionItemEntryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transaction-item-entries/{id}")
    public ResponseEntity<Void> deleteTransactionItemEntry(@PathVariable Long id) {
        log.debug("REST request to delete TransactionItemEntry : {}", id);
        transactionItemEntryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/transaction-item-entries?query=:query} : search for the transactionItemEntry corresponding
     * to the query.
     *
     * @param query the query of the transactionItemEntry search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/transaction-item-entries")
    public ResponseEntity<List<TransactionItemEntryDTO>> searchTransactionItemEntries(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TransactionItemEntries for query {}", query);
        Page<TransactionItemEntryDTO> page = transactionItemEntryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

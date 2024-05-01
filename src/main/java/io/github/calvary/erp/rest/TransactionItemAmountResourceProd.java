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

import io.github.calvary.repository.TransactionItemAmountRepository;
import io.github.calvary.service.TransactionItemAmountQueryService;
import io.github.calvary.service.TransactionItemAmountService;
import io.github.calvary.service.criteria.TransactionItemAmountCriteria;
import io.github.calvary.service.dto.TransactionItemAmountDTO;
import io.github.calvary.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 * REST controller for managing {@link io.github.calvary.domain.TransactionItemAmount}.
 */
@RestController
@RequestMapping("/api/app")
public class TransactionItemAmountResourceProd {

    private final Logger log = LoggerFactory.getLogger(TransactionItemAmountResourceProd.class);

    private static final String ENTITY_NAME = "transactionItemAmount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionItemAmountService transactionItemAmountService;

    private final TransactionItemAmountRepository transactionItemAmountRepository;

    private final TransactionItemAmountQueryService transactionItemAmountQueryService;

    public TransactionItemAmountResourceProd(
        TransactionItemAmountService transactionItemAmountService,
        TransactionItemAmountRepository transactionItemAmountRepository,
        TransactionItemAmountQueryService transactionItemAmountQueryService
    ) {
        this.transactionItemAmountService = transactionItemAmountService;
        this.transactionItemAmountRepository = transactionItemAmountRepository;
        this.transactionItemAmountQueryService = transactionItemAmountQueryService;
    }

    /**
     * {@code POST  /transaction-item-amounts} : Create a new transactionItemAmount.
     *
     * @param transactionItemAmountDTO the transactionItemAmountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionItemAmountDTO, or with status {@code 400 (Bad Request)} if the transactionItemAmount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transaction-item-amounts")
    public ResponseEntity<TransactionItemAmountDTO> createTransactionItemAmount(
        @Valid @RequestBody TransactionItemAmountDTO transactionItemAmountDTO
    ) throws URISyntaxException {
        log.debug("REST request to save TransactionItemAmount : {}", transactionItemAmountDTO);
        if (transactionItemAmountDTO.getId() != null) {
            throw new BadRequestAlertException("A new transactionItemAmount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransactionItemAmountDTO result = transactionItemAmountService.save(transactionItemAmountDTO);
        return ResponseEntity
            .created(new URI("/api/transaction-item-amounts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transaction-item-amounts/:id} : Updates an existing transactionItemAmount.
     *
     * @param id the id of the transactionItemAmountDTO to save.
     * @param transactionItemAmountDTO the transactionItemAmountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionItemAmountDTO,
     * or with status {@code 400 (Bad Request)} if the transactionItemAmountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionItemAmountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transaction-item-amounts/{id}")
    public ResponseEntity<TransactionItemAmountDTO> updateTransactionItemAmount(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransactionItemAmountDTO transactionItemAmountDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TransactionItemAmount : {}, {}", id, transactionItemAmountDTO);
        if (transactionItemAmountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionItemAmountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionItemAmountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransactionItemAmountDTO result = transactionItemAmountService.update(transactionItemAmountDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transactionItemAmountDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /transaction-item-amounts/:id} : Partial updates given fields of an existing transactionItemAmount, field will ignore if it is null
     *
     * @param id the id of the transactionItemAmountDTO to save.
     * @param transactionItemAmountDTO the transactionItemAmountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionItemAmountDTO,
     * or with status {@code 400 (Bad Request)} if the transactionItemAmountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transactionItemAmountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transactionItemAmountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transaction-item-amounts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionItemAmountDTO> partialUpdateTransactionItemAmount(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransactionItemAmountDTO transactionItemAmountDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TransactionItemAmount partially : {}, {}", id, transactionItemAmountDTO);
        if (transactionItemAmountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionItemAmountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionItemAmountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionItemAmountDTO> result = transactionItemAmountService.partialUpdate(transactionItemAmountDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transactionItemAmountDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transaction-item-amounts} : get all the transactionItemAmounts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactionItemAmounts in body.
     */
    @GetMapping("/transaction-item-amounts")
    public ResponseEntity<List<TransactionItemAmountDTO>> getAllTransactionItemAmounts(
        TransactionItemAmountCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TransactionItemAmounts by criteria: {}", criteria);
        Page<TransactionItemAmountDTO> page = transactionItemAmountQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transaction-item-amounts/count} : count all the transactionItemAmounts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/transaction-item-amounts/count")
    public ResponseEntity<Long> countTransactionItemAmounts(TransactionItemAmountCriteria criteria) {
        log.debug("REST request to count TransactionItemAmounts by criteria: {}", criteria);
        return ResponseEntity.ok().body(transactionItemAmountQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /transaction-item-amounts/:id} : get the "id" transactionItemAmount.
     *
     * @param id the id of the transactionItemAmountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionItemAmountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transaction-item-amounts/{id}")
    public ResponseEntity<TransactionItemAmountDTO> getTransactionItemAmount(@PathVariable Long id) {
        log.debug("REST request to get TransactionItemAmount : {}", id);
        Optional<TransactionItemAmountDTO> transactionItemAmountDTO = transactionItemAmountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionItemAmountDTO);
    }

    /**
     * {@code DELETE  /transaction-item-amounts/:id} : delete the "id" transactionItemAmount.
     *
     * @param id the id of the transactionItemAmountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transaction-item-amounts/{id}")
    public ResponseEntity<Void> deleteTransactionItemAmount(@PathVariable Long id) {
        log.debug("REST request to delete TransactionItemAmount : {}", id);
        transactionItemAmountService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/transaction-item-amounts?query=:query} : search for the transactionItemAmount corresponding
     * to the query.
     *
     * @param query the query of the transactionItemAmount search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/transaction-item-amounts")
    public ResponseEntity<List<TransactionItemAmountDTO>> searchTransactionItemAmounts(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TransactionItemAmounts for query {}", query);
        Page<TransactionItemAmountDTO> page = transactionItemAmountService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

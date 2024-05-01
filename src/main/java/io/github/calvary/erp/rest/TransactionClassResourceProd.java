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

import io.github.calvary.repository.TransactionClassRepository;
import io.github.calvary.service.TransactionClassQueryService;
import io.github.calvary.service.TransactionClassService;
import io.github.calvary.service.criteria.TransactionClassCriteria;
import io.github.calvary.service.dto.TransactionClassDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.TransactionClass}.
 */
@RestController
@RequestMapping("/api/app")
public class TransactionClassResourceProd {

    private final Logger log = LoggerFactory.getLogger(TransactionClassResourceProd.class);

    private static final String ENTITY_NAME = "transactionClass";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionClassService transactionClassService;

    private final TransactionClassRepository transactionClassRepository;

    private final TransactionClassQueryService transactionClassQueryService;

    public TransactionClassResourceProd(
        TransactionClassService transactionClassService,
        TransactionClassRepository transactionClassRepository,
        TransactionClassQueryService transactionClassQueryService
    ) {
        this.transactionClassService = transactionClassService;
        this.transactionClassRepository = transactionClassRepository;
        this.transactionClassQueryService = transactionClassQueryService;
    }

    /**
     * {@code POST  /transaction-classes} : Create a new transactionClass.
     *
     * @param transactionClassDTO the transactionClassDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionClassDTO, or with status {@code 400 (Bad Request)} if the transactionClass has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transaction-classes")
    public ResponseEntity<TransactionClassDTO> createTransactionClass(@Valid @RequestBody TransactionClassDTO transactionClassDTO)
        throws URISyntaxException {
        log.debug("REST request to save TransactionClass : {}", transactionClassDTO);
        if (transactionClassDTO.getId() != null) {
            throw new BadRequestAlertException("A new transactionClass cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransactionClassDTO result = transactionClassService.save(transactionClassDTO);
        return ResponseEntity
            .created(new URI("/api/transaction-classes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transaction-classes/:id} : Updates an existing transactionClass.
     *
     * @param id the id of the transactionClassDTO to save.
     * @param transactionClassDTO the transactionClassDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionClassDTO,
     * or with status {@code 400 (Bad Request)} if the transactionClassDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionClassDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transaction-classes/{id}")
    public ResponseEntity<TransactionClassDTO> updateTransactionClass(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransactionClassDTO transactionClassDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TransactionClass : {}, {}", id, transactionClassDTO);
        if (transactionClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionClassRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransactionClassDTO result = transactionClassService.update(transactionClassDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transactionClassDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /transaction-classes/:id} : Partial updates given fields of an existing transactionClass, field will ignore if it is null
     *
     * @param id the id of the transactionClassDTO to save.
     * @param transactionClassDTO the transactionClassDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionClassDTO,
     * or with status {@code 400 (Bad Request)} if the transactionClassDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transactionClassDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transactionClassDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transaction-classes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionClassDTO> partialUpdateTransactionClass(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransactionClassDTO transactionClassDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TransactionClass partially : {}, {}", id, transactionClassDTO);
        if (transactionClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionClassRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionClassDTO> result = transactionClassService.partialUpdate(transactionClassDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transactionClassDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transaction-classes} : get all the transactionClasses.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactionClasses in body.
     */
    @GetMapping("/transaction-classes")
    public ResponseEntity<List<TransactionClassDTO>> getAllTransactionClasses(
        TransactionClassCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TransactionClasses by criteria: {}", criteria);
        Page<TransactionClassDTO> page = transactionClassQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transaction-classes/count} : count all the transactionClasses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/transaction-classes/count")
    public ResponseEntity<Long> countTransactionClasses(TransactionClassCriteria criteria) {
        log.debug("REST request to count TransactionClasses by criteria: {}", criteria);
        return ResponseEntity.ok().body(transactionClassQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /transaction-classes/:id} : get the "id" transactionClass.
     *
     * @param id the id of the transactionClassDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionClassDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transaction-classes/{id}")
    public ResponseEntity<TransactionClassDTO> getTransactionClass(@PathVariable Long id) {
        log.debug("REST request to get TransactionClass : {}", id);
        Optional<TransactionClassDTO> transactionClassDTO = transactionClassService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionClassDTO);
    }

    /**
     * {@code DELETE  /transaction-classes/:id} : delete the "id" transactionClass.
     *
     * @param id the id of the transactionClassDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transaction-classes/{id}")
    public ResponseEntity<Void> deleteTransactionClass(@PathVariable Long id) {
        log.debug("REST request to delete TransactionClass : {}", id);
        transactionClassService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/transaction-classes?query=:query} : search for the transactionClass corresponding
     * to the query.
     *
     * @param query the query of the transactionClass search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/transaction-classes")
    public ResponseEntity<List<TransactionClassDTO>> searchTransactionClasses(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TransactionClasses for query {}", query);
        Page<TransactionClassDTO> page = transactionClassService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

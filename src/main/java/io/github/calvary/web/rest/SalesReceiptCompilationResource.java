package io.github.calvary.web.rest;

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

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.github.calvary.repository.SalesReceiptCompilationRepository;
import io.github.calvary.service.SalesReceiptCompilationQueryService;
import io.github.calvary.service.SalesReceiptCompilationService;
import io.github.calvary.service.criteria.SalesReceiptCompilationCriteria;
import io.github.calvary.service.dto.SalesReceiptCompilationDTO;
import io.github.calvary.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
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
 * REST controller for managing {@link io.github.calvary.domain.SalesReceiptCompilation}.
 */
@RestController
@RequestMapping("/api")
public class SalesReceiptCompilationResource {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptCompilationResource.class);

    private static final String ENTITY_NAME = "salesReceiptCompilation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalesReceiptCompilationService salesReceiptCompilationService;

    private final SalesReceiptCompilationRepository salesReceiptCompilationRepository;

    private final SalesReceiptCompilationQueryService salesReceiptCompilationQueryService;

    public SalesReceiptCompilationResource(
        SalesReceiptCompilationService salesReceiptCompilationService,
        SalesReceiptCompilationRepository salesReceiptCompilationRepository,
        SalesReceiptCompilationQueryService salesReceiptCompilationQueryService
    ) {
        this.salesReceiptCompilationService = salesReceiptCompilationService;
        this.salesReceiptCompilationRepository = salesReceiptCompilationRepository;
        this.salesReceiptCompilationQueryService = salesReceiptCompilationQueryService;
    }

    /**
     * {@code POST  /sales-receipt-compilations} : Create a new salesReceiptCompilation.
     *
     * @param salesReceiptCompilationDTO the salesReceiptCompilationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new salesReceiptCompilationDTO, or with status {@code 400 (Bad Request)} if the salesReceiptCompilation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sales-receipt-compilations")
    public ResponseEntity<SalesReceiptCompilationDTO> createSalesReceiptCompilation(
        @RequestBody SalesReceiptCompilationDTO salesReceiptCompilationDTO
    ) throws URISyntaxException {
        log.debug("REST request to save SalesReceiptCompilation : {}", salesReceiptCompilationDTO);
        if (salesReceiptCompilationDTO.getId() != null) {
            throw new BadRequestAlertException("A new salesReceiptCompilation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SalesReceiptCompilationDTO result = salesReceiptCompilationService.save(salesReceiptCompilationDTO);
        return ResponseEntity
            .created(new URI("/api/sales-receipt-compilations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sales-receipt-compilations/:id} : Updates an existing salesReceiptCompilation.
     *
     * @param id the id of the salesReceiptCompilationDTO to save.
     * @param salesReceiptCompilationDTO the salesReceiptCompilationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesReceiptCompilationDTO,
     * or with status {@code 400 (Bad Request)} if the salesReceiptCompilationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the salesReceiptCompilationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sales-receipt-compilations/{id}")
    public ResponseEntity<SalesReceiptCompilationDTO> updateSalesReceiptCompilation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SalesReceiptCompilationDTO salesReceiptCompilationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update SalesReceiptCompilation : {}, {}", id, salesReceiptCompilationDTO);
        if (salesReceiptCompilationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesReceiptCompilationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesReceiptCompilationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SalesReceiptCompilationDTO result = salesReceiptCompilationService.update(salesReceiptCompilationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesReceiptCompilationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sales-receipt-compilations/:id} : Partial updates given fields of an existing salesReceiptCompilation, field will ignore if it is null
     *
     * @param id the id of the salesReceiptCompilationDTO to save.
     * @param salesReceiptCompilationDTO the salesReceiptCompilationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesReceiptCompilationDTO,
     * or with status {@code 400 (Bad Request)} if the salesReceiptCompilationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the salesReceiptCompilationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the salesReceiptCompilationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sales-receipt-compilations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SalesReceiptCompilationDTO> partialUpdateSalesReceiptCompilation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SalesReceiptCompilationDTO salesReceiptCompilationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SalesReceiptCompilation partially : {}, {}", id, salesReceiptCompilationDTO);
        if (salesReceiptCompilationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesReceiptCompilationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesReceiptCompilationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SalesReceiptCompilationDTO> result = salesReceiptCompilationService.partialUpdate(salesReceiptCompilationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesReceiptCompilationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sales-receipt-compilations} : get all the salesReceiptCompilations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of salesReceiptCompilations in body.
     */
    @GetMapping("/sales-receipt-compilations")
    public ResponseEntity<List<SalesReceiptCompilationDTO>> getAllSalesReceiptCompilations(
        SalesReceiptCompilationCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get SalesReceiptCompilations by criteria: {}", criteria);
        Page<SalesReceiptCompilationDTO> page = salesReceiptCompilationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sales-receipt-compilations/count} : count all the salesReceiptCompilations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/sales-receipt-compilations/count")
    public ResponseEntity<Long> countSalesReceiptCompilations(SalesReceiptCompilationCriteria criteria) {
        log.debug("REST request to count SalesReceiptCompilations by criteria: {}", criteria);
        return ResponseEntity.ok().body(salesReceiptCompilationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sales-receipt-compilations/:id} : get the "id" salesReceiptCompilation.
     *
     * @param id the id of the salesReceiptCompilationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the salesReceiptCompilationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sales-receipt-compilations/{id}")
    public ResponseEntity<SalesReceiptCompilationDTO> getSalesReceiptCompilation(@PathVariable Long id) {
        log.debug("REST request to get SalesReceiptCompilation : {}", id);
        Optional<SalesReceiptCompilationDTO> salesReceiptCompilationDTO = salesReceiptCompilationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(salesReceiptCompilationDTO);
    }

    /**
     * {@code DELETE  /sales-receipt-compilations/:id} : delete the "id" salesReceiptCompilation.
     *
     * @param id the id of the salesReceiptCompilationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sales-receipt-compilations/{id}")
    public ResponseEntity<Void> deleteSalesReceiptCompilation(@PathVariable Long id) {
        log.debug("REST request to delete SalesReceiptCompilation : {}", id);
        salesReceiptCompilationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/sales-receipt-compilations?query=:query} : search for the salesReceiptCompilation corresponding
     * to the query.
     *
     * @param query the query of the salesReceiptCompilation search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/sales-receipt-compilations")
    public ResponseEntity<List<SalesReceiptCompilationDTO>> searchSalesReceiptCompilations(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of SalesReceiptCompilations for query {}", query);
        Page<SalesReceiptCompilationDTO> page = salesReceiptCompilationService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

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

import io.github.calvary.repository.SalesReceiptProposalRepository;
import io.github.calvary.service.SalesReceiptProposalQueryService;
import io.github.calvary.service.SalesReceiptProposalService;
import io.github.calvary.service.criteria.SalesReceiptProposalCriteria;
import io.github.calvary.service.dto.SalesReceiptProposalDTO;
import io.github.calvary.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
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
 * REST controller for managing {@link io.github.calvary.domain.SalesReceiptProposal}.
 */
@RestController
@RequestMapping("/api")
public class SalesReceiptProposalResource {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptProposalResource.class);

    private static final String ENTITY_NAME = "salesReceiptProposal";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalesReceiptProposalService salesReceiptProposalService;

    private final SalesReceiptProposalRepository salesReceiptProposalRepository;

    private final SalesReceiptProposalQueryService salesReceiptProposalQueryService;

    public SalesReceiptProposalResource(
        SalesReceiptProposalService salesReceiptProposalService,
        SalesReceiptProposalRepository salesReceiptProposalRepository,
        SalesReceiptProposalQueryService salesReceiptProposalQueryService
    ) {
        this.salesReceiptProposalService = salesReceiptProposalService;
        this.salesReceiptProposalRepository = salesReceiptProposalRepository;
        this.salesReceiptProposalQueryService = salesReceiptProposalQueryService;
    }

    /**
     * {@code POST  /sales-receipt-proposals} : Create a new salesReceiptProposal.
     *
     * @param salesReceiptProposalDTO the salesReceiptProposalDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new salesReceiptProposalDTO, or with status {@code 400 (Bad Request)} if the salesReceiptProposal has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sales-receipt-proposals")
    public ResponseEntity<SalesReceiptProposalDTO> createSalesReceiptProposal(
        @Valid @RequestBody SalesReceiptProposalDTO salesReceiptProposalDTO
    ) throws URISyntaxException {
        log.debug("REST request to save SalesReceiptProposal : {}", salesReceiptProposalDTO);
        if (salesReceiptProposalDTO.getId() != null) {
            throw new BadRequestAlertException("A new salesReceiptProposal cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SalesReceiptProposalDTO result = salesReceiptProposalService.save(salesReceiptProposalDTO);
        return ResponseEntity
            .created(new URI("/api/sales-receipt-proposals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sales-receipt-proposals/:id} : Updates an existing salesReceiptProposal.
     *
     * @param id the id of the salesReceiptProposalDTO to save.
     * @param salesReceiptProposalDTO the salesReceiptProposalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesReceiptProposalDTO,
     * or with status {@code 400 (Bad Request)} if the salesReceiptProposalDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the salesReceiptProposalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sales-receipt-proposals/{id}")
    public ResponseEntity<SalesReceiptProposalDTO> updateSalesReceiptProposal(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SalesReceiptProposalDTO salesReceiptProposalDTO
    ) throws URISyntaxException {
        log.debug("REST request to update SalesReceiptProposal : {}, {}", id, salesReceiptProposalDTO);
        if (salesReceiptProposalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesReceiptProposalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesReceiptProposalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SalesReceiptProposalDTO result = salesReceiptProposalService.update(salesReceiptProposalDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesReceiptProposalDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sales-receipt-proposals/:id} : Partial updates given fields of an existing salesReceiptProposal, field will ignore if it is null
     *
     * @param id the id of the salesReceiptProposalDTO to save.
     * @param salesReceiptProposalDTO the salesReceiptProposalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesReceiptProposalDTO,
     * or with status {@code 400 (Bad Request)} if the salesReceiptProposalDTO is not valid,
     * or with status {@code 404 (Not Found)} if the salesReceiptProposalDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the salesReceiptProposalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sales-receipt-proposals/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SalesReceiptProposalDTO> partialUpdateSalesReceiptProposal(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SalesReceiptProposalDTO salesReceiptProposalDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SalesReceiptProposal partially : {}, {}", id, salesReceiptProposalDTO);
        if (salesReceiptProposalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesReceiptProposalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesReceiptProposalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SalesReceiptProposalDTO> result = salesReceiptProposalService.partialUpdate(salesReceiptProposalDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesReceiptProposalDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sales-receipt-proposals} : get all the salesReceiptProposals.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of salesReceiptProposals in body.
     */
    @GetMapping("/sales-receipt-proposals")
    public ResponseEntity<List<SalesReceiptProposalDTO>> getAllSalesReceiptProposals(
        SalesReceiptProposalCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get SalesReceiptProposals by criteria: {}", criteria);
        Page<SalesReceiptProposalDTO> page = salesReceiptProposalQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sales-receipt-proposals/count} : count all the salesReceiptProposals.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/sales-receipt-proposals/count")
    public ResponseEntity<Long> countSalesReceiptProposals(SalesReceiptProposalCriteria criteria) {
        log.debug("REST request to count SalesReceiptProposals by criteria: {}", criteria);
        return ResponseEntity.ok().body(salesReceiptProposalQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sales-receipt-proposals/:id} : get the "id" salesReceiptProposal.
     *
     * @param id the id of the salesReceiptProposalDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the salesReceiptProposalDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sales-receipt-proposals/{id}")
    public ResponseEntity<SalesReceiptProposalDTO> getSalesReceiptProposal(@PathVariable Long id) {
        log.debug("REST request to get SalesReceiptProposal : {}", id);
        Optional<SalesReceiptProposalDTO> salesReceiptProposalDTO = salesReceiptProposalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(salesReceiptProposalDTO);
    }

    /**
     * {@code DELETE  /sales-receipt-proposals/:id} : delete the "id" salesReceiptProposal.
     *
     * @param id the id of the salesReceiptProposalDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sales-receipt-proposals/{id}")
    public ResponseEntity<Void> deleteSalesReceiptProposal(@PathVariable Long id) {
        log.debug("REST request to delete SalesReceiptProposal : {}", id);
        salesReceiptProposalService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/sales-receipt-proposals?query=:query} : search for the salesReceiptProposal corresponding
     * to the query.
     *
     * @param query the query of the salesReceiptProposal search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/sales-receipt-proposals")
    public ResponseEntity<List<SalesReceiptProposalDTO>> searchSalesReceiptProposals(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of SalesReceiptProposals for query {}", query);
        Page<SalesReceiptProposalDTO> page = salesReceiptProposalService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

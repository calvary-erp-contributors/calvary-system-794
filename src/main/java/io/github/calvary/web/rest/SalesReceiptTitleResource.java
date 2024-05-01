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

import io.github.calvary.repository.SalesReceiptTitleRepository;
import io.github.calvary.service.SalesReceiptTitleQueryService;
import io.github.calvary.service.SalesReceiptTitleService;
import io.github.calvary.service.criteria.SalesReceiptTitleCriteria;
import io.github.calvary.service.dto.SalesReceiptTitleDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.SalesReceiptTitle}.
 */
@RestController
@RequestMapping("/api")
public class SalesReceiptTitleResource {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptTitleResource.class);

    private static final String ENTITY_NAME = "salesReceiptTitle";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalesReceiptTitleService salesReceiptTitleService;

    private final SalesReceiptTitleRepository salesReceiptTitleRepository;

    private final SalesReceiptTitleQueryService salesReceiptTitleQueryService;

    public SalesReceiptTitleResource(
        SalesReceiptTitleService salesReceiptTitleService,
        SalesReceiptTitleRepository salesReceiptTitleRepository,
        SalesReceiptTitleQueryService salesReceiptTitleQueryService
    ) {
        this.salesReceiptTitleService = salesReceiptTitleService;
        this.salesReceiptTitleRepository = salesReceiptTitleRepository;
        this.salesReceiptTitleQueryService = salesReceiptTitleQueryService;
    }

    /**
     * {@code POST  /sales-receipt-titles} : Create a new salesReceiptTitle.
     *
     * @param salesReceiptTitleDTO the salesReceiptTitleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new salesReceiptTitleDTO, or with status {@code 400 (Bad Request)} if the salesReceiptTitle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sales-receipt-titles")
    public ResponseEntity<SalesReceiptTitleDTO> createSalesReceiptTitle(@Valid @RequestBody SalesReceiptTitleDTO salesReceiptTitleDTO)
        throws URISyntaxException {
        log.debug("REST request to save SalesReceiptTitle : {}", salesReceiptTitleDTO);
        if (salesReceiptTitleDTO.getId() != null) {
            throw new BadRequestAlertException("A new salesReceiptTitle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SalesReceiptTitleDTO result = salesReceiptTitleService.save(salesReceiptTitleDTO);
        return ResponseEntity
            .created(new URI("/api/sales-receipt-titles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sales-receipt-titles/:id} : Updates an existing salesReceiptTitle.
     *
     * @param id the id of the salesReceiptTitleDTO to save.
     * @param salesReceiptTitleDTO the salesReceiptTitleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesReceiptTitleDTO,
     * or with status {@code 400 (Bad Request)} if the salesReceiptTitleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the salesReceiptTitleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sales-receipt-titles/{id}")
    public ResponseEntity<SalesReceiptTitleDTO> updateSalesReceiptTitle(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SalesReceiptTitleDTO salesReceiptTitleDTO
    ) throws URISyntaxException {
        log.debug("REST request to update SalesReceiptTitle : {}, {}", id, salesReceiptTitleDTO);
        if (salesReceiptTitleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesReceiptTitleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesReceiptTitleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SalesReceiptTitleDTO result = salesReceiptTitleService.update(salesReceiptTitleDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesReceiptTitleDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sales-receipt-titles/:id} : Partial updates given fields of an existing salesReceiptTitle, field will ignore if it is null
     *
     * @param id the id of the salesReceiptTitleDTO to save.
     * @param salesReceiptTitleDTO the salesReceiptTitleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesReceiptTitleDTO,
     * or with status {@code 400 (Bad Request)} if the salesReceiptTitleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the salesReceiptTitleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the salesReceiptTitleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sales-receipt-titles/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SalesReceiptTitleDTO> partialUpdateSalesReceiptTitle(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SalesReceiptTitleDTO salesReceiptTitleDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SalesReceiptTitle partially : {}, {}", id, salesReceiptTitleDTO);
        if (salesReceiptTitleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesReceiptTitleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesReceiptTitleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SalesReceiptTitleDTO> result = salesReceiptTitleService.partialUpdate(salesReceiptTitleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesReceiptTitleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sales-receipt-titles} : get all the salesReceiptTitles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of salesReceiptTitles in body.
     */
    @GetMapping("/sales-receipt-titles")
    public ResponseEntity<List<SalesReceiptTitleDTO>> getAllSalesReceiptTitles(
        SalesReceiptTitleCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get SalesReceiptTitles by criteria: {}", criteria);
        Page<SalesReceiptTitleDTO> page = salesReceiptTitleQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sales-receipt-titles/count} : count all the salesReceiptTitles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/sales-receipt-titles/count")
    public ResponseEntity<Long> countSalesReceiptTitles(SalesReceiptTitleCriteria criteria) {
        log.debug("REST request to count SalesReceiptTitles by criteria: {}", criteria);
        return ResponseEntity.ok().body(salesReceiptTitleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sales-receipt-titles/:id} : get the "id" salesReceiptTitle.
     *
     * @param id the id of the salesReceiptTitleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the salesReceiptTitleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sales-receipt-titles/{id}")
    public ResponseEntity<SalesReceiptTitleDTO> getSalesReceiptTitle(@PathVariable Long id) {
        log.debug("REST request to get SalesReceiptTitle : {}", id);
        Optional<SalesReceiptTitleDTO> salesReceiptTitleDTO = salesReceiptTitleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(salesReceiptTitleDTO);
    }

    /**
     * {@code DELETE  /sales-receipt-titles/:id} : delete the "id" salesReceiptTitle.
     *
     * @param id the id of the salesReceiptTitleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sales-receipt-titles/{id}")
    public ResponseEntity<Void> deleteSalesReceiptTitle(@PathVariable Long id) {
        log.debug("REST request to delete SalesReceiptTitle : {}", id);
        salesReceiptTitleService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/sales-receipt-titles?query=:query} : search for the salesReceiptTitle corresponding
     * to the query.
     *
     * @param query the query of the salesReceiptTitle search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/sales-receipt-titles")
    public ResponseEntity<List<SalesReceiptTitleDTO>> searchSalesReceiptTitles(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of SalesReceiptTitles for query {}", query);
        Page<SalesReceiptTitleDTO> page = salesReceiptTitleService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

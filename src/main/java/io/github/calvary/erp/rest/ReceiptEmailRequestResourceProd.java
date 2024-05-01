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

import io.github.calvary.repository.ReceiptEmailRequestRepository;
import io.github.calvary.service.ReceiptEmailRequestQueryService;
import io.github.calvary.service.ReceiptEmailRequestService;
import io.github.calvary.service.criteria.ReceiptEmailRequestCriteria;
import io.github.calvary.service.dto.ReceiptEmailRequestDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.ReceiptEmailRequest}.
 */
@RestController
@RequestMapping("/api/app")
public class ReceiptEmailRequestResourceProd {

    private final Logger log = LoggerFactory.getLogger(ReceiptEmailRequestResourceProd.class);

    private static final String ENTITY_NAME = "receiptEmailRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReceiptEmailRequestService receiptEmailRequestService;

    private final ReceiptEmailRequestRepository receiptEmailRequestRepository;

    private final ReceiptEmailRequestQueryService receiptEmailRequestQueryService;

    public ReceiptEmailRequestResourceProd(
        ReceiptEmailRequestService receiptEmailRequestService,
        ReceiptEmailRequestRepository receiptEmailRequestRepository,
        ReceiptEmailRequestQueryService receiptEmailRequestQueryService
    ) {
        this.receiptEmailRequestService = receiptEmailRequestService;
        this.receiptEmailRequestRepository = receiptEmailRequestRepository;
        this.receiptEmailRequestQueryService = receiptEmailRequestQueryService;
    }

    /**
     * {@code POST  /receipt-email-requests} : Create a new receiptEmailRequest.
     *
     * @param receiptEmailRequestDTO the receiptEmailRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new receiptEmailRequestDTO, or with status {@code 400 (Bad Request)} if the receiptEmailRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/receipt-email-requests")
    public ResponseEntity<ReceiptEmailRequestDTO> createReceiptEmailRequest(
        @Valid @RequestBody ReceiptEmailRequestDTO receiptEmailRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ReceiptEmailRequest : {}", receiptEmailRequestDTO);
        if (receiptEmailRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new receiptEmailRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ReceiptEmailRequestDTO result = receiptEmailRequestService.save(receiptEmailRequestDTO);
        return ResponseEntity
            .created(new URI("/api/receipt-email-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /receipt-email-requests/:id} : Updates an existing receiptEmailRequest.
     *
     * @param id the id of the receiptEmailRequestDTO to save.
     * @param receiptEmailRequestDTO the receiptEmailRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated receiptEmailRequestDTO,
     * or with status {@code 400 (Bad Request)} if the receiptEmailRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the receiptEmailRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/receipt-email-requests/{id}")
    public ResponseEntity<ReceiptEmailRequestDTO> updateReceiptEmailRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReceiptEmailRequestDTO receiptEmailRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ReceiptEmailRequest : {}, {}", id, receiptEmailRequestDTO);
        if (receiptEmailRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, receiptEmailRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!receiptEmailRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ReceiptEmailRequestDTO result = receiptEmailRequestService.update(receiptEmailRequestDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, receiptEmailRequestDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /receipt-email-requests/:id} : Partial updates given fields of an existing receiptEmailRequest, field will ignore if it is null
     *
     * @param id the id of the receiptEmailRequestDTO to save.
     * @param receiptEmailRequestDTO the receiptEmailRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated receiptEmailRequestDTO,
     * or with status {@code 400 (Bad Request)} if the receiptEmailRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the receiptEmailRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the receiptEmailRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/receipt-email-requests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReceiptEmailRequestDTO> partialUpdateReceiptEmailRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReceiptEmailRequestDTO receiptEmailRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ReceiptEmailRequest partially : {}, {}", id, receiptEmailRequestDTO);
        if (receiptEmailRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, receiptEmailRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!receiptEmailRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReceiptEmailRequestDTO> result = receiptEmailRequestService.partialUpdate(receiptEmailRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, receiptEmailRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /receipt-email-requests} : get all the receiptEmailRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of receiptEmailRequests in body.
     */
    @GetMapping("/receipt-email-requests")
    public ResponseEntity<List<ReceiptEmailRequestDTO>> getAllReceiptEmailRequests(
        ReceiptEmailRequestCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get ReceiptEmailRequests by criteria: {}", criteria);
        Page<ReceiptEmailRequestDTO> page = receiptEmailRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /receipt-email-requests/count} : count all the receiptEmailRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/receipt-email-requests/count")
    public ResponseEntity<Long> countReceiptEmailRequests(ReceiptEmailRequestCriteria criteria) {
        log.debug("REST request to count ReceiptEmailRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(receiptEmailRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /receipt-email-requests/:id} : get the "id" receiptEmailRequest.
     *
     * @param id the id of the receiptEmailRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the receiptEmailRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/receipt-email-requests/{id}")
    public ResponseEntity<ReceiptEmailRequestDTO> getReceiptEmailRequest(@PathVariable Long id) {
        log.debug("REST request to get ReceiptEmailRequest : {}", id);
        Optional<ReceiptEmailRequestDTO> receiptEmailRequestDTO = receiptEmailRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(receiptEmailRequestDTO);
    }

    /**
     * {@code DELETE  /receipt-email-requests/:id} : delete the "id" receiptEmailRequest.
     *
     * @param id the id of the receiptEmailRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/receipt-email-requests/{id}")
    public ResponseEntity<Void> deleteReceiptEmailRequest(@PathVariable Long id) {
        log.debug("REST request to delete ReceiptEmailRequest : {}", id);
        receiptEmailRequestService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/receipt-email-requests?query=:query} : search for the receiptEmailRequest corresponding
     * to the query.
     *
     * @param query the query of the receiptEmailRequest search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/receipt-email-requests")
    public ResponseEntity<List<ReceiptEmailRequestDTO>> searchReceiptEmailRequests(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of ReceiptEmailRequests for query {}", query);
        Page<ReceiptEmailRequestDTO> page = receiptEmailRequestService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

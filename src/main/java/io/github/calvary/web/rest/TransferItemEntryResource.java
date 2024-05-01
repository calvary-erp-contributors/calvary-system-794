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

import io.github.calvary.repository.TransferItemEntryRepository;
import io.github.calvary.service.TransferItemEntryQueryService;
import io.github.calvary.service.TransferItemEntryService;
import io.github.calvary.service.criteria.TransferItemEntryCriteria;
import io.github.calvary.service.dto.TransferItemEntryDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.TransferItemEntry}.
 */
@RestController
@RequestMapping("/api")
public class TransferItemEntryResource {

    private final Logger log = LoggerFactory.getLogger(TransferItemEntryResource.class);

    private static final String ENTITY_NAME = "transferItemEntry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransferItemEntryService transferItemEntryService;

    private final TransferItemEntryRepository transferItemEntryRepository;

    private final TransferItemEntryQueryService transferItemEntryQueryService;

    public TransferItemEntryResource(
        TransferItemEntryService transferItemEntryService,
        TransferItemEntryRepository transferItemEntryRepository,
        TransferItemEntryQueryService transferItemEntryQueryService
    ) {
        this.transferItemEntryService = transferItemEntryService;
        this.transferItemEntryRepository = transferItemEntryRepository;
        this.transferItemEntryQueryService = transferItemEntryQueryService;
    }

    /**
     * {@code POST  /transfer-item-entries} : Create a new transferItemEntry.
     *
     * @param transferItemEntryDTO the transferItemEntryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transferItemEntryDTO, or with status {@code 400 (Bad Request)} if the transferItemEntry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transfer-item-entries")
    public ResponseEntity<TransferItemEntryDTO> createTransferItemEntry(@Valid @RequestBody TransferItemEntryDTO transferItemEntryDTO)
        throws URISyntaxException {
        log.debug("REST request to save TransferItemEntry : {}", transferItemEntryDTO);
        if (transferItemEntryDTO.getId() != null) {
            throw new BadRequestAlertException("A new transferItemEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransferItemEntryDTO result = transferItemEntryService.save(transferItemEntryDTO);
        return ResponseEntity
            .created(new URI("/api/transfer-item-entries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transfer-item-entries/:id} : Updates an existing transferItemEntry.
     *
     * @param id the id of the transferItemEntryDTO to save.
     * @param transferItemEntryDTO the transferItemEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transferItemEntryDTO,
     * or with status {@code 400 (Bad Request)} if the transferItemEntryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transferItemEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transfer-item-entries/{id}")
    public ResponseEntity<TransferItemEntryDTO> updateTransferItemEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransferItemEntryDTO transferItemEntryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TransferItemEntry : {}, {}", id, transferItemEntryDTO);
        if (transferItemEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transferItemEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transferItemEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransferItemEntryDTO result = transferItemEntryService.update(transferItemEntryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transferItemEntryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /transfer-item-entries/:id} : Partial updates given fields of an existing transferItemEntry, field will ignore if it is null
     *
     * @param id the id of the transferItemEntryDTO to save.
     * @param transferItemEntryDTO the transferItemEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transferItemEntryDTO,
     * or with status {@code 400 (Bad Request)} if the transferItemEntryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transferItemEntryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transferItemEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transfer-item-entries/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransferItemEntryDTO> partialUpdateTransferItemEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransferItemEntryDTO transferItemEntryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TransferItemEntry partially : {}, {}", id, transferItemEntryDTO);
        if (transferItemEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transferItemEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transferItemEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransferItemEntryDTO> result = transferItemEntryService.partialUpdate(transferItemEntryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transferItemEntryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transfer-item-entries} : get all the transferItemEntries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transferItemEntries in body.
     */
    @GetMapping("/transfer-item-entries")
    public ResponseEntity<List<TransferItemEntryDTO>> getAllTransferItemEntries(
        TransferItemEntryCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TransferItemEntries by criteria: {}", criteria);
        Page<TransferItemEntryDTO> page = transferItemEntryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transfer-item-entries/count} : count all the transferItemEntries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/transfer-item-entries/count")
    public ResponseEntity<Long> countTransferItemEntries(TransferItemEntryCriteria criteria) {
        log.debug("REST request to count TransferItemEntries by criteria: {}", criteria);
        return ResponseEntity.ok().body(transferItemEntryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /transfer-item-entries/:id} : get the "id" transferItemEntry.
     *
     * @param id the id of the transferItemEntryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transferItemEntryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transfer-item-entries/{id}")
    public ResponseEntity<TransferItemEntryDTO> getTransferItemEntry(@PathVariable Long id) {
        log.debug("REST request to get TransferItemEntry : {}", id);
        Optional<TransferItemEntryDTO> transferItemEntryDTO = transferItemEntryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transferItemEntryDTO);
    }

    /**
     * {@code DELETE  /transfer-item-entries/:id} : delete the "id" transferItemEntry.
     *
     * @param id the id of the transferItemEntryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transfer-item-entries/{id}")
    public ResponseEntity<Void> deleteTransferItemEntry(@PathVariable Long id) {
        log.debug("REST request to delete TransferItemEntry : {}", id);
        transferItemEntryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/transfer-item-entries?query=:query} : search for the transferItemEntry corresponding
     * to the query.
     *
     * @param query the query of the transferItemEntry search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/transfer-item-entries")
    public ResponseEntity<List<TransferItemEntryDTO>> searchTransferItemEntries(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TransferItemEntries for query {}", query);
        Page<TransferItemEntryDTO> page = transferItemEntryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

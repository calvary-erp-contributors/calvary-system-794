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

import io.github.calvary.repository.TransferItemRepository;
import io.github.calvary.service.TransferItemQueryService;
import io.github.calvary.service.TransferItemService;
import io.github.calvary.service.criteria.TransferItemCriteria;
import io.github.calvary.service.dto.TransferItemDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.TransferItem}.
 */
@RestController
@RequestMapping("/api")
public class TransferItemResource {

    private final Logger log = LoggerFactory.getLogger(TransferItemResource.class);

    private static final String ENTITY_NAME = "transferItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransferItemService transferItemService;

    private final TransferItemRepository transferItemRepository;

    private final TransferItemQueryService transferItemQueryService;

    public TransferItemResource(
        TransferItemService transferItemService,
        TransferItemRepository transferItemRepository,
        TransferItemQueryService transferItemQueryService
    ) {
        this.transferItemService = transferItemService;
        this.transferItemRepository = transferItemRepository;
        this.transferItemQueryService = transferItemQueryService;
    }

    /**
     * {@code POST  /transfer-items} : Create a new transferItem.
     *
     * @param transferItemDTO the transferItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transferItemDTO, or with status {@code 400 (Bad Request)} if the transferItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transfer-items")
    public ResponseEntity<TransferItemDTO> createTransferItem(@Valid @RequestBody TransferItemDTO transferItemDTO)
        throws URISyntaxException {
        log.debug("REST request to save TransferItem : {}", transferItemDTO);
        if (transferItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new transferItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransferItemDTO result = transferItemService.save(transferItemDTO);
        return ResponseEntity
            .created(new URI("/api/transfer-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transfer-items/:id} : Updates an existing transferItem.
     *
     * @param id the id of the transferItemDTO to save.
     * @param transferItemDTO the transferItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transferItemDTO,
     * or with status {@code 400 (Bad Request)} if the transferItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transferItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transfer-items/{id}")
    public ResponseEntity<TransferItemDTO> updateTransferItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransferItemDTO transferItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TransferItem : {}, {}", id, transferItemDTO);
        if (transferItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transferItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transferItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransferItemDTO result = transferItemService.update(transferItemDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transferItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /transfer-items/:id} : Partial updates given fields of an existing transferItem, field will ignore if it is null
     *
     * @param id the id of the transferItemDTO to save.
     * @param transferItemDTO the transferItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transferItemDTO,
     * or with status {@code 400 (Bad Request)} if the transferItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transferItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transferItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transfer-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransferItemDTO> partialUpdateTransferItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransferItemDTO transferItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TransferItem partially : {}, {}", id, transferItemDTO);
        if (transferItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transferItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transferItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransferItemDTO> result = transferItemService.partialUpdate(transferItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transferItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transfer-items} : get all the transferItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transferItems in body.
     */
    @GetMapping("/transfer-items")
    public ResponseEntity<List<TransferItemDTO>> getAllTransferItems(
        TransferItemCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TransferItems by criteria: {}", criteria);
        Page<TransferItemDTO> page = transferItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transfer-items/count} : count all the transferItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/transfer-items/count")
    public ResponseEntity<Long> countTransferItems(TransferItemCriteria criteria) {
        log.debug("REST request to count TransferItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(transferItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /transfer-items/:id} : get the "id" transferItem.
     *
     * @param id the id of the transferItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transferItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transfer-items/{id}")
    public ResponseEntity<TransferItemDTO> getTransferItem(@PathVariable Long id) {
        log.debug("REST request to get TransferItem : {}", id);
        Optional<TransferItemDTO> transferItemDTO = transferItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transferItemDTO);
    }

    /**
     * {@code DELETE  /transfer-items/:id} : delete the "id" transferItem.
     *
     * @param id the id of the transferItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transfer-items/{id}")
    public ResponseEntity<Void> deleteTransferItem(@PathVariable Long id) {
        log.debug("REST request to delete TransferItem : {}", id);
        transferItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/transfer-items?query=:query} : search for the transferItem corresponding
     * to the query.
     *
     * @param query the query of the transferItem search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/transfer-items")
    public ResponseEntity<List<TransferItemDTO>> searchTransferItems(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TransferItems for query {}", query);
        Page<TransferItemDTO> page = transferItemService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

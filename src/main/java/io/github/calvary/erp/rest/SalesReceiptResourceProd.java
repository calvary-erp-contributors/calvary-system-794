package io.github.calvary.erp.rest;

import io.github.calvary.erp.internal.InternalSalesReceiptService;
import io.github.calvary.repository.SalesReceiptRepository;
import io.github.calvary.service.SalesReceiptQueryService;
import io.github.calvary.service.SalesReceiptService;
import io.github.calvary.service.criteria.SalesReceiptCriteria;
import io.github.calvary.service.dto.SalesReceiptDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.SalesReceipt}.
 */
@RestController
@RequestMapping("/api/app")
public class SalesReceiptResourceProd {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptResourceProd.class);

    private static final String ENTITY_NAME = "salesReceipt";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InternalSalesReceiptService salesReceiptService;

    private final SalesReceiptRepository salesReceiptRepository;

    private final SalesReceiptQueryService salesReceiptQueryService;

    public SalesReceiptResourceProd(
        InternalSalesReceiptService salesReceiptService,
        SalesReceiptRepository salesReceiptRepository,
        SalesReceiptQueryService salesReceiptQueryService
    ) {
        this.salesReceiptService = salesReceiptService;
        this.salesReceiptRepository = salesReceiptRepository;
        this.salesReceiptQueryService = salesReceiptQueryService;
    }

    /**
     * {@code POST  /sales-receipts} : Create a new salesReceipt.
     *
     * @param salesReceiptDTO the salesReceiptDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new salesReceiptDTO, or with status {@code 400 (Bad Request)} if the salesReceipt has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sales-receipts")
    public ResponseEntity<SalesReceiptDTO> createSalesReceipt(@Valid @RequestBody SalesReceiptDTO salesReceiptDTO)
        throws URISyntaxException {
        log.debug("REST request to save SalesReceipt : {}", salesReceiptDTO);
        if (salesReceiptDTO.getId() != null) {
            throw new BadRequestAlertException("A new salesReceipt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SalesReceiptDTO result = salesReceiptService.save(salesReceiptDTO);
        return ResponseEntity
            .created(new URI("/api/sales-receipts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sales-receipts/:id} : Updates an existing salesReceipt.
     *
     * @param id the id of the salesReceiptDTO to save.
     * @param salesReceiptDTO the salesReceiptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesReceiptDTO,
     * or with status {@code 400 (Bad Request)} if the salesReceiptDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the salesReceiptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sales-receipts/{id}")
    public ResponseEntity<SalesReceiptDTO> updateSalesReceipt(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SalesReceiptDTO salesReceiptDTO
    ) throws URISyntaxException {
        log.debug("REST request to update SalesReceipt : {}, {}", id, salesReceiptDTO);
        if (salesReceiptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesReceiptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesReceiptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SalesReceiptDTO result = salesReceiptService.update(salesReceiptDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesReceiptDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sales-receipts/:id} : Partial updates given fields of an existing salesReceipt, field will ignore if it is null
     *
     * @param id the id of the salesReceiptDTO to save.
     * @param salesReceiptDTO the salesReceiptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesReceiptDTO,
     * or with status {@code 400 (Bad Request)} if the salesReceiptDTO is not valid,
     * or with status {@code 404 (Not Found)} if the salesReceiptDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the salesReceiptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sales-receipts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SalesReceiptDTO> partialUpdateSalesReceipt(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SalesReceiptDTO salesReceiptDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SalesReceipt partially : {}, {}", id, salesReceiptDTO);
        if (salesReceiptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesReceiptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesReceiptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SalesReceiptDTO> result = salesReceiptService.partialUpdate(salesReceiptDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesReceiptDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sales-receipts} : get all the salesReceipts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of salesReceipts in body.
     */
    @GetMapping("/sales-receipts")
    public ResponseEntity<List<SalesReceiptDTO>> getAllSalesReceipts(
        SalesReceiptCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get SalesReceipts by criteria: {}", criteria);
        Page<SalesReceiptDTO> page = salesReceiptQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sales-receipts/count} : count all the salesReceipts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/sales-receipts/count")
    public ResponseEntity<Long> countSalesReceipts(SalesReceiptCriteria criteria) {
        log.debug("REST request to count SalesReceipts by criteria: {}", criteria);
        return ResponseEntity.ok().body(salesReceiptQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sales-receipts/:id} : get the "id" salesReceipt.
     *
     * @param id the id of the salesReceiptDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the salesReceiptDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sales-receipts/{id}")
    public ResponseEntity<SalesReceiptDTO> getSalesReceipt(@PathVariable Long id) {
        log.debug("REST request to get SalesReceipt : {}", id);
        Optional<SalesReceiptDTO> salesReceiptDTO = salesReceiptService.findOne(id);
        return ResponseUtil.wrapOrNotFound(salesReceiptDTO);
    }

    /**
     * {@code DELETE  /sales-receipts/:id} : delete the "id" salesReceipt.
     *
     * @param id the id of the salesReceiptDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sales-receipts/{id}")
    public ResponseEntity<Void> deleteSalesReceipt(@PathVariable Long id) {
        log.debug("REST request to delete SalesReceipt : {}", id);
        salesReceiptService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/sales-receipts?query=:query} : search for the salesReceipt corresponding
     * to the query.
     *
     * @param query the query of the salesReceipt search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/sales-receipts")
    public ResponseEntity<List<SalesReceiptDTO>> searchSalesReceipts(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of SalesReceipts for query {}", query);
        Page<SalesReceiptDTO> page = salesReceiptService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

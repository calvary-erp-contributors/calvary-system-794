package io.github.calvary.erp.rest;

import io.github.calvary.repository.SalesReceiptEmailPersonaRepository;
import io.github.calvary.service.SalesReceiptEmailPersonaQueryService;
import io.github.calvary.service.SalesReceiptEmailPersonaService;
import io.github.calvary.service.criteria.SalesReceiptEmailPersonaCriteria;
import io.github.calvary.service.dto.SalesReceiptEmailPersonaDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.SalesReceiptEmailPersona}.
 */
@RestController
@RequestMapping("/api/app")
public class SalesReceiptEmailPersonaResourceProd {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptEmailPersonaResourceProd.class);

    private static final String ENTITY_NAME = "salesReceiptEmailPersona";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalesReceiptEmailPersonaService salesReceiptEmailPersonaService;

    private final SalesReceiptEmailPersonaRepository salesReceiptEmailPersonaRepository;

    private final SalesReceiptEmailPersonaQueryService salesReceiptEmailPersonaQueryService;

    public SalesReceiptEmailPersonaResourceProd(
        SalesReceiptEmailPersonaService salesReceiptEmailPersonaService,
        SalesReceiptEmailPersonaRepository salesReceiptEmailPersonaRepository,
        SalesReceiptEmailPersonaQueryService salesReceiptEmailPersonaQueryService
    ) {
        this.salesReceiptEmailPersonaService = salesReceiptEmailPersonaService;
        this.salesReceiptEmailPersonaRepository = salesReceiptEmailPersonaRepository;
        this.salesReceiptEmailPersonaQueryService = salesReceiptEmailPersonaQueryService;
    }

    /**
     * {@code POST  /sales-receipt-email-personas} : Create a new salesReceiptEmailPersona.
     *
     * @param salesReceiptEmailPersonaDTO the salesReceiptEmailPersonaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new salesReceiptEmailPersonaDTO, or with status {@code 400 (Bad Request)} if the salesReceiptEmailPersona has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sales-receipt-email-personas")
    public ResponseEntity<SalesReceiptEmailPersonaDTO> createSalesReceiptEmailPersona(
        @Valid @RequestBody SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO
    ) throws URISyntaxException {
        log.debug("REST request to save SalesReceiptEmailPersona : {}", salesReceiptEmailPersonaDTO);
        if (salesReceiptEmailPersonaDTO.getId() != null) {
            throw new BadRequestAlertException("A new salesReceiptEmailPersona cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SalesReceiptEmailPersonaDTO result = salesReceiptEmailPersonaService.save(salesReceiptEmailPersonaDTO);
        return ResponseEntity
            .created(new URI("/api/sales-receipt-email-personas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sales-receipt-email-personas/:id} : Updates an existing salesReceiptEmailPersona.
     *
     * @param id the id of the salesReceiptEmailPersonaDTO to save.
     * @param salesReceiptEmailPersonaDTO the salesReceiptEmailPersonaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesReceiptEmailPersonaDTO,
     * or with status {@code 400 (Bad Request)} if the salesReceiptEmailPersonaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the salesReceiptEmailPersonaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sales-receipt-email-personas/{id}")
    public ResponseEntity<SalesReceiptEmailPersonaDTO> updateSalesReceiptEmailPersona(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO
    ) throws URISyntaxException {
        log.debug("REST request to update SalesReceiptEmailPersona : {}, {}", id, salesReceiptEmailPersonaDTO);
        if (salesReceiptEmailPersonaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesReceiptEmailPersonaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesReceiptEmailPersonaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SalesReceiptEmailPersonaDTO result = salesReceiptEmailPersonaService.update(salesReceiptEmailPersonaDTO);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesReceiptEmailPersonaDTO.getId().toString())
            )
            .body(result);
    }

    /**
     * {@code PATCH  /sales-receipt-email-personas/:id} : Partial updates given fields of an existing salesReceiptEmailPersona, field will ignore if it is null
     *
     * @param id the id of the salesReceiptEmailPersonaDTO to save.
     * @param salesReceiptEmailPersonaDTO the salesReceiptEmailPersonaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesReceiptEmailPersonaDTO,
     * or with status {@code 400 (Bad Request)} if the salesReceiptEmailPersonaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the salesReceiptEmailPersonaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the salesReceiptEmailPersonaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sales-receipt-email-personas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SalesReceiptEmailPersonaDTO> partialUpdateSalesReceiptEmailPersona(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SalesReceiptEmailPersona partially : {}, {}", id, salesReceiptEmailPersonaDTO);
        if (salesReceiptEmailPersonaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesReceiptEmailPersonaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesReceiptEmailPersonaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SalesReceiptEmailPersonaDTO> result = salesReceiptEmailPersonaService.partialUpdate(salesReceiptEmailPersonaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesReceiptEmailPersonaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sales-receipt-email-personas} : get all the salesReceiptEmailPersonas.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of salesReceiptEmailPersonas in body.
     */
    @GetMapping("/sales-receipt-email-personas")
    public ResponseEntity<List<SalesReceiptEmailPersonaDTO>> getAllSalesReceiptEmailPersonas(
        SalesReceiptEmailPersonaCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get SalesReceiptEmailPersonas by criteria: {}", criteria);
        Page<SalesReceiptEmailPersonaDTO> page = salesReceiptEmailPersonaQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sales-receipt-email-personas/count} : count all the salesReceiptEmailPersonas.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/sales-receipt-email-personas/count")
    public ResponseEntity<Long> countSalesReceiptEmailPersonas(SalesReceiptEmailPersonaCriteria criteria) {
        log.debug("REST request to count SalesReceiptEmailPersonas by criteria: {}", criteria);
        return ResponseEntity.ok().body(salesReceiptEmailPersonaQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sales-receipt-email-personas/:id} : get the "id" salesReceiptEmailPersona.
     *
     * @param id the id of the salesReceiptEmailPersonaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the salesReceiptEmailPersonaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sales-receipt-email-personas/{id}")
    public ResponseEntity<SalesReceiptEmailPersonaDTO> getSalesReceiptEmailPersona(@PathVariable Long id) {
        log.debug("REST request to get SalesReceiptEmailPersona : {}", id);
        Optional<SalesReceiptEmailPersonaDTO> salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(salesReceiptEmailPersonaDTO);
    }

    /**
     * {@code DELETE  /sales-receipt-email-personas/:id} : delete the "id" salesReceiptEmailPersona.
     *
     * @param id the id of the salesReceiptEmailPersonaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sales-receipt-email-personas/{id}")
    public ResponseEntity<Void> deleteSalesReceiptEmailPersona(@PathVariable Long id) {
        log.debug("REST request to delete SalesReceiptEmailPersona : {}", id);
        salesReceiptEmailPersonaService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/sales-receipt-email-personas?query=:query} : search for the salesReceiptEmailPersona corresponding
     * to the query.
     *
     * @param query the query of the salesReceiptEmailPersona search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/sales-receipt-email-personas")
    public ResponseEntity<List<SalesReceiptEmailPersonaDTO>> searchSalesReceiptEmailPersonas(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of SalesReceiptEmailPersonas for query {}", query);
        Page<SalesReceiptEmailPersonaDTO> page = salesReceiptEmailPersonaService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

package io.github.calvary.erp.rest;

import io.github.calvary.repository.TransactionItemRepository;
import io.github.calvary.service.TransactionItemQueryService;
import io.github.calvary.service.TransactionItemService;
import io.github.calvary.service.criteria.TransactionItemCriteria;
import io.github.calvary.service.dto.TransactionItemDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.TransactionItem}.
 */
@RestController
@RequestMapping("/api/app")
public class TransactionItemResourceProd {

    private final Logger log = LoggerFactory.getLogger(TransactionItemResourceProd.class);

    private static final String ENTITY_NAME = "transactionItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionItemService transactionItemService;

    private final TransactionItemRepository transactionItemRepository;

    private final TransactionItemQueryService transactionItemQueryService;

    public TransactionItemResourceProd(
        TransactionItemService transactionItemService,
        TransactionItemRepository transactionItemRepository,
        TransactionItemQueryService transactionItemQueryService
    ) {
        this.transactionItemService = transactionItemService;
        this.transactionItemRepository = transactionItemRepository;
        this.transactionItemQueryService = transactionItemQueryService;
    }

    /**
     * {@code POST  /transaction-items} : Create a new transactionItem.
     *
     * @param transactionItemDTO the transactionItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionItemDTO, or with status {@code 400 (Bad Request)} if the transactionItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transaction-items")
    public ResponseEntity<TransactionItemDTO> createTransactionItem(@Valid @RequestBody TransactionItemDTO transactionItemDTO)
        throws URISyntaxException {
        log.debug("REST request to save TransactionItem : {}", transactionItemDTO);
        if (transactionItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new transactionItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransactionItemDTO result = transactionItemService.save(transactionItemDTO);
        return ResponseEntity
            .created(new URI("/api/transaction-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transaction-items/:id} : Updates an existing transactionItem.
     *
     * @param id the id of the transactionItemDTO to save.
     * @param transactionItemDTO the transactionItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionItemDTO,
     * or with status {@code 400 (Bad Request)} if the transactionItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transaction-items/{id}")
    public ResponseEntity<TransactionItemDTO> updateTransactionItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransactionItemDTO transactionItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TransactionItem : {}, {}", id, transactionItemDTO);
        if (transactionItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransactionItemDTO result = transactionItemService.update(transactionItemDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transactionItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /transaction-items/:id} : Partial updates given fields of an existing transactionItem, field will ignore if it is null
     *
     * @param id the id of the transactionItemDTO to save.
     * @param transactionItemDTO the transactionItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionItemDTO,
     * or with status {@code 400 (Bad Request)} if the transactionItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transactionItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transactionItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transaction-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionItemDTO> partialUpdateTransactionItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransactionItemDTO transactionItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TransactionItem partially : {}, {}", id, transactionItemDTO);
        if (transactionItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionItemDTO> result = transactionItemService.partialUpdate(transactionItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transactionItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transaction-items} : get all the transactionItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactionItems in body.
     */
    @GetMapping("/transaction-items")
    public ResponseEntity<List<TransactionItemDTO>> getAllTransactionItems(
        TransactionItemCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TransactionItems by criteria: {}", criteria);
        Page<TransactionItemDTO> page = transactionItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transaction-items/count} : count all the transactionItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/transaction-items/count")
    public ResponseEntity<Long> countTransactionItems(TransactionItemCriteria criteria) {
        log.debug("REST request to count TransactionItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(transactionItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /transaction-items/:id} : get the "id" transactionItem.
     *
     * @param id the id of the transactionItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transaction-items/{id}")
    public ResponseEntity<TransactionItemDTO> getTransactionItem(@PathVariable Long id) {
        log.debug("REST request to get TransactionItem : {}", id);
        Optional<TransactionItemDTO> transactionItemDTO = transactionItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionItemDTO);
    }

    /**
     * {@code DELETE  /transaction-items/:id} : delete the "id" transactionItem.
     *
     * @param id the id of the transactionItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transaction-items/{id}")
    public ResponseEntity<Void> deleteTransactionItem(@PathVariable Long id) {
        log.debug("REST request to delete TransactionItem : {}", id);
        transactionItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/transaction-items?query=:query} : search for the transactionItem corresponding
     * to the query.
     *
     * @param query the query of the transactionItem search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/transaction-items")
    public ResponseEntity<List<TransactionItemDTO>> searchTransactionItems(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TransactionItems for query {}", query);
        Page<TransactionItemDTO> page = transactionItemService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

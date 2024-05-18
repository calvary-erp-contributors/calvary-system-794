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

import io.github.calvary.erp.internal.InternalAccountBalanceReportItemService;
import io.github.calvary.erp.repository.InternalAccountBalanceReportItemRepository;
import io.github.calvary.repository.AccountBalanceReportItemRepository;
import io.github.calvary.service.AccountBalanceReportItemQueryService;
import io.github.calvary.service.AccountBalanceReportItemService;
import io.github.calvary.service.criteria.AccountBalanceReportItemCriteria;
import io.github.calvary.service.dto.AccountBalanceReportItemDTO;
import io.github.calvary.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * REST controller for managing {@link io.github.calvary.domain.AccountBalanceReportItem}.
 */
@RestController
@RequestMapping("/api/app")
public class AccountBalanceReportItemResourceProd {

    private final Logger log = LoggerFactory.getLogger(AccountBalanceReportItemResourceProd.class);

    private static final String ENTITY_NAME = "accountBalanceReportItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InternalAccountBalanceReportItemService accountBalanceReportItemService;

    private final InternalAccountBalanceReportItemRepository accountBalanceReportItemRepository;

    private final AccountBalanceReportItemQueryService accountBalanceReportItemQueryService;

    public AccountBalanceReportItemResourceProd(
        InternalAccountBalanceReportItemService accountBalanceReportItemService,
        InternalAccountBalanceReportItemRepository accountBalanceReportItemRepository,
        AccountBalanceReportItemQueryService accountBalanceReportItemQueryService
    ) {
        this.accountBalanceReportItemService = accountBalanceReportItemService;
        this.accountBalanceReportItemRepository = accountBalanceReportItemRepository;
        this.accountBalanceReportItemQueryService = accountBalanceReportItemQueryService;
    }

    /**
     * {@code POST  /account-balance-report-items} : Create a new accountBalanceReportItem.
     *
     * @param accountBalanceReportItemDTO the accountBalanceReportItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new accountBalanceReportItemDTO, or with status {@code 400 (Bad Request)} if the accountBalanceReportItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/account-balance-report-items")
    public ResponseEntity<AccountBalanceReportItemDTO> createAccountBalanceReportItem(
        @RequestBody AccountBalanceReportItemDTO accountBalanceReportItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to save AccountBalanceReportItem : {}", accountBalanceReportItemDTO);
        if (accountBalanceReportItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new accountBalanceReportItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AccountBalanceReportItemDTO result = accountBalanceReportItemService.save(accountBalanceReportItemDTO);
        return ResponseEntity
            .created(new URI("/api/account-balance-report-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /account-balance-report-items/:id} : Updates an existing accountBalanceReportItem.
     *
     * @param id the id of the accountBalanceReportItemDTO to save.
     * @param accountBalanceReportItemDTO the accountBalanceReportItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountBalanceReportItemDTO,
     * or with status {@code 400 (Bad Request)} if the accountBalanceReportItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the accountBalanceReportItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/account-balance-report-items/{id}")
    public ResponseEntity<AccountBalanceReportItemDTO> updateAccountBalanceReportItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AccountBalanceReportItemDTO accountBalanceReportItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update AccountBalanceReportItem : {}, {}", id, accountBalanceReportItemDTO);
        if (accountBalanceReportItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountBalanceReportItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountBalanceReportItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AccountBalanceReportItemDTO result = accountBalanceReportItemService.update(accountBalanceReportItemDTO);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, accountBalanceReportItemDTO.getId().toString())
            )
            .body(result);
    }

    /**
     * {@code PATCH  /account-balance-report-items/:id} : Partial updates given fields of an existing accountBalanceReportItem, field will ignore if it is null
     *
     * @param id the id of the accountBalanceReportItemDTO to save.
     * @param accountBalanceReportItemDTO the accountBalanceReportItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountBalanceReportItemDTO,
     * or with status {@code 400 (Bad Request)} if the accountBalanceReportItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the accountBalanceReportItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the accountBalanceReportItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/account-balance-report-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AccountBalanceReportItemDTO> partialUpdateAccountBalanceReportItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AccountBalanceReportItemDTO accountBalanceReportItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AccountBalanceReportItem partially : {}, {}", id, accountBalanceReportItemDTO);
        if (accountBalanceReportItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountBalanceReportItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountBalanceReportItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AccountBalanceReportItemDTO> result = accountBalanceReportItemService.partialUpdate(accountBalanceReportItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, accountBalanceReportItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /account-balance-report-items} : get all the accountBalanceReportItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of accountBalanceReportItems in body.
     */
    @GetMapping("/account-balance-report-items")
    public ResponseEntity<List<AccountBalanceReportItemDTO>> getAllAccountBalanceReportItems(
        AccountBalanceReportItemCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get AccountBalanceReportItems by criteria: {}", criteria);
        Page<AccountBalanceReportItemDTO> page = accountBalanceReportItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /account-balance-report-items} : get all the accountBalanceReportItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of accountBalanceReportItems in body.
     */
    @GetMapping("/account-balance-report-items/report")
    public ResponseEntity<List<AccountBalanceReportItemDTO>> getReportBalanceItems(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        Page<AccountBalanceReportItemDTO> page = accountBalanceReportItemService.listAccountBalanceItems(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /account-balance-report-items/count} : count all the accountBalanceReportItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/account-balance-report-items/count")
    public ResponseEntity<Long> countAccountBalanceReportItems(AccountBalanceReportItemCriteria criteria) {
        log.debug("REST request to count AccountBalanceReportItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(accountBalanceReportItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /account-balance-report-items/:id} : get the "id" accountBalanceReportItem.
     *
     * @param id the id of the accountBalanceReportItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accountBalanceReportItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/account-balance-report-items/{id}")
    public ResponseEntity<AccountBalanceReportItemDTO> getAccountBalanceReportItem(@PathVariable Long id) {
        log.debug("REST request to get AccountBalanceReportItem : {}", id);
        Optional<AccountBalanceReportItemDTO> accountBalanceReportItemDTO = accountBalanceReportItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(accountBalanceReportItemDTO);
    }

    /**
     * {@code DELETE  /account-balance-report-items/:id} : delete the "id" accountBalanceReportItem.
     *
     * @param id the id of the accountBalanceReportItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/account-balance-report-items/{id}")
    public ResponseEntity<Void> deleteAccountBalanceReportItem(@PathVariable Long id) {
        log.debug("REST request to delete AccountBalanceReportItem : {}", id);
        accountBalanceReportItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/account-balance-report-items?query=:query} : search for the accountBalanceReportItem corresponding
     * to the query.
     *
     * @param query the query of the accountBalanceReportItem search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/account-balance-report-items")
    public ResponseEntity<List<AccountBalanceReportItemDTO>> searchAccountBalanceReportItems(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of AccountBalanceReportItems for query {}", query);
        Page<AccountBalanceReportItemDTO> page = accountBalanceReportItemService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

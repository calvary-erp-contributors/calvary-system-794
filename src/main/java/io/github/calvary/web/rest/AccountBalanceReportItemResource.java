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
 * REST controller for managing {@link io.github.calvary.domain.AccountBalanceReportItem}.
 */
@RestController
@RequestMapping("/api")
public class AccountBalanceReportItemResource {

    private final Logger log = LoggerFactory.getLogger(AccountBalanceReportItemResource.class);

    private final AccountBalanceReportItemService accountBalanceReportItemService;

    private final AccountBalanceReportItemRepository accountBalanceReportItemRepository;

    private final AccountBalanceReportItemQueryService accountBalanceReportItemQueryService;

    public AccountBalanceReportItemResource(
        AccountBalanceReportItemService accountBalanceReportItemService,
        AccountBalanceReportItemRepository accountBalanceReportItemRepository,
        AccountBalanceReportItemQueryService accountBalanceReportItemQueryService
    ) {
        this.accountBalanceReportItemService = accountBalanceReportItemService;
        this.accountBalanceReportItemRepository = accountBalanceReportItemRepository;
        this.accountBalanceReportItemQueryService = accountBalanceReportItemQueryService;
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

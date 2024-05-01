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

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.SalesReceiptTitle;
import io.github.calvary.repository.SalesReceiptTitleRepository;
import io.github.calvary.repository.search.SalesReceiptTitleSearchRepository;
import io.github.calvary.service.criteria.SalesReceiptTitleCriteria;
import io.github.calvary.service.dto.SalesReceiptTitleDTO;
import io.github.calvary.service.mapper.SalesReceiptTitleMapper;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SalesReceiptTitleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SalesReceiptTitleResourceIT {

    private static final String DEFAULT_RECEIPT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_RECEIPT_TITLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sales-receipt-titles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/sales-receipt-titles";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SalesReceiptTitleRepository salesReceiptTitleRepository;

    @Autowired
    private SalesReceiptTitleMapper salesReceiptTitleMapper;

    @Autowired
    private SalesReceiptTitleSearchRepository salesReceiptTitleSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalesReceiptTitleMockMvc;

    private SalesReceiptTitle salesReceiptTitle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesReceiptTitle createEntity(EntityManager em) {
        SalesReceiptTitle salesReceiptTitle = new SalesReceiptTitle().receiptTitle(DEFAULT_RECEIPT_TITLE);
        return salesReceiptTitle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesReceiptTitle createUpdatedEntity(EntityManager em) {
        SalesReceiptTitle salesReceiptTitle = new SalesReceiptTitle().receiptTitle(UPDATED_RECEIPT_TITLE);
        return salesReceiptTitle;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        salesReceiptTitleSearchRepository.deleteAll();
        assertThat(salesReceiptTitleSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        salesReceiptTitle = createEntity(em);
    }

    @Test
    @Transactional
    void createSalesReceiptTitle() throws Exception {
        int databaseSizeBeforeCreate = salesReceiptTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        // Create the SalesReceiptTitle
        SalesReceiptTitleDTO salesReceiptTitleDTO = salesReceiptTitleMapper.toDto(salesReceiptTitle);
        restSalesReceiptTitleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptTitleDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SalesReceiptTitle in the database
        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SalesReceiptTitle testSalesReceiptTitle = salesReceiptTitleList.get(salesReceiptTitleList.size() - 1);
        assertThat(testSalesReceiptTitle.getReceiptTitle()).isEqualTo(DEFAULT_RECEIPT_TITLE);
    }

    @Test
    @Transactional
    void createSalesReceiptTitleWithExistingId() throws Exception {
        // Create the SalesReceiptTitle with an existing ID
        salesReceiptTitle.setId(1L);
        SalesReceiptTitleDTO salesReceiptTitleDTO = salesReceiptTitleMapper.toDto(salesReceiptTitle);

        int databaseSizeBeforeCreate = salesReceiptTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalesReceiptTitleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptTitleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptTitle in the database
        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReceiptTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = salesReceiptTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        // set the field null
        salesReceiptTitle.setReceiptTitle(null);

        // Create the SalesReceiptTitle, which fails.
        SalesReceiptTitleDTO salesReceiptTitleDTO = salesReceiptTitleMapper.toDto(salesReceiptTitle);

        restSalesReceiptTitleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptTitleDTO))
            )
            .andExpect(status().isBadRequest());

        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSalesReceiptTitles() throws Exception {
        // Initialize the database
        salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);

        // Get all the salesReceiptTitleList
        restSalesReceiptTitleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceiptTitle.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiptTitle").value(hasItem(DEFAULT_RECEIPT_TITLE)));
    }

    @Test
    @Transactional
    void getSalesReceiptTitle() throws Exception {
        // Initialize the database
        salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);

        // Get the salesReceiptTitle
        restSalesReceiptTitleMockMvc
            .perform(get(ENTITY_API_URL_ID, salesReceiptTitle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(salesReceiptTitle.getId().intValue()))
            .andExpect(jsonPath("$.receiptTitle").value(DEFAULT_RECEIPT_TITLE));
    }

    @Test
    @Transactional
    void getSalesReceiptTitlesByIdFiltering() throws Exception {
        // Initialize the database
        salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);

        Long id = salesReceiptTitle.getId();

        defaultSalesReceiptTitleShouldBeFound("id.equals=" + id);
        defaultSalesReceiptTitleShouldNotBeFound("id.notEquals=" + id);

        defaultSalesReceiptTitleShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSalesReceiptTitleShouldNotBeFound("id.greaterThan=" + id);

        defaultSalesReceiptTitleShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSalesReceiptTitleShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalesReceiptTitlesByReceiptTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);

        // Get all the salesReceiptTitleList where receiptTitle equals to DEFAULT_RECEIPT_TITLE
        defaultSalesReceiptTitleShouldBeFound("receiptTitle.equals=" + DEFAULT_RECEIPT_TITLE);

        // Get all the salesReceiptTitleList where receiptTitle equals to UPDATED_RECEIPT_TITLE
        defaultSalesReceiptTitleShouldNotBeFound("receiptTitle.equals=" + UPDATED_RECEIPT_TITLE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptTitlesByReceiptTitleIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);

        // Get all the salesReceiptTitleList where receiptTitle in DEFAULT_RECEIPT_TITLE or UPDATED_RECEIPT_TITLE
        defaultSalesReceiptTitleShouldBeFound("receiptTitle.in=" + DEFAULT_RECEIPT_TITLE + "," + UPDATED_RECEIPT_TITLE);

        // Get all the salesReceiptTitleList where receiptTitle equals to UPDATED_RECEIPT_TITLE
        defaultSalesReceiptTitleShouldNotBeFound("receiptTitle.in=" + UPDATED_RECEIPT_TITLE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptTitlesByReceiptTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);

        // Get all the salesReceiptTitleList where receiptTitle is not null
        defaultSalesReceiptTitleShouldBeFound("receiptTitle.specified=true");

        // Get all the salesReceiptTitleList where receiptTitle is null
        defaultSalesReceiptTitleShouldNotBeFound("receiptTitle.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptTitlesByReceiptTitleContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);

        // Get all the salesReceiptTitleList where receiptTitle contains DEFAULT_RECEIPT_TITLE
        defaultSalesReceiptTitleShouldBeFound("receiptTitle.contains=" + DEFAULT_RECEIPT_TITLE);

        // Get all the salesReceiptTitleList where receiptTitle contains UPDATED_RECEIPT_TITLE
        defaultSalesReceiptTitleShouldNotBeFound("receiptTitle.contains=" + UPDATED_RECEIPT_TITLE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptTitlesByReceiptTitleNotContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);

        // Get all the salesReceiptTitleList where receiptTitle does not contain DEFAULT_RECEIPT_TITLE
        defaultSalesReceiptTitleShouldNotBeFound("receiptTitle.doesNotContain=" + DEFAULT_RECEIPT_TITLE);

        // Get all the salesReceiptTitleList where receiptTitle does not contain UPDATED_RECEIPT_TITLE
        defaultSalesReceiptTitleShouldBeFound("receiptTitle.doesNotContain=" + UPDATED_RECEIPT_TITLE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSalesReceiptTitleShouldBeFound(String filter) throws Exception {
        restSalesReceiptTitleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceiptTitle.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiptTitle").value(hasItem(DEFAULT_RECEIPT_TITLE)));

        // Check, that the count call also returns 1
        restSalesReceiptTitleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSalesReceiptTitleShouldNotBeFound(String filter) throws Exception {
        restSalesReceiptTitleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSalesReceiptTitleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSalesReceiptTitle() throws Exception {
        // Get the salesReceiptTitle
        restSalesReceiptTitleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSalesReceiptTitle() throws Exception {
        // Initialize the database
        salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);

        int databaseSizeBeforeUpdate = salesReceiptTitleRepository.findAll().size();
        salesReceiptTitleSearchRepository.save(salesReceiptTitle);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());

        // Update the salesReceiptTitle
        SalesReceiptTitle updatedSalesReceiptTitle = salesReceiptTitleRepository.findById(salesReceiptTitle.getId()).get();
        // Disconnect from session so that the updates on updatedSalesReceiptTitle are not directly saved in db
        em.detach(updatedSalesReceiptTitle);
        updatedSalesReceiptTitle.receiptTitle(UPDATED_RECEIPT_TITLE);
        SalesReceiptTitleDTO salesReceiptTitleDTO = salesReceiptTitleMapper.toDto(updatedSalesReceiptTitle);

        restSalesReceiptTitleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesReceiptTitleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptTitleDTO))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceiptTitle in the database
        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeUpdate);
        SalesReceiptTitle testSalesReceiptTitle = salesReceiptTitleList.get(salesReceiptTitleList.size() - 1);
        assertThat(testSalesReceiptTitle.getReceiptTitle()).isEqualTo(UPDATED_RECEIPT_TITLE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SalesReceiptTitle> salesReceiptTitleSearchList = IterableUtils.toList(salesReceiptTitleSearchRepository.findAll());
                SalesReceiptTitle testSalesReceiptTitleSearch = salesReceiptTitleSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSalesReceiptTitleSearch.getReceiptTitle()).isEqualTo(UPDATED_RECEIPT_TITLE);
            });
    }

    @Test
    @Transactional
    void putNonExistingSalesReceiptTitle() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        salesReceiptTitle.setId(count.incrementAndGet());

        // Create the SalesReceiptTitle
        SalesReceiptTitleDTO salesReceiptTitleDTO = salesReceiptTitleMapper.toDto(salesReceiptTitle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesReceiptTitleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesReceiptTitleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptTitleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptTitle in the database
        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSalesReceiptTitle() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        salesReceiptTitle.setId(count.incrementAndGet());

        // Create the SalesReceiptTitle
        SalesReceiptTitleDTO salesReceiptTitleDTO = salesReceiptTitleMapper.toDto(salesReceiptTitle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptTitleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptTitleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptTitle in the database
        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSalesReceiptTitle() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        salesReceiptTitle.setId(count.incrementAndGet());

        // Create the SalesReceiptTitle
        SalesReceiptTitleDTO salesReceiptTitleDTO = salesReceiptTitleMapper.toDto(salesReceiptTitle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptTitleMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesReceiptTitleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesReceiptTitle in the database
        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSalesReceiptTitleWithPatch() throws Exception {
        // Initialize the database
        salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);

        int databaseSizeBeforeUpdate = salesReceiptTitleRepository.findAll().size();

        // Update the salesReceiptTitle using partial update
        SalesReceiptTitle partialUpdatedSalesReceiptTitle = new SalesReceiptTitle();
        partialUpdatedSalesReceiptTitle.setId(salesReceiptTitle.getId());

        partialUpdatedSalesReceiptTitle.receiptTitle(UPDATED_RECEIPT_TITLE);

        restSalesReceiptTitleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesReceiptTitle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesReceiptTitle))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceiptTitle in the database
        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeUpdate);
        SalesReceiptTitle testSalesReceiptTitle = salesReceiptTitleList.get(salesReceiptTitleList.size() - 1);
        assertThat(testSalesReceiptTitle.getReceiptTitle()).isEqualTo(UPDATED_RECEIPT_TITLE);
    }

    @Test
    @Transactional
    void fullUpdateSalesReceiptTitleWithPatch() throws Exception {
        // Initialize the database
        salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);

        int databaseSizeBeforeUpdate = salesReceiptTitleRepository.findAll().size();

        // Update the salesReceiptTitle using partial update
        SalesReceiptTitle partialUpdatedSalesReceiptTitle = new SalesReceiptTitle();
        partialUpdatedSalesReceiptTitle.setId(salesReceiptTitle.getId());

        partialUpdatedSalesReceiptTitle.receiptTitle(UPDATED_RECEIPT_TITLE);

        restSalesReceiptTitleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesReceiptTitle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesReceiptTitle))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceiptTitle in the database
        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeUpdate);
        SalesReceiptTitle testSalesReceiptTitle = salesReceiptTitleList.get(salesReceiptTitleList.size() - 1);
        assertThat(testSalesReceiptTitle.getReceiptTitle()).isEqualTo(UPDATED_RECEIPT_TITLE);
    }

    @Test
    @Transactional
    void patchNonExistingSalesReceiptTitle() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        salesReceiptTitle.setId(count.incrementAndGet());

        // Create the SalesReceiptTitle
        SalesReceiptTitleDTO salesReceiptTitleDTO = salesReceiptTitleMapper.toDto(salesReceiptTitle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesReceiptTitleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salesReceiptTitleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptTitleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptTitle in the database
        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSalesReceiptTitle() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        salesReceiptTitle.setId(count.incrementAndGet());

        // Create the SalesReceiptTitle
        SalesReceiptTitleDTO salesReceiptTitleDTO = salesReceiptTitleMapper.toDto(salesReceiptTitle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptTitleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptTitleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptTitle in the database
        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSalesReceiptTitle() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        salesReceiptTitle.setId(count.incrementAndGet());

        // Create the SalesReceiptTitle
        SalesReceiptTitleDTO salesReceiptTitleDTO = salesReceiptTitleMapper.toDto(salesReceiptTitle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptTitleMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptTitleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesReceiptTitle in the database
        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSalesReceiptTitle() throws Exception {
        // Initialize the database
        salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);
        salesReceiptTitleRepository.save(salesReceiptTitle);
        salesReceiptTitleSearchRepository.save(salesReceiptTitle);

        int databaseSizeBeforeDelete = salesReceiptTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the salesReceiptTitle
        restSalesReceiptTitleMockMvc
            .perform(delete(ENTITY_API_URL_ID, salesReceiptTitle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SalesReceiptTitle> salesReceiptTitleList = salesReceiptTitleRepository.findAll();
        assertThat(salesReceiptTitleList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSalesReceiptTitle() throws Exception {
        // Initialize the database
        salesReceiptTitle = salesReceiptTitleRepository.saveAndFlush(salesReceiptTitle);
        salesReceiptTitleSearchRepository.save(salesReceiptTitle);

        // Search the salesReceiptTitle
        restSalesReceiptTitleMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + salesReceiptTitle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceiptTitle.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiptTitle").value(hasItem(DEFAULT_RECEIPT_TITLE)));
    }
}

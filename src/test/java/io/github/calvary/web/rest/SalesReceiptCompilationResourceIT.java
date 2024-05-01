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
import io.github.calvary.domain.ApplicationUser;
import io.github.calvary.domain.SalesReceiptCompilation;
import io.github.calvary.repository.SalesReceiptCompilationRepository;
import io.github.calvary.repository.search.SalesReceiptCompilationSearchRepository;
import io.github.calvary.service.SalesReceiptCompilationService;
import io.github.calvary.service.criteria.SalesReceiptCompilationCriteria;
import io.github.calvary.service.dto.SalesReceiptCompilationDTO;
import io.github.calvary.service.mapper.SalesReceiptCompilationMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
 * Integration tests for the {@link SalesReceiptCompilationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SalesReceiptCompilationResourceIT {

    private static final Instant DEFAULT_TIME_OF_COMPILATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME_OF_COMPILATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final UUID DEFAULT_COMPILATION_IDENTIFIER = UUID.randomUUID();
    private static final UUID UPDATED_COMPILATION_IDENTIFIER = UUID.randomUUID();

    private static final Integer DEFAULT_RECEIPTS_COMPILED = 1;
    private static final Integer UPDATED_RECEIPTS_COMPILED = 2;
    private static final Integer SMALLER_RECEIPTS_COMPILED = 1 - 1;

    private static final String ENTITY_API_URL = "/api/sales-receipt-compilations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/sales-receipt-compilations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SalesReceiptCompilationRepository salesReceiptCompilationRepository;

    @Mock
    private SalesReceiptCompilationRepository salesReceiptCompilationRepositoryMock;

    @Autowired
    private SalesReceiptCompilationMapper salesReceiptCompilationMapper;

    @Mock
    private SalesReceiptCompilationService salesReceiptCompilationServiceMock;

    @Autowired
    private SalesReceiptCompilationSearchRepository salesReceiptCompilationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalesReceiptCompilationMockMvc;

    private SalesReceiptCompilation salesReceiptCompilation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesReceiptCompilation createEntity(EntityManager em) {
        SalesReceiptCompilation salesReceiptCompilation = new SalesReceiptCompilation()
            .timeOfCompilation(DEFAULT_TIME_OF_COMPILATION)
            .compilationIdentifier(DEFAULT_COMPILATION_IDENTIFIER)
            .receiptsCompiled(DEFAULT_RECEIPTS_COMPILED);
        return salesReceiptCompilation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesReceiptCompilation createUpdatedEntity(EntityManager em) {
        SalesReceiptCompilation salesReceiptCompilation = new SalesReceiptCompilation()
            .timeOfCompilation(UPDATED_TIME_OF_COMPILATION)
            .compilationIdentifier(UPDATED_COMPILATION_IDENTIFIER)
            .receiptsCompiled(UPDATED_RECEIPTS_COMPILED);
        return salesReceiptCompilation;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        salesReceiptCompilationSearchRepository.deleteAll();
        assertThat(salesReceiptCompilationSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        salesReceiptCompilation = createEntity(em);
    }

    @Test
    @Transactional
    void createSalesReceiptCompilation() throws Exception {
        int databaseSizeBeforeCreate = salesReceiptCompilationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        // Create the SalesReceiptCompilation
        SalesReceiptCompilationDTO salesReceiptCompilationDTO = salesReceiptCompilationMapper.toDto(salesReceiptCompilation);
        restSalesReceiptCompilationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptCompilationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SalesReceiptCompilation in the database
        List<SalesReceiptCompilation> salesReceiptCompilationList = salesReceiptCompilationRepository.findAll();
        assertThat(salesReceiptCompilationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SalesReceiptCompilation testSalesReceiptCompilation = salesReceiptCompilationList.get(salesReceiptCompilationList.size() - 1);
        assertThat(testSalesReceiptCompilation.getTimeOfCompilation()).isEqualTo(DEFAULT_TIME_OF_COMPILATION);
        assertThat(testSalesReceiptCompilation.getCompilationIdentifier()).isEqualTo(DEFAULT_COMPILATION_IDENTIFIER);
        assertThat(testSalesReceiptCompilation.getReceiptsCompiled()).isEqualTo(DEFAULT_RECEIPTS_COMPILED);
    }

    @Test
    @Transactional
    void createSalesReceiptCompilationWithExistingId() throws Exception {
        // Create the SalesReceiptCompilation with an existing ID
        salesReceiptCompilation.setId(1L);
        SalesReceiptCompilationDTO salesReceiptCompilationDTO = salesReceiptCompilationMapper.toDto(salesReceiptCompilation);

        int databaseSizeBeforeCreate = salesReceiptCompilationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalesReceiptCompilationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptCompilationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptCompilation in the database
        List<SalesReceiptCompilation> salesReceiptCompilationList = salesReceiptCompilationRepository.findAll();
        assertThat(salesReceiptCompilationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilations() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList
        restSalesReceiptCompilationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceiptCompilation.getId().intValue())))
            .andExpect(jsonPath("$.[*].timeOfCompilation").value(hasItem(DEFAULT_TIME_OF_COMPILATION.toString())))
            .andExpect(jsonPath("$.[*].compilationIdentifier").value(hasItem(DEFAULT_COMPILATION_IDENTIFIER.toString())))
            .andExpect(jsonPath("$.[*].receiptsCompiled").value(hasItem(DEFAULT_RECEIPTS_COMPILED)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesReceiptCompilationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(salesReceiptCompilationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSalesReceiptCompilationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(salesReceiptCompilationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesReceiptCompilationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(salesReceiptCompilationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSalesReceiptCompilationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(salesReceiptCompilationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSalesReceiptCompilation() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get the salesReceiptCompilation
        restSalesReceiptCompilationMockMvc
            .perform(get(ENTITY_API_URL_ID, salesReceiptCompilation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(salesReceiptCompilation.getId().intValue()))
            .andExpect(jsonPath("$.timeOfCompilation").value(DEFAULT_TIME_OF_COMPILATION.toString()))
            .andExpect(jsonPath("$.compilationIdentifier").value(DEFAULT_COMPILATION_IDENTIFIER.toString()))
            .andExpect(jsonPath("$.receiptsCompiled").value(DEFAULT_RECEIPTS_COMPILED));
    }

    @Test
    @Transactional
    void getSalesReceiptCompilationsByIdFiltering() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        Long id = salesReceiptCompilation.getId();

        defaultSalesReceiptCompilationShouldBeFound("id.equals=" + id);
        defaultSalesReceiptCompilationShouldNotBeFound("id.notEquals=" + id);

        defaultSalesReceiptCompilationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSalesReceiptCompilationShouldNotBeFound("id.greaterThan=" + id);

        defaultSalesReceiptCompilationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSalesReceiptCompilationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByTimeOfCompilationIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where timeOfCompilation equals to DEFAULT_TIME_OF_COMPILATION
        defaultSalesReceiptCompilationShouldBeFound("timeOfCompilation.equals=" + DEFAULT_TIME_OF_COMPILATION);

        // Get all the salesReceiptCompilationList where timeOfCompilation equals to UPDATED_TIME_OF_COMPILATION
        defaultSalesReceiptCompilationShouldNotBeFound("timeOfCompilation.equals=" + UPDATED_TIME_OF_COMPILATION);
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByTimeOfCompilationIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where timeOfCompilation in DEFAULT_TIME_OF_COMPILATION or UPDATED_TIME_OF_COMPILATION
        defaultSalesReceiptCompilationShouldBeFound(
            "timeOfCompilation.in=" + DEFAULT_TIME_OF_COMPILATION + "," + UPDATED_TIME_OF_COMPILATION
        );

        // Get all the salesReceiptCompilationList where timeOfCompilation equals to UPDATED_TIME_OF_COMPILATION
        defaultSalesReceiptCompilationShouldNotBeFound("timeOfCompilation.in=" + UPDATED_TIME_OF_COMPILATION);
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByTimeOfCompilationIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where timeOfCompilation is not null
        defaultSalesReceiptCompilationShouldBeFound("timeOfCompilation.specified=true");

        // Get all the salesReceiptCompilationList where timeOfCompilation is null
        defaultSalesReceiptCompilationShouldNotBeFound("timeOfCompilation.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByCompilationIdentifierIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where compilationIdentifier equals to DEFAULT_COMPILATION_IDENTIFIER
        defaultSalesReceiptCompilationShouldBeFound("compilationIdentifier.equals=" + DEFAULT_COMPILATION_IDENTIFIER);

        // Get all the salesReceiptCompilationList where compilationIdentifier equals to UPDATED_COMPILATION_IDENTIFIER
        defaultSalesReceiptCompilationShouldNotBeFound("compilationIdentifier.equals=" + UPDATED_COMPILATION_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByCompilationIdentifierIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where compilationIdentifier in DEFAULT_COMPILATION_IDENTIFIER or UPDATED_COMPILATION_IDENTIFIER
        defaultSalesReceiptCompilationShouldBeFound(
            "compilationIdentifier.in=" + DEFAULT_COMPILATION_IDENTIFIER + "," + UPDATED_COMPILATION_IDENTIFIER
        );

        // Get all the salesReceiptCompilationList where compilationIdentifier equals to UPDATED_COMPILATION_IDENTIFIER
        defaultSalesReceiptCompilationShouldNotBeFound("compilationIdentifier.in=" + UPDATED_COMPILATION_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByCompilationIdentifierIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where compilationIdentifier is not null
        defaultSalesReceiptCompilationShouldBeFound("compilationIdentifier.specified=true");

        // Get all the salesReceiptCompilationList where compilationIdentifier is null
        defaultSalesReceiptCompilationShouldNotBeFound("compilationIdentifier.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByReceiptsCompiledIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where receiptsCompiled equals to DEFAULT_RECEIPTS_COMPILED
        defaultSalesReceiptCompilationShouldBeFound("receiptsCompiled.equals=" + DEFAULT_RECEIPTS_COMPILED);

        // Get all the salesReceiptCompilationList where receiptsCompiled equals to UPDATED_RECEIPTS_COMPILED
        defaultSalesReceiptCompilationShouldNotBeFound("receiptsCompiled.equals=" + UPDATED_RECEIPTS_COMPILED);
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByReceiptsCompiledIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where receiptsCompiled in DEFAULT_RECEIPTS_COMPILED or UPDATED_RECEIPTS_COMPILED
        defaultSalesReceiptCompilationShouldBeFound("receiptsCompiled.in=" + DEFAULT_RECEIPTS_COMPILED + "," + UPDATED_RECEIPTS_COMPILED);

        // Get all the salesReceiptCompilationList where receiptsCompiled equals to UPDATED_RECEIPTS_COMPILED
        defaultSalesReceiptCompilationShouldNotBeFound("receiptsCompiled.in=" + UPDATED_RECEIPTS_COMPILED);
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByReceiptsCompiledIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where receiptsCompiled is not null
        defaultSalesReceiptCompilationShouldBeFound("receiptsCompiled.specified=true");

        // Get all the salesReceiptCompilationList where receiptsCompiled is null
        defaultSalesReceiptCompilationShouldNotBeFound("receiptsCompiled.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByReceiptsCompiledIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where receiptsCompiled is greater than or equal to DEFAULT_RECEIPTS_COMPILED
        defaultSalesReceiptCompilationShouldBeFound("receiptsCompiled.greaterThanOrEqual=" + DEFAULT_RECEIPTS_COMPILED);

        // Get all the salesReceiptCompilationList where receiptsCompiled is greater than or equal to UPDATED_RECEIPTS_COMPILED
        defaultSalesReceiptCompilationShouldNotBeFound("receiptsCompiled.greaterThanOrEqual=" + UPDATED_RECEIPTS_COMPILED);
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByReceiptsCompiledIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where receiptsCompiled is less than or equal to DEFAULT_RECEIPTS_COMPILED
        defaultSalesReceiptCompilationShouldBeFound("receiptsCompiled.lessThanOrEqual=" + DEFAULT_RECEIPTS_COMPILED);

        // Get all the salesReceiptCompilationList where receiptsCompiled is less than or equal to SMALLER_RECEIPTS_COMPILED
        defaultSalesReceiptCompilationShouldNotBeFound("receiptsCompiled.lessThanOrEqual=" + SMALLER_RECEIPTS_COMPILED);
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByReceiptsCompiledIsLessThanSomething() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where receiptsCompiled is less than DEFAULT_RECEIPTS_COMPILED
        defaultSalesReceiptCompilationShouldNotBeFound("receiptsCompiled.lessThan=" + DEFAULT_RECEIPTS_COMPILED);

        // Get all the salesReceiptCompilationList where receiptsCompiled is less than UPDATED_RECEIPTS_COMPILED
        defaultSalesReceiptCompilationShouldBeFound("receiptsCompiled.lessThan=" + UPDATED_RECEIPTS_COMPILED);
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByReceiptsCompiledIsGreaterThanSomething() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        // Get all the salesReceiptCompilationList where receiptsCompiled is greater than DEFAULT_RECEIPTS_COMPILED
        defaultSalesReceiptCompilationShouldNotBeFound("receiptsCompiled.greaterThan=" + DEFAULT_RECEIPTS_COMPILED);

        // Get all the salesReceiptCompilationList where receiptsCompiled is greater than SMALLER_RECEIPTS_COMPILED
        defaultSalesReceiptCompilationShouldBeFound("receiptsCompiled.greaterThan=" + SMALLER_RECEIPTS_COMPILED);
    }

    @Test
    @Transactional
    void getAllSalesReceiptCompilationsByCompiledByIsEqualToSomething() throws Exception {
        ApplicationUser compiledBy;
        if (TestUtil.findAll(em, ApplicationUser.class).isEmpty()) {
            salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);
            compiledBy = ApplicationUserResourceIT.createEntity(em);
        } else {
            compiledBy = TestUtil.findAll(em, ApplicationUser.class).get(0);
        }
        em.persist(compiledBy);
        em.flush();
        salesReceiptCompilation.setCompiledBy(compiledBy);
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);
        Long compiledById = compiledBy.getId();

        // Get all the salesReceiptCompilationList where compiledBy equals to compiledById
        defaultSalesReceiptCompilationShouldBeFound("compiledById.equals=" + compiledById);

        // Get all the salesReceiptCompilationList where compiledBy equals to (compiledById + 1)
        defaultSalesReceiptCompilationShouldNotBeFound("compiledById.equals=" + (compiledById + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSalesReceiptCompilationShouldBeFound(String filter) throws Exception {
        restSalesReceiptCompilationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceiptCompilation.getId().intValue())))
            .andExpect(jsonPath("$.[*].timeOfCompilation").value(hasItem(DEFAULT_TIME_OF_COMPILATION.toString())))
            .andExpect(jsonPath("$.[*].compilationIdentifier").value(hasItem(DEFAULT_COMPILATION_IDENTIFIER.toString())))
            .andExpect(jsonPath("$.[*].receiptsCompiled").value(hasItem(DEFAULT_RECEIPTS_COMPILED)));

        // Check, that the count call also returns 1
        restSalesReceiptCompilationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSalesReceiptCompilationShouldNotBeFound(String filter) throws Exception {
        restSalesReceiptCompilationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSalesReceiptCompilationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSalesReceiptCompilation() throws Exception {
        // Get the salesReceiptCompilation
        restSalesReceiptCompilationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSalesReceiptCompilation() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        int databaseSizeBeforeUpdate = salesReceiptCompilationRepository.findAll().size();
        salesReceiptCompilationSearchRepository.save(salesReceiptCompilation);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());

        // Update the salesReceiptCompilation
        SalesReceiptCompilation updatedSalesReceiptCompilation = salesReceiptCompilationRepository
            .findById(salesReceiptCompilation.getId())
            .get();
        // Disconnect from session so that the updates on updatedSalesReceiptCompilation are not directly saved in db
        em.detach(updatedSalesReceiptCompilation);
        updatedSalesReceiptCompilation
            .timeOfCompilation(UPDATED_TIME_OF_COMPILATION)
            .compilationIdentifier(UPDATED_COMPILATION_IDENTIFIER)
            .receiptsCompiled(UPDATED_RECEIPTS_COMPILED);
        SalesReceiptCompilationDTO salesReceiptCompilationDTO = salesReceiptCompilationMapper.toDto(updatedSalesReceiptCompilation);

        restSalesReceiptCompilationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesReceiptCompilationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptCompilationDTO))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceiptCompilation in the database
        List<SalesReceiptCompilation> salesReceiptCompilationList = salesReceiptCompilationRepository.findAll();
        assertThat(salesReceiptCompilationList).hasSize(databaseSizeBeforeUpdate);
        SalesReceiptCompilation testSalesReceiptCompilation = salesReceiptCompilationList.get(salesReceiptCompilationList.size() - 1);
        assertThat(testSalesReceiptCompilation.getTimeOfCompilation()).isEqualTo(UPDATED_TIME_OF_COMPILATION);
        assertThat(testSalesReceiptCompilation.getCompilationIdentifier()).isEqualTo(UPDATED_COMPILATION_IDENTIFIER);
        assertThat(testSalesReceiptCompilation.getReceiptsCompiled()).isEqualTo(UPDATED_RECEIPTS_COMPILED);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SalesReceiptCompilation> salesReceiptCompilationSearchList = IterableUtils.toList(
                    salesReceiptCompilationSearchRepository.findAll()
                );
                SalesReceiptCompilation testSalesReceiptCompilationSearch = salesReceiptCompilationSearchList.get(
                    searchDatabaseSizeAfter - 1
                );
                assertThat(testSalesReceiptCompilationSearch.getTimeOfCompilation()).isEqualTo(UPDATED_TIME_OF_COMPILATION);
                assertThat(testSalesReceiptCompilationSearch.getCompilationIdentifier()).isEqualTo(UPDATED_COMPILATION_IDENTIFIER);
                assertThat(testSalesReceiptCompilationSearch.getReceiptsCompiled()).isEqualTo(UPDATED_RECEIPTS_COMPILED);
            });
    }

    @Test
    @Transactional
    void putNonExistingSalesReceiptCompilation() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptCompilationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        salesReceiptCompilation.setId(count.incrementAndGet());

        // Create the SalesReceiptCompilation
        SalesReceiptCompilationDTO salesReceiptCompilationDTO = salesReceiptCompilationMapper.toDto(salesReceiptCompilation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesReceiptCompilationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesReceiptCompilationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptCompilationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptCompilation in the database
        List<SalesReceiptCompilation> salesReceiptCompilationList = salesReceiptCompilationRepository.findAll();
        assertThat(salesReceiptCompilationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSalesReceiptCompilation() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptCompilationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        salesReceiptCompilation.setId(count.incrementAndGet());

        // Create the SalesReceiptCompilation
        SalesReceiptCompilationDTO salesReceiptCompilationDTO = salesReceiptCompilationMapper.toDto(salesReceiptCompilation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptCompilationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptCompilationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptCompilation in the database
        List<SalesReceiptCompilation> salesReceiptCompilationList = salesReceiptCompilationRepository.findAll();
        assertThat(salesReceiptCompilationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSalesReceiptCompilation() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptCompilationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        salesReceiptCompilation.setId(count.incrementAndGet());

        // Create the SalesReceiptCompilation
        SalesReceiptCompilationDTO salesReceiptCompilationDTO = salesReceiptCompilationMapper.toDto(salesReceiptCompilation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptCompilationMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptCompilationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesReceiptCompilation in the database
        List<SalesReceiptCompilation> salesReceiptCompilationList = salesReceiptCompilationRepository.findAll();
        assertThat(salesReceiptCompilationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSalesReceiptCompilationWithPatch() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        int databaseSizeBeforeUpdate = salesReceiptCompilationRepository.findAll().size();

        // Update the salesReceiptCompilation using partial update
        SalesReceiptCompilation partialUpdatedSalesReceiptCompilation = new SalesReceiptCompilation();
        partialUpdatedSalesReceiptCompilation.setId(salesReceiptCompilation.getId());

        partialUpdatedSalesReceiptCompilation
            .timeOfCompilation(UPDATED_TIME_OF_COMPILATION)
            .compilationIdentifier(UPDATED_COMPILATION_IDENTIFIER);

        restSalesReceiptCompilationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesReceiptCompilation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesReceiptCompilation))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceiptCompilation in the database
        List<SalesReceiptCompilation> salesReceiptCompilationList = salesReceiptCompilationRepository.findAll();
        assertThat(salesReceiptCompilationList).hasSize(databaseSizeBeforeUpdate);
        SalesReceiptCompilation testSalesReceiptCompilation = salesReceiptCompilationList.get(salesReceiptCompilationList.size() - 1);
        assertThat(testSalesReceiptCompilation.getTimeOfCompilation()).isEqualTo(UPDATED_TIME_OF_COMPILATION);
        assertThat(testSalesReceiptCompilation.getCompilationIdentifier()).isEqualTo(UPDATED_COMPILATION_IDENTIFIER);
        assertThat(testSalesReceiptCompilation.getReceiptsCompiled()).isEqualTo(DEFAULT_RECEIPTS_COMPILED);
    }

    @Test
    @Transactional
    void fullUpdateSalesReceiptCompilationWithPatch() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);

        int databaseSizeBeforeUpdate = salesReceiptCompilationRepository.findAll().size();

        // Update the salesReceiptCompilation using partial update
        SalesReceiptCompilation partialUpdatedSalesReceiptCompilation = new SalesReceiptCompilation();
        partialUpdatedSalesReceiptCompilation.setId(salesReceiptCompilation.getId());

        partialUpdatedSalesReceiptCompilation
            .timeOfCompilation(UPDATED_TIME_OF_COMPILATION)
            .compilationIdentifier(UPDATED_COMPILATION_IDENTIFIER)
            .receiptsCompiled(UPDATED_RECEIPTS_COMPILED);

        restSalesReceiptCompilationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesReceiptCompilation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesReceiptCompilation))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceiptCompilation in the database
        List<SalesReceiptCompilation> salesReceiptCompilationList = salesReceiptCompilationRepository.findAll();
        assertThat(salesReceiptCompilationList).hasSize(databaseSizeBeforeUpdate);
        SalesReceiptCompilation testSalesReceiptCompilation = salesReceiptCompilationList.get(salesReceiptCompilationList.size() - 1);
        assertThat(testSalesReceiptCompilation.getTimeOfCompilation()).isEqualTo(UPDATED_TIME_OF_COMPILATION);
        assertThat(testSalesReceiptCompilation.getCompilationIdentifier()).isEqualTo(UPDATED_COMPILATION_IDENTIFIER);
        assertThat(testSalesReceiptCompilation.getReceiptsCompiled()).isEqualTo(UPDATED_RECEIPTS_COMPILED);
    }

    @Test
    @Transactional
    void patchNonExistingSalesReceiptCompilation() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptCompilationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        salesReceiptCompilation.setId(count.incrementAndGet());

        // Create the SalesReceiptCompilation
        SalesReceiptCompilationDTO salesReceiptCompilationDTO = salesReceiptCompilationMapper.toDto(salesReceiptCompilation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesReceiptCompilationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salesReceiptCompilationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptCompilationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptCompilation in the database
        List<SalesReceiptCompilation> salesReceiptCompilationList = salesReceiptCompilationRepository.findAll();
        assertThat(salesReceiptCompilationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSalesReceiptCompilation() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptCompilationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        salesReceiptCompilation.setId(count.incrementAndGet());

        // Create the SalesReceiptCompilation
        SalesReceiptCompilationDTO salesReceiptCompilationDTO = salesReceiptCompilationMapper.toDto(salesReceiptCompilation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptCompilationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptCompilationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptCompilation in the database
        List<SalesReceiptCompilation> salesReceiptCompilationList = salesReceiptCompilationRepository.findAll();
        assertThat(salesReceiptCompilationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSalesReceiptCompilation() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptCompilationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        salesReceiptCompilation.setId(count.incrementAndGet());

        // Create the SalesReceiptCompilation
        SalesReceiptCompilationDTO salesReceiptCompilationDTO = salesReceiptCompilationMapper.toDto(salesReceiptCompilation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptCompilationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptCompilationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesReceiptCompilation in the database
        List<SalesReceiptCompilation> salesReceiptCompilationList = salesReceiptCompilationRepository.findAll();
        assertThat(salesReceiptCompilationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSalesReceiptCompilation() throws Exception {
        // Initialize the database
        salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);
        salesReceiptCompilationRepository.save(salesReceiptCompilation);
        salesReceiptCompilationSearchRepository.save(salesReceiptCompilation);

        int databaseSizeBeforeDelete = salesReceiptCompilationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the salesReceiptCompilation
        restSalesReceiptCompilationMockMvc
            .perform(delete(ENTITY_API_URL_ID, salesReceiptCompilation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SalesReceiptCompilation> salesReceiptCompilationList = salesReceiptCompilationRepository.findAll();
        assertThat(salesReceiptCompilationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptCompilationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSalesReceiptCompilation() throws Exception {
        // Initialize the database
        salesReceiptCompilation = salesReceiptCompilationRepository.saveAndFlush(salesReceiptCompilation);
        salesReceiptCompilationSearchRepository.save(salesReceiptCompilation);

        // Search the salesReceiptCompilation
        restSalesReceiptCompilationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + salesReceiptCompilation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceiptCompilation.getId().intValue())))
            .andExpect(jsonPath("$.[*].timeOfCompilation").value(hasItem(DEFAULT_TIME_OF_COMPILATION.toString())))
            .andExpect(jsonPath("$.[*].compilationIdentifier").value(hasItem(DEFAULT_COMPILATION_IDENTIFIER.toString())))
            .andExpect(jsonPath("$.[*].receiptsCompiled").value(hasItem(DEFAULT_RECEIPTS_COMPILED)));
    }
}

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

import static io.github.calvary.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.SalesReceipt;
import io.github.calvary.domain.TransactionItem;
import io.github.calvary.domain.TransactionItemEntry;
import io.github.calvary.repository.TransactionItemEntryRepository;
import io.github.calvary.repository.search.TransactionItemEntrySearchRepository;
import io.github.calvary.service.TransactionItemEntryService;
import io.github.calvary.service.criteria.TransactionItemEntryCriteria;
import io.github.calvary.service.dto.TransactionItemEntryDTO;
import io.github.calvary.service.mapper.TransactionItemEntryMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
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
 * Integration tests for the {@link TransactionItemEntryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TransactionItemEntryResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_ITEM_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_ITEM_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_ITEM_AMOUNT = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/transaction-item-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/transaction-item-entries";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionItemEntryRepository transactionItemEntryRepository;

    @Mock
    private TransactionItemEntryRepository transactionItemEntryRepositoryMock;

    @Autowired
    private TransactionItemEntryMapper transactionItemEntryMapper;

    @Mock
    private TransactionItemEntryService transactionItemEntryServiceMock;

    @Autowired
    private TransactionItemEntrySearchRepository transactionItemEntrySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionItemEntryMockMvc;

    private TransactionItemEntry transactionItemEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionItemEntry createEntity(EntityManager em) {
        TransactionItemEntry transactionItemEntry = new TransactionItemEntry()
            .description(DEFAULT_DESCRIPTION)
            .itemAmount(DEFAULT_ITEM_AMOUNT);
        return transactionItemEntry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionItemEntry createUpdatedEntity(EntityManager em) {
        TransactionItemEntry transactionItemEntry = new TransactionItemEntry()
            .description(UPDATED_DESCRIPTION)
            .itemAmount(UPDATED_ITEM_AMOUNT);
        return transactionItemEntry;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        transactionItemEntrySearchRepository.deleteAll();
        assertThat(transactionItemEntrySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        transactionItemEntry = createEntity(em);
    }

    @Test
    @Transactional
    void createTransactionItemEntry() throws Exception {
        int databaseSizeBeforeCreate = transactionItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        // Create the TransactionItemEntry
        TransactionItemEntryDTO transactionItemEntryDTO = transactionItemEntryMapper.toDto(transactionItemEntry);
        restTransactionItemEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemEntryDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TransactionItemEntry in the database
        List<TransactionItemEntry> transactionItemEntryList = transactionItemEntryRepository.findAll();
        assertThat(transactionItemEntryList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TransactionItemEntry testTransactionItemEntry = transactionItemEntryList.get(transactionItemEntryList.size() - 1);
        assertThat(testTransactionItemEntry.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTransactionItemEntry.getItemAmount()).isEqualByComparingTo(DEFAULT_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void createTransactionItemEntryWithExistingId() throws Exception {
        // Create the TransactionItemEntry with an existing ID
        transactionItemEntry.setId(1L);
        TransactionItemEntryDTO transactionItemEntryDTO = transactionItemEntryMapper.toDto(transactionItemEntry);

        int databaseSizeBeforeCreate = transactionItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionItemEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItemEntry in the database
        List<TransactionItemEntry> transactionItemEntryList = transactionItemEntryRepository.findAll();
        assertThat(transactionItemEntryList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTransactionItemEntries() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList
        restTransactionItemEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionItemEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].itemAmount").value(hasItem(sameNumber(DEFAULT_ITEM_AMOUNT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransactionItemEntriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(transactionItemEntryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransactionItemEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(transactionItemEntryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransactionItemEntriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(transactionItemEntryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransactionItemEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(transactionItemEntryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTransactionItemEntry() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get the transactionItemEntry
        restTransactionItemEntryMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionItemEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionItemEntry.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.itemAmount").value(sameNumber(DEFAULT_ITEM_AMOUNT)));
    }

    @Test
    @Transactional
    void getTransactionItemEntriesByIdFiltering() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        Long id = transactionItemEntry.getId();

        defaultTransactionItemEntryShouldBeFound("id.equals=" + id);
        defaultTransactionItemEntryShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionItemEntryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionItemEntryShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionItemEntryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionItemEntryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList where description equals to DEFAULT_DESCRIPTION
        defaultTransactionItemEntryShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the transactionItemEntryList where description equals to UPDATED_DESCRIPTION
        defaultTransactionItemEntryShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTransactionItemEntryShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the transactionItemEntryList where description equals to UPDATED_DESCRIPTION
        defaultTransactionItemEntryShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList where description is not null
        defaultTransactionItemEntryShouldBeFound("description.specified=true");

        // Get all the transactionItemEntryList where description is null
        defaultTransactionItemEntryShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList where description contains DEFAULT_DESCRIPTION
        defaultTransactionItemEntryShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the transactionItemEntryList where description contains UPDATED_DESCRIPTION
        defaultTransactionItemEntryShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList where description does not contain DEFAULT_DESCRIPTION
        defaultTransactionItemEntryShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the transactionItemEntryList where description does not contain UPDATED_DESCRIPTION
        defaultTransactionItemEntryShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByItemAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList where itemAmount equals to DEFAULT_ITEM_AMOUNT
        defaultTransactionItemEntryShouldBeFound("itemAmount.equals=" + DEFAULT_ITEM_AMOUNT);

        // Get all the transactionItemEntryList where itemAmount equals to UPDATED_ITEM_AMOUNT
        defaultTransactionItemEntryShouldNotBeFound("itemAmount.equals=" + UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByItemAmountIsInShouldWork() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList where itemAmount in DEFAULT_ITEM_AMOUNT or UPDATED_ITEM_AMOUNT
        defaultTransactionItemEntryShouldBeFound("itemAmount.in=" + DEFAULT_ITEM_AMOUNT + "," + UPDATED_ITEM_AMOUNT);

        // Get all the transactionItemEntryList where itemAmount equals to UPDATED_ITEM_AMOUNT
        defaultTransactionItemEntryShouldNotBeFound("itemAmount.in=" + UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByItemAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList where itemAmount is not null
        defaultTransactionItemEntryShouldBeFound("itemAmount.specified=true");

        // Get all the transactionItemEntryList where itemAmount is null
        defaultTransactionItemEntryShouldNotBeFound("itemAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByItemAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList where itemAmount is greater than or equal to DEFAULT_ITEM_AMOUNT
        defaultTransactionItemEntryShouldBeFound("itemAmount.greaterThanOrEqual=" + DEFAULT_ITEM_AMOUNT);

        // Get all the transactionItemEntryList where itemAmount is greater than or equal to UPDATED_ITEM_AMOUNT
        defaultTransactionItemEntryShouldNotBeFound("itemAmount.greaterThanOrEqual=" + UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByItemAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList where itemAmount is less than or equal to DEFAULT_ITEM_AMOUNT
        defaultTransactionItemEntryShouldBeFound("itemAmount.lessThanOrEqual=" + DEFAULT_ITEM_AMOUNT);

        // Get all the transactionItemEntryList where itemAmount is less than or equal to SMALLER_ITEM_AMOUNT
        defaultTransactionItemEntryShouldNotBeFound("itemAmount.lessThanOrEqual=" + SMALLER_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByItemAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList where itemAmount is less than DEFAULT_ITEM_AMOUNT
        defaultTransactionItemEntryShouldNotBeFound("itemAmount.lessThan=" + DEFAULT_ITEM_AMOUNT);

        // Get all the transactionItemEntryList where itemAmount is less than UPDATED_ITEM_AMOUNT
        defaultTransactionItemEntryShouldBeFound("itemAmount.lessThan=" + UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByItemAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        // Get all the transactionItemEntryList where itemAmount is greater than DEFAULT_ITEM_AMOUNT
        defaultTransactionItemEntryShouldNotBeFound("itemAmount.greaterThan=" + DEFAULT_ITEM_AMOUNT);

        // Get all the transactionItemEntryList where itemAmount is greater than SMALLER_ITEM_AMOUNT
        defaultTransactionItemEntryShouldBeFound("itemAmount.greaterThan=" + SMALLER_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesByTransactionItemIsEqualToSomething() throws Exception {
        TransactionItem transactionItem;
        if (TestUtil.findAll(em, TransactionItem.class).isEmpty()) {
            transactionItemEntryRepository.saveAndFlush(transactionItemEntry);
            transactionItem = TransactionItemResourceIT.createEntity(em);
        } else {
            transactionItem = TestUtil.findAll(em, TransactionItem.class).get(0);
        }
        em.persist(transactionItem);
        em.flush();
        transactionItemEntry.setTransactionItem(transactionItem);
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);
        Long transactionItemId = transactionItem.getId();

        // Get all the transactionItemEntryList where transactionItem equals to transactionItemId
        defaultTransactionItemEntryShouldBeFound("transactionItemId.equals=" + transactionItemId);

        // Get all the transactionItemEntryList where transactionItem equals to (transactionItemId + 1)
        defaultTransactionItemEntryShouldNotBeFound("transactionItemId.equals=" + (transactionItemId + 1));
    }

    @Test
    @Transactional
    void getAllTransactionItemEntriesBySalesReceiptIsEqualToSomething() throws Exception {
        SalesReceipt salesReceipt;
        if (TestUtil.findAll(em, SalesReceipt.class).isEmpty()) {
            transactionItemEntryRepository.saveAndFlush(transactionItemEntry);
            salesReceipt = SalesReceiptResourceIT.createEntity(em);
        } else {
            salesReceipt = TestUtil.findAll(em, SalesReceipt.class).get(0);
        }
        em.persist(salesReceipt);
        em.flush();
        transactionItemEntry.setSalesReceipt(salesReceipt);
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);
        Long salesReceiptId = salesReceipt.getId();

        // Get all the transactionItemEntryList where salesReceipt equals to salesReceiptId
        defaultTransactionItemEntryShouldBeFound("salesReceiptId.equals=" + salesReceiptId);

        // Get all the transactionItemEntryList where salesReceipt equals to (salesReceiptId + 1)
        defaultTransactionItemEntryShouldNotBeFound("salesReceiptId.equals=" + (salesReceiptId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionItemEntryShouldBeFound(String filter) throws Exception {
        restTransactionItemEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionItemEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].itemAmount").value(hasItem(sameNumber(DEFAULT_ITEM_AMOUNT))));

        // Check, that the count call also returns 1
        restTransactionItemEntryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionItemEntryShouldNotBeFound(String filter) throws Exception {
        restTransactionItemEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionItemEntryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransactionItemEntry() throws Exception {
        // Get the transactionItemEntry
        restTransactionItemEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransactionItemEntry() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        int databaseSizeBeforeUpdate = transactionItemEntryRepository.findAll().size();
        transactionItemEntrySearchRepository.save(transactionItemEntry);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());

        // Update the transactionItemEntry
        TransactionItemEntry updatedTransactionItemEntry = transactionItemEntryRepository.findById(transactionItemEntry.getId()).get();
        // Disconnect from session so that the updates on updatedTransactionItemEntry are not directly saved in db
        em.detach(updatedTransactionItemEntry);
        updatedTransactionItemEntry.description(UPDATED_DESCRIPTION).itemAmount(UPDATED_ITEM_AMOUNT);
        TransactionItemEntryDTO transactionItemEntryDTO = transactionItemEntryMapper.toDto(updatedTransactionItemEntry);

        restTransactionItemEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionItemEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemEntryDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransactionItemEntry in the database
        List<TransactionItemEntry> transactionItemEntryList = transactionItemEntryRepository.findAll();
        assertThat(transactionItemEntryList).hasSize(databaseSizeBeforeUpdate);
        TransactionItemEntry testTransactionItemEntry = transactionItemEntryList.get(transactionItemEntryList.size() - 1);
        assertThat(testTransactionItemEntry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTransactionItemEntry.getItemAmount()).isEqualByComparingTo(UPDATED_ITEM_AMOUNT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TransactionItemEntry> transactionItemEntrySearchList = IterableUtils.toList(
                    transactionItemEntrySearchRepository.findAll()
                );
                TransactionItemEntry testTransactionItemEntrySearch = transactionItemEntrySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTransactionItemEntrySearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testTransactionItemEntrySearch.getItemAmount()).isEqualByComparingTo(UPDATED_ITEM_AMOUNT);
            });
    }

    @Test
    @Transactional
    void putNonExistingTransactionItemEntry() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        transactionItemEntry.setId(count.incrementAndGet());

        // Create the TransactionItemEntry
        TransactionItemEntryDTO transactionItemEntryDTO = transactionItemEntryMapper.toDto(transactionItemEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionItemEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionItemEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItemEntry in the database
        List<TransactionItemEntry> transactionItemEntryList = transactionItemEntryRepository.findAll();
        assertThat(transactionItemEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactionItemEntry() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        transactionItemEntry.setId(count.incrementAndGet());

        // Create the TransactionItemEntry
        TransactionItemEntryDTO transactionItemEntryDTO = transactionItemEntryMapper.toDto(transactionItemEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionItemEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItemEntry in the database
        List<TransactionItemEntry> transactionItemEntryList = transactionItemEntryRepository.findAll();
        assertThat(transactionItemEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactionItemEntry() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        transactionItemEntry.setId(count.incrementAndGet());

        // Create the TransactionItemEntry
        TransactionItemEntryDTO transactionItemEntryDTO = transactionItemEntryMapper.toDto(transactionItemEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionItemEntryMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemEntryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionItemEntry in the database
        List<TransactionItemEntry> transactionItemEntryList = transactionItemEntryRepository.findAll();
        assertThat(transactionItemEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTransactionItemEntryWithPatch() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        int databaseSizeBeforeUpdate = transactionItemEntryRepository.findAll().size();

        // Update the transactionItemEntry using partial update
        TransactionItemEntry partialUpdatedTransactionItemEntry = new TransactionItemEntry();
        partialUpdatedTransactionItemEntry.setId(transactionItemEntry.getId());

        partialUpdatedTransactionItemEntry.description(UPDATED_DESCRIPTION);

        restTransactionItemEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionItemEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionItemEntry))
            )
            .andExpect(status().isOk());

        // Validate the TransactionItemEntry in the database
        List<TransactionItemEntry> transactionItemEntryList = transactionItemEntryRepository.findAll();
        assertThat(transactionItemEntryList).hasSize(databaseSizeBeforeUpdate);
        TransactionItemEntry testTransactionItemEntry = transactionItemEntryList.get(transactionItemEntryList.size() - 1);
        assertThat(testTransactionItemEntry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTransactionItemEntry.getItemAmount()).isEqualByComparingTo(DEFAULT_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void fullUpdateTransactionItemEntryWithPatch() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);

        int databaseSizeBeforeUpdate = transactionItemEntryRepository.findAll().size();

        // Update the transactionItemEntry using partial update
        TransactionItemEntry partialUpdatedTransactionItemEntry = new TransactionItemEntry();
        partialUpdatedTransactionItemEntry.setId(transactionItemEntry.getId());

        partialUpdatedTransactionItemEntry.description(UPDATED_DESCRIPTION).itemAmount(UPDATED_ITEM_AMOUNT);

        restTransactionItemEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionItemEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionItemEntry))
            )
            .andExpect(status().isOk());

        // Validate the TransactionItemEntry in the database
        List<TransactionItemEntry> transactionItemEntryList = transactionItemEntryRepository.findAll();
        assertThat(transactionItemEntryList).hasSize(databaseSizeBeforeUpdate);
        TransactionItemEntry testTransactionItemEntry = transactionItemEntryList.get(transactionItemEntryList.size() - 1);
        assertThat(testTransactionItemEntry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTransactionItemEntry.getItemAmount()).isEqualByComparingTo(UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingTransactionItemEntry() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        transactionItemEntry.setId(count.incrementAndGet());

        // Create the TransactionItemEntry
        TransactionItemEntryDTO transactionItemEntryDTO = transactionItemEntryMapper.toDto(transactionItemEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionItemEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionItemEntryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItemEntry in the database
        List<TransactionItemEntry> transactionItemEntryList = transactionItemEntryRepository.findAll();
        assertThat(transactionItemEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactionItemEntry() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        transactionItemEntry.setId(count.incrementAndGet());

        // Create the TransactionItemEntry
        TransactionItemEntryDTO transactionItemEntryDTO = transactionItemEntryMapper.toDto(transactionItemEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionItemEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItemEntry in the database
        List<TransactionItemEntry> transactionItemEntryList = transactionItemEntryRepository.findAll();
        assertThat(transactionItemEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactionItemEntry() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        transactionItemEntry.setId(count.incrementAndGet());

        // Create the TransactionItemEntry
        TransactionItemEntryDTO transactionItemEntryDTO = transactionItemEntryMapper.toDto(transactionItemEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionItemEntryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemEntryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionItemEntry in the database
        List<TransactionItemEntry> transactionItemEntryList = transactionItemEntryRepository.findAll();
        assertThat(transactionItemEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTransactionItemEntry() throws Exception {
        // Initialize the database
        transactionItemEntryRepository.saveAndFlush(transactionItemEntry);
        transactionItemEntryRepository.save(transactionItemEntry);
        transactionItemEntrySearchRepository.save(transactionItemEntry);

        int databaseSizeBeforeDelete = transactionItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transactionItemEntry
        restTransactionItemEntryMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionItemEntry.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransactionItemEntry> transactionItemEntryList = transactionItemEntryRepository.findAll();
        assertThat(transactionItemEntryList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTransactionItemEntry() throws Exception {
        // Initialize the database
        transactionItemEntry = transactionItemEntryRepository.saveAndFlush(transactionItemEntry);
        transactionItemEntrySearchRepository.save(transactionItemEntry);

        // Search the transactionItemEntry
        restTransactionItemEntryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transactionItemEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionItemEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].itemAmount").value(hasItem(sameNumber(DEFAULT_ITEM_AMOUNT))));
    }
}

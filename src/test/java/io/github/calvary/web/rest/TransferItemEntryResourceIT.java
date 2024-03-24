package io.github.calvary.web.rest;

import static io.github.calvary.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.SalesReceipt;
import io.github.calvary.domain.TransferItem;
import io.github.calvary.domain.TransferItemEntry;
import io.github.calvary.repository.TransferItemEntryRepository;
import io.github.calvary.repository.search.TransferItemEntrySearchRepository;
import io.github.calvary.service.TransferItemEntryService;
import io.github.calvary.service.criteria.TransferItemEntryCriteria;
import io.github.calvary.service.dto.TransferItemEntryDTO;
import io.github.calvary.service.mapper.TransferItemEntryMapper;
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
 * Integration tests for the {@link TransferItemEntryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TransferItemEntryResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_ITEM_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_ITEM_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_ITEM_AMOUNT = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/transfer-item-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/transfer-item-entries";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransferItemEntryRepository transferItemEntryRepository;

    @Mock
    private TransferItemEntryRepository transferItemEntryRepositoryMock;

    @Autowired
    private TransferItemEntryMapper transferItemEntryMapper;

    @Mock
    private TransferItemEntryService transferItemEntryServiceMock;

    @Autowired
    private TransferItemEntrySearchRepository transferItemEntrySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransferItemEntryMockMvc;

    private TransferItemEntry transferItemEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransferItemEntry createEntity(EntityManager em) {
        TransferItemEntry transferItemEntry = new TransferItemEntry().description(DEFAULT_DESCRIPTION).itemAmount(DEFAULT_ITEM_AMOUNT);
        // Add required entity
        SalesReceipt salesReceipt;
        if (TestUtil.findAll(em, SalesReceipt.class).isEmpty()) {
            salesReceipt = SalesReceiptResourceIT.createEntity(em);
            em.persist(salesReceipt);
            em.flush();
        } else {
            salesReceipt = TestUtil.findAll(em, SalesReceipt.class).get(0);
        }
        transferItemEntry.setSalesReceipt(salesReceipt);
        // Add required entity
        TransferItem transferItem;
        if (TestUtil.findAll(em, TransferItem.class).isEmpty()) {
            transferItem = TransferItemResourceIT.createEntity(em);
            em.persist(transferItem);
            em.flush();
        } else {
            transferItem = TestUtil.findAll(em, TransferItem.class).get(0);
        }
        transferItemEntry.setTransferItem(transferItem);
        return transferItemEntry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransferItemEntry createUpdatedEntity(EntityManager em) {
        TransferItemEntry transferItemEntry = new TransferItemEntry().description(UPDATED_DESCRIPTION).itemAmount(UPDATED_ITEM_AMOUNT);
        // Add required entity
        SalesReceipt salesReceipt;
        if (TestUtil.findAll(em, SalesReceipt.class).isEmpty()) {
            salesReceipt = SalesReceiptResourceIT.createUpdatedEntity(em);
            em.persist(salesReceipt);
            em.flush();
        } else {
            salesReceipt = TestUtil.findAll(em, SalesReceipt.class).get(0);
        }
        transferItemEntry.setSalesReceipt(salesReceipt);
        // Add required entity
        TransferItem transferItem;
        if (TestUtil.findAll(em, TransferItem.class).isEmpty()) {
            transferItem = TransferItemResourceIT.createUpdatedEntity(em);
            em.persist(transferItem);
            em.flush();
        } else {
            transferItem = TestUtil.findAll(em, TransferItem.class).get(0);
        }
        transferItemEntry.setTransferItem(transferItem);
        return transferItemEntry;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        transferItemEntrySearchRepository.deleteAll();
        assertThat(transferItemEntrySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        transferItemEntry = createEntity(em);
    }

    @Test
    @Transactional
    void createTransferItemEntry() throws Exception {
        int databaseSizeBeforeCreate = transferItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        // Create the TransferItemEntry
        TransferItemEntryDTO transferItemEntryDTO = transferItemEntryMapper.toDto(transferItemEntry);
        restTransferItemEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transferItemEntryDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TransferItemEntry in the database
        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TransferItemEntry testTransferItemEntry = transferItemEntryList.get(transferItemEntryList.size() - 1);
        assertThat(testTransferItemEntry.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTransferItemEntry.getItemAmount()).isEqualByComparingTo(DEFAULT_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void createTransferItemEntryWithExistingId() throws Exception {
        // Create the TransferItemEntry with an existing ID
        transferItemEntry.setId(1L);
        TransferItemEntryDTO transferItemEntryDTO = transferItemEntryMapper.toDto(transferItemEntry);

        int databaseSizeBeforeCreate = transferItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransferItemEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transferItemEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransferItemEntry in the database
        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkItemAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = transferItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        // set the field null
        transferItemEntry.setItemAmount(null);

        // Create the TransferItemEntry, which fails.
        TransferItemEntryDTO transferItemEntryDTO = transferItemEntryMapper.toDto(transferItemEntry);

        restTransferItemEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transferItemEntryDTO))
            )
            .andExpect(status().isBadRequest());

        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTransferItemEntries() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList
        restTransferItemEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transferItemEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].itemAmount").value(hasItem(sameNumber(DEFAULT_ITEM_AMOUNT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransferItemEntriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(transferItemEntryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransferItemEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(transferItemEntryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransferItemEntriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(transferItemEntryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransferItemEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(transferItemEntryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTransferItemEntry() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get the transferItemEntry
        restTransferItemEntryMockMvc
            .perform(get(ENTITY_API_URL_ID, transferItemEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transferItemEntry.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.itemAmount").value(sameNumber(DEFAULT_ITEM_AMOUNT)));
    }

    @Test
    @Transactional
    void getTransferItemEntriesByIdFiltering() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        Long id = transferItemEntry.getId();

        defaultTransferItemEntryShouldBeFound("id.equals=" + id);
        defaultTransferItemEntryShouldNotBeFound("id.notEquals=" + id);

        defaultTransferItemEntryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransferItemEntryShouldNotBeFound("id.greaterThan=" + id);

        defaultTransferItemEntryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransferItemEntryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList where description equals to DEFAULT_DESCRIPTION
        defaultTransferItemEntryShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the transferItemEntryList where description equals to UPDATED_DESCRIPTION
        defaultTransferItemEntryShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTransferItemEntryShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the transferItemEntryList where description equals to UPDATED_DESCRIPTION
        defaultTransferItemEntryShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList where description is not null
        defaultTransferItemEntryShouldBeFound("description.specified=true");

        // Get all the transferItemEntryList where description is null
        defaultTransferItemEntryShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList where description contains DEFAULT_DESCRIPTION
        defaultTransferItemEntryShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the transferItemEntryList where description contains UPDATED_DESCRIPTION
        defaultTransferItemEntryShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList where description does not contain DEFAULT_DESCRIPTION
        defaultTransferItemEntryShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the transferItemEntryList where description does not contain UPDATED_DESCRIPTION
        defaultTransferItemEntryShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByItemAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList where itemAmount equals to DEFAULT_ITEM_AMOUNT
        defaultTransferItemEntryShouldBeFound("itemAmount.equals=" + DEFAULT_ITEM_AMOUNT);

        // Get all the transferItemEntryList where itemAmount equals to UPDATED_ITEM_AMOUNT
        defaultTransferItemEntryShouldNotBeFound("itemAmount.equals=" + UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByItemAmountIsInShouldWork() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList where itemAmount in DEFAULT_ITEM_AMOUNT or UPDATED_ITEM_AMOUNT
        defaultTransferItemEntryShouldBeFound("itemAmount.in=" + DEFAULT_ITEM_AMOUNT + "," + UPDATED_ITEM_AMOUNT);

        // Get all the transferItemEntryList where itemAmount equals to UPDATED_ITEM_AMOUNT
        defaultTransferItemEntryShouldNotBeFound("itemAmount.in=" + UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByItemAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList where itemAmount is not null
        defaultTransferItemEntryShouldBeFound("itemAmount.specified=true");

        // Get all the transferItemEntryList where itemAmount is null
        defaultTransferItemEntryShouldNotBeFound("itemAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByItemAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList where itemAmount is greater than or equal to DEFAULT_ITEM_AMOUNT
        defaultTransferItemEntryShouldBeFound("itemAmount.greaterThanOrEqual=" + DEFAULT_ITEM_AMOUNT);

        // Get all the transferItemEntryList where itemAmount is greater than or equal to UPDATED_ITEM_AMOUNT
        defaultTransferItemEntryShouldNotBeFound("itemAmount.greaterThanOrEqual=" + UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByItemAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList where itemAmount is less than or equal to DEFAULT_ITEM_AMOUNT
        defaultTransferItemEntryShouldBeFound("itemAmount.lessThanOrEqual=" + DEFAULT_ITEM_AMOUNT);

        // Get all the transferItemEntryList where itemAmount is less than or equal to SMALLER_ITEM_AMOUNT
        defaultTransferItemEntryShouldNotBeFound("itemAmount.lessThanOrEqual=" + SMALLER_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByItemAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList where itemAmount is less than DEFAULT_ITEM_AMOUNT
        defaultTransferItemEntryShouldNotBeFound("itemAmount.lessThan=" + DEFAULT_ITEM_AMOUNT);

        // Get all the transferItemEntryList where itemAmount is less than UPDATED_ITEM_AMOUNT
        defaultTransferItemEntryShouldBeFound("itemAmount.lessThan=" + UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByItemAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        // Get all the transferItemEntryList where itemAmount is greater than DEFAULT_ITEM_AMOUNT
        defaultTransferItemEntryShouldNotBeFound("itemAmount.greaterThan=" + DEFAULT_ITEM_AMOUNT);

        // Get all the transferItemEntryList where itemAmount is greater than SMALLER_ITEM_AMOUNT
        defaultTransferItemEntryShouldBeFound("itemAmount.greaterThan=" + SMALLER_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesBySalesReceiptIsEqualToSomething() throws Exception {
        SalesReceipt salesReceipt;
        if (TestUtil.findAll(em, SalesReceipt.class).isEmpty()) {
            transferItemEntryRepository.saveAndFlush(transferItemEntry);
            salesReceipt = SalesReceiptResourceIT.createEntity(em);
        } else {
            salesReceipt = TestUtil.findAll(em, SalesReceipt.class).get(0);
        }
        em.persist(salesReceipt);
        em.flush();
        transferItemEntry.setSalesReceipt(salesReceipt);
        transferItemEntryRepository.saveAndFlush(transferItemEntry);
        Long salesReceiptId = salesReceipt.getId();

        // Get all the transferItemEntryList where salesReceipt equals to salesReceiptId
        defaultTransferItemEntryShouldBeFound("salesReceiptId.equals=" + salesReceiptId);

        // Get all the transferItemEntryList where salesReceipt equals to (salesReceiptId + 1)
        defaultTransferItemEntryShouldNotBeFound("salesReceiptId.equals=" + (salesReceiptId + 1));
    }

    @Test
    @Transactional
    void getAllTransferItemEntriesByTransferItemIsEqualToSomething() throws Exception {
        TransferItem transferItem;
        if (TestUtil.findAll(em, TransferItem.class).isEmpty()) {
            transferItemEntryRepository.saveAndFlush(transferItemEntry);
            transferItem = TransferItemResourceIT.createEntity(em);
        } else {
            transferItem = TestUtil.findAll(em, TransferItem.class).get(0);
        }
        em.persist(transferItem);
        em.flush();
        transferItemEntry.setTransferItem(transferItem);
        transferItemEntryRepository.saveAndFlush(transferItemEntry);
        Long transferItemId = transferItem.getId();

        // Get all the transferItemEntryList where transferItem equals to transferItemId
        defaultTransferItemEntryShouldBeFound("transferItemId.equals=" + transferItemId);

        // Get all the transferItemEntryList where transferItem equals to (transferItemId + 1)
        defaultTransferItemEntryShouldNotBeFound("transferItemId.equals=" + (transferItemId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransferItemEntryShouldBeFound(String filter) throws Exception {
        restTransferItemEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transferItemEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].itemAmount").value(hasItem(sameNumber(DEFAULT_ITEM_AMOUNT))));

        // Check, that the count call also returns 1
        restTransferItemEntryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransferItemEntryShouldNotBeFound(String filter) throws Exception {
        restTransferItemEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransferItemEntryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransferItemEntry() throws Exception {
        // Get the transferItemEntry
        restTransferItemEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransferItemEntry() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        int databaseSizeBeforeUpdate = transferItemEntryRepository.findAll().size();
        transferItemEntrySearchRepository.save(transferItemEntry);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());

        // Update the transferItemEntry
        TransferItemEntry updatedTransferItemEntry = transferItemEntryRepository.findById(transferItemEntry.getId()).get();
        // Disconnect from session so that the updates on updatedTransferItemEntry are not directly saved in db
        em.detach(updatedTransferItemEntry);
        updatedTransferItemEntry.description(UPDATED_DESCRIPTION).itemAmount(UPDATED_ITEM_AMOUNT);
        TransferItemEntryDTO transferItemEntryDTO = transferItemEntryMapper.toDto(updatedTransferItemEntry);

        restTransferItemEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transferItemEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transferItemEntryDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransferItemEntry in the database
        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeUpdate);
        TransferItemEntry testTransferItemEntry = transferItemEntryList.get(transferItemEntryList.size() - 1);
        assertThat(testTransferItemEntry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTransferItemEntry.getItemAmount()).isEqualByComparingTo(UPDATED_ITEM_AMOUNT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TransferItemEntry> transferItemEntrySearchList = IterableUtils.toList(transferItemEntrySearchRepository.findAll());
                TransferItemEntry testTransferItemEntrySearch = transferItemEntrySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTransferItemEntrySearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testTransferItemEntrySearch.getItemAmount()).isEqualByComparingTo(UPDATED_ITEM_AMOUNT);
            });
    }

    @Test
    @Transactional
    void putNonExistingTransferItemEntry() throws Exception {
        int databaseSizeBeforeUpdate = transferItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        transferItemEntry.setId(count.incrementAndGet());

        // Create the TransferItemEntry
        TransferItemEntryDTO transferItemEntryDTO = transferItemEntryMapper.toDto(transferItemEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransferItemEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transferItemEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transferItemEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransferItemEntry in the database
        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransferItemEntry() throws Exception {
        int databaseSizeBeforeUpdate = transferItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        transferItemEntry.setId(count.incrementAndGet());

        // Create the TransferItemEntry
        TransferItemEntryDTO transferItemEntryDTO = transferItemEntryMapper.toDto(transferItemEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransferItemEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transferItemEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransferItemEntry in the database
        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransferItemEntry() throws Exception {
        int databaseSizeBeforeUpdate = transferItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        transferItemEntry.setId(count.incrementAndGet());

        // Create the TransferItemEntry
        TransferItemEntryDTO transferItemEntryDTO = transferItemEntryMapper.toDto(transferItemEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransferItemEntryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transferItemEntryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransferItemEntry in the database
        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTransferItemEntryWithPatch() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        int databaseSizeBeforeUpdate = transferItemEntryRepository.findAll().size();

        // Update the transferItemEntry using partial update
        TransferItemEntry partialUpdatedTransferItemEntry = new TransferItemEntry();
        partialUpdatedTransferItemEntry.setId(transferItemEntry.getId());

        restTransferItemEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransferItemEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransferItemEntry))
            )
            .andExpect(status().isOk());

        // Validate the TransferItemEntry in the database
        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeUpdate);
        TransferItemEntry testTransferItemEntry = transferItemEntryList.get(transferItemEntryList.size() - 1);
        assertThat(testTransferItemEntry.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTransferItemEntry.getItemAmount()).isEqualByComparingTo(DEFAULT_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void fullUpdateTransferItemEntryWithPatch() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);

        int databaseSizeBeforeUpdate = transferItemEntryRepository.findAll().size();

        // Update the transferItemEntry using partial update
        TransferItemEntry partialUpdatedTransferItemEntry = new TransferItemEntry();
        partialUpdatedTransferItemEntry.setId(transferItemEntry.getId());

        partialUpdatedTransferItemEntry.description(UPDATED_DESCRIPTION).itemAmount(UPDATED_ITEM_AMOUNT);

        restTransferItemEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransferItemEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransferItemEntry))
            )
            .andExpect(status().isOk());

        // Validate the TransferItemEntry in the database
        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeUpdate);
        TransferItemEntry testTransferItemEntry = transferItemEntryList.get(transferItemEntryList.size() - 1);
        assertThat(testTransferItemEntry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTransferItemEntry.getItemAmount()).isEqualByComparingTo(UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingTransferItemEntry() throws Exception {
        int databaseSizeBeforeUpdate = transferItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        transferItemEntry.setId(count.incrementAndGet());

        // Create the TransferItemEntry
        TransferItemEntryDTO transferItemEntryDTO = transferItemEntryMapper.toDto(transferItemEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransferItemEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transferItemEntryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transferItemEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransferItemEntry in the database
        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransferItemEntry() throws Exception {
        int databaseSizeBeforeUpdate = transferItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        transferItemEntry.setId(count.incrementAndGet());

        // Create the TransferItemEntry
        TransferItemEntryDTO transferItemEntryDTO = transferItemEntryMapper.toDto(transferItemEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransferItemEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transferItemEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransferItemEntry in the database
        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransferItemEntry() throws Exception {
        int databaseSizeBeforeUpdate = transferItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        transferItemEntry.setId(count.incrementAndGet());

        // Create the TransferItemEntry
        TransferItemEntryDTO transferItemEntryDTO = transferItemEntryMapper.toDto(transferItemEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransferItemEntryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transferItemEntryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransferItemEntry in the database
        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTransferItemEntry() throws Exception {
        // Initialize the database
        transferItemEntryRepository.saveAndFlush(transferItemEntry);
        transferItemEntryRepository.save(transferItemEntry);
        transferItemEntrySearchRepository.save(transferItemEntry);

        int databaseSizeBeforeDelete = transferItemEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transferItemEntry
        restTransferItemEntryMockMvc
            .perform(delete(ENTITY_API_URL_ID, transferItemEntry.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransferItemEntry> transferItemEntryList = transferItemEntryRepository.findAll();
        assertThat(transferItemEntryList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTransferItemEntry() throws Exception {
        // Initialize the database
        transferItemEntry = transferItemEntryRepository.saveAndFlush(transferItemEntry);
        transferItemEntrySearchRepository.save(transferItemEntry);

        // Search the transferItemEntry
        restTransferItemEntryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transferItemEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transferItemEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].itemAmount").value(hasItem(sameNumber(DEFAULT_ITEM_AMOUNT))));
    }
}

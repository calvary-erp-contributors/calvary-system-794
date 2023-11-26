package io.github.calvary.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.Dealer;
import io.github.calvary.domain.SalesReceipt;
import io.github.calvary.domain.TransactionClass;
import io.github.calvary.domain.TransactionItemAmount;
import io.github.calvary.repository.SalesReceiptRepository;
import io.github.calvary.repository.search.SalesReceiptSearchRepository;
import io.github.calvary.service.SalesReceiptService;
import io.github.calvary.service.criteria.SalesReceiptCriteria;
import io.github.calvary.service.dto.SalesReceiptDTO;
import io.github.calvary.service.mapper.SalesReceiptMapper;
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
 * Integration tests for the {@link SalesReceiptResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SalesReceiptResourceIT {

    private static final String DEFAULT_SALES_RECEIPT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_SALES_RECEIPT_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sales-receipts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/sales-receipts";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SalesReceiptRepository salesReceiptRepository;

    @Mock
    private SalesReceiptRepository salesReceiptRepositoryMock;

    @Autowired
    private SalesReceiptMapper salesReceiptMapper;

    @Mock
    private SalesReceiptService salesReceiptServiceMock;

    @Autowired
    private SalesReceiptSearchRepository salesReceiptSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalesReceiptMockMvc;

    private SalesReceipt salesReceipt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesReceipt createEntity(EntityManager em) {
        SalesReceipt salesReceipt = new SalesReceipt().salesReceiptTitle(DEFAULT_SALES_RECEIPT_TITLE).description(DEFAULT_DESCRIPTION);
        // Add required entity
        Dealer dealer;
        if (TestUtil.findAll(em, Dealer.class).isEmpty()) {
            dealer = DealerResourceIT.createEntity(em);
            em.persist(dealer);
            em.flush();
        } else {
            dealer = TestUtil.findAll(em, Dealer.class).get(0);
        }
        salesReceipt.setDealer(dealer);
        // Add required entity
        TransactionItemAmount transactionItemAmount;
        if (TestUtil.findAll(em, TransactionItemAmount.class).isEmpty()) {
            transactionItemAmount = TransactionItemAmountResourceIT.createEntity(em);
            em.persist(transactionItemAmount);
            em.flush();
        } else {
            transactionItemAmount = TestUtil.findAll(em, TransactionItemAmount.class).get(0);
        }
        salesReceipt.getTransactionItemAmounts().add(transactionItemAmount);
        return salesReceipt;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesReceipt createUpdatedEntity(EntityManager em) {
        SalesReceipt salesReceipt = new SalesReceipt().salesReceiptTitle(UPDATED_SALES_RECEIPT_TITLE).description(UPDATED_DESCRIPTION);
        // Add required entity
        Dealer dealer;
        if (TestUtil.findAll(em, Dealer.class).isEmpty()) {
            dealer = DealerResourceIT.createUpdatedEntity(em);
            em.persist(dealer);
            em.flush();
        } else {
            dealer = TestUtil.findAll(em, Dealer.class).get(0);
        }
        salesReceipt.setDealer(dealer);
        // Add required entity
        TransactionItemAmount transactionItemAmount;
        if (TestUtil.findAll(em, TransactionItemAmount.class).isEmpty()) {
            transactionItemAmount = TransactionItemAmountResourceIT.createUpdatedEntity(em);
            em.persist(transactionItemAmount);
            em.flush();
        } else {
            transactionItemAmount = TestUtil.findAll(em, TransactionItemAmount.class).get(0);
        }
        salesReceipt.getTransactionItemAmounts().add(transactionItemAmount);
        return salesReceipt;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        salesReceiptSearchRepository.deleteAll();
        assertThat(salesReceiptSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        salesReceipt = createEntity(em);
    }

    @Test
    @Transactional
    void createSalesReceipt() throws Exception {
        int databaseSizeBeforeCreate = salesReceiptRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        // Create the SalesReceipt
        SalesReceiptDTO salesReceiptDTO = salesReceiptMapper.toDto(salesReceipt);
        restSalesReceiptMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesReceiptDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SalesReceipt in the database
        List<SalesReceipt> salesReceiptList = salesReceiptRepository.findAll();
        assertThat(salesReceiptList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SalesReceipt testSalesReceipt = salesReceiptList.get(salesReceiptList.size() - 1);
        assertThat(testSalesReceipt.getSalesReceiptTitle()).isEqualTo(DEFAULT_SALES_RECEIPT_TITLE);
        assertThat(testSalesReceipt.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createSalesReceiptWithExistingId() throws Exception {
        // Create the SalesReceipt with an existing ID
        salesReceipt.setId(1L);
        SalesReceiptDTO salesReceiptDTO = salesReceiptMapper.toDto(salesReceipt);

        int databaseSizeBeforeCreate = salesReceiptRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalesReceiptMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceipt in the database
        List<SalesReceipt> salesReceiptList = salesReceiptRepository.findAll();
        assertThat(salesReceiptList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSalesReceipts() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        // Get all the salesReceiptList
        restSalesReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceipt.getId().intValue())))
            .andExpect(jsonPath("$.[*].salesReceiptTitle").value(hasItem(DEFAULT_SALES_RECEIPT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesReceiptsWithEagerRelationshipsIsEnabled() throws Exception {
        when(salesReceiptServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSalesReceiptMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(salesReceiptServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesReceiptsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(salesReceiptServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSalesReceiptMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(salesReceiptRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSalesReceipt() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        // Get the salesReceipt
        restSalesReceiptMockMvc
            .perform(get(ENTITY_API_URL_ID, salesReceipt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(salesReceipt.getId().intValue()))
            .andExpect(jsonPath("$.salesReceiptTitle").value(DEFAULT_SALES_RECEIPT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getSalesReceiptsByIdFiltering() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        Long id = salesReceipt.getId();

        defaultSalesReceiptShouldBeFound("id.equals=" + id);
        defaultSalesReceiptShouldNotBeFound("id.notEquals=" + id);

        defaultSalesReceiptShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSalesReceiptShouldNotBeFound("id.greaterThan=" + id);

        defaultSalesReceiptShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSalesReceiptShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalesReceiptsBySalesReceiptTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        // Get all the salesReceiptList where salesReceiptTitle equals to DEFAULT_SALES_RECEIPT_TITLE
        defaultSalesReceiptShouldBeFound("salesReceiptTitle.equals=" + DEFAULT_SALES_RECEIPT_TITLE);

        // Get all the salesReceiptList where salesReceiptTitle equals to UPDATED_SALES_RECEIPT_TITLE
        defaultSalesReceiptShouldNotBeFound("salesReceiptTitle.equals=" + UPDATED_SALES_RECEIPT_TITLE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptsBySalesReceiptTitleIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        // Get all the salesReceiptList where salesReceiptTitle in DEFAULT_SALES_RECEIPT_TITLE or UPDATED_SALES_RECEIPT_TITLE
        defaultSalesReceiptShouldBeFound("salesReceiptTitle.in=" + DEFAULT_SALES_RECEIPT_TITLE + "," + UPDATED_SALES_RECEIPT_TITLE);

        // Get all the salesReceiptList where salesReceiptTitle equals to UPDATED_SALES_RECEIPT_TITLE
        defaultSalesReceiptShouldNotBeFound("salesReceiptTitle.in=" + UPDATED_SALES_RECEIPT_TITLE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptsBySalesReceiptTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        // Get all the salesReceiptList where salesReceiptTitle is not null
        defaultSalesReceiptShouldBeFound("salesReceiptTitle.specified=true");

        // Get all the salesReceiptList where salesReceiptTitle is null
        defaultSalesReceiptShouldNotBeFound("salesReceiptTitle.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptsBySalesReceiptTitleContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        // Get all the salesReceiptList where salesReceiptTitle contains DEFAULT_SALES_RECEIPT_TITLE
        defaultSalesReceiptShouldBeFound("salesReceiptTitle.contains=" + DEFAULT_SALES_RECEIPT_TITLE);

        // Get all the salesReceiptList where salesReceiptTitle contains UPDATED_SALES_RECEIPT_TITLE
        defaultSalesReceiptShouldNotBeFound("salesReceiptTitle.contains=" + UPDATED_SALES_RECEIPT_TITLE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptsBySalesReceiptTitleNotContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        // Get all the salesReceiptList where salesReceiptTitle does not contain DEFAULT_SALES_RECEIPT_TITLE
        defaultSalesReceiptShouldNotBeFound("salesReceiptTitle.doesNotContain=" + DEFAULT_SALES_RECEIPT_TITLE);

        // Get all the salesReceiptList where salesReceiptTitle does not contain UPDATED_SALES_RECEIPT_TITLE
        defaultSalesReceiptShouldBeFound("salesReceiptTitle.doesNotContain=" + UPDATED_SALES_RECEIPT_TITLE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        // Get all the salesReceiptList where description equals to DEFAULT_DESCRIPTION
        defaultSalesReceiptShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the salesReceiptList where description equals to UPDATED_DESCRIPTION
        defaultSalesReceiptShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSalesReceiptsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        // Get all the salesReceiptList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultSalesReceiptShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the salesReceiptList where description equals to UPDATED_DESCRIPTION
        defaultSalesReceiptShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSalesReceiptsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        // Get all the salesReceiptList where description is not null
        defaultSalesReceiptShouldBeFound("description.specified=true");

        // Get all the salesReceiptList where description is null
        defaultSalesReceiptShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        // Get all the salesReceiptList where description contains DEFAULT_DESCRIPTION
        defaultSalesReceiptShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the salesReceiptList where description contains UPDATED_DESCRIPTION
        defaultSalesReceiptShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSalesReceiptsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        // Get all the salesReceiptList where description does not contain DEFAULT_DESCRIPTION
        defaultSalesReceiptShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the salesReceiptList where description does not contain UPDATED_DESCRIPTION
        defaultSalesReceiptShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSalesReceiptsByTransactionClassIsEqualToSomething() throws Exception {
        TransactionClass transactionClass;
        if (TestUtil.findAll(em, TransactionClass.class).isEmpty()) {
            salesReceiptRepository.saveAndFlush(salesReceipt);
            transactionClass = TransactionClassResourceIT.createEntity(em);
        } else {
            transactionClass = TestUtil.findAll(em, TransactionClass.class).get(0);
        }
        em.persist(transactionClass);
        em.flush();
        salesReceipt.setTransactionClass(transactionClass);
        salesReceiptRepository.saveAndFlush(salesReceipt);
        Long transactionClassId = transactionClass.getId();

        // Get all the salesReceiptList where transactionClass equals to transactionClassId
        defaultSalesReceiptShouldBeFound("transactionClassId.equals=" + transactionClassId);

        // Get all the salesReceiptList where transactionClass equals to (transactionClassId + 1)
        defaultSalesReceiptShouldNotBeFound("transactionClassId.equals=" + (transactionClassId + 1));
    }

    @Test
    @Transactional
    void getAllSalesReceiptsByDealerIsEqualToSomething() throws Exception {
        Dealer dealer;
        if (TestUtil.findAll(em, Dealer.class).isEmpty()) {
            salesReceiptRepository.saveAndFlush(salesReceipt);
            dealer = DealerResourceIT.createEntity(em);
        } else {
            dealer = TestUtil.findAll(em, Dealer.class).get(0);
        }
        em.persist(dealer);
        em.flush();
        salesReceipt.setDealer(dealer);
        salesReceiptRepository.saveAndFlush(salesReceipt);
        Long dealerId = dealer.getId();

        // Get all the salesReceiptList where dealer equals to dealerId
        defaultSalesReceiptShouldBeFound("dealerId.equals=" + dealerId);

        // Get all the salesReceiptList where dealer equals to (dealerId + 1)
        defaultSalesReceiptShouldNotBeFound("dealerId.equals=" + (dealerId + 1));
    }

    @Test
    @Transactional
    void getAllSalesReceiptsByTransactionItemAmountIsEqualToSomething() throws Exception {
        TransactionItemAmount transactionItemAmount;
        if (TestUtil.findAll(em, TransactionItemAmount.class).isEmpty()) {
            salesReceiptRepository.saveAndFlush(salesReceipt);
            transactionItemAmount = TransactionItemAmountResourceIT.createEntity(em);
        } else {
            transactionItemAmount = TestUtil.findAll(em, TransactionItemAmount.class).get(0);
        }
        em.persist(transactionItemAmount);
        em.flush();
        salesReceipt.addTransactionItemAmount(transactionItemAmount);
        salesReceiptRepository.saveAndFlush(salesReceipt);
        Long transactionItemAmountId = transactionItemAmount.getId();

        // Get all the salesReceiptList where transactionItemAmount equals to transactionItemAmountId
        defaultSalesReceiptShouldBeFound("transactionItemAmountId.equals=" + transactionItemAmountId);

        // Get all the salesReceiptList where transactionItemAmount equals to (transactionItemAmountId + 1)
        defaultSalesReceiptShouldNotBeFound("transactionItemAmountId.equals=" + (transactionItemAmountId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSalesReceiptShouldBeFound(String filter) throws Exception {
        restSalesReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceipt.getId().intValue())))
            .andExpect(jsonPath("$.[*].salesReceiptTitle").value(hasItem(DEFAULT_SALES_RECEIPT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restSalesReceiptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSalesReceiptShouldNotBeFound(String filter) throws Exception {
        restSalesReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSalesReceiptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSalesReceipt() throws Exception {
        // Get the salesReceipt
        restSalesReceiptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSalesReceipt() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        int databaseSizeBeforeUpdate = salesReceiptRepository.findAll().size();
        salesReceiptSearchRepository.save(salesReceipt);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());

        // Update the salesReceipt
        SalesReceipt updatedSalesReceipt = salesReceiptRepository.findById(salesReceipt.getId()).get();
        // Disconnect from session so that the updates on updatedSalesReceipt are not directly saved in db
        em.detach(updatedSalesReceipt);
        updatedSalesReceipt.salesReceiptTitle(UPDATED_SALES_RECEIPT_TITLE).description(UPDATED_DESCRIPTION);
        SalesReceiptDTO salesReceiptDTO = salesReceiptMapper.toDto(updatedSalesReceipt);

        restSalesReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesReceiptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptDTO))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceipt in the database
        List<SalesReceipt> salesReceiptList = salesReceiptRepository.findAll();
        assertThat(salesReceiptList).hasSize(databaseSizeBeforeUpdate);
        SalesReceipt testSalesReceipt = salesReceiptList.get(salesReceiptList.size() - 1);
        assertThat(testSalesReceipt.getSalesReceiptTitle()).isEqualTo(UPDATED_SALES_RECEIPT_TITLE);
        assertThat(testSalesReceipt.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SalesReceipt> salesReceiptSearchList = IterableUtils.toList(salesReceiptSearchRepository.findAll());
                SalesReceipt testSalesReceiptSearch = salesReceiptSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSalesReceiptSearch.getSalesReceiptTitle()).isEqualTo(UPDATED_SALES_RECEIPT_TITLE);
                assertThat(testSalesReceiptSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingSalesReceipt() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        salesReceipt.setId(count.incrementAndGet());

        // Create the SalesReceipt
        SalesReceiptDTO salesReceiptDTO = salesReceiptMapper.toDto(salesReceipt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesReceiptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceipt in the database
        List<SalesReceipt> salesReceiptList = salesReceiptRepository.findAll();
        assertThat(salesReceiptList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSalesReceipt() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        salesReceipt.setId(count.incrementAndGet());

        // Create the SalesReceipt
        SalesReceiptDTO salesReceiptDTO = salesReceiptMapper.toDto(salesReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceipt in the database
        List<SalesReceipt> salesReceiptList = salesReceiptRepository.findAll();
        assertThat(salesReceiptList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSalesReceipt() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        salesReceipt.setId(count.incrementAndGet());

        // Create the SalesReceipt
        SalesReceiptDTO salesReceiptDTO = salesReceiptMapper.toDto(salesReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesReceiptDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesReceipt in the database
        List<SalesReceipt> salesReceiptList = salesReceiptRepository.findAll();
        assertThat(salesReceiptList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSalesReceiptWithPatch() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        int databaseSizeBeforeUpdate = salesReceiptRepository.findAll().size();

        // Update the salesReceipt using partial update
        SalesReceipt partialUpdatedSalesReceipt = new SalesReceipt();
        partialUpdatedSalesReceipt.setId(salesReceipt.getId());

        partialUpdatedSalesReceipt.salesReceiptTitle(UPDATED_SALES_RECEIPT_TITLE).description(UPDATED_DESCRIPTION);

        restSalesReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesReceipt.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesReceipt))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceipt in the database
        List<SalesReceipt> salesReceiptList = salesReceiptRepository.findAll();
        assertThat(salesReceiptList).hasSize(databaseSizeBeforeUpdate);
        SalesReceipt testSalesReceipt = salesReceiptList.get(salesReceiptList.size() - 1);
        assertThat(testSalesReceipt.getSalesReceiptTitle()).isEqualTo(UPDATED_SALES_RECEIPT_TITLE);
        assertThat(testSalesReceipt.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateSalesReceiptWithPatch() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);

        int databaseSizeBeforeUpdate = salesReceiptRepository.findAll().size();

        // Update the salesReceipt using partial update
        SalesReceipt partialUpdatedSalesReceipt = new SalesReceipt();
        partialUpdatedSalesReceipt.setId(salesReceipt.getId());

        partialUpdatedSalesReceipt.salesReceiptTitle(UPDATED_SALES_RECEIPT_TITLE).description(UPDATED_DESCRIPTION);

        restSalesReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesReceipt.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesReceipt))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceipt in the database
        List<SalesReceipt> salesReceiptList = salesReceiptRepository.findAll();
        assertThat(salesReceiptList).hasSize(databaseSizeBeforeUpdate);
        SalesReceipt testSalesReceipt = salesReceiptList.get(salesReceiptList.size() - 1);
        assertThat(testSalesReceipt.getSalesReceiptTitle()).isEqualTo(UPDATED_SALES_RECEIPT_TITLE);
        assertThat(testSalesReceipt.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingSalesReceipt() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        salesReceipt.setId(count.incrementAndGet());

        // Create the SalesReceipt
        SalesReceiptDTO salesReceiptDTO = salesReceiptMapper.toDto(salesReceipt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salesReceiptDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceipt in the database
        List<SalesReceipt> salesReceiptList = salesReceiptRepository.findAll();
        assertThat(salesReceiptList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSalesReceipt() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        salesReceipt.setId(count.incrementAndGet());

        // Create the SalesReceipt
        SalesReceiptDTO salesReceiptDTO = salesReceiptMapper.toDto(salesReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceipt in the database
        List<SalesReceipt> salesReceiptList = salesReceiptRepository.findAll();
        assertThat(salesReceiptList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSalesReceipt() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        salesReceipt.setId(count.incrementAndGet());

        // Create the SalesReceipt
        SalesReceiptDTO salesReceiptDTO = salesReceiptMapper.toDto(salesReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesReceipt in the database
        List<SalesReceipt> salesReceiptList = salesReceiptRepository.findAll();
        assertThat(salesReceiptList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSalesReceipt() throws Exception {
        // Initialize the database
        salesReceiptRepository.saveAndFlush(salesReceipt);
        salesReceiptRepository.save(salesReceipt);
        salesReceiptSearchRepository.save(salesReceipt);

        int databaseSizeBeforeDelete = salesReceiptRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the salesReceipt
        restSalesReceiptMockMvc
            .perform(delete(ENTITY_API_URL_ID, salesReceipt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SalesReceipt> salesReceiptList = salesReceiptRepository.findAll();
        assertThat(salesReceiptList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSalesReceipt() throws Exception {
        // Initialize the database
        salesReceipt = salesReceiptRepository.saveAndFlush(salesReceipt);
        salesReceiptSearchRepository.save(salesReceipt);

        // Search the salesReceipt
        restSalesReceiptMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + salesReceipt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceipt.getId().intValue())))
            .andExpect(jsonPath("$.[*].salesReceiptTitle").value(hasItem(DEFAULT_SALES_RECEIPT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}

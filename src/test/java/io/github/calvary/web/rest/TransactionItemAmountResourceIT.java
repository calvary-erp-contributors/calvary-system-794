package io.github.calvary.web.rest;

import static io.github.calvary.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.TransactionItem;
import io.github.calvary.domain.TransactionItemAmount;
import io.github.calvary.repository.TransactionItemAmountRepository;
import io.github.calvary.repository.search.TransactionItemAmountSearchRepository;
import io.github.calvary.service.TransactionItemAmountService;
import io.github.calvary.service.criteria.TransactionItemAmountCriteria;
import io.github.calvary.service.dto.TransactionItemAmountDTO;
import io.github.calvary.service.mapper.TransactionItemAmountMapper;
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
 * Integration tests for the {@link TransactionItemAmountResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TransactionItemAmountResourceIT {

    private static final BigDecimal DEFAULT_TRANSACTION_ITEM_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_TRANSACTION_ITEM_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_TRANSACTION_ITEM_AMOUNT = new BigDecimal(0 - 1);

    private static final String ENTITY_API_URL = "/api/transaction-item-amounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/transaction-item-amounts";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionItemAmountRepository transactionItemAmountRepository;

    @Mock
    private TransactionItemAmountRepository transactionItemAmountRepositoryMock;

    @Autowired
    private TransactionItemAmountMapper transactionItemAmountMapper;

    @Mock
    private TransactionItemAmountService transactionItemAmountServiceMock;

    @Autowired
    private TransactionItemAmountSearchRepository transactionItemAmountSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionItemAmountMockMvc;

    private TransactionItemAmount transactionItemAmount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionItemAmount createEntity(EntityManager em) {
        TransactionItemAmount transactionItemAmount = new TransactionItemAmount().transactionItemAmount(DEFAULT_TRANSACTION_ITEM_AMOUNT);
        // Add required entity
        TransactionItem transactionItem;
        if (TestUtil.findAll(em, TransactionItem.class).isEmpty()) {
            transactionItem = TransactionItemResourceIT.createEntity(em);
            em.persist(transactionItem);
            em.flush();
        } else {
            transactionItem = TestUtil.findAll(em, TransactionItem.class).get(0);
        }
        transactionItemAmount.setTransactionItem(transactionItem);
        return transactionItemAmount;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionItemAmount createUpdatedEntity(EntityManager em) {
        TransactionItemAmount transactionItemAmount = new TransactionItemAmount().transactionItemAmount(UPDATED_TRANSACTION_ITEM_AMOUNT);
        // Add required entity
        TransactionItem transactionItem;
        if (TestUtil.findAll(em, TransactionItem.class).isEmpty()) {
            transactionItem = TransactionItemResourceIT.createUpdatedEntity(em);
            em.persist(transactionItem);
            em.flush();
        } else {
            transactionItem = TestUtil.findAll(em, TransactionItem.class).get(0);
        }
        transactionItemAmount.setTransactionItem(transactionItem);
        return transactionItemAmount;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        transactionItemAmountSearchRepository.deleteAll();
        assertThat(transactionItemAmountSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        transactionItemAmount = createEntity(em);
    }

    @Test
    @Transactional
    void createTransactionItemAmount() throws Exception {
        int databaseSizeBeforeCreate = transactionItemAmountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        // Create the TransactionItemAmount
        TransactionItemAmountDTO transactionItemAmountDTO = transactionItemAmountMapper.toDto(transactionItemAmount);
        restTransactionItemAmountMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemAmountDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TransactionItemAmount in the database
        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TransactionItemAmount testTransactionItemAmount = transactionItemAmountList.get(transactionItemAmountList.size() - 1);
        assertThat(testTransactionItemAmount.getTransactionItemAmount()).isEqualByComparingTo(DEFAULT_TRANSACTION_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void createTransactionItemAmountWithExistingId() throws Exception {
        // Create the TransactionItemAmount with an existing ID
        transactionItemAmount.setId(1L);
        TransactionItemAmountDTO transactionItemAmountDTO = transactionItemAmountMapper.toDto(transactionItemAmount);

        int databaseSizeBeforeCreate = transactionItemAmountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionItemAmountMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemAmountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItemAmount in the database
        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTransactionItemAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionItemAmountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        // set the field null
        transactionItemAmount.setTransactionItemAmount(null);

        // Create the TransactionItemAmount, which fails.
        TransactionItemAmountDTO transactionItemAmountDTO = transactionItemAmountMapper.toDto(transactionItemAmount);

        restTransactionItemAmountMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemAmountDTO))
            )
            .andExpect(status().isBadRequest());

        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTransactionItemAmounts() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        // Get all the transactionItemAmountList
        restTransactionItemAmountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionItemAmount.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionItemAmount").value(hasItem(sameNumber(DEFAULT_TRANSACTION_ITEM_AMOUNT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransactionItemAmountsWithEagerRelationshipsIsEnabled() throws Exception {
        when(transactionItemAmountServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransactionItemAmountMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(transactionItemAmountServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransactionItemAmountsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(transactionItemAmountServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransactionItemAmountMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(transactionItemAmountRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTransactionItemAmount() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        // Get the transactionItemAmount
        restTransactionItemAmountMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionItemAmount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionItemAmount.getId().intValue()))
            .andExpect(jsonPath("$.transactionItemAmount").value(sameNumber(DEFAULT_TRANSACTION_ITEM_AMOUNT)));
    }

    @Test
    @Transactional
    void getTransactionItemAmountsByIdFiltering() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        Long id = transactionItemAmount.getId();

        defaultTransactionItemAmountShouldBeFound("id.equals=" + id);
        defaultTransactionItemAmountShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionItemAmountShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionItemAmountShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionItemAmountShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionItemAmountShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionItemAmountsByTransactionItemAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        // Get all the transactionItemAmountList where transactionItemAmount equals to DEFAULT_TRANSACTION_ITEM_AMOUNT
        defaultTransactionItemAmountShouldBeFound("transactionItemAmount.equals=" + DEFAULT_TRANSACTION_ITEM_AMOUNT);

        // Get all the transactionItemAmountList where transactionItemAmount equals to UPDATED_TRANSACTION_ITEM_AMOUNT
        defaultTransactionItemAmountShouldNotBeFound("transactionItemAmount.equals=" + UPDATED_TRANSACTION_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionItemAmountsByTransactionItemAmountIsInShouldWork() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        // Get all the transactionItemAmountList where transactionItemAmount in DEFAULT_TRANSACTION_ITEM_AMOUNT or UPDATED_TRANSACTION_ITEM_AMOUNT
        defaultTransactionItemAmountShouldBeFound(
            "transactionItemAmount.in=" + DEFAULT_TRANSACTION_ITEM_AMOUNT + "," + UPDATED_TRANSACTION_ITEM_AMOUNT
        );

        // Get all the transactionItemAmountList where transactionItemAmount equals to UPDATED_TRANSACTION_ITEM_AMOUNT
        defaultTransactionItemAmountShouldNotBeFound("transactionItemAmount.in=" + UPDATED_TRANSACTION_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionItemAmountsByTransactionItemAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        // Get all the transactionItemAmountList where transactionItemAmount is not null
        defaultTransactionItemAmountShouldBeFound("transactionItemAmount.specified=true");

        // Get all the transactionItemAmountList where transactionItemAmount is null
        defaultTransactionItemAmountShouldNotBeFound("transactionItemAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionItemAmountsByTransactionItemAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        // Get all the transactionItemAmountList where transactionItemAmount is greater than or equal to DEFAULT_TRANSACTION_ITEM_AMOUNT
        defaultTransactionItemAmountShouldBeFound("transactionItemAmount.greaterThanOrEqual=" + DEFAULT_TRANSACTION_ITEM_AMOUNT);

        // Get all the transactionItemAmountList where transactionItemAmount is greater than or equal to UPDATED_TRANSACTION_ITEM_AMOUNT
        defaultTransactionItemAmountShouldNotBeFound("transactionItemAmount.greaterThanOrEqual=" + UPDATED_TRANSACTION_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionItemAmountsByTransactionItemAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        // Get all the transactionItemAmountList where transactionItemAmount is less than or equal to DEFAULT_TRANSACTION_ITEM_AMOUNT
        defaultTransactionItemAmountShouldBeFound("transactionItemAmount.lessThanOrEqual=" + DEFAULT_TRANSACTION_ITEM_AMOUNT);

        // Get all the transactionItemAmountList where transactionItemAmount is less than or equal to SMALLER_TRANSACTION_ITEM_AMOUNT
        defaultTransactionItemAmountShouldNotBeFound("transactionItemAmount.lessThanOrEqual=" + SMALLER_TRANSACTION_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionItemAmountsByTransactionItemAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        // Get all the transactionItemAmountList where transactionItemAmount is less than DEFAULT_TRANSACTION_ITEM_AMOUNT
        defaultTransactionItemAmountShouldNotBeFound("transactionItemAmount.lessThan=" + DEFAULT_TRANSACTION_ITEM_AMOUNT);

        // Get all the transactionItemAmountList where transactionItemAmount is less than UPDATED_TRANSACTION_ITEM_AMOUNT
        defaultTransactionItemAmountShouldBeFound("transactionItemAmount.lessThan=" + UPDATED_TRANSACTION_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionItemAmountsByTransactionItemAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        // Get all the transactionItemAmountList where transactionItemAmount is greater than DEFAULT_TRANSACTION_ITEM_AMOUNT
        defaultTransactionItemAmountShouldNotBeFound("transactionItemAmount.greaterThan=" + DEFAULT_TRANSACTION_ITEM_AMOUNT);

        // Get all the transactionItemAmountList where transactionItemAmount is greater than SMALLER_TRANSACTION_ITEM_AMOUNT
        defaultTransactionItemAmountShouldBeFound("transactionItemAmount.greaterThan=" + SMALLER_TRANSACTION_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionItemAmountsByTransactionItemIsEqualToSomething() throws Exception {
        TransactionItem transactionItem;
        if (TestUtil.findAll(em, TransactionItem.class).isEmpty()) {
            transactionItemAmountRepository.saveAndFlush(transactionItemAmount);
            transactionItem = TransactionItemResourceIT.createEntity(em);
        } else {
            transactionItem = TestUtil.findAll(em, TransactionItem.class).get(0);
        }
        em.persist(transactionItem);
        em.flush();
        transactionItemAmount.setTransactionItem(transactionItem);
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);
        Long transactionItemId = transactionItem.getId();

        // Get all the transactionItemAmountList where transactionItem equals to transactionItemId
        defaultTransactionItemAmountShouldBeFound("transactionItemId.equals=" + transactionItemId);

        // Get all the transactionItemAmountList where transactionItem equals to (transactionItemId + 1)
        defaultTransactionItemAmountShouldNotBeFound("transactionItemId.equals=" + (transactionItemId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionItemAmountShouldBeFound(String filter) throws Exception {
        restTransactionItemAmountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionItemAmount.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionItemAmount").value(hasItem(sameNumber(DEFAULT_TRANSACTION_ITEM_AMOUNT))));

        // Check, that the count call also returns 1
        restTransactionItemAmountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionItemAmountShouldNotBeFound(String filter) throws Exception {
        restTransactionItemAmountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionItemAmountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransactionItemAmount() throws Exception {
        // Get the transactionItemAmount
        restTransactionItemAmountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransactionItemAmount() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        int databaseSizeBeforeUpdate = transactionItemAmountRepository.findAll().size();
        transactionItemAmountSearchRepository.save(transactionItemAmount);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());

        // Update the transactionItemAmount
        TransactionItemAmount updatedTransactionItemAmount = transactionItemAmountRepository.findById(transactionItemAmount.getId()).get();
        // Disconnect from session so that the updates on updatedTransactionItemAmount are not directly saved in db
        em.detach(updatedTransactionItemAmount);
        updatedTransactionItemAmount.transactionItemAmount(UPDATED_TRANSACTION_ITEM_AMOUNT);
        TransactionItemAmountDTO transactionItemAmountDTO = transactionItemAmountMapper.toDto(updatedTransactionItemAmount);

        restTransactionItemAmountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionItemAmountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemAmountDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransactionItemAmount in the database
        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeUpdate);
        TransactionItemAmount testTransactionItemAmount = transactionItemAmountList.get(transactionItemAmountList.size() - 1);
        assertThat(testTransactionItemAmount.getTransactionItemAmount()).isEqualByComparingTo(UPDATED_TRANSACTION_ITEM_AMOUNT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TransactionItemAmount> transactionItemAmountSearchList = IterableUtils.toList(
                    transactionItemAmountSearchRepository.findAll()
                );
                TransactionItemAmount testTransactionItemAmountSearch = transactionItemAmountSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTransactionItemAmountSearch.getTransactionItemAmount())
                    .isEqualByComparingTo(UPDATED_TRANSACTION_ITEM_AMOUNT);
            });
    }

    @Test
    @Transactional
    void putNonExistingTransactionItemAmount() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemAmountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        transactionItemAmount.setId(count.incrementAndGet());

        // Create the TransactionItemAmount
        TransactionItemAmountDTO transactionItemAmountDTO = transactionItemAmountMapper.toDto(transactionItemAmount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionItemAmountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionItemAmountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemAmountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItemAmount in the database
        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactionItemAmount() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemAmountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        transactionItemAmount.setId(count.incrementAndGet());

        // Create the TransactionItemAmount
        TransactionItemAmountDTO transactionItemAmountDTO = transactionItemAmountMapper.toDto(transactionItemAmount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionItemAmountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemAmountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItemAmount in the database
        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactionItemAmount() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemAmountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        transactionItemAmount.setId(count.incrementAndGet());

        // Create the TransactionItemAmount
        TransactionItemAmountDTO transactionItemAmountDTO = transactionItemAmountMapper.toDto(transactionItemAmount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionItemAmountMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemAmountDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionItemAmount in the database
        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTransactionItemAmountWithPatch() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        int databaseSizeBeforeUpdate = transactionItemAmountRepository.findAll().size();

        // Update the transactionItemAmount using partial update
        TransactionItemAmount partialUpdatedTransactionItemAmount = new TransactionItemAmount();
        partialUpdatedTransactionItemAmount.setId(transactionItemAmount.getId());

        partialUpdatedTransactionItemAmount.transactionItemAmount(UPDATED_TRANSACTION_ITEM_AMOUNT);

        restTransactionItemAmountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionItemAmount.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionItemAmount))
            )
            .andExpect(status().isOk());

        // Validate the TransactionItemAmount in the database
        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeUpdate);
        TransactionItemAmount testTransactionItemAmount = transactionItemAmountList.get(transactionItemAmountList.size() - 1);
        assertThat(testTransactionItemAmount.getTransactionItemAmount()).isEqualByComparingTo(UPDATED_TRANSACTION_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void fullUpdateTransactionItemAmountWithPatch() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);

        int databaseSizeBeforeUpdate = transactionItemAmountRepository.findAll().size();

        // Update the transactionItemAmount using partial update
        TransactionItemAmount partialUpdatedTransactionItemAmount = new TransactionItemAmount();
        partialUpdatedTransactionItemAmount.setId(transactionItemAmount.getId());

        partialUpdatedTransactionItemAmount.transactionItemAmount(UPDATED_TRANSACTION_ITEM_AMOUNT);

        restTransactionItemAmountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionItemAmount.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionItemAmount))
            )
            .andExpect(status().isOk());

        // Validate the TransactionItemAmount in the database
        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeUpdate);
        TransactionItemAmount testTransactionItemAmount = transactionItemAmountList.get(transactionItemAmountList.size() - 1);
        assertThat(testTransactionItemAmount.getTransactionItemAmount()).isEqualByComparingTo(UPDATED_TRANSACTION_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingTransactionItemAmount() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemAmountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        transactionItemAmount.setId(count.incrementAndGet());

        // Create the TransactionItemAmount
        TransactionItemAmountDTO transactionItemAmountDTO = transactionItemAmountMapper.toDto(transactionItemAmount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionItemAmountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionItemAmountDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemAmountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItemAmount in the database
        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactionItemAmount() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemAmountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        transactionItemAmount.setId(count.incrementAndGet());

        // Create the TransactionItemAmount
        TransactionItemAmountDTO transactionItemAmountDTO = transactionItemAmountMapper.toDto(transactionItemAmount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionItemAmountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemAmountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItemAmount in the database
        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactionItemAmount() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemAmountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        transactionItemAmount.setId(count.incrementAndGet());

        // Create the TransactionItemAmount
        TransactionItemAmountDTO transactionItemAmountDTO = transactionItemAmountMapper.toDto(transactionItemAmount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionItemAmountMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemAmountDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionItemAmount in the database
        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTransactionItemAmount() throws Exception {
        // Initialize the database
        transactionItemAmountRepository.saveAndFlush(transactionItemAmount);
        transactionItemAmountRepository.save(transactionItemAmount);
        transactionItemAmountSearchRepository.save(transactionItemAmount);

        int databaseSizeBeforeDelete = transactionItemAmountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transactionItemAmount
        restTransactionItemAmountMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionItemAmount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransactionItemAmount> transactionItemAmountList = transactionItemAmountRepository.findAll();
        assertThat(transactionItemAmountList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemAmountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTransactionItemAmount() throws Exception {
        // Initialize the database
        transactionItemAmount = transactionItemAmountRepository.saveAndFlush(transactionItemAmount);
        transactionItemAmountSearchRepository.save(transactionItemAmount);

        // Search the transactionItemAmount
        restTransactionItemAmountMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transactionItemAmount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionItemAmount.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionItemAmount").value(hasItem(sameNumber(DEFAULT_TRANSACTION_ITEM_AMOUNT))));
    }
}

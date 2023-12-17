package io.github.calvary.web.rest;

import static io.github.calvary.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.SalesReceiptProposal;
import io.github.calvary.repository.SalesReceiptProposalRepository;
import io.github.calvary.repository.search.SalesReceiptProposalSearchRepository;
import io.github.calvary.service.criteria.SalesReceiptProposalCriteria;
import io.github.calvary.service.dto.SalesReceiptProposalDTO;
import io.github.calvary.service.mapper.SalesReceiptProposalMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link SalesReceiptProposalResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SalesReceiptProposalResourceIT {

    private static final ZonedDateTime DEFAULT_TIME_OF_POSTING = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TIME_OF_POSTING = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_TIME_OF_POSTING = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final UUID DEFAULT_POSTING_IDENTIFIER = UUID.randomUUID();
    private static final UUID UPDATED_POSTING_IDENTIFIER = UUID.randomUUID();

    private static final Integer DEFAULT_NUMBER_OF_RECEIPTS_POSTED = 1;
    private static final Integer UPDATED_NUMBER_OF_RECEIPTS_POSTED = 2;
    private static final Integer SMALLER_NUMBER_OF_RECEIPTS_POSTED = 1 - 1;

    private static final String ENTITY_API_URL = "/api/sales-receipt-proposals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/sales-receipt-proposals";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SalesReceiptProposalRepository salesReceiptProposalRepository;

    @Autowired
    private SalesReceiptProposalMapper salesReceiptProposalMapper;

    @Autowired
    private SalesReceiptProposalSearchRepository salesReceiptProposalSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalesReceiptProposalMockMvc;

    private SalesReceiptProposal salesReceiptProposal;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesReceiptProposal createEntity(EntityManager em) {
        SalesReceiptProposal salesReceiptProposal = new SalesReceiptProposal()
            .timeOfPosting(DEFAULT_TIME_OF_POSTING)
            .postingIdentifier(DEFAULT_POSTING_IDENTIFIER)
            .numberOfReceiptsPosted(DEFAULT_NUMBER_OF_RECEIPTS_POSTED);
        return salesReceiptProposal;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesReceiptProposal createUpdatedEntity(EntityManager em) {
        SalesReceiptProposal salesReceiptProposal = new SalesReceiptProposal()
            .timeOfPosting(UPDATED_TIME_OF_POSTING)
            .postingIdentifier(UPDATED_POSTING_IDENTIFIER)
            .numberOfReceiptsPosted(UPDATED_NUMBER_OF_RECEIPTS_POSTED);
        return salesReceiptProposal;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        salesReceiptProposalSearchRepository.deleteAll();
        assertThat(salesReceiptProposalSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        salesReceiptProposal = createEntity(em);
    }

    @Test
    @Transactional
    void createSalesReceiptProposal() throws Exception {
        int databaseSizeBeforeCreate = salesReceiptProposalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        // Create the SalesReceiptProposal
        SalesReceiptProposalDTO salesReceiptProposalDTO = salesReceiptProposalMapper.toDto(salesReceiptProposal);
        restSalesReceiptProposalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptProposalDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SalesReceiptProposal in the database
        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SalesReceiptProposal testSalesReceiptProposal = salesReceiptProposalList.get(salesReceiptProposalList.size() - 1);
        assertThat(testSalesReceiptProposal.getTimeOfPosting()).isEqualTo(DEFAULT_TIME_OF_POSTING);
        assertThat(testSalesReceiptProposal.getPostingIdentifier()).isEqualTo(DEFAULT_POSTING_IDENTIFIER);
        assertThat(testSalesReceiptProposal.getNumberOfReceiptsPosted()).isEqualTo(DEFAULT_NUMBER_OF_RECEIPTS_POSTED);
    }

    @Test
    @Transactional
    void createSalesReceiptProposalWithExistingId() throws Exception {
        // Create the SalesReceiptProposal with an existing ID
        salesReceiptProposal.setId(1L);
        SalesReceiptProposalDTO salesReceiptProposalDTO = salesReceiptProposalMapper.toDto(salesReceiptProposal);

        int databaseSizeBeforeCreate = salesReceiptProposalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalesReceiptProposalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptProposalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptProposal in the database
        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTimeOfPostingIsRequired() throws Exception {
        int databaseSizeBeforeTest = salesReceiptProposalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        // set the field null
        salesReceiptProposal.setTimeOfPosting(null);

        // Create the SalesReceiptProposal, which fails.
        SalesReceiptProposalDTO salesReceiptProposalDTO = salesReceiptProposalMapper.toDto(salesReceiptProposal);

        restSalesReceiptProposalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptProposalDTO))
            )
            .andExpect(status().isBadRequest());

        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPostingIdentifierIsRequired() throws Exception {
        int databaseSizeBeforeTest = salesReceiptProposalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        // set the field null
        salesReceiptProposal.setPostingIdentifier(null);

        // Create the SalesReceiptProposal, which fails.
        SalesReceiptProposalDTO salesReceiptProposalDTO = salesReceiptProposalMapper.toDto(salesReceiptProposal);

        restSalesReceiptProposalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptProposalDTO))
            )
            .andExpect(status().isBadRequest());

        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposals() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList
        restSalesReceiptProposalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceiptProposal.getId().intValue())))
            .andExpect(jsonPath("$.[*].timeOfPosting").value(hasItem(sameInstant(DEFAULT_TIME_OF_POSTING))))
            .andExpect(jsonPath("$.[*].postingIdentifier").value(hasItem(DEFAULT_POSTING_IDENTIFIER.toString())))
            .andExpect(jsonPath("$.[*].numberOfReceiptsPosted").value(hasItem(DEFAULT_NUMBER_OF_RECEIPTS_POSTED)));
    }

    @Test
    @Transactional
    void getSalesReceiptProposal() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get the salesReceiptProposal
        restSalesReceiptProposalMockMvc
            .perform(get(ENTITY_API_URL_ID, salesReceiptProposal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(salesReceiptProposal.getId().intValue()))
            .andExpect(jsonPath("$.timeOfPosting").value(sameInstant(DEFAULT_TIME_OF_POSTING)))
            .andExpect(jsonPath("$.postingIdentifier").value(DEFAULT_POSTING_IDENTIFIER.toString()))
            .andExpect(jsonPath("$.numberOfReceiptsPosted").value(DEFAULT_NUMBER_OF_RECEIPTS_POSTED));
    }

    @Test
    @Transactional
    void getSalesReceiptProposalsByIdFiltering() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        Long id = salesReceiptProposal.getId();

        defaultSalesReceiptProposalShouldBeFound("id.equals=" + id);
        defaultSalesReceiptProposalShouldNotBeFound("id.notEquals=" + id);

        defaultSalesReceiptProposalShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSalesReceiptProposalShouldNotBeFound("id.greaterThan=" + id);

        defaultSalesReceiptProposalShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSalesReceiptProposalShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByTimeOfPostingIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where timeOfPosting equals to DEFAULT_TIME_OF_POSTING
        defaultSalesReceiptProposalShouldBeFound("timeOfPosting.equals=" + DEFAULT_TIME_OF_POSTING);

        // Get all the salesReceiptProposalList where timeOfPosting equals to UPDATED_TIME_OF_POSTING
        defaultSalesReceiptProposalShouldNotBeFound("timeOfPosting.equals=" + UPDATED_TIME_OF_POSTING);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByTimeOfPostingIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where timeOfPosting in DEFAULT_TIME_OF_POSTING or UPDATED_TIME_OF_POSTING
        defaultSalesReceiptProposalShouldBeFound("timeOfPosting.in=" + DEFAULT_TIME_OF_POSTING + "," + UPDATED_TIME_OF_POSTING);

        // Get all the salesReceiptProposalList where timeOfPosting equals to UPDATED_TIME_OF_POSTING
        defaultSalesReceiptProposalShouldNotBeFound("timeOfPosting.in=" + UPDATED_TIME_OF_POSTING);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByTimeOfPostingIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where timeOfPosting is not null
        defaultSalesReceiptProposalShouldBeFound("timeOfPosting.specified=true");

        // Get all the salesReceiptProposalList where timeOfPosting is null
        defaultSalesReceiptProposalShouldNotBeFound("timeOfPosting.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByTimeOfPostingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where timeOfPosting is greater than or equal to DEFAULT_TIME_OF_POSTING
        defaultSalesReceiptProposalShouldBeFound("timeOfPosting.greaterThanOrEqual=" + DEFAULT_TIME_OF_POSTING);

        // Get all the salesReceiptProposalList where timeOfPosting is greater than or equal to UPDATED_TIME_OF_POSTING
        defaultSalesReceiptProposalShouldNotBeFound("timeOfPosting.greaterThanOrEqual=" + UPDATED_TIME_OF_POSTING);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByTimeOfPostingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where timeOfPosting is less than or equal to DEFAULT_TIME_OF_POSTING
        defaultSalesReceiptProposalShouldBeFound("timeOfPosting.lessThanOrEqual=" + DEFAULT_TIME_OF_POSTING);

        // Get all the salesReceiptProposalList where timeOfPosting is less than or equal to SMALLER_TIME_OF_POSTING
        defaultSalesReceiptProposalShouldNotBeFound("timeOfPosting.lessThanOrEqual=" + SMALLER_TIME_OF_POSTING);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByTimeOfPostingIsLessThanSomething() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where timeOfPosting is less than DEFAULT_TIME_OF_POSTING
        defaultSalesReceiptProposalShouldNotBeFound("timeOfPosting.lessThan=" + DEFAULT_TIME_OF_POSTING);

        // Get all the salesReceiptProposalList where timeOfPosting is less than UPDATED_TIME_OF_POSTING
        defaultSalesReceiptProposalShouldBeFound("timeOfPosting.lessThan=" + UPDATED_TIME_OF_POSTING);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByTimeOfPostingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where timeOfPosting is greater than DEFAULT_TIME_OF_POSTING
        defaultSalesReceiptProposalShouldNotBeFound("timeOfPosting.greaterThan=" + DEFAULT_TIME_OF_POSTING);

        // Get all the salesReceiptProposalList where timeOfPosting is greater than SMALLER_TIME_OF_POSTING
        defaultSalesReceiptProposalShouldBeFound("timeOfPosting.greaterThan=" + SMALLER_TIME_OF_POSTING);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByPostingIdentifierIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where postingIdentifier equals to DEFAULT_POSTING_IDENTIFIER
        defaultSalesReceiptProposalShouldBeFound("postingIdentifier.equals=" + DEFAULT_POSTING_IDENTIFIER);

        // Get all the salesReceiptProposalList where postingIdentifier equals to UPDATED_POSTING_IDENTIFIER
        defaultSalesReceiptProposalShouldNotBeFound("postingIdentifier.equals=" + UPDATED_POSTING_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByPostingIdentifierIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where postingIdentifier in DEFAULT_POSTING_IDENTIFIER or UPDATED_POSTING_IDENTIFIER
        defaultSalesReceiptProposalShouldBeFound("postingIdentifier.in=" + DEFAULT_POSTING_IDENTIFIER + "," + UPDATED_POSTING_IDENTIFIER);

        // Get all the salesReceiptProposalList where postingIdentifier equals to UPDATED_POSTING_IDENTIFIER
        defaultSalesReceiptProposalShouldNotBeFound("postingIdentifier.in=" + UPDATED_POSTING_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByPostingIdentifierIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where postingIdentifier is not null
        defaultSalesReceiptProposalShouldBeFound("postingIdentifier.specified=true");

        // Get all the salesReceiptProposalList where postingIdentifier is null
        defaultSalesReceiptProposalShouldNotBeFound("postingIdentifier.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByNumberOfReceiptsPostedIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted equals to DEFAULT_NUMBER_OF_RECEIPTS_POSTED
        defaultSalesReceiptProposalShouldBeFound("numberOfReceiptsPosted.equals=" + DEFAULT_NUMBER_OF_RECEIPTS_POSTED);

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted equals to UPDATED_NUMBER_OF_RECEIPTS_POSTED
        defaultSalesReceiptProposalShouldNotBeFound("numberOfReceiptsPosted.equals=" + UPDATED_NUMBER_OF_RECEIPTS_POSTED);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByNumberOfReceiptsPostedIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted in DEFAULT_NUMBER_OF_RECEIPTS_POSTED or UPDATED_NUMBER_OF_RECEIPTS_POSTED
        defaultSalesReceiptProposalShouldBeFound(
            "numberOfReceiptsPosted.in=" + DEFAULT_NUMBER_OF_RECEIPTS_POSTED + "," + UPDATED_NUMBER_OF_RECEIPTS_POSTED
        );

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted equals to UPDATED_NUMBER_OF_RECEIPTS_POSTED
        defaultSalesReceiptProposalShouldNotBeFound("numberOfReceiptsPosted.in=" + UPDATED_NUMBER_OF_RECEIPTS_POSTED);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByNumberOfReceiptsPostedIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted is not null
        defaultSalesReceiptProposalShouldBeFound("numberOfReceiptsPosted.specified=true");

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted is null
        defaultSalesReceiptProposalShouldNotBeFound("numberOfReceiptsPosted.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByNumberOfReceiptsPostedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted is greater than or equal to DEFAULT_NUMBER_OF_RECEIPTS_POSTED
        defaultSalesReceiptProposalShouldBeFound("numberOfReceiptsPosted.greaterThanOrEqual=" + DEFAULT_NUMBER_OF_RECEIPTS_POSTED);

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted is greater than or equal to UPDATED_NUMBER_OF_RECEIPTS_POSTED
        defaultSalesReceiptProposalShouldNotBeFound("numberOfReceiptsPosted.greaterThanOrEqual=" + UPDATED_NUMBER_OF_RECEIPTS_POSTED);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByNumberOfReceiptsPostedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted is less than or equal to DEFAULT_NUMBER_OF_RECEIPTS_POSTED
        defaultSalesReceiptProposalShouldBeFound("numberOfReceiptsPosted.lessThanOrEqual=" + DEFAULT_NUMBER_OF_RECEIPTS_POSTED);

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted is less than or equal to SMALLER_NUMBER_OF_RECEIPTS_POSTED
        defaultSalesReceiptProposalShouldNotBeFound("numberOfReceiptsPosted.lessThanOrEqual=" + SMALLER_NUMBER_OF_RECEIPTS_POSTED);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByNumberOfReceiptsPostedIsLessThanSomething() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted is less than DEFAULT_NUMBER_OF_RECEIPTS_POSTED
        defaultSalesReceiptProposalShouldNotBeFound("numberOfReceiptsPosted.lessThan=" + DEFAULT_NUMBER_OF_RECEIPTS_POSTED);

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted is less than UPDATED_NUMBER_OF_RECEIPTS_POSTED
        defaultSalesReceiptProposalShouldBeFound("numberOfReceiptsPosted.lessThan=" + UPDATED_NUMBER_OF_RECEIPTS_POSTED);
    }

    @Test
    @Transactional
    void getAllSalesReceiptProposalsByNumberOfReceiptsPostedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted is greater than DEFAULT_NUMBER_OF_RECEIPTS_POSTED
        defaultSalesReceiptProposalShouldNotBeFound("numberOfReceiptsPosted.greaterThan=" + DEFAULT_NUMBER_OF_RECEIPTS_POSTED);

        // Get all the salesReceiptProposalList where numberOfReceiptsPosted is greater than SMALLER_NUMBER_OF_RECEIPTS_POSTED
        defaultSalesReceiptProposalShouldBeFound("numberOfReceiptsPosted.greaterThan=" + SMALLER_NUMBER_OF_RECEIPTS_POSTED);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSalesReceiptProposalShouldBeFound(String filter) throws Exception {
        restSalesReceiptProposalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceiptProposal.getId().intValue())))
            .andExpect(jsonPath("$.[*].timeOfPosting").value(hasItem(sameInstant(DEFAULT_TIME_OF_POSTING))))
            .andExpect(jsonPath("$.[*].postingIdentifier").value(hasItem(DEFAULT_POSTING_IDENTIFIER.toString())))
            .andExpect(jsonPath("$.[*].numberOfReceiptsPosted").value(hasItem(DEFAULT_NUMBER_OF_RECEIPTS_POSTED)));

        // Check, that the count call also returns 1
        restSalesReceiptProposalMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSalesReceiptProposalShouldNotBeFound(String filter) throws Exception {
        restSalesReceiptProposalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSalesReceiptProposalMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSalesReceiptProposal() throws Exception {
        // Get the salesReceiptProposal
        restSalesReceiptProposalMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSalesReceiptProposal() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        int databaseSizeBeforeUpdate = salesReceiptProposalRepository.findAll().size();
        salesReceiptProposalSearchRepository.save(salesReceiptProposal);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());

        // Update the salesReceiptProposal
        SalesReceiptProposal updatedSalesReceiptProposal = salesReceiptProposalRepository.findById(salesReceiptProposal.getId()).get();
        // Disconnect from session so that the updates on updatedSalesReceiptProposal are not directly saved in db
        em.detach(updatedSalesReceiptProposal);
        updatedSalesReceiptProposal
            .timeOfPosting(UPDATED_TIME_OF_POSTING)
            .postingIdentifier(UPDATED_POSTING_IDENTIFIER)
            .numberOfReceiptsPosted(UPDATED_NUMBER_OF_RECEIPTS_POSTED);
        SalesReceiptProposalDTO salesReceiptProposalDTO = salesReceiptProposalMapper.toDto(updatedSalesReceiptProposal);

        restSalesReceiptProposalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesReceiptProposalDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptProposalDTO))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceiptProposal in the database
        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeUpdate);
        SalesReceiptProposal testSalesReceiptProposal = salesReceiptProposalList.get(salesReceiptProposalList.size() - 1);
        assertThat(testSalesReceiptProposal.getTimeOfPosting()).isEqualTo(UPDATED_TIME_OF_POSTING);
        assertThat(testSalesReceiptProposal.getPostingIdentifier()).isEqualTo(UPDATED_POSTING_IDENTIFIER);
        assertThat(testSalesReceiptProposal.getNumberOfReceiptsPosted()).isEqualTo(UPDATED_NUMBER_OF_RECEIPTS_POSTED);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SalesReceiptProposal> salesReceiptProposalSearchList = IterableUtils.toList(
                    salesReceiptProposalSearchRepository.findAll()
                );
                SalesReceiptProposal testSalesReceiptProposalSearch = salesReceiptProposalSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSalesReceiptProposalSearch.getTimeOfPosting()).isEqualTo(UPDATED_TIME_OF_POSTING);
                assertThat(testSalesReceiptProposalSearch.getPostingIdentifier()).isEqualTo(UPDATED_POSTING_IDENTIFIER);
                assertThat(testSalesReceiptProposalSearch.getNumberOfReceiptsPosted()).isEqualTo(UPDATED_NUMBER_OF_RECEIPTS_POSTED);
            });
    }

    @Test
    @Transactional
    void putNonExistingSalesReceiptProposal() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptProposalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        salesReceiptProposal.setId(count.incrementAndGet());

        // Create the SalesReceiptProposal
        SalesReceiptProposalDTO salesReceiptProposalDTO = salesReceiptProposalMapper.toDto(salesReceiptProposal);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesReceiptProposalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesReceiptProposalDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptProposalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptProposal in the database
        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSalesReceiptProposal() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptProposalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        salesReceiptProposal.setId(count.incrementAndGet());

        // Create the SalesReceiptProposal
        SalesReceiptProposalDTO salesReceiptProposalDTO = salesReceiptProposalMapper.toDto(salesReceiptProposal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptProposalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptProposalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptProposal in the database
        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSalesReceiptProposal() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptProposalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        salesReceiptProposal.setId(count.incrementAndGet());

        // Create the SalesReceiptProposal
        SalesReceiptProposalDTO salesReceiptProposalDTO = salesReceiptProposalMapper.toDto(salesReceiptProposal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptProposalMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptProposalDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesReceiptProposal in the database
        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSalesReceiptProposalWithPatch() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        int databaseSizeBeforeUpdate = salesReceiptProposalRepository.findAll().size();

        // Update the salesReceiptProposal using partial update
        SalesReceiptProposal partialUpdatedSalesReceiptProposal = new SalesReceiptProposal();
        partialUpdatedSalesReceiptProposal.setId(salesReceiptProposal.getId());

        partialUpdatedSalesReceiptProposal.timeOfPosting(UPDATED_TIME_OF_POSTING).postingIdentifier(UPDATED_POSTING_IDENTIFIER);

        restSalesReceiptProposalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesReceiptProposal.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesReceiptProposal))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceiptProposal in the database
        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeUpdate);
        SalesReceiptProposal testSalesReceiptProposal = salesReceiptProposalList.get(salesReceiptProposalList.size() - 1);
        assertThat(testSalesReceiptProposal.getTimeOfPosting()).isEqualTo(UPDATED_TIME_OF_POSTING);
        assertThat(testSalesReceiptProposal.getPostingIdentifier()).isEqualTo(UPDATED_POSTING_IDENTIFIER);
        assertThat(testSalesReceiptProposal.getNumberOfReceiptsPosted()).isEqualTo(DEFAULT_NUMBER_OF_RECEIPTS_POSTED);
    }

    @Test
    @Transactional
    void fullUpdateSalesReceiptProposalWithPatch() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);

        int databaseSizeBeforeUpdate = salesReceiptProposalRepository.findAll().size();

        // Update the salesReceiptProposal using partial update
        SalesReceiptProposal partialUpdatedSalesReceiptProposal = new SalesReceiptProposal();
        partialUpdatedSalesReceiptProposal.setId(salesReceiptProposal.getId());

        partialUpdatedSalesReceiptProposal
            .timeOfPosting(UPDATED_TIME_OF_POSTING)
            .postingIdentifier(UPDATED_POSTING_IDENTIFIER)
            .numberOfReceiptsPosted(UPDATED_NUMBER_OF_RECEIPTS_POSTED);

        restSalesReceiptProposalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesReceiptProposal.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesReceiptProposal))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceiptProposal in the database
        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeUpdate);
        SalesReceiptProposal testSalesReceiptProposal = salesReceiptProposalList.get(salesReceiptProposalList.size() - 1);
        assertThat(testSalesReceiptProposal.getTimeOfPosting()).isEqualTo(UPDATED_TIME_OF_POSTING);
        assertThat(testSalesReceiptProposal.getPostingIdentifier()).isEqualTo(UPDATED_POSTING_IDENTIFIER);
        assertThat(testSalesReceiptProposal.getNumberOfReceiptsPosted()).isEqualTo(UPDATED_NUMBER_OF_RECEIPTS_POSTED);
    }

    @Test
    @Transactional
    void patchNonExistingSalesReceiptProposal() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptProposalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        salesReceiptProposal.setId(count.incrementAndGet());

        // Create the SalesReceiptProposal
        SalesReceiptProposalDTO salesReceiptProposalDTO = salesReceiptProposalMapper.toDto(salesReceiptProposal);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesReceiptProposalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salesReceiptProposalDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptProposalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptProposal in the database
        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSalesReceiptProposal() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptProposalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        salesReceiptProposal.setId(count.incrementAndGet());

        // Create the SalesReceiptProposal
        SalesReceiptProposalDTO salesReceiptProposalDTO = salesReceiptProposalMapper.toDto(salesReceiptProposal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptProposalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptProposalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptProposal in the database
        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSalesReceiptProposal() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptProposalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        salesReceiptProposal.setId(count.incrementAndGet());

        // Create the SalesReceiptProposal
        SalesReceiptProposalDTO salesReceiptProposalDTO = salesReceiptProposalMapper.toDto(salesReceiptProposal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptProposalMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptProposalDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesReceiptProposal in the database
        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSalesReceiptProposal() throws Exception {
        // Initialize the database
        salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);
        salesReceiptProposalRepository.save(salesReceiptProposal);
        salesReceiptProposalSearchRepository.save(salesReceiptProposal);

        int databaseSizeBeforeDelete = salesReceiptProposalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the salesReceiptProposal
        restSalesReceiptProposalMockMvc
            .perform(delete(ENTITY_API_URL_ID, salesReceiptProposal.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SalesReceiptProposal> salesReceiptProposalList = salesReceiptProposalRepository.findAll();
        assertThat(salesReceiptProposalList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptProposalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSalesReceiptProposal() throws Exception {
        // Initialize the database
        salesReceiptProposal = salesReceiptProposalRepository.saveAndFlush(salesReceiptProposal);
        salesReceiptProposalSearchRepository.save(salesReceiptProposal);

        // Search the salesReceiptProposal
        restSalesReceiptProposalMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + salesReceiptProposal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceiptProposal.getId().intValue())))
            .andExpect(jsonPath("$.[*].timeOfPosting").value(hasItem(sameInstant(DEFAULT_TIME_OF_POSTING))))
            .andExpect(jsonPath("$.[*].postingIdentifier").value(hasItem(DEFAULT_POSTING_IDENTIFIER.toString())))
            .andExpect(jsonPath("$.[*].numberOfReceiptsPosted").value(hasItem(DEFAULT_NUMBER_OF_RECEIPTS_POSTED)));
    }
}

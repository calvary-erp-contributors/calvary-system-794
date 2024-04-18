package io.github.calvary.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.Dealer;
import io.github.calvary.domain.DealerType;
import io.github.calvary.domain.SalesReceiptEmailPersona;
import io.github.calvary.repository.DealerRepository;
import io.github.calvary.repository.search.DealerSearchRepository;
import io.github.calvary.service.DealerService;
import io.github.calvary.service.criteria.DealerCriteria;
import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.mapper.DealerMapper;
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
 * Integration tests for the {@link DealerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DealerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MAIN_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_MAIN_EMAIL = "BBBBBBBBBB";

    private static final UUID DEFAULT_DEALER_REFERENCE = UUID.randomUUID();
    private static final UUID UPDATED_DEALER_REFERENCE = UUID.randomUUID();

    private static final String ENTITY_API_URL = "/api/dealers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/dealers";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DealerRepository dealerRepository;

    @Mock
    private DealerRepository dealerRepositoryMock;

    @Autowired
    private DealerMapper dealerMapper;

    @Mock
    private DealerService dealerServiceMock;

    @Autowired
    private DealerSearchRepository dealerSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDealerMockMvc;

    private Dealer dealer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dealer createEntity(EntityManager em) {
        Dealer dealer = new Dealer().name(DEFAULT_NAME).mainEmail(DEFAULT_MAIN_EMAIL).dealerReference(DEFAULT_DEALER_REFERENCE);
        // Add required entity
        DealerType dealerType;
        if (TestUtil.findAll(em, DealerType.class).isEmpty()) {
            dealerType = DealerTypeResourceIT.createEntity(em);
            em.persist(dealerType);
            em.flush();
        } else {
            dealerType = TestUtil.findAll(em, DealerType.class).get(0);
        }
        dealer.setDealerType(dealerType);
        return dealer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dealer createUpdatedEntity(EntityManager em) {
        Dealer dealer = new Dealer().name(UPDATED_NAME).mainEmail(UPDATED_MAIN_EMAIL).dealerReference(UPDATED_DEALER_REFERENCE);
        // Add required entity
        DealerType dealerType;
        if (TestUtil.findAll(em, DealerType.class).isEmpty()) {
            dealerType = DealerTypeResourceIT.createUpdatedEntity(em);
            em.persist(dealerType);
            em.flush();
        } else {
            dealerType = TestUtil.findAll(em, DealerType.class).get(0);
        }
        dealer.setDealerType(dealerType);
        return dealer;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        dealerSearchRepository.deleteAll();
        assertThat(dealerSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        dealer = createEntity(em);
    }

    @Test
    @Transactional
    void createDealer() throws Exception {
        int databaseSizeBeforeCreate = dealerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        // Create the Dealer
        DealerDTO dealerDTO = dealerMapper.toDto(dealer);
        restDealerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dealerDTO)))
            .andExpect(status().isCreated());

        // Validate the Dealer in the database
        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Dealer testDealer = dealerList.get(dealerList.size() - 1);
        assertThat(testDealer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDealer.getMainEmail()).isEqualTo(DEFAULT_MAIN_EMAIL);
        assertThat(testDealer.getDealerReference()).isEqualTo(DEFAULT_DEALER_REFERENCE);
    }

    @Test
    @Transactional
    void createDealerWithExistingId() throws Exception {
        // Create the Dealer with an existing ID
        dealer.setId(1L);
        DealerDTO dealerDTO = dealerMapper.toDto(dealer);

        int databaseSizeBeforeCreate = dealerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDealerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dealerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Dealer in the database
        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dealerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        // set the field null
        dealer.setName(null);

        // Create the Dealer, which fails.
        DealerDTO dealerDTO = dealerMapper.toDto(dealer);

        restDealerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dealerDTO)))
            .andExpect(status().isBadRequest());

        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDealers() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList
        restDealerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dealer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].mainEmail").value(hasItem(DEFAULT_MAIN_EMAIL)))
            .andExpect(jsonPath("$.[*].dealerReference").value(hasItem(DEFAULT_DEALER_REFERENCE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDealersWithEagerRelationshipsIsEnabled() throws Exception {
        when(dealerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDealerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(dealerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDealersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(dealerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDealerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(dealerRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDealer() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get the dealer
        restDealerMockMvc
            .perform(get(ENTITY_API_URL_ID, dealer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dealer.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.mainEmail").value(DEFAULT_MAIN_EMAIL))
            .andExpect(jsonPath("$.dealerReference").value(DEFAULT_DEALER_REFERENCE.toString()));
    }

    @Test
    @Transactional
    void getDealersByIdFiltering() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        Long id = dealer.getId();

        defaultDealerShouldBeFound("id.equals=" + id);
        defaultDealerShouldNotBeFound("id.notEquals=" + id);

        defaultDealerShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDealerShouldNotBeFound("id.greaterThan=" + id);

        defaultDealerShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDealerShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDealersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where name equals to DEFAULT_NAME
        defaultDealerShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the dealerList where name equals to UPDATED_NAME
        defaultDealerShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDealersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where name in DEFAULT_NAME or UPDATED_NAME
        defaultDealerShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the dealerList where name equals to UPDATED_NAME
        defaultDealerShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDealersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where name is not null
        defaultDealerShouldBeFound("name.specified=true");

        // Get all the dealerList where name is null
        defaultDealerShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllDealersByNameContainsSomething() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where name contains DEFAULT_NAME
        defaultDealerShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the dealerList where name contains UPDATED_NAME
        defaultDealerShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDealersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where name does not contain DEFAULT_NAME
        defaultDealerShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the dealerList where name does not contain UPDATED_NAME
        defaultDealerShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDealersByMainEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where mainEmail equals to DEFAULT_MAIN_EMAIL
        defaultDealerShouldBeFound("mainEmail.equals=" + DEFAULT_MAIN_EMAIL);

        // Get all the dealerList where mainEmail equals to UPDATED_MAIN_EMAIL
        defaultDealerShouldNotBeFound("mainEmail.equals=" + UPDATED_MAIN_EMAIL);
    }

    @Test
    @Transactional
    void getAllDealersByMainEmailIsInShouldWork() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where mainEmail in DEFAULT_MAIN_EMAIL or UPDATED_MAIN_EMAIL
        defaultDealerShouldBeFound("mainEmail.in=" + DEFAULT_MAIN_EMAIL + "," + UPDATED_MAIN_EMAIL);

        // Get all the dealerList where mainEmail equals to UPDATED_MAIN_EMAIL
        defaultDealerShouldNotBeFound("mainEmail.in=" + UPDATED_MAIN_EMAIL);
    }

    @Test
    @Transactional
    void getAllDealersByMainEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where mainEmail is not null
        defaultDealerShouldBeFound("mainEmail.specified=true");

        // Get all the dealerList where mainEmail is null
        defaultDealerShouldNotBeFound("mainEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllDealersByMainEmailContainsSomething() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where mainEmail contains DEFAULT_MAIN_EMAIL
        defaultDealerShouldBeFound("mainEmail.contains=" + DEFAULT_MAIN_EMAIL);

        // Get all the dealerList where mainEmail contains UPDATED_MAIN_EMAIL
        defaultDealerShouldNotBeFound("mainEmail.contains=" + UPDATED_MAIN_EMAIL);
    }

    @Test
    @Transactional
    void getAllDealersByMainEmailNotContainsSomething() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where mainEmail does not contain DEFAULT_MAIN_EMAIL
        defaultDealerShouldNotBeFound("mainEmail.doesNotContain=" + DEFAULT_MAIN_EMAIL);

        // Get all the dealerList where mainEmail does not contain UPDATED_MAIN_EMAIL
        defaultDealerShouldBeFound("mainEmail.doesNotContain=" + UPDATED_MAIN_EMAIL);
    }

    @Test
    @Transactional
    void getAllDealersByDealerReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where dealerReference equals to DEFAULT_DEALER_REFERENCE
        defaultDealerShouldBeFound("dealerReference.equals=" + DEFAULT_DEALER_REFERENCE);

        // Get all the dealerList where dealerReference equals to UPDATED_DEALER_REFERENCE
        defaultDealerShouldNotBeFound("dealerReference.equals=" + UPDATED_DEALER_REFERENCE);
    }

    @Test
    @Transactional
    void getAllDealersByDealerReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where dealerReference in DEFAULT_DEALER_REFERENCE or UPDATED_DEALER_REFERENCE
        defaultDealerShouldBeFound("dealerReference.in=" + DEFAULT_DEALER_REFERENCE + "," + UPDATED_DEALER_REFERENCE);

        // Get all the dealerList where dealerReference equals to UPDATED_DEALER_REFERENCE
        defaultDealerShouldNotBeFound("dealerReference.in=" + UPDATED_DEALER_REFERENCE);
    }

    @Test
    @Transactional
    void getAllDealersByDealerReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        // Get all the dealerList where dealerReference is not null
        defaultDealerShouldBeFound("dealerReference.specified=true");

        // Get all the dealerList where dealerReference is null
        defaultDealerShouldNotBeFound("dealerReference.specified=false");
    }

    @Test
    @Transactional
    void getAllDealersByDealerTypeIsEqualToSomething() throws Exception {
        DealerType dealerType;
        if (TestUtil.findAll(em, DealerType.class).isEmpty()) {
            dealerRepository.saveAndFlush(dealer);
            dealerType = DealerTypeResourceIT.createEntity(em);
        } else {
            dealerType = TestUtil.findAll(em, DealerType.class).get(0);
        }
        em.persist(dealerType);
        em.flush();
        dealer.setDealerType(dealerType);
        dealerRepository.saveAndFlush(dealer);
        Long dealerTypeId = dealerType.getId();

        // Get all the dealerList where dealerType equals to dealerTypeId
        defaultDealerShouldBeFound("dealerTypeId.equals=" + dealerTypeId);

        // Get all the dealerList where dealerType equals to (dealerTypeId + 1)
        defaultDealerShouldNotBeFound("dealerTypeId.equals=" + (dealerTypeId + 1));
    }

    @Test
    @Transactional
    void getAllDealersBySalesReceiptEmailPersonaIsEqualToSomething() throws Exception {
        SalesReceiptEmailPersona salesReceiptEmailPersona;
        if (TestUtil.findAll(em, SalesReceiptEmailPersona.class).isEmpty()) {
            dealerRepository.saveAndFlush(dealer);
            salesReceiptEmailPersona = SalesReceiptEmailPersonaResourceIT.createEntity(em);
        } else {
            salesReceiptEmailPersona = TestUtil.findAll(em, SalesReceiptEmailPersona.class).get(0);
        }
        em.persist(salesReceiptEmailPersona);
        em.flush();
        dealer.addSalesReceiptEmailPersona(salesReceiptEmailPersona);
        dealerRepository.saveAndFlush(dealer);
        Long salesReceiptEmailPersonaId = salesReceiptEmailPersona.getId();

        // Get all the dealerList where salesReceiptEmailPersona equals to salesReceiptEmailPersonaId
        defaultDealerShouldBeFound("salesReceiptEmailPersonaId.equals=" + salesReceiptEmailPersonaId);

        // Get all the dealerList where salesReceiptEmailPersona equals to (salesReceiptEmailPersonaId + 1)
        defaultDealerShouldNotBeFound("salesReceiptEmailPersonaId.equals=" + (salesReceiptEmailPersonaId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDealerShouldBeFound(String filter) throws Exception {
        restDealerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dealer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].mainEmail").value(hasItem(DEFAULT_MAIN_EMAIL)))
            .andExpect(jsonPath("$.[*].dealerReference").value(hasItem(DEFAULT_DEALER_REFERENCE.toString())));

        // Check, that the count call also returns 1
        restDealerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDealerShouldNotBeFound(String filter) throws Exception {
        restDealerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDealerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDealer() throws Exception {
        // Get the dealer
        restDealerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDealer() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        int databaseSizeBeforeUpdate = dealerRepository.findAll().size();
        dealerSearchRepository.save(dealer);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerSearchRepository.findAll());

        // Update the dealer
        Dealer updatedDealer = dealerRepository.findById(dealer.getId()).get();
        // Disconnect from session so that the updates on updatedDealer are not directly saved in db
        em.detach(updatedDealer);
        updatedDealer.name(UPDATED_NAME).mainEmail(UPDATED_MAIN_EMAIL).dealerReference(UPDATED_DEALER_REFERENCE);
        DealerDTO dealerDTO = dealerMapper.toDto(updatedDealer);

        restDealerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dealerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dealerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Dealer in the database
        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeUpdate);
        Dealer testDealer = dealerList.get(dealerList.size() - 1);
        assertThat(testDealer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDealer.getMainEmail()).isEqualTo(UPDATED_MAIN_EMAIL);
        assertThat(testDealer.getDealerReference()).isEqualTo(UPDATED_DEALER_REFERENCE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Dealer> dealerSearchList = IterableUtils.toList(dealerSearchRepository.findAll());
                Dealer testDealerSearch = dealerSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testDealerSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testDealerSearch.getMainEmail()).isEqualTo(UPDATED_MAIN_EMAIL);
                assertThat(testDealerSearch.getDealerReference()).isEqualTo(UPDATED_DEALER_REFERENCE);
            });
    }

    @Test
    @Transactional
    void putNonExistingDealer() throws Exception {
        int databaseSizeBeforeUpdate = dealerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        dealer.setId(count.incrementAndGet());

        // Create the Dealer
        DealerDTO dealerDTO = dealerMapper.toDto(dealer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDealerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dealerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dealerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dealer in the database
        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDealer() throws Exception {
        int databaseSizeBeforeUpdate = dealerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        dealer.setId(count.incrementAndGet());

        // Create the Dealer
        DealerDTO dealerDTO = dealerMapper.toDto(dealer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDealerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dealerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dealer in the database
        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDealer() throws Exception {
        int databaseSizeBeforeUpdate = dealerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        dealer.setId(count.incrementAndGet());

        // Create the Dealer
        DealerDTO dealerDTO = dealerMapper.toDto(dealer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDealerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dealerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dealer in the database
        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDealerWithPatch() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        int databaseSizeBeforeUpdate = dealerRepository.findAll().size();

        // Update the dealer using partial update
        Dealer partialUpdatedDealer = new Dealer();
        partialUpdatedDealer.setId(dealer.getId());

        partialUpdatedDealer.mainEmail(UPDATED_MAIN_EMAIL);

        restDealerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDealer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDealer))
            )
            .andExpect(status().isOk());

        // Validate the Dealer in the database
        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeUpdate);
        Dealer testDealer = dealerList.get(dealerList.size() - 1);
        assertThat(testDealer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDealer.getMainEmail()).isEqualTo(UPDATED_MAIN_EMAIL);
        assertThat(testDealer.getDealerReference()).isEqualTo(DEFAULT_DEALER_REFERENCE);
    }

    @Test
    @Transactional
    void fullUpdateDealerWithPatch() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);

        int databaseSizeBeforeUpdate = dealerRepository.findAll().size();

        // Update the dealer using partial update
        Dealer partialUpdatedDealer = new Dealer();
        partialUpdatedDealer.setId(dealer.getId());

        partialUpdatedDealer.name(UPDATED_NAME).mainEmail(UPDATED_MAIN_EMAIL).dealerReference(UPDATED_DEALER_REFERENCE);

        restDealerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDealer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDealer))
            )
            .andExpect(status().isOk());

        // Validate the Dealer in the database
        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeUpdate);
        Dealer testDealer = dealerList.get(dealerList.size() - 1);
        assertThat(testDealer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDealer.getMainEmail()).isEqualTo(UPDATED_MAIN_EMAIL);
        assertThat(testDealer.getDealerReference()).isEqualTo(UPDATED_DEALER_REFERENCE);
    }

    @Test
    @Transactional
    void patchNonExistingDealer() throws Exception {
        int databaseSizeBeforeUpdate = dealerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        dealer.setId(count.incrementAndGet());

        // Create the Dealer
        DealerDTO dealerDTO = dealerMapper.toDto(dealer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDealerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dealerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dealerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dealer in the database
        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDealer() throws Exception {
        int databaseSizeBeforeUpdate = dealerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        dealer.setId(count.incrementAndGet());

        // Create the Dealer
        DealerDTO dealerDTO = dealerMapper.toDto(dealer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDealerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dealerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dealer in the database
        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDealer() throws Exception {
        int databaseSizeBeforeUpdate = dealerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        dealer.setId(count.incrementAndGet());

        // Create the Dealer
        DealerDTO dealerDTO = dealerMapper.toDto(dealer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDealerMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dealerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dealer in the database
        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDealer() throws Exception {
        // Initialize the database
        dealerRepository.saveAndFlush(dealer);
        dealerRepository.save(dealer);
        dealerSearchRepository.save(dealer);

        int databaseSizeBeforeDelete = dealerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the dealer
        restDealerMockMvc
            .perform(delete(ENTITY_API_URL_ID, dealer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Dealer> dealerList = dealerRepository.findAll();
        assertThat(dealerList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDealer() throws Exception {
        // Initialize the database
        dealer = dealerRepository.saveAndFlush(dealer);
        dealerSearchRepository.save(dealer);

        // Search the dealer
        restDealerMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + dealer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dealer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].mainEmail").value(hasItem(DEFAULT_MAIN_EMAIL)))
            .andExpect(jsonPath("$.[*].dealerReference").value(hasItem(DEFAULT_DEALER_REFERENCE.toString())));
    }
}

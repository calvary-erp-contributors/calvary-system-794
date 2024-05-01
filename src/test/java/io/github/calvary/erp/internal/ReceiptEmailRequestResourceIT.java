package io.github.calvary.erp.internal;

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

import static io.github.calvary.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.ApplicationUser;
import io.github.calvary.domain.ReceiptEmailRequest;
import io.github.calvary.repository.ReceiptEmailRequestRepository;
import io.github.calvary.repository.search.ReceiptEmailRequestSearchRepository;
import io.github.calvary.service.ReceiptEmailRequestService;
import io.github.calvary.service.dto.ReceiptEmailRequestDTO;
import io.github.calvary.service.mapper.ReceiptEmailRequestMapper;
import io.github.calvary.web.rest.ReceiptEmailRequestResource;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ReceiptEmailRequestResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReceiptEmailRequestResourceIT {

    private static final ZonedDateTime DEFAULT_TIME_OF_REQUISITION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TIME_OF_REQUISITION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_TIME_OF_REQUISITION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_UPLOAD_COMPLETE = false;
    private static final Boolean UPDATED_UPLOAD_COMPLETE = true;

    private static final Integer DEFAULT_NUMBER_OF_UPDATES = 1;
    private static final Integer UPDATED_NUMBER_OF_UPDATES = 2;
    private static final Integer SMALLER_NUMBER_OF_UPDATES = 1 - 1;

    private static final String ENTITY_API_URL = "/api/app/receipt-email-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/app/_search/receipt-email-requests";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReceiptEmailRequestRepository receiptEmailRequestRepository;

    @Mock
    private ReceiptEmailRequestRepository receiptEmailRequestRepositoryMock;

    @Autowired
    private ReceiptEmailRequestMapper receiptEmailRequestMapper;

    @Mock
    private ReceiptEmailRequestService receiptEmailRequestServiceMock;

    @Autowired
    private ReceiptEmailRequestSearchRepository receiptEmailRequestSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReceiptEmailRequestMockMvc;

    private ReceiptEmailRequest receiptEmailRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReceiptEmailRequest createEntity(EntityManager em) {
        ReceiptEmailRequest receiptEmailRequest = new ReceiptEmailRequest()
            .timeOfRequisition(DEFAULT_TIME_OF_REQUISITION)
            .uploadComplete(DEFAULT_UPLOAD_COMPLETE)
            .numberOfUpdates(DEFAULT_NUMBER_OF_UPDATES);
        return receiptEmailRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReceiptEmailRequest createUpdatedEntity(EntityManager em) {
        ReceiptEmailRequest receiptEmailRequest = new ReceiptEmailRequest()
            .timeOfRequisition(UPDATED_TIME_OF_REQUISITION)
            .uploadComplete(UPDATED_UPLOAD_COMPLETE)
            .numberOfUpdates(UPDATED_NUMBER_OF_UPDATES);
        return receiptEmailRequest;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        receiptEmailRequestSearchRepository.deleteAll();
        assertThat(receiptEmailRequestSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        receiptEmailRequest = createEntity(em);
    }

    @Test
    @Transactional
    void createReceiptEmailRequest() throws Exception {
        int databaseSizeBeforeCreate = receiptEmailRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        // Create the ReceiptEmailRequest
        ReceiptEmailRequestDTO receiptEmailRequestDTO = receiptEmailRequestMapper.toDto(receiptEmailRequest);
        restReceiptEmailRequestMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(receiptEmailRequestDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ReceiptEmailRequest in the database
        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ReceiptEmailRequest testReceiptEmailRequest = receiptEmailRequestList.get(receiptEmailRequestList.size() - 1);
        assertThat(testReceiptEmailRequest.getTimeOfRequisition()).isEqualTo(DEFAULT_TIME_OF_REQUISITION);
        assertThat(testReceiptEmailRequest.getUploadComplete()).isEqualTo(DEFAULT_UPLOAD_COMPLETE);
        assertThat(testReceiptEmailRequest.getNumberOfUpdates()).isEqualTo(DEFAULT_NUMBER_OF_UPDATES);
    }

    @Test
    @Transactional
    void createReceiptEmailRequestWithExistingId() throws Exception {
        // Create the ReceiptEmailRequest with an existing ID
        receiptEmailRequest.setId(1L);
        ReceiptEmailRequestDTO receiptEmailRequestDTO = receiptEmailRequestMapper.toDto(receiptEmailRequest);

        int databaseSizeBeforeCreate = receiptEmailRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restReceiptEmailRequestMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(receiptEmailRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceiptEmailRequest in the database
        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTimeOfRequisitionIsRequired() throws Exception {
        int databaseSizeBeforeTest = receiptEmailRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        // set the field null
        receiptEmailRequest.setTimeOfRequisition(null);

        // Create the ReceiptEmailRequest, which fails.
        ReceiptEmailRequestDTO receiptEmailRequestDTO = receiptEmailRequestMapper.toDto(receiptEmailRequest);

        restReceiptEmailRequestMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(receiptEmailRequestDTO))
            )
            .andExpect(status().isBadRequest());

        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequests() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList
        restReceiptEmailRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(receiptEmailRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].timeOfRequisition").value(hasItem(sameInstant(DEFAULT_TIME_OF_REQUISITION))))
            .andExpect(jsonPath("$.[*].uploadComplete").value(hasItem(DEFAULT_UPLOAD_COMPLETE.booleanValue())))
            .andExpect(jsonPath("$.[*].numberOfUpdates").value(hasItem(DEFAULT_NUMBER_OF_UPDATES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReceiptEmailRequestsWithEagerRelationshipsIsEnabled() throws Exception {
        when(receiptEmailRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReceiptEmailRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(receiptEmailRequestServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReceiptEmailRequestsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(receiptEmailRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReceiptEmailRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(receiptEmailRequestRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReceiptEmailRequest() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get the receiptEmailRequest
        restReceiptEmailRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, receiptEmailRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(receiptEmailRequest.getId().intValue()))
            .andExpect(jsonPath("$.timeOfRequisition").value(sameInstant(DEFAULT_TIME_OF_REQUISITION)))
            .andExpect(jsonPath("$.uploadComplete").value(DEFAULT_UPLOAD_COMPLETE.booleanValue()))
            .andExpect(jsonPath("$.numberOfUpdates").value(DEFAULT_NUMBER_OF_UPDATES));
    }

    @Test
    @Transactional
    void getReceiptEmailRequestsByIdFiltering() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        Long id = receiptEmailRequest.getId();

        defaultReceiptEmailRequestShouldBeFound("id.equals=" + id);
        defaultReceiptEmailRequestShouldNotBeFound("id.notEquals=" + id);

        defaultReceiptEmailRequestShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultReceiptEmailRequestShouldNotBeFound("id.greaterThan=" + id);

        defaultReceiptEmailRequestShouldBeFound("id.lessThanOrEqual=" + id);
        defaultReceiptEmailRequestShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByTimeOfRequisitionIsEqualToSomething() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where timeOfRequisition equals to DEFAULT_TIME_OF_REQUISITION
        defaultReceiptEmailRequestShouldBeFound("timeOfRequisition.equals=" + DEFAULT_TIME_OF_REQUISITION);

        // Get all the receiptEmailRequestList where timeOfRequisition equals to UPDATED_TIME_OF_REQUISITION
        defaultReceiptEmailRequestShouldNotBeFound("timeOfRequisition.equals=" + UPDATED_TIME_OF_REQUISITION);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByTimeOfRequisitionIsInShouldWork() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where timeOfRequisition in DEFAULT_TIME_OF_REQUISITION or UPDATED_TIME_OF_REQUISITION
        defaultReceiptEmailRequestShouldBeFound("timeOfRequisition.in=" + DEFAULT_TIME_OF_REQUISITION + "," + UPDATED_TIME_OF_REQUISITION);

        // Get all the receiptEmailRequestList where timeOfRequisition equals to UPDATED_TIME_OF_REQUISITION
        defaultReceiptEmailRequestShouldNotBeFound("timeOfRequisition.in=" + UPDATED_TIME_OF_REQUISITION);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByTimeOfRequisitionIsNullOrNotNull() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where timeOfRequisition is not null
        defaultReceiptEmailRequestShouldBeFound("timeOfRequisition.specified=true");

        // Get all the receiptEmailRequestList where timeOfRequisition is null
        defaultReceiptEmailRequestShouldNotBeFound("timeOfRequisition.specified=false");
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByTimeOfRequisitionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where timeOfRequisition is greater than or equal to DEFAULT_TIME_OF_REQUISITION
        defaultReceiptEmailRequestShouldBeFound("timeOfRequisition.greaterThanOrEqual=" + DEFAULT_TIME_OF_REQUISITION);

        // Get all the receiptEmailRequestList where timeOfRequisition is greater than or equal to UPDATED_TIME_OF_REQUISITION
        defaultReceiptEmailRequestShouldNotBeFound("timeOfRequisition.greaterThanOrEqual=" + UPDATED_TIME_OF_REQUISITION);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByTimeOfRequisitionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where timeOfRequisition is less than or equal to DEFAULT_TIME_OF_REQUISITION
        defaultReceiptEmailRequestShouldBeFound("timeOfRequisition.lessThanOrEqual=" + DEFAULT_TIME_OF_REQUISITION);

        // Get all the receiptEmailRequestList where timeOfRequisition is less than or equal to SMALLER_TIME_OF_REQUISITION
        defaultReceiptEmailRequestShouldNotBeFound("timeOfRequisition.lessThanOrEqual=" + SMALLER_TIME_OF_REQUISITION);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByTimeOfRequisitionIsLessThanSomething() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where timeOfRequisition is less than DEFAULT_TIME_OF_REQUISITION
        defaultReceiptEmailRequestShouldNotBeFound("timeOfRequisition.lessThan=" + DEFAULT_TIME_OF_REQUISITION);

        // Get all the receiptEmailRequestList where timeOfRequisition is less than UPDATED_TIME_OF_REQUISITION
        defaultReceiptEmailRequestShouldBeFound("timeOfRequisition.lessThan=" + UPDATED_TIME_OF_REQUISITION);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByTimeOfRequisitionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where timeOfRequisition is greater than DEFAULT_TIME_OF_REQUISITION
        defaultReceiptEmailRequestShouldNotBeFound("timeOfRequisition.greaterThan=" + DEFAULT_TIME_OF_REQUISITION);

        // Get all the receiptEmailRequestList where timeOfRequisition is greater than SMALLER_TIME_OF_REQUISITION
        defaultReceiptEmailRequestShouldBeFound("timeOfRequisition.greaterThan=" + SMALLER_TIME_OF_REQUISITION);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByUploadCompleteIsEqualToSomething() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where uploadComplete equals to DEFAULT_UPLOAD_COMPLETE
        defaultReceiptEmailRequestShouldBeFound("uploadComplete.equals=" + DEFAULT_UPLOAD_COMPLETE);

        // Get all the receiptEmailRequestList where uploadComplete equals to UPDATED_UPLOAD_COMPLETE
        defaultReceiptEmailRequestShouldNotBeFound("uploadComplete.equals=" + UPDATED_UPLOAD_COMPLETE);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByUploadCompleteIsInShouldWork() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where uploadComplete in DEFAULT_UPLOAD_COMPLETE or UPDATED_UPLOAD_COMPLETE
        defaultReceiptEmailRequestShouldBeFound("uploadComplete.in=" + DEFAULT_UPLOAD_COMPLETE + "," + UPDATED_UPLOAD_COMPLETE);

        // Get all the receiptEmailRequestList where uploadComplete equals to UPDATED_UPLOAD_COMPLETE
        defaultReceiptEmailRequestShouldNotBeFound("uploadComplete.in=" + UPDATED_UPLOAD_COMPLETE);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByUploadCompleteIsNullOrNotNull() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where uploadComplete is not null
        defaultReceiptEmailRequestShouldBeFound("uploadComplete.specified=true");

        // Get all the receiptEmailRequestList where uploadComplete is null
        defaultReceiptEmailRequestShouldNotBeFound("uploadComplete.specified=false");
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByNumberOfUpdatesIsEqualToSomething() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where numberOfUpdates equals to DEFAULT_NUMBER_OF_UPDATES
        defaultReceiptEmailRequestShouldBeFound("numberOfUpdates.equals=" + DEFAULT_NUMBER_OF_UPDATES);

        // Get all the receiptEmailRequestList where numberOfUpdates equals to UPDATED_NUMBER_OF_UPDATES
        defaultReceiptEmailRequestShouldNotBeFound("numberOfUpdates.equals=" + UPDATED_NUMBER_OF_UPDATES);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByNumberOfUpdatesIsInShouldWork() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where numberOfUpdates in DEFAULT_NUMBER_OF_UPDATES or UPDATED_NUMBER_OF_UPDATES
        defaultReceiptEmailRequestShouldBeFound("numberOfUpdates.in=" + DEFAULT_NUMBER_OF_UPDATES + "," + UPDATED_NUMBER_OF_UPDATES);

        // Get all the receiptEmailRequestList where numberOfUpdates equals to UPDATED_NUMBER_OF_UPDATES
        defaultReceiptEmailRequestShouldNotBeFound("numberOfUpdates.in=" + UPDATED_NUMBER_OF_UPDATES);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByNumberOfUpdatesIsNullOrNotNull() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where numberOfUpdates is not null
        defaultReceiptEmailRequestShouldBeFound("numberOfUpdates.specified=true");

        // Get all the receiptEmailRequestList where numberOfUpdates is null
        defaultReceiptEmailRequestShouldNotBeFound("numberOfUpdates.specified=false");
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByNumberOfUpdatesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where numberOfUpdates is greater than or equal to DEFAULT_NUMBER_OF_UPDATES
        defaultReceiptEmailRequestShouldBeFound("numberOfUpdates.greaterThanOrEqual=" + DEFAULT_NUMBER_OF_UPDATES);

        // Get all the receiptEmailRequestList where numberOfUpdates is greater than or equal to UPDATED_NUMBER_OF_UPDATES
        defaultReceiptEmailRequestShouldNotBeFound("numberOfUpdates.greaterThanOrEqual=" + UPDATED_NUMBER_OF_UPDATES);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByNumberOfUpdatesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where numberOfUpdates is less than or equal to DEFAULT_NUMBER_OF_UPDATES
        defaultReceiptEmailRequestShouldBeFound("numberOfUpdates.lessThanOrEqual=" + DEFAULT_NUMBER_OF_UPDATES);

        // Get all the receiptEmailRequestList where numberOfUpdates is less than or equal to SMALLER_NUMBER_OF_UPDATES
        defaultReceiptEmailRequestShouldNotBeFound("numberOfUpdates.lessThanOrEqual=" + SMALLER_NUMBER_OF_UPDATES);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByNumberOfUpdatesIsLessThanSomething() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where numberOfUpdates is less than DEFAULT_NUMBER_OF_UPDATES
        defaultReceiptEmailRequestShouldNotBeFound("numberOfUpdates.lessThan=" + DEFAULT_NUMBER_OF_UPDATES);

        // Get all the receiptEmailRequestList where numberOfUpdates is less than UPDATED_NUMBER_OF_UPDATES
        defaultReceiptEmailRequestShouldBeFound("numberOfUpdates.lessThan=" + UPDATED_NUMBER_OF_UPDATES);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByNumberOfUpdatesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        // Get all the receiptEmailRequestList where numberOfUpdates is greater than DEFAULT_NUMBER_OF_UPDATES
        defaultReceiptEmailRequestShouldNotBeFound("numberOfUpdates.greaterThan=" + DEFAULT_NUMBER_OF_UPDATES);

        // Get all the receiptEmailRequestList where numberOfUpdates is greater than SMALLER_NUMBER_OF_UPDATES
        defaultReceiptEmailRequestShouldBeFound("numberOfUpdates.greaterThan=" + SMALLER_NUMBER_OF_UPDATES);
    }

    @Test
    @Transactional
    void getAllReceiptEmailRequestsByRequestedByIsEqualToSomething() throws Exception {
        ApplicationUser requestedBy;
        if (TestUtil.findAll(em, ApplicationUser.class).isEmpty()) {
            receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);
            requestedBy = ApplicationUserResourceIT.createEntity(em);
        } else {
            requestedBy = TestUtil.findAll(em, ApplicationUser.class).get(0);
        }
        em.persist(requestedBy);
        em.flush();
        receiptEmailRequest.setRequestedBy(requestedBy);
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);
        Long requestedById = requestedBy.getId();

        // Get all the receiptEmailRequestList where requestedBy equals to requestedById
        defaultReceiptEmailRequestShouldBeFound("requestedById.equals=" + requestedById);

        // Get all the receiptEmailRequestList where requestedBy equals to (requestedById + 1)
        defaultReceiptEmailRequestShouldNotBeFound("requestedById.equals=" + (requestedById + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReceiptEmailRequestShouldBeFound(String filter) throws Exception {
        restReceiptEmailRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(receiptEmailRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].timeOfRequisition").value(hasItem(sameInstant(DEFAULT_TIME_OF_REQUISITION))))
            .andExpect(jsonPath("$.[*].uploadComplete").value(hasItem(DEFAULT_UPLOAD_COMPLETE.booleanValue())))
            .andExpect(jsonPath("$.[*].numberOfUpdates").value(hasItem(DEFAULT_NUMBER_OF_UPDATES)));

        // Check, that the count call also returns 1
        restReceiptEmailRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReceiptEmailRequestShouldNotBeFound(String filter) throws Exception {
        restReceiptEmailRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReceiptEmailRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReceiptEmailRequest() throws Exception {
        // Get the receiptEmailRequest
        restReceiptEmailRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReceiptEmailRequest() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        int databaseSizeBeforeUpdate = receiptEmailRequestRepository.findAll().size();
        receiptEmailRequestSearchRepository.save(receiptEmailRequest);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());

        // Update the receiptEmailRequest
        ReceiptEmailRequest updatedReceiptEmailRequest = receiptEmailRequestRepository.findById(receiptEmailRequest.getId()).get();
        // Disconnect from session so that the updates on updatedReceiptEmailRequest are not directly saved in db
        em.detach(updatedReceiptEmailRequest);
        updatedReceiptEmailRequest
            .timeOfRequisition(UPDATED_TIME_OF_REQUISITION)
            .uploadComplete(UPDATED_UPLOAD_COMPLETE)
            .numberOfUpdates(UPDATED_NUMBER_OF_UPDATES);
        ReceiptEmailRequestDTO receiptEmailRequestDTO = receiptEmailRequestMapper.toDto(updatedReceiptEmailRequest);

        restReceiptEmailRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, receiptEmailRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(receiptEmailRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReceiptEmailRequest in the database
        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeUpdate);
        ReceiptEmailRequest testReceiptEmailRequest = receiptEmailRequestList.get(receiptEmailRequestList.size() - 1);
        assertThat(testReceiptEmailRequest.getTimeOfRequisition()).isEqualTo(UPDATED_TIME_OF_REQUISITION);
        assertThat(testReceiptEmailRequest.getUploadComplete()).isEqualTo(UPDATED_UPLOAD_COMPLETE);
        assertThat(testReceiptEmailRequest.getNumberOfUpdates()).isEqualTo(UPDATED_NUMBER_OF_UPDATES);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ReceiptEmailRequest> receiptEmailRequestSearchList = IterableUtils.toList(
                    receiptEmailRequestSearchRepository.findAll()
                );
                ReceiptEmailRequest testReceiptEmailRequestSearch = receiptEmailRequestSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testReceiptEmailRequestSearch.getTimeOfRequisition()).isEqualTo(UPDATED_TIME_OF_REQUISITION);
                assertThat(testReceiptEmailRequestSearch.getUploadComplete()).isEqualTo(UPDATED_UPLOAD_COMPLETE);
                assertThat(testReceiptEmailRequestSearch.getNumberOfUpdates()).isEqualTo(UPDATED_NUMBER_OF_UPDATES);
            });
    }

    @Test
    @Transactional
    void putNonExistingReceiptEmailRequest() throws Exception {
        int databaseSizeBeforeUpdate = receiptEmailRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        receiptEmailRequest.setId(count.incrementAndGet());

        // Create the ReceiptEmailRequest
        ReceiptEmailRequestDTO receiptEmailRequestDTO = receiptEmailRequestMapper.toDto(receiptEmailRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReceiptEmailRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, receiptEmailRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(receiptEmailRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceiptEmailRequest in the database
        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchReceiptEmailRequest() throws Exception {
        int databaseSizeBeforeUpdate = receiptEmailRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        receiptEmailRequest.setId(count.incrementAndGet());

        // Create the ReceiptEmailRequest
        ReceiptEmailRequestDTO receiptEmailRequestDTO = receiptEmailRequestMapper.toDto(receiptEmailRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReceiptEmailRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(receiptEmailRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceiptEmailRequest in the database
        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReceiptEmailRequest() throws Exception {
        int databaseSizeBeforeUpdate = receiptEmailRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        receiptEmailRequest.setId(count.incrementAndGet());

        // Create the ReceiptEmailRequest
        ReceiptEmailRequestDTO receiptEmailRequestDTO = receiptEmailRequestMapper.toDto(receiptEmailRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReceiptEmailRequestMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(receiptEmailRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReceiptEmailRequest in the database
        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateReceiptEmailRequestWithPatch() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        int databaseSizeBeforeUpdate = receiptEmailRequestRepository.findAll().size();

        // Update the receiptEmailRequest using partial update
        ReceiptEmailRequest partialUpdatedReceiptEmailRequest = new ReceiptEmailRequest();
        partialUpdatedReceiptEmailRequest.setId(receiptEmailRequest.getId());

        partialUpdatedReceiptEmailRequest.numberOfUpdates(UPDATED_NUMBER_OF_UPDATES);

        restReceiptEmailRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReceiptEmailRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReceiptEmailRequest))
            )
            .andExpect(status().isOk());

        // Validate the ReceiptEmailRequest in the database
        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeUpdate);
        ReceiptEmailRequest testReceiptEmailRequest = receiptEmailRequestList.get(receiptEmailRequestList.size() - 1);
        assertThat(testReceiptEmailRequest.getTimeOfRequisition()).isEqualTo(DEFAULT_TIME_OF_REQUISITION);
        assertThat(testReceiptEmailRequest.getUploadComplete()).isEqualTo(DEFAULT_UPLOAD_COMPLETE);
        assertThat(testReceiptEmailRequest.getNumberOfUpdates()).isEqualTo(UPDATED_NUMBER_OF_UPDATES);
    }

    @Test
    @Transactional
    void fullUpdateReceiptEmailRequestWithPatch() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);

        int databaseSizeBeforeUpdate = receiptEmailRequestRepository.findAll().size();

        // Update the receiptEmailRequest using partial update
        ReceiptEmailRequest partialUpdatedReceiptEmailRequest = new ReceiptEmailRequest();
        partialUpdatedReceiptEmailRequest.setId(receiptEmailRequest.getId());

        partialUpdatedReceiptEmailRequest
            .timeOfRequisition(UPDATED_TIME_OF_REQUISITION)
            .uploadComplete(UPDATED_UPLOAD_COMPLETE)
            .numberOfUpdates(UPDATED_NUMBER_OF_UPDATES);

        restReceiptEmailRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReceiptEmailRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReceiptEmailRequest))
            )
            .andExpect(status().isOk());

        // Validate the ReceiptEmailRequest in the database
        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeUpdate);
        ReceiptEmailRequest testReceiptEmailRequest = receiptEmailRequestList.get(receiptEmailRequestList.size() - 1);
        assertThat(testReceiptEmailRequest.getTimeOfRequisition()).isEqualTo(UPDATED_TIME_OF_REQUISITION);
        assertThat(testReceiptEmailRequest.getUploadComplete()).isEqualTo(UPDATED_UPLOAD_COMPLETE);
        assertThat(testReceiptEmailRequest.getNumberOfUpdates()).isEqualTo(UPDATED_NUMBER_OF_UPDATES);
    }

    @Test
    @Transactional
    void patchNonExistingReceiptEmailRequest() throws Exception {
        int databaseSizeBeforeUpdate = receiptEmailRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        receiptEmailRequest.setId(count.incrementAndGet());

        // Create the ReceiptEmailRequest
        ReceiptEmailRequestDTO receiptEmailRequestDTO = receiptEmailRequestMapper.toDto(receiptEmailRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReceiptEmailRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, receiptEmailRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(receiptEmailRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceiptEmailRequest in the database
        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReceiptEmailRequest() throws Exception {
        int databaseSizeBeforeUpdate = receiptEmailRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        receiptEmailRequest.setId(count.incrementAndGet());

        // Create the ReceiptEmailRequest
        ReceiptEmailRequestDTO receiptEmailRequestDTO = receiptEmailRequestMapper.toDto(receiptEmailRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReceiptEmailRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(receiptEmailRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceiptEmailRequest in the database
        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReceiptEmailRequest() throws Exception {
        int databaseSizeBeforeUpdate = receiptEmailRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        receiptEmailRequest.setId(count.incrementAndGet());

        // Create the ReceiptEmailRequest
        ReceiptEmailRequestDTO receiptEmailRequestDTO = receiptEmailRequestMapper.toDto(receiptEmailRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReceiptEmailRequestMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(receiptEmailRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReceiptEmailRequest in the database
        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteReceiptEmailRequest() throws Exception {
        // Initialize the database
        receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);
        receiptEmailRequestRepository.save(receiptEmailRequest);
        receiptEmailRequestSearchRepository.save(receiptEmailRequest);

        int databaseSizeBeforeDelete = receiptEmailRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the receiptEmailRequest
        restReceiptEmailRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, receiptEmailRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ReceiptEmailRequest> receiptEmailRequestList = receiptEmailRequestRepository.findAll();
        assertThat(receiptEmailRequestList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(receiptEmailRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchReceiptEmailRequest() throws Exception {
        // Initialize the database
        receiptEmailRequest = receiptEmailRequestRepository.saveAndFlush(receiptEmailRequest);
        receiptEmailRequestSearchRepository.save(receiptEmailRequest);

        // Search the receiptEmailRequest
        restReceiptEmailRequestMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + receiptEmailRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(receiptEmailRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].timeOfRequisition").value(hasItem(sameInstant(DEFAULT_TIME_OF_REQUISITION))))
            .andExpect(jsonPath("$.[*].uploadComplete").value(hasItem(DEFAULT_UPLOAD_COMPLETE.booleanValue())))
            .andExpect(jsonPath("$.[*].numberOfUpdates").value(hasItem(DEFAULT_NUMBER_OF_UPDATES)));
    }
}

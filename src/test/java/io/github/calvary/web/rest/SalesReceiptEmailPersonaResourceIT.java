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

import static io.github.calvary.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.ApplicationUser;
import io.github.calvary.domain.Dealer;
import io.github.calvary.domain.SalesReceiptEmailPersona;
import io.github.calvary.repository.SalesReceiptEmailPersonaRepository;
import io.github.calvary.repository.search.SalesReceiptEmailPersonaSearchRepository;
import io.github.calvary.service.SalesReceiptEmailPersonaService;
import io.github.calvary.service.criteria.SalesReceiptEmailPersonaCriteria;
import io.github.calvary.service.dto.SalesReceiptEmailPersonaDTO;
import io.github.calvary.service.mapper.SalesReceiptEmailPersonaMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link SalesReceiptEmailPersonaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SalesReceiptEmailPersonaResourceIT {

    private static final UUID DEFAULT_EMAIL_IDENTIFIER = UUID.randomUUID();
    private static final UUID UPDATED_EMAIL_IDENTIFIER = UUID.randomUUID();

    private static final String DEFAULT_MAIN_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_MAIN_EMAIL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_CLEAR_COPY_EMAIL = false;
    private static final Boolean UPDATED_CLEAR_COPY_EMAIL = true;

    private static final Boolean DEFAULT_BLIND_COPY_EMAIL = false;
    private static final Boolean UPDATED_BLIND_COPY_EMAIL = true;

    private static final String DEFAULT_LANGUAGE_KEY_CODE = "ug";
    private static final String UPDATED_LANGUAGE_KEY_CODE = "rzuvi";

    private static final String DEFAULT_PREFERRED_GREETING = "AAAAAAAAAA";
    private static final String UPDATED_PREFERRED_GREETING = "BBBBBBBBBB";

    private static final String DEFAULT_PREFERRED_GREETING_DESIGNATION = "AAAAAAAAAA";
    private static final String UPDATED_PREFERRED_GREETING_DESIGNATION = "BBBBBBBBBB";

    private static final String DEFAULT_PREFERRED_PREFIX = "AAAAAAAAAA";
    private static final String UPDATED_PREFERRED_PREFIX = "BBBBBBBBBB";

    private static final String DEFAULT_PREFERRED_SUFFIX = "AAAAAAAAAA";
    private static final String UPDATED_PREFERRED_SUFFIX = "BBBBBBBBBB";

    private static final Boolean DEFAULT_TIME_BASED_GREETINGS = false;
    private static final Boolean UPDATED_TIME_BASED_GREETINGS = true;

    private static final Boolean DEFAULT_SLOGAN_BASED_GREETING = false;
    private static final Boolean UPDATED_SLOGAN_BASED_GREETING = true;

    private static final Boolean DEFAULT_ADD_PREFIX = false;
    private static final Boolean UPDATED_ADD_PREFIX = true;

    private static final Boolean DEFAULT_ADD_SUFFIX = false;
    private static final Boolean UPDATED_ADD_SUFFIX = true;

    private static final String DEFAULT_PREFERRED_SIGNATURE = "AAAAAAAAAA";
    private static final String UPDATED_PREFERRED_SIGNATURE = "BBBBBBBBBB";

    private static final String DEFAULT_PREFERRED_SIGNATURE_DESIGNATION = "AAAAAAAAAA";
    private static final String UPDATED_PREFERRED_SIGNATURE_DESIGNATION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_INCLUDE_SERVICE_DETAILS = false;
    private static final Boolean UPDATED_INCLUDE_SERVICE_DETAILS = true;

    private static final Boolean DEFAULT_INCLUDE_MESSAGE_OF_THE_DAY = false;
    private static final Boolean UPDATED_INCLUDE_MESSAGE_OF_THE_DAY = true;

    private static final Boolean DEFAULT_INCLUDE_TREASURY_QUOTE = false;
    private static final Boolean UPDATED_INCLUDE_TREASURY_QUOTE = true;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_LAST_MODIFED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_MODIFED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LAST_MODIFED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_PERSONA_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PERSONA_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sales-receipt-email-personas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/sales-receipt-email-personas";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SalesReceiptEmailPersonaRepository salesReceiptEmailPersonaRepository;

    @Mock
    private SalesReceiptEmailPersonaRepository salesReceiptEmailPersonaRepositoryMock;

    @Autowired
    private SalesReceiptEmailPersonaMapper salesReceiptEmailPersonaMapper;

    @Mock
    private SalesReceiptEmailPersonaService salesReceiptEmailPersonaServiceMock;

    @Autowired
    private SalesReceiptEmailPersonaSearchRepository salesReceiptEmailPersonaSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalesReceiptEmailPersonaMockMvc;

    private SalesReceiptEmailPersona salesReceiptEmailPersona;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesReceiptEmailPersona createEntity(EntityManager em) {
        SalesReceiptEmailPersona salesReceiptEmailPersona = new SalesReceiptEmailPersona()
            .emailIdentifier(DEFAULT_EMAIL_IDENTIFIER)
            .mainEmail(DEFAULT_MAIN_EMAIL)
            .clearCopyEmail(DEFAULT_CLEAR_COPY_EMAIL)
            .blindCopyEmail(DEFAULT_BLIND_COPY_EMAIL)
            .languageKeyCode(DEFAULT_LANGUAGE_KEY_CODE)
            .preferredGreeting(DEFAULT_PREFERRED_GREETING)
            .preferredGreetingDesignation(DEFAULT_PREFERRED_GREETING_DESIGNATION)
            .preferredPrefix(DEFAULT_PREFERRED_PREFIX)
            .preferredSuffix(DEFAULT_PREFERRED_SUFFIX)
            .timeBasedGreetings(DEFAULT_TIME_BASED_GREETINGS)
            .sloganBasedGreeting(DEFAULT_SLOGAN_BASED_GREETING)
            .addPrefix(DEFAULT_ADD_PREFIX)
            .addSuffix(DEFAULT_ADD_SUFFIX)
            .preferredSignature(DEFAULT_PREFERRED_SIGNATURE)
            .preferredSignatureDesignation(DEFAULT_PREFERRED_SIGNATURE_DESIGNATION)
            .includeServiceDetails(DEFAULT_INCLUDE_SERVICE_DETAILS)
            .includeMessageOfTheDay(DEFAULT_INCLUDE_MESSAGE_OF_THE_DAY)
            .includeTreasuryQuote(DEFAULT_INCLUDE_TREASURY_QUOTE)
            .createdAt(DEFAULT_CREATED_AT)
            .lastModifedAt(DEFAULT_LAST_MODIFED_AT)
            .personaName(DEFAULT_PERSONA_NAME);
        return salesReceiptEmailPersona;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesReceiptEmailPersona createUpdatedEntity(EntityManager em) {
        SalesReceiptEmailPersona salesReceiptEmailPersona = new SalesReceiptEmailPersona()
            .emailIdentifier(UPDATED_EMAIL_IDENTIFIER)
            .mainEmail(UPDATED_MAIN_EMAIL)
            .clearCopyEmail(UPDATED_CLEAR_COPY_EMAIL)
            .blindCopyEmail(UPDATED_BLIND_COPY_EMAIL)
            .languageKeyCode(UPDATED_LANGUAGE_KEY_CODE)
            .preferredGreeting(UPDATED_PREFERRED_GREETING)
            .preferredGreetingDesignation(UPDATED_PREFERRED_GREETING_DESIGNATION)
            .preferredPrefix(UPDATED_PREFERRED_PREFIX)
            .preferredSuffix(UPDATED_PREFERRED_SUFFIX)
            .timeBasedGreetings(UPDATED_TIME_BASED_GREETINGS)
            .sloganBasedGreeting(UPDATED_SLOGAN_BASED_GREETING)
            .addPrefix(UPDATED_ADD_PREFIX)
            .addSuffix(UPDATED_ADD_SUFFIX)
            .preferredSignature(UPDATED_PREFERRED_SIGNATURE)
            .preferredSignatureDesignation(UPDATED_PREFERRED_SIGNATURE_DESIGNATION)
            .includeServiceDetails(UPDATED_INCLUDE_SERVICE_DETAILS)
            .includeMessageOfTheDay(UPDATED_INCLUDE_MESSAGE_OF_THE_DAY)
            .includeTreasuryQuote(UPDATED_INCLUDE_TREASURY_QUOTE)
            .createdAt(UPDATED_CREATED_AT)
            .lastModifedAt(UPDATED_LAST_MODIFED_AT)
            .personaName(UPDATED_PERSONA_NAME);
        return salesReceiptEmailPersona;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        salesReceiptEmailPersonaSearchRepository.deleteAll();
        assertThat(salesReceiptEmailPersonaSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        salesReceiptEmailPersona = createEntity(em);
    }

    @Test
    @Transactional
    void createSalesReceiptEmailPersona() throws Exception {
        int databaseSizeBeforeCreate = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        // Create the SalesReceiptEmailPersona
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);
        restSalesReceiptEmailPersonaMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SalesReceiptEmailPersona in the database
        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SalesReceiptEmailPersona testSalesReceiptEmailPersona = salesReceiptEmailPersonaList.get(salesReceiptEmailPersonaList.size() - 1);
        assertThat(testSalesReceiptEmailPersona.getEmailIdentifier()).isEqualTo(DEFAULT_EMAIL_IDENTIFIER);
        assertThat(testSalesReceiptEmailPersona.getMainEmail()).isEqualTo(DEFAULT_MAIN_EMAIL);
        assertThat(testSalesReceiptEmailPersona.getClearCopyEmail()).isEqualTo(DEFAULT_CLEAR_COPY_EMAIL);
        assertThat(testSalesReceiptEmailPersona.getBlindCopyEmail()).isEqualTo(DEFAULT_BLIND_COPY_EMAIL);
        assertThat(testSalesReceiptEmailPersona.getLanguageKeyCode()).isEqualTo(DEFAULT_LANGUAGE_KEY_CODE);
        assertThat(testSalesReceiptEmailPersona.getPreferredGreeting()).isEqualTo(DEFAULT_PREFERRED_GREETING);
        assertThat(testSalesReceiptEmailPersona.getPreferredGreetingDesignation()).isEqualTo(DEFAULT_PREFERRED_GREETING_DESIGNATION);
        assertThat(testSalesReceiptEmailPersona.getPreferredPrefix()).isEqualTo(DEFAULT_PREFERRED_PREFIX);
        assertThat(testSalesReceiptEmailPersona.getPreferredSuffix()).isEqualTo(DEFAULT_PREFERRED_SUFFIX);
        assertThat(testSalesReceiptEmailPersona.getTimeBasedGreetings()).isEqualTo(DEFAULT_TIME_BASED_GREETINGS);
        assertThat(testSalesReceiptEmailPersona.getSloganBasedGreeting()).isEqualTo(DEFAULT_SLOGAN_BASED_GREETING);
        assertThat(testSalesReceiptEmailPersona.getAddPrefix()).isEqualTo(DEFAULT_ADD_PREFIX);
        assertThat(testSalesReceiptEmailPersona.getAddSuffix()).isEqualTo(DEFAULT_ADD_SUFFIX);
        assertThat(testSalesReceiptEmailPersona.getPreferredSignature()).isEqualTo(DEFAULT_PREFERRED_SIGNATURE);
        assertThat(testSalesReceiptEmailPersona.getPreferredSignatureDesignation()).isEqualTo(DEFAULT_PREFERRED_SIGNATURE_DESIGNATION);
        assertThat(testSalesReceiptEmailPersona.getIncludeServiceDetails()).isEqualTo(DEFAULT_INCLUDE_SERVICE_DETAILS);
        assertThat(testSalesReceiptEmailPersona.getIncludeMessageOfTheDay()).isEqualTo(DEFAULT_INCLUDE_MESSAGE_OF_THE_DAY);
        assertThat(testSalesReceiptEmailPersona.getIncludeTreasuryQuote()).isEqualTo(DEFAULT_INCLUDE_TREASURY_QUOTE);
        assertThat(testSalesReceiptEmailPersona.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testSalesReceiptEmailPersona.getLastModifedAt()).isEqualTo(DEFAULT_LAST_MODIFED_AT);
        assertThat(testSalesReceiptEmailPersona.getPersonaName()).isEqualTo(DEFAULT_PERSONA_NAME);
    }

    @Test
    @Transactional
    void createSalesReceiptEmailPersonaWithExistingId() throws Exception {
        // Create the SalesReceiptEmailPersona with an existing ID
        salesReceiptEmailPersona.setId(1L);
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        int databaseSizeBeforeCreate = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalesReceiptEmailPersonaMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptEmailPersona in the database
        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIdentifierIsRequired() throws Exception {
        int databaseSizeBeforeTest = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        // set the field null
        salesReceiptEmailPersona.setEmailIdentifier(null);

        // Create the SalesReceiptEmailPersona, which fails.
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        restSalesReceiptEmailPersonaMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkMainEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        // set the field null
        salesReceiptEmailPersona.setMainEmail(null);

        // Create the SalesReceiptEmailPersona, which fails.
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        restSalesReceiptEmailPersonaMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLanguageKeyCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        // set the field null
        salesReceiptEmailPersona.setLanguageKeyCode(null);

        // Create the SalesReceiptEmailPersona, which fails.
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        restSalesReceiptEmailPersonaMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPreferredGreetingIsRequired() throws Exception {
        int databaseSizeBeforeTest = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        // set the field null
        salesReceiptEmailPersona.setPreferredGreeting(null);

        // Create the SalesReceiptEmailPersona, which fails.
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        restSalesReceiptEmailPersonaMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPreferredSignatureIsRequired() throws Exception {
        int databaseSizeBeforeTest = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        // set the field null
        salesReceiptEmailPersona.setPreferredSignature(null);

        // Create the SalesReceiptEmailPersona, which fails.
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        restSalesReceiptEmailPersonaMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPreferredSignatureDesignationIsRequired() throws Exception {
        int databaseSizeBeforeTest = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        // set the field null
        salesReceiptEmailPersona.setPreferredSignatureDesignation(null);

        // Create the SalesReceiptEmailPersona, which fails.
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        restSalesReceiptEmailPersonaMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        // set the field null
        salesReceiptEmailPersona.setCreatedAt(null);

        // Create the SalesReceiptEmailPersona, which fails.
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        restSalesReceiptEmailPersonaMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPersonaNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        // set the field null
        salesReceiptEmailPersona.setPersonaName(null);

        // Create the SalesReceiptEmailPersona, which fails.
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        restSalesReceiptEmailPersonaMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonas() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList
        restSalesReceiptEmailPersonaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceiptEmailPersona.getId().intValue())))
            .andExpect(jsonPath("$.[*].emailIdentifier").value(hasItem(DEFAULT_EMAIL_IDENTIFIER.toString())))
            .andExpect(jsonPath("$.[*].mainEmail").value(hasItem(DEFAULT_MAIN_EMAIL)))
            .andExpect(jsonPath("$.[*].clearCopyEmail").value(hasItem(DEFAULT_CLEAR_COPY_EMAIL.booleanValue())))
            .andExpect(jsonPath("$.[*].blindCopyEmail").value(hasItem(DEFAULT_BLIND_COPY_EMAIL.booleanValue())))
            .andExpect(jsonPath("$.[*].languageKeyCode").value(hasItem(DEFAULT_LANGUAGE_KEY_CODE)))
            .andExpect(jsonPath("$.[*].preferredGreeting").value(hasItem(DEFAULT_PREFERRED_GREETING)))
            .andExpect(jsonPath("$.[*].preferredGreetingDesignation").value(hasItem(DEFAULT_PREFERRED_GREETING_DESIGNATION)))
            .andExpect(jsonPath("$.[*].preferredPrefix").value(hasItem(DEFAULT_PREFERRED_PREFIX)))
            .andExpect(jsonPath("$.[*].preferredSuffix").value(hasItem(DEFAULT_PREFERRED_SUFFIX)))
            .andExpect(jsonPath("$.[*].timeBasedGreetings").value(hasItem(DEFAULT_TIME_BASED_GREETINGS.booleanValue())))
            .andExpect(jsonPath("$.[*].sloganBasedGreeting").value(hasItem(DEFAULT_SLOGAN_BASED_GREETING.booleanValue())))
            .andExpect(jsonPath("$.[*].addPrefix").value(hasItem(DEFAULT_ADD_PREFIX.booleanValue())))
            .andExpect(jsonPath("$.[*].addSuffix").value(hasItem(DEFAULT_ADD_SUFFIX.booleanValue())))
            .andExpect(jsonPath("$.[*].preferredSignature").value(hasItem(DEFAULT_PREFERRED_SIGNATURE)))
            .andExpect(jsonPath("$.[*].preferredSignatureDesignation").value(hasItem(DEFAULT_PREFERRED_SIGNATURE_DESIGNATION)))
            .andExpect(jsonPath("$.[*].includeServiceDetails").value(hasItem(DEFAULT_INCLUDE_SERVICE_DETAILS.booleanValue())))
            .andExpect(jsonPath("$.[*].includeMessageOfTheDay").value(hasItem(DEFAULT_INCLUDE_MESSAGE_OF_THE_DAY.booleanValue())))
            .andExpect(jsonPath("$.[*].includeTreasuryQuote").value(hasItem(DEFAULT_INCLUDE_TREASURY_QUOTE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].lastModifedAt").value(hasItem(sameInstant(DEFAULT_LAST_MODIFED_AT))))
            .andExpect(jsonPath("$.[*].personaName").value(hasItem(DEFAULT_PERSONA_NAME)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesReceiptEmailPersonasWithEagerRelationshipsIsEnabled() throws Exception {
        when(salesReceiptEmailPersonaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSalesReceiptEmailPersonaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(salesReceiptEmailPersonaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesReceiptEmailPersonasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(salesReceiptEmailPersonaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSalesReceiptEmailPersonaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(salesReceiptEmailPersonaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSalesReceiptEmailPersona() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get the salesReceiptEmailPersona
        restSalesReceiptEmailPersonaMockMvc
            .perform(get(ENTITY_API_URL_ID, salesReceiptEmailPersona.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(salesReceiptEmailPersona.getId().intValue()))
            .andExpect(jsonPath("$.emailIdentifier").value(DEFAULT_EMAIL_IDENTIFIER.toString()))
            .andExpect(jsonPath("$.mainEmail").value(DEFAULT_MAIN_EMAIL))
            .andExpect(jsonPath("$.clearCopyEmail").value(DEFAULT_CLEAR_COPY_EMAIL.booleanValue()))
            .andExpect(jsonPath("$.blindCopyEmail").value(DEFAULT_BLIND_COPY_EMAIL.booleanValue()))
            .andExpect(jsonPath("$.languageKeyCode").value(DEFAULT_LANGUAGE_KEY_CODE))
            .andExpect(jsonPath("$.preferredGreeting").value(DEFAULT_PREFERRED_GREETING))
            .andExpect(jsonPath("$.preferredGreetingDesignation").value(DEFAULT_PREFERRED_GREETING_DESIGNATION))
            .andExpect(jsonPath("$.preferredPrefix").value(DEFAULT_PREFERRED_PREFIX))
            .andExpect(jsonPath("$.preferredSuffix").value(DEFAULT_PREFERRED_SUFFIX))
            .andExpect(jsonPath("$.timeBasedGreetings").value(DEFAULT_TIME_BASED_GREETINGS.booleanValue()))
            .andExpect(jsonPath("$.sloganBasedGreeting").value(DEFAULT_SLOGAN_BASED_GREETING.booleanValue()))
            .andExpect(jsonPath("$.addPrefix").value(DEFAULT_ADD_PREFIX.booleanValue()))
            .andExpect(jsonPath("$.addSuffix").value(DEFAULT_ADD_SUFFIX.booleanValue()))
            .andExpect(jsonPath("$.preferredSignature").value(DEFAULT_PREFERRED_SIGNATURE))
            .andExpect(jsonPath("$.preferredSignatureDesignation").value(DEFAULT_PREFERRED_SIGNATURE_DESIGNATION))
            .andExpect(jsonPath("$.includeServiceDetails").value(DEFAULT_INCLUDE_SERVICE_DETAILS.booleanValue()))
            .andExpect(jsonPath("$.includeMessageOfTheDay").value(DEFAULT_INCLUDE_MESSAGE_OF_THE_DAY.booleanValue()))
            .andExpect(jsonPath("$.includeTreasuryQuote").value(DEFAULT_INCLUDE_TREASURY_QUOTE.booleanValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.lastModifedAt").value(sameInstant(DEFAULT_LAST_MODIFED_AT)))
            .andExpect(jsonPath("$.personaName").value(DEFAULT_PERSONA_NAME));
    }

    @Test
    @Transactional
    void getSalesReceiptEmailPersonasByIdFiltering() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        Long id = salesReceiptEmailPersona.getId();

        defaultSalesReceiptEmailPersonaShouldBeFound("id.equals=" + id);
        defaultSalesReceiptEmailPersonaShouldNotBeFound("id.notEquals=" + id);

        defaultSalesReceiptEmailPersonaShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSalesReceiptEmailPersonaShouldNotBeFound("id.greaterThan=" + id);

        defaultSalesReceiptEmailPersonaShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSalesReceiptEmailPersonaShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByEmailIdentifierIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where emailIdentifier equals to DEFAULT_EMAIL_IDENTIFIER
        defaultSalesReceiptEmailPersonaShouldBeFound("emailIdentifier.equals=" + DEFAULT_EMAIL_IDENTIFIER);

        // Get all the salesReceiptEmailPersonaList where emailIdentifier equals to UPDATED_EMAIL_IDENTIFIER
        defaultSalesReceiptEmailPersonaShouldNotBeFound("emailIdentifier.equals=" + UPDATED_EMAIL_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByEmailIdentifierIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where emailIdentifier in DEFAULT_EMAIL_IDENTIFIER or UPDATED_EMAIL_IDENTIFIER
        defaultSalesReceiptEmailPersonaShouldBeFound("emailIdentifier.in=" + DEFAULT_EMAIL_IDENTIFIER + "," + UPDATED_EMAIL_IDENTIFIER);

        // Get all the salesReceiptEmailPersonaList where emailIdentifier equals to UPDATED_EMAIL_IDENTIFIER
        defaultSalesReceiptEmailPersonaShouldNotBeFound("emailIdentifier.in=" + UPDATED_EMAIL_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByEmailIdentifierIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where emailIdentifier is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("emailIdentifier.specified=true");

        // Get all the salesReceiptEmailPersonaList where emailIdentifier is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("emailIdentifier.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByMainEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where mainEmail equals to DEFAULT_MAIN_EMAIL
        defaultSalesReceiptEmailPersonaShouldBeFound("mainEmail.equals=" + DEFAULT_MAIN_EMAIL);

        // Get all the salesReceiptEmailPersonaList where mainEmail equals to UPDATED_MAIN_EMAIL
        defaultSalesReceiptEmailPersonaShouldNotBeFound("mainEmail.equals=" + UPDATED_MAIN_EMAIL);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByMainEmailIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where mainEmail in DEFAULT_MAIN_EMAIL or UPDATED_MAIN_EMAIL
        defaultSalesReceiptEmailPersonaShouldBeFound("mainEmail.in=" + DEFAULT_MAIN_EMAIL + "," + UPDATED_MAIN_EMAIL);

        // Get all the salesReceiptEmailPersonaList where mainEmail equals to UPDATED_MAIN_EMAIL
        defaultSalesReceiptEmailPersonaShouldNotBeFound("mainEmail.in=" + UPDATED_MAIN_EMAIL);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByMainEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where mainEmail is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("mainEmail.specified=true");

        // Get all the salesReceiptEmailPersonaList where mainEmail is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("mainEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByMainEmailContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where mainEmail contains DEFAULT_MAIN_EMAIL
        defaultSalesReceiptEmailPersonaShouldBeFound("mainEmail.contains=" + DEFAULT_MAIN_EMAIL);

        // Get all the salesReceiptEmailPersonaList where mainEmail contains UPDATED_MAIN_EMAIL
        defaultSalesReceiptEmailPersonaShouldNotBeFound("mainEmail.contains=" + UPDATED_MAIN_EMAIL);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByMainEmailNotContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where mainEmail does not contain DEFAULT_MAIN_EMAIL
        defaultSalesReceiptEmailPersonaShouldNotBeFound("mainEmail.doesNotContain=" + DEFAULT_MAIN_EMAIL);

        // Get all the salesReceiptEmailPersonaList where mainEmail does not contain UPDATED_MAIN_EMAIL
        defaultSalesReceiptEmailPersonaShouldBeFound("mainEmail.doesNotContain=" + UPDATED_MAIN_EMAIL);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByClearCopyEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where clearCopyEmail equals to DEFAULT_CLEAR_COPY_EMAIL
        defaultSalesReceiptEmailPersonaShouldBeFound("clearCopyEmail.equals=" + DEFAULT_CLEAR_COPY_EMAIL);

        // Get all the salesReceiptEmailPersonaList where clearCopyEmail equals to UPDATED_CLEAR_COPY_EMAIL
        defaultSalesReceiptEmailPersonaShouldNotBeFound("clearCopyEmail.equals=" + UPDATED_CLEAR_COPY_EMAIL);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByClearCopyEmailIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where clearCopyEmail in DEFAULT_CLEAR_COPY_EMAIL or UPDATED_CLEAR_COPY_EMAIL
        defaultSalesReceiptEmailPersonaShouldBeFound("clearCopyEmail.in=" + DEFAULT_CLEAR_COPY_EMAIL + "," + UPDATED_CLEAR_COPY_EMAIL);

        // Get all the salesReceiptEmailPersonaList where clearCopyEmail equals to UPDATED_CLEAR_COPY_EMAIL
        defaultSalesReceiptEmailPersonaShouldNotBeFound("clearCopyEmail.in=" + UPDATED_CLEAR_COPY_EMAIL);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByClearCopyEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where clearCopyEmail is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("clearCopyEmail.specified=true");

        // Get all the salesReceiptEmailPersonaList where clearCopyEmail is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("clearCopyEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByBlindCopyEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where blindCopyEmail equals to DEFAULT_BLIND_COPY_EMAIL
        defaultSalesReceiptEmailPersonaShouldBeFound("blindCopyEmail.equals=" + DEFAULT_BLIND_COPY_EMAIL);

        // Get all the salesReceiptEmailPersonaList where blindCopyEmail equals to UPDATED_BLIND_COPY_EMAIL
        defaultSalesReceiptEmailPersonaShouldNotBeFound("blindCopyEmail.equals=" + UPDATED_BLIND_COPY_EMAIL);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByBlindCopyEmailIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where blindCopyEmail in DEFAULT_BLIND_COPY_EMAIL or UPDATED_BLIND_COPY_EMAIL
        defaultSalesReceiptEmailPersonaShouldBeFound("blindCopyEmail.in=" + DEFAULT_BLIND_COPY_EMAIL + "," + UPDATED_BLIND_COPY_EMAIL);

        // Get all the salesReceiptEmailPersonaList where blindCopyEmail equals to UPDATED_BLIND_COPY_EMAIL
        defaultSalesReceiptEmailPersonaShouldNotBeFound("blindCopyEmail.in=" + UPDATED_BLIND_COPY_EMAIL);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByBlindCopyEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where blindCopyEmail is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("blindCopyEmail.specified=true");

        // Get all the salesReceiptEmailPersonaList where blindCopyEmail is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("blindCopyEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLanguageKeyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where languageKeyCode equals to DEFAULT_LANGUAGE_KEY_CODE
        defaultSalesReceiptEmailPersonaShouldBeFound("languageKeyCode.equals=" + DEFAULT_LANGUAGE_KEY_CODE);

        // Get all the salesReceiptEmailPersonaList where languageKeyCode equals to UPDATED_LANGUAGE_KEY_CODE
        defaultSalesReceiptEmailPersonaShouldNotBeFound("languageKeyCode.equals=" + UPDATED_LANGUAGE_KEY_CODE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLanguageKeyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where languageKeyCode in DEFAULT_LANGUAGE_KEY_CODE or UPDATED_LANGUAGE_KEY_CODE
        defaultSalesReceiptEmailPersonaShouldBeFound("languageKeyCode.in=" + DEFAULT_LANGUAGE_KEY_CODE + "," + UPDATED_LANGUAGE_KEY_CODE);

        // Get all the salesReceiptEmailPersonaList where languageKeyCode equals to UPDATED_LANGUAGE_KEY_CODE
        defaultSalesReceiptEmailPersonaShouldNotBeFound("languageKeyCode.in=" + UPDATED_LANGUAGE_KEY_CODE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLanguageKeyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where languageKeyCode is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("languageKeyCode.specified=true");

        // Get all the salesReceiptEmailPersonaList where languageKeyCode is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("languageKeyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLanguageKeyCodeContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where languageKeyCode contains DEFAULT_LANGUAGE_KEY_CODE
        defaultSalesReceiptEmailPersonaShouldBeFound("languageKeyCode.contains=" + DEFAULT_LANGUAGE_KEY_CODE);

        // Get all the salesReceiptEmailPersonaList where languageKeyCode contains UPDATED_LANGUAGE_KEY_CODE
        defaultSalesReceiptEmailPersonaShouldNotBeFound("languageKeyCode.contains=" + UPDATED_LANGUAGE_KEY_CODE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLanguageKeyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where languageKeyCode does not contain DEFAULT_LANGUAGE_KEY_CODE
        defaultSalesReceiptEmailPersonaShouldNotBeFound("languageKeyCode.doesNotContain=" + DEFAULT_LANGUAGE_KEY_CODE);

        // Get all the salesReceiptEmailPersonaList where languageKeyCode does not contain UPDATED_LANGUAGE_KEY_CODE
        defaultSalesReceiptEmailPersonaShouldBeFound("languageKeyCode.doesNotContain=" + UPDATED_LANGUAGE_KEY_CODE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredGreetingIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredGreeting equals to DEFAULT_PREFERRED_GREETING
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredGreeting.equals=" + DEFAULT_PREFERRED_GREETING);

        // Get all the salesReceiptEmailPersonaList where preferredGreeting equals to UPDATED_PREFERRED_GREETING
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredGreeting.equals=" + UPDATED_PREFERRED_GREETING);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredGreetingIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredGreeting in DEFAULT_PREFERRED_GREETING or UPDATED_PREFERRED_GREETING
        defaultSalesReceiptEmailPersonaShouldBeFound(
            "preferredGreeting.in=" + DEFAULT_PREFERRED_GREETING + "," + UPDATED_PREFERRED_GREETING
        );

        // Get all the salesReceiptEmailPersonaList where preferredGreeting equals to UPDATED_PREFERRED_GREETING
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredGreeting.in=" + UPDATED_PREFERRED_GREETING);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredGreetingIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredGreeting is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredGreeting.specified=true");

        // Get all the salesReceiptEmailPersonaList where preferredGreeting is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredGreeting.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredGreetingContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredGreeting contains DEFAULT_PREFERRED_GREETING
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredGreeting.contains=" + DEFAULT_PREFERRED_GREETING);

        // Get all the salesReceiptEmailPersonaList where preferredGreeting contains UPDATED_PREFERRED_GREETING
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredGreeting.contains=" + UPDATED_PREFERRED_GREETING);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredGreetingNotContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredGreeting does not contain DEFAULT_PREFERRED_GREETING
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredGreeting.doesNotContain=" + DEFAULT_PREFERRED_GREETING);

        // Get all the salesReceiptEmailPersonaList where preferredGreeting does not contain UPDATED_PREFERRED_GREETING
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredGreeting.doesNotContain=" + UPDATED_PREFERRED_GREETING);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredGreetingDesignationIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredGreetingDesignation equals to DEFAULT_PREFERRED_GREETING_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredGreetingDesignation.equals=" + DEFAULT_PREFERRED_GREETING_DESIGNATION);

        // Get all the salesReceiptEmailPersonaList where preferredGreetingDesignation equals to UPDATED_PREFERRED_GREETING_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredGreetingDesignation.equals=" + UPDATED_PREFERRED_GREETING_DESIGNATION);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredGreetingDesignationIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredGreetingDesignation in DEFAULT_PREFERRED_GREETING_DESIGNATION or UPDATED_PREFERRED_GREETING_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldBeFound(
            "preferredGreetingDesignation.in=" + DEFAULT_PREFERRED_GREETING_DESIGNATION + "," + UPDATED_PREFERRED_GREETING_DESIGNATION
        );

        // Get all the salesReceiptEmailPersonaList where preferredGreetingDesignation equals to UPDATED_PREFERRED_GREETING_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredGreetingDesignation.in=" + UPDATED_PREFERRED_GREETING_DESIGNATION);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredGreetingDesignationIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredGreetingDesignation is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredGreetingDesignation.specified=true");

        // Get all the salesReceiptEmailPersonaList where preferredGreetingDesignation is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredGreetingDesignation.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredGreetingDesignationContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredGreetingDesignation contains DEFAULT_PREFERRED_GREETING_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredGreetingDesignation.contains=" + DEFAULT_PREFERRED_GREETING_DESIGNATION);

        // Get all the salesReceiptEmailPersonaList where preferredGreetingDesignation contains UPDATED_PREFERRED_GREETING_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredGreetingDesignation.contains=" + UPDATED_PREFERRED_GREETING_DESIGNATION);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredGreetingDesignationNotContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredGreetingDesignation does not contain DEFAULT_PREFERRED_GREETING_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldNotBeFound(
            "preferredGreetingDesignation.doesNotContain=" + DEFAULT_PREFERRED_GREETING_DESIGNATION
        );

        // Get all the salesReceiptEmailPersonaList where preferredGreetingDesignation does not contain UPDATED_PREFERRED_GREETING_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldBeFound(
            "preferredGreetingDesignation.doesNotContain=" + UPDATED_PREFERRED_GREETING_DESIGNATION
        );
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredPrefixIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredPrefix equals to DEFAULT_PREFERRED_PREFIX
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredPrefix.equals=" + DEFAULT_PREFERRED_PREFIX);

        // Get all the salesReceiptEmailPersonaList where preferredPrefix equals to UPDATED_PREFERRED_PREFIX
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredPrefix.equals=" + UPDATED_PREFERRED_PREFIX);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredPrefixIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredPrefix in DEFAULT_PREFERRED_PREFIX or UPDATED_PREFERRED_PREFIX
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredPrefix.in=" + DEFAULT_PREFERRED_PREFIX + "," + UPDATED_PREFERRED_PREFIX);

        // Get all the salesReceiptEmailPersonaList where preferredPrefix equals to UPDATED_PREFERRED_PREFIX
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredPrefix.in=" + UPDATED_PREFERRED_PREFIX);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredPrefixIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredPrefix is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredPrefix.specified=true");

        // Get all the salesReceiptEmailPersonaList where preferredPrefix is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredPrefix.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredPrefixContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredPrefix contains DEFAULT_PREFERRED_PREFIX
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredPrefix.contains=" + DEFAULT_PREFERRED_PREFIX);

        // Get all the salesReceiptEmailPersonaList where preferredPrefix contains UPDATED_PREFERRED_PREFIX
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredPrefix.contains=" + UPDATED_PREFERRED_PREFIX);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredPrefixNotContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredPrefix does not contain DEFAULT_PREFERRED_PREFIX
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredPrefix.doesNotContain=" + DEFAULT_PREFERRED_PREFIX);

        // Get all the salesReceiptEmailPersonaList where preferredPrefix does not contain UPDATED_PREFERRED_PREFIX
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredPrefix.doesNotContain=" + UPDATED_PREFERRED_PREFIX);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSuffixIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSuffix equals to DEFAULT_PREFERRED_SUFFIX
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredSuffix.equals=" + DEFAULT_PREFERRED_SUFFIX);

        // Get all the salesReceiptEmailPersonaList where preferredSuffix equals to UPDATED_PREFERRED_SUFFIX
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSuffix.equals=" + UPDATED_PREFERRED_SUFFIX);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSuffixIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSuffix in DEFAULT_PREFERRED_SUFFIX or UPDATED_PREFERRED_SUFFIX
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredSuffix.in=" + DEFAULT_PREFERRED_SUFFIX + "," + UPDATED_PREFERRED_SUFFIX);

        // Get all the salesReceiptEmailPersonaList where preferredSuffix equals to UPDATED_PREFERRED_SUFFIX
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSuffix.in=" + UPDATED_PREFERRED_SUFFIX);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSuffixIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSuffix is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredSuffix.specified=true");

        // Get all the salesReceiptEmailPersonaList where preferredSuffix is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSuffix.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSuffixContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSuffix contains DEFAULT_PREFERRED_SUFFIX
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredSuffix.contains=" + DEFAULT_PREFERRED_SUFFIX);

        // Get all the salesReceiptEmailPersonaList where preferredSuffix contains UPDATED_PREFERRED_SUFFIX
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSuffix.contains=" + UPDATED_PREFERRED_SUFFIX);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSuffixNotContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSuffix does not contain DEFAULT_PREFERRED_SUFFIX
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSuffix.doesNotContain=" + DEFAULT_PREFERRED_SUFFIX);

        // Get all the salesReceiptEmailPersonaList where preferredSuffix does not contain UPDATED_PREFERRED_SUFFIX
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredSuffix.doesNotContain=" + UPDATED_PREFERRED_SUFFIX);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByTimeBasedGreetingsIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where timeBasedGreetings equals to DEFAULT_TIME_BASED_GREETINGS
        defaultSalesReceiptEmailPersonaShouldBeFound("timeBasedGreetings.equals=" + DEFAULT_TIME_BASED_GREETINGS);

        // Get all the salesReceiptEmailPersonaList where timeBasedGreetings equals to UPDATED_TIME_BASED_GREETINGS
        defaultSalesReceiptEmailPersonaShouldNotBeFound("timeBasedGreetings.equals=" + UPDATED_TIME_BASED_GREETINGS);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByTimeBasedGreetingsIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where timeBasedGreetings in DEFAULT_TIME_BASED_GREETINGS or UPDATED_TIME_BASED_GREETINGS
        defaultSalesReceiptEmailPersonaShouldBeFound(
            "timeBasedGreetings.in=" + DEFAULT_TIME_BASED_GREETINGS + "," + UPDATED_TIME_BASED_GREETINGS
        );

        // Get all the salesReceiptEmailPersonaList where timeBasedGreetings equals to UPDATED_TIME_BASED_GREETINGS
        defaultSalesReceiptEmailPersonaShouldNotBeFound("timeBasedGreetings.in=" + UPDATED_TIME_BASED_GREETINGS);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByTimeBasedGreetingsIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where timeBasedGreetings is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("timeBasedGreetings.specified=true");

        // Get all the salesReceiptEmailPersonaList where timeBasedGreetings is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("timeBasedGreetings.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasBySloganBasedGreetingIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where sloganBasedGreeting equals to DEFAULT_SLOGAN_BASED_GREETING
        defaultSalesReceiptEmailPersonaShouldBeFound("sloganBasedGreeting.equals=" + DEFAULT_SLOGAN_BASED_GREETING);

        // Get all the salesReceiptEmailPersonaList where sloganBasedGreeting equals to UPDATED_SLOGAN_BASED_GREETING
        defaultSalesReceiptEmailPersonaShouldNotBeFound("sloganBasedGreeting.equals=" + UPDATED_SLOGAN_BASED_GREETING);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasBySloganBasedGreetingIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where sloganBasedGreeting in DEFAULT_SLOGAN_BASED_GREETING or UPDATED_SLOGAN_BASED_GREETING
        defaultSalesReceiptEmailPersonaShouldBeFound(
            "sloganBasedGreeting.in=" + DEFAULT_SLOGAN_BASED_GREETING + "," + UPDATED_SLOGAN_BASED_GREETING
        );

        // Get all the salesReceiptEmailPersonaList where sloganBasedGreeting equals to UPDATED_SLOGAN_BASED_GREETING
        defaultSalesReceiptEmailPersonaShouldNotBeFound("sloganBasedGreeting.in=" + UPDATED_SLOGAN_BASED_GREETING);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasBySloganBasedGreetingIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where sloganBasedGreeting is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("sloganBasedGreeting.specified=true");

        // Get all the salesReceiptEmailPersonaList where sloganBasedGreeting is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("sloganBasedGreeting.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByAddPrefixIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where addPrefix equals to DEFAULT_ADD_PREFIX
        defaultSalesReceiptEmailPersonaShouldBeFound("addPrefix.equals=" + DEFAULT_ADD_PREFIX);

        // Get all the salesReceiptEmailPersonaList where addPrefix equals to UPDATED_ADD_PREFIX
        defaultSalesReceiptEmailPersonaShouldNotBeFound("addPrefix.equals=" + UPDATED_ADD_PREFIX);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByAddPrefixIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where addPrefix in DEFAULT_ADD_PREFIX or UPDATED_ADD_PREFIX
        defaultSalesReceiptEmailPersonaShouldBeFound("addPrefix.in=" + DEFAULT_ADD_PREFIX + "," + UPDATED_ADD_PREFIX);

        // Get all the salesReceiptEmailPersonaList where addPrefix equals to UPDATED_ADD_PREFIX
        defaultSalesReceiptEmailPersonaShouldNotBeFound("addPrefix.in=" + UPDATED_ADD_PREFIX);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByAddPrefixIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where addPrefix is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("addPrefix.specified=true");

        // Get all the salesReceiptEmailPersonaList where addPrefix is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("addPrefix.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByAddSuffixIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where addSuffix equals to DEFAULT_ADD_SUFFIX
        defaultSalesReceiptEmailPersonaShouldBeFound("addSuffix.equals=" + DEFAULT_ADD_SUFFIX);

        // Get all the salesReceiptEmailPersonaList where addSuffix equals to UPDATED_ADD_SUFFIX
        defaultSalesReceiptEmailPersonaShouldNotBeFound("addSuffix.equals=" + UPDATED_ADD_SUFFIX);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByAddSuffixIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where addSuffix in DEFAULT_ADD_SUFFIX or UPDATED_ADD_SUFFIX
        defaultSalesReceiptEmailPersonaShouldBeFound("addSuffix.in=" + DEFAULT_ADD_SUFFIX + "," + UPDATED_ADD_SUFFIX);

        // Get all the salesReceiptEmailPersonaList where addSuffix equals to UPDATED_ADD_SUFFIX
        defaultSalesReceiptEmailPersonaShouldNotBeFound("addSuffix.in=" + UPDATED_ADD_SUFFIX);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByAddSuffixIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where addSuffix is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("addSuffix.specified=true");

        // Get all the salesReceiptEmailPersonaList where addSuffix is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("addSuffix.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSignatureIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSignature equals to DEFAULT_PREFERRED_SIGNATURE
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredSignature.equals=" + DEFAULT_PREFERRED_SIGNATURE);

        // Get all the salesReceiptEmailPersonaList where preferredSignature equals to UPDATED_PREFERRED_SIGNATURE
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSignature.equals=" + UPDATED_PREFERRED_SIGNATURE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSignatureIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSignature in DEFAULT_PREFERRED_SIGNATURE or UPDATED_PREFERRED_SIGNATURE
        defaultSalesReceiptEmailPersonaShouldBeFound(
            "preferredSignature.in=" + DEFAULT_PREFERRED_SIGNATURE + "," + UPDATED_PREFERRED_SIGNATURE
        );

        // Get all the salesReceiptEmailPersonaList where preferredSignature equals to UPDATED_PREFERRED_SIGNATURE
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSignature.in=" + UPDATED_PREFERRED_SIGNATURE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSignatureIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSignature is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredSignature.specified=true");

        // Get all the salesReceiptEmailPersonaList where preferredSignature is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSignature.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSignatureContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSignature contains DEFAULT_PREFERRED_SIGNATURE
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredSignature.contains=" + DEFAULT_PREFERRED_SIGNATURE);

        // Get all the salesReceiptEmailPersonaList where preferredSignature contains UPDATED_PREFERRED_SIGNATURE
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSignature.contains=" + UPDATED_PREFERRED_SIGNATURE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSignatureNotContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSignature does not contain DEFAULT_PREFERRED_SIGNATURE
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSignature.doesNotContain=" + DEFAULT_PREFERRED_SIGNATURE);

        // Get all the salesReceiptEmailPersonaList where preferredSignature does not contain UPDATED_PREFERRED_SIGNATURE
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredSignature.doesNotContain=" + UPDATED_PREFERRED_SIGNATURE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSignatureDesignationIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSignatureDesignation equals to DEFAULT_PREFERRED_SIGNATURE_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredSignatureDesignation.equals=" + DEFAULT_PREFERRED_SIGNATURE_DESIGNATION);

        // Get all the salesReceiptEmailPersonaList where preferredSignatureDesignation equals to UPDATED_PREFERRED_SIGNATURE_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSignatureDesignation.equals=" + UPDATED_PREFERRED_SIGNATURE_DESIGNATION);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSignatureDesignationIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSignatureDesignation in DEFAULT_PREFERRED_SIGNATURE_DESIGNATION or UPDATED_PREFERRED_SIGNATURE_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldBeFound(
            "preferredSignatureDesignation.in=" + DEFAULT_PREFERRED_SIGNATURE_DESIGNATION + "," + UPDATED_PREFERRED_SIGNATURE_DESIGNATION
        );

        // Get all the salesReceiptEmailPersonaList where preferredSignatureDesignation equals to UPDATED_PREFERRED_SIGNATURE_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSignatureDesignation.in=" + UPDATED_PREFERRED_SIGNATURE_DESIGNATION);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSignatureDesignationIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSignatureDesignation is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredSignatureDesignation.specified=true");

        // Get all the salesReceiptEmailPersonaList where preferredSignatureDesignation is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("preferredSignatureDesignation.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSignatureDesignationContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSignatureDesignation contains DEFAULT_PREFERRED_SIGNATURE_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldBeFound("preferredSignatureDesignation.contains=" + DEFAULT_PREFERRED_SIGNATURE_DESIGNATION);

        // Get all the salesReceiptEmailPersonaList where preferredSignatureDesignation contains UPDATED_PREFERRED_SIGNATURE_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldNotBeFound(
            "preferredSignatureDesignation.contains=" + UPDATED_PREFERRED_SIGNATURE_DESIGNATION
        );
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPreferredSignatureDesignationNotContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where preferredSignatureDesignation does not contain DEFAULT_PREFERRED_SIGNATURE_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldNotBeFound(
            "preferredSignatureDesignation.doesNotContain=" + DEFAULT_PREFERRED_SIGNATURE_DESIGNATION
        );

        // Get all the salesReceiptEmailPersonaList where preferredSignatureDesignation does not contain UPDATED_PREFERRED_SIGNATURE_DESIGNATION
        defaultSalesReceiptEmailPersonaShouldBeFound(
            "preferredSignatureDesignation.doesNotContain=" + UPDATED_PREFERRED_SIGNATURE_DESIGNATION
        );
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByIncludeServiceDetailsIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where includeServiceDetails equals to DEFAULT_INCLUDE_SERVICE_DETAILS
        defaultSalesReceiptEmailPersonaShouldBeFound("includeServiceDetails.equals=" + DEFAULT_INCLUDE_SERVICE_DETAILS);

        // Get all the salesReceiptEmailPersonaList where includeServiceDetails equals to UPDATED_INCLUDE_SERVICE_DETAILS
        defaultSalesReceiptEmailPersonaShouldNotBeFound("includeServiceDetails.equals=" + UPDATED_INCLUDE_SERVICE_DETAILS);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByIncludeServiceDetailsIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where includeServiceDetails in DEFAULT_INCLUDE_SERVICE_DETAILS or UPDATED_INCLUDE_SERVICE_DETAILS
        defaultSalesReceiptEmailPersonaShouldBeFound(
            "includeServiceDetails.in=" + DEFAULT_INCLUDE_SERVICE_DETAILS + "," + UPDATED_INCLUDE_SERVICE_DETAILS
        );

        // Get all the salesReceiptEmailPersonaList where includeServiceDetails equals to UPDATED_INCLUDE_SERVICE_DETAILS
        defaultSalesReceiptEmailPersonaShouldNotBeFound("includeServiceDetails.in=" + UPDATED_INCLUDE_SERVICE_DETAILS);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByIncludeServiceDetailsIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where includeServiceDetails is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("includeServiceDetails.specified=true");

        // Get all the salesReceiptEmailPersonaList where includeServiceDetails is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("includeServiceDetails.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByIncludeMessageOfTheDayIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where includeMessageOfTheDay equals to DEFAULT_INCLUDE_MESSAGE_OF_THE_DAY
        defaultSalesReceiptEmailPersonaShouldBeFound("includeMessageOfTheDay.equals=" + DEFAULT_INCLUDE_MESSAGE_OF_THE_DAY);

        // Get all the salesReceiptEmailPersonaList where includeMessageOfTheDay equals to UPDATED_INCLUDE_MESSAGE_OF_THE_DAY
        defaultSalesReceiptEmailPersonaShouldNotBeFound("includeMessageOfTheDay.equals=" + UPDATED_INCLUDE_MESSAGE_OF_THE_DAY);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByIncludeMessageOfTheDayIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where includeMessageOfTheDay in DEFAULT_INCLUDE_MESSAGE_OF_THE_DAY or UPDATED_INCLUDE_MESSAGE_OF_THE_DAY
        defaultSalesReceiptEmailPersonaShouldBeFound(
            "includeMessageOfTheDay.in=" + DEFAULT_INCLUDE_MESSAGE_OF_THE_DAY + "," + UPDATED_INCLUDE_MESSAGE_OF_THE_DAY
        );

        // Get all the salesReceiptEmailPersonaList where includeMessageOfTheDay equals to UPDATED_INCLUDE_MESSAGE_OF_THE_DAY
        defaultSalesReceiptEmailPersonaShouldNotBeFound("includeMessageOfTheDay.in=" + UPDATED_INCLUDE_MESSAGE_OF_THE_DAY);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByIncludeMessageOfTheDayIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where includeMessageOfTheDay is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("includeMessageOfTheDay.specified=true");

        // Get all the salesReceiptEmailPersonaList where includeMessageOfTheDay is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("includeMessageOfTheDay.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByIncludeTreasuryQuoteIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where includeTreasuryQuote equals to DEFAULT_INCLUDE_TREASURY_QUOTE
        defaultSalesReceiptEmailPersonaShouldBeFound("includeTreasuryQuote.equals=" + DEFAULT_INCLUDE_TREASURY_QUOTE);

        // Get all the salesReceiptEmailPersonaList where includeTreasuryQuote equals to UPDATED_INCLUDE_TREASURY_QUOTE
        defaultSalesReceiptEmailPersonaShouldNotBeFound("includeTreasuryQuote.equals=" + UPDATED_INCLUDE_TREASURY_QUOTE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByIncludeTreasuryQuoteIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where includeTreasuryQuote in DEFAULT_INCLUDE_TREASURY_QUOTE or UPDATED_INCLUDE_TREASURY_QUOTE
        defaultSalesReceiptEmailPersonaShouldBeFound(
            "includeTreasuryQuote.in=" + DEFAULT_INCLUDE_TREASURY_QUOTE + "," + UPDATED_INCLUDE_TREASURY_QUOTE
        );

        // Get all the salesReceiptEmailPersonaList where includeTreasuryQuote equals to UPDATED_INCLUDE_TREASURY_QUOTE
        defaultSalesReceiptEmailPersonaShouldNotBeFound("includeTreasuryQuote.in=" + UPDATED_INCLUDE_TREASURY_QUOTE);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByIncludeTreasuryQuoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where includeTreasuryQuote is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("includeTreasuryQuote.specified=true");

        // Get all the salesReceiptEmailPersonaList where includeTreasuryQuote is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("includeTreasuryQuote.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where createdAt equals to DEFAULT_CREATED_AT
        defaultSalesReceiptEmailPersonaShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the salesReceiptEmailPersonaList where createdAt equals to UPDATED_CREATED_AT
        defaultSalesReceiptEmailPersonaShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultSalesReceiptEmailPersonaShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the salesReceiptEmailPersonaList where createdAt equals to UPDATED_CREATED_AT
        defaultSalesReceiptEmailPersonaShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where createdAt is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("createdAt.specified=true");

        // Get all the salesReceiptEmailPersonaList where createdAt is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultSalesReceiptEmailPersonaShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the salesReceiptEmailPersonaList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultSalesReceiptEmailPersonaShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultSalesReceiptEmailPersonaShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the salesReceiptEmailPersonaList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultSalesReceiptEmailPersonaShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where createdAt is less than DEFAULT_CREATED_AT
        defaultSalesReceiptEmailPersonaShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the salesReceiptEmailPersonaList where createdAt is less than UPDATED_CREATED_AT
        defaultSalesReceiptEmailPersonaShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where createdAt is greater than DEFAULT_CREATED_AT
        defaultSalesReceiptEmailPersonaShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the salesReceiptEmailPersonaList where createdAt is greater than SMALLER_CREATED_AT
        defaultSalesReceiptEmailPersonaShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLastModifedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt equals to DEFAULT_LAST_MODIFED_AT
        defaultSalesReceiptEmailPersonaShouldBeFound("lastModifedAt.equals=" + DEFAULT_LAST_MODIFED_AT);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt equals to UPDATED_LAST_MODIFED_AT
        defaultSalesReceiptEmailPersonaShouldNotBeFound("lastModifedAt.equals=" + UPDATED_LAST_MODIFED_AT);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLastModifedAtIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt in DEFAULT_LAST_MODIFED_AT or UPDATED_LAST_MODIFED_AT
        defaultSalesReceiptEmailPersonaShouldBeFound("lastModifedAt.in=" + DEFAULT_LAST_MODIFED_AT + "," + UPDATED_LAST_MODIFED_AT);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt equals to UPDATED_LAST_MODIFED_AT
        defaultSalesReceiptEmailPersonaShouldNotBeFound("lastModifedAt.in=" + UPDATED_LAST_MODIFED_AT);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLastModifedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("lastModifedAt.specified=true");

        // Get all the salesReceiptEmailPersonaList where lastModifedAt is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("lastModifedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLastModifedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt is greater than or equal to DEFAULT_LAST_MODIFED_AT
        defaultSalesReceiptEmailPersonaShouldBeFound("lastModifedAt.greaterThanOrEqual=" + DEFAULT_LAST_MODIFED_AT);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt is greater than or equal to UPDATED_LAST_MODIFED_AT
        defaultSalesReceiptEmailPersonaShouldNotBeFound("lastModifedAt.greaterThanOrEqual=" + UPDATED_LAST_MODIFED_AT);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLastModifedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt is less than or equal to DEFAULT_LAST_MODIFED_AT
        defaultSalesReceiptEmailPersonaShouldBeFound("lastModifedAt.lessThanOrEqual=" + DEFAULT_LAST_MODIFED_AT);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt is less than or equal to SMALLER_LAST_MODIFED_AT
        defaultSalesReceiptEmailPersonaShouldNotBeFound("lastModifedAt.lessThanOrEqual=" + SMALLER_LAST_MODIFED_AT);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLastModifedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt is less than DEFAULT_LAST_MODIFED_AT
        defaultSalesReceiptEmailPersonaShouldNotBeFound("lastModifedAt.lessThan=" + DEFAULT_LAST_MODIFED_AT);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt is less than UPDATED_LAST_MODIFED_AT
        defaultSalesReceiptEmailPersonaShouldBeFound("lastModifedAt.lessThan=" + UPDATED_LAST_MODIFED_AT);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLastModifedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt is greater than DEFAULT_LAST_MODIFED_AT
        defaultSalesReceiptEmailPersonaShouldNotBeFound("lastModifedAt.greaterThan=" + DEFAULT_LAST_MODIFED_AT);

        // Get all the salesReceiptEmailPersonaList where lastModifedAt is greater than SMALLER_LAST_MODIFED_AT
        defaultSalesReceiptEmailPersonaShouldBeFound("lastModifedAt.greaterThan=" + SMALLER_LAST_MODIFED_AT);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPersonaNameIsEqualToSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where personaName equals to DEFAULT_PERSONA_NAME
        defaultSalesReceiptEmailPersonaShouldBeFound("personaName.equals=" + DEFAULT_PERSONA_NAME);

        // Get all the salesReceiptEmailPersonaList where personaName equals to UPDATED_PERSONA_NAME
        defaultSalesReceiptEmailPersonaShouldNotBeFound("personaName.equals=" + UPDATED_PERSONA_NAME);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPersonaNameIsInShouldWork() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where personaName in DEFAULT_PERSONA_NAME or UPDATED_PERSONA_NAME
        defaultSalesReceiptEmailPersonaShouldBeFound("personaName.in=" + DEFAULT_PERSONA_NAME + "," + UPDATED_PERSONA_NAME);

        // Get all the salesReceiptEmailPersonaList where personaName equals to UPDATED_PERSONA_NAME
        defaultSalesReceiptEmailPersonaShouldNotBeFound("personaName.in=" + UPDATED_PERSONA_NAME);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPersonaNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where personaName is not null
        defaultSalesReceiptEmailPersonaShouldBeFound("personaName.specified=true");

        // Get all the salesReceiptEmailPersonaList where personaName is null
        defaultSalesReceiptEmailPersonaShouldNotBeFound("personaName.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPersonaNameContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where personaName contains DEFAULT_PERSONA_NAME
        defaultSalesReceiptEmailPersonaShouldBeFound("personaName.contains=" + DEFAULT_PERSONA_NAME);

        // Get all the salesReceiptEmailPersonaList where personaName contains UPDATED_PERSONA_NAME
        defaultSalesReceiptEmailPersonaShouldNotBeFound("personaName.contains=" + UPDATED_PERSONA_NAME);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByPersonaNameNotContainsSomething() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        // Get all the salesReceiptEmailPersonaList where personaName does not contain DEFAULT_PERSONA_NAME
        defaultSalesReceiptEmailPersonaShouldNotBeFound("personaName.doesNotContain=" + DEFAULT_PERSONA_NAME);

        // Get all the salesReceiptEmailPersonaList where personaName does not contain UPDATED_PERSONA_NAME
        defaultSalesReceiptEmailPersonaShouldBeFound("personaName.doesNotContain=" + UPDATED_PERSONA_NAME);
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByCreatedByIsEqualToSomething() throws Exception {
        ApplicationUser createdBy;
        if (TestUtil.findAll(em, ApplicationUser.class).isEmpty()) {
            salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);
            createdBy = ApplicationUserResourceIT.createEntity(em);
        } else {
            createdBy = TestUtil.findAll(em, ApplicationUser.class).get(0);
        }
        em.persist(createdBy);
        em.flush();
        salesReceiptEmailPersona.setCreatedBy(createdBy);
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);
        Long createdById = createdBy.getId();

        // Get all the salesReceiptEmailPersonaList where createdBy equals to createdById
        defaultSalesReceiptEmailPersonaShouldBeFound("createdById.equals=" + createdById);

        // Get all the salesReceiptEmailPersonaList where createdBy equals to (createdById + 1)
        defaultSalesReceiptEmailPersonaShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByLastModifiedByIsEqualToSomething() throws Exception {
        ApplicationUser lastModifiedBy;
        if (TestUtil.findAll(em, ApplicationUser.class).isEmpty()) {
            salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);
            lastModifiedBy = ApplicationUserResourceIT.createEntity(em);
        } else {
            lastModifiedBy = TestUtil.findAll(em, ApplicationUser.class).get(0);
        }
        em.persist(lastModifiedBy);
        em.flush();
        salesReceiptEmailPersona.setLastModifiedBy(lastModifiedBy);
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);
        Long lastModifiedById = lastModifiedBy.getId();

        // Get all the salesReceiptEmailPersonaList where lastModifiedBy equals to lastModifiedById
        defaultSalesReceiptEmailPersonaShouldBeFound("lastModifiedById.equals=" + lastModifiedById);

        // Get all the salesReceiptEmailPersonaList where lastModifiedBy equals to (lastModifiedById + 1)
        defaultSalesReceiptEmailPersonaShouldNotBeFound("lastModifiedById.equals=" + (lastModifiedById + 1));
    }

    @Test
    @Transactional
    void getAllSalesReceiptEmailPersonasByContributorIsEqualToSomething() throws Exception {
        Dealer contributor;
        if (TestUtil.findAll(em, Dealer.class).isEmpty()) {
            salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);
            contributor = DealerResourceIT.createEntity(em);
        } else {
            contributor = TestUtil.findAll(em, Dealer.class).get(0);
        }
        em.persist(contributor);
        em.flush();
        salesReceiptEmailPersona.setContributor(contributor);
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);
        Long contributorId = contributor.getId();

        // Get all the salesReceiptEmailPersonaList where contributor equals to contributorId
        defaultSalesReceiptEmailPersonaShouldBeFound("contributorId.equals=" + contributorId);

        // Get all the salesReceiptEmailPersonaList where contributor equals to (contributorId + 1)
        defaultSalesReceiptEmailPersonaShouldNotBeFound("contributorId.equals=" + (contributorId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSalesReceiptEmailPersonaShouldBeFound(String filter) throws Exception {
        restSalesReceiptEmailPersonaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceiptEmailPersona.getId().intValue())))
            .andExpect(jsonPath("$.[*].emailIdentifier").value(hasItem(DEFAULT_EMAIL_IDENTIFIER.toString())))
            .andExpect(jsonPath("$.[*].mainEmail").value(hasItem(DEFAULT_MAIN_EMAIL)))
            .andExpect(jsonPath("$.[*].clearCopyEmail").value(hasItem(DEFAULT_CLEAR_COPY_EMAIL.booleanValue())))
            .andExpect(jsonPath("$.[*].blindCopyEmail").value(hasItem(DEFAULT_BLIND_COPY_EMAIL.booleanValue())))
            .andExpect(jsonPath("$.[*].languageKeyCode").value(hasItem(DEFAULT_LANGUAGE_KEY_CODE)))
            .andExpect(jsonPath("$.[*].preferredGreeting").value(hasItem(DEFAULT_PREFERRED_GREETING)))
            .andExpect(jsonPath("$.[*].preferredGreetingDesignation").value(hasItem(DEFAULT_PREFERRED_GREETING_DESIGNATION)))
            .andExpect(jsonPath("$.[*].preferredPrefix").value(hasItem(DEFAULT_PREFERRED_PREFIX)))
            .andExpect(jsonPath("$.[*].preferredSuffix").value(hasItem(DEFAULT_PREFERRED_SUFFIX)))
            .andExpect(jsonPath("$.[*].timeBasedGreetings").value(hasItem(DEFAULT_TIME_BASED_GREETINGS.booleanValue())))
            .andExpect(jsonPath("$.[*].sloganBasedGreeting").value(hasItem(DEFAULT_SLOGAN_BASED_GREETING.booleanValue())))
            .andExpect(jsonPath("$.[*].addPrefix").value(hasItem(DEFAULT_ADD_PREFIX.booleanValue())))
            .andExpect(jsonPath("$.[*].addSuffix").value(hasItem(DEFAULT_ADD_SUFFIX.booleanValue())))
            .andExpect(jsonPath("$.[*].preferredSignature").value(hasItem(DEFAULT_PREFERRED_SIGNATURE)))
            .andExpect(jsonPath("$.[*].preferredSignatureDesignation").value(hasItem(DEFAULT_PREFERRED_SIGNATURE_DESIGNATION)))
            .andExpect(jsonPath("$.[*].includeServiceDetails").value(hasItem(DEFAULT_INCLUDE_SERVICE_DETAILS.booleanValue())))
            .andExpect(jsonPath("$.[*].includeMessageOfTheDay").value(hasItem(DEFAULT_INCLUDE_MESSAGE_OF_THE_DAY.booleanValue())))
            .andExpect(jsonPath("$.[*].includeTreasuryQuote").value(hasItem(DEFAULT_INCLUDE_TREASURY_QUOTE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].lastModifedAt").value(hasItem(sameInstant(DEFAULT_LAST_MODIFED_AT))))
            .andExpect(jsonPath("$.[*].personaName").value(hasItem(DEFAULT_PERSONA_NAME)));

        // Check, that the count call also returns 1
        restSalesReceiptEmailPersonaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSalesReceiptEmailPersonaShouldNotBeFound(String filter) throws Exception {
        restSalesReceiptEmailPersonaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSalesReceiptEmailPersonaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSalesReceiptEmailPersona() throws Exception {
        // Get the salesReceiptEmailPersona
        restSalesReceiptEmailPersonaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSalesReceiptEmailPersona() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        int databaseSizeBeforeUpdate = salesReceiptEmailPersonaRepository.findAll().size();
        salesReceiptEmailPersonaSearchRepository.save(salesReceiptEmailPersona);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());

        // Update the salesReceiptEmailPersona
        SalesReceiptEmailPersona updatedSalesReceiptEmailPersona = salesReceiptEmailPersonaRepository
            .findById(salesReceiptEmailPersona.getId())
            .get();
        // Disconnect from session so that the updates on updatedSalesReceiptEmailPersona are not directly saved in db
        em.detach(updatedSalesReceiptEmailPersona);
        updatedSalesReceiptEmailPersona
            .emailIdentifier(UPDATED_EMAIL_IDENTIFIER)
            .mainEmail(UPDATED_MAIN_EMAIL)
            .clearCopyEmail(UPDATED_CLEAR_COPY_EMAIL)
            .blindCopyEmail(UPDATED_BLIND_COPY_EMAIL)
            .languageKeyCode(UPDATED_LANGUAGE_KEY_CODE)
            .preferredGreeting(UPDATED_PREFERRED_GREETING)
            .preferredGreetingDesignation(UPDATED_PREFERRED_GREETING_DESIGNATION)
            .preferredPrefix(UPDATED_PREFERRED_PREFIX)
            .preferredSuffix(UPDATED_PREFERRED_SUFFIX)
            .timeBasedGreetings(UPDATED_TIME_BASED_GREETINGS)
            .sloganBasedGreeting(UPDATED_SLOGAN_BASED_GREETING)
            .addPrefix(UPDATED_ADD_PREFIX)
            .addSuffix(UPDATED_ADD_SUFFIX)
            .preferredSignature(UPDATED_PREFERRED_SIGNATURE)
            .preferredSignatureDesignation(UPDATED_PREFERRED_SIGNATURE_DESIGNATION)
            .includeServiceDetails(UPDATED_INCLUDE_SERVICE_DETAILS)
            .includeMessageOfTheDay(UPDATED_INCLUDE_MESSAGE_OF_THE_DAY)
            .includeTreasuryQuote(UPDATED_INCLUDE_TREASURY_QUOTE)
            .createdAt(UPDATED_CREATED_AT)
            .lastModifedAt(UPDATED_LAST_MODIFED_AT)
            .personaName(UPDATED_PERSONA_NAME);
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(updatedSalesReceiptEmailPersona);

        restSalesReceiptEmailPersonaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesReceiptEmailPersonaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceiptEmailPersona in the database
        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeUpdate);
        SalesReceiptEmailPersona testSalesReceiptEmailPersona = salesReceiptEmailPersonaList.get(salesReceiptEmailPersonaList.size() - 1);
        assertThat(testSalesReceiptEmailPersona.getEmailIdentifier()).isEqualTo(UPDATED_EMAIL_IDENTIFIER);
        assertThat(testSalesReceiptEmailPersona.getMainEmail()).isEqualTo(UPDATED_MAIN_EMAIL);
        assertThat(testSalesReceiptEmailPersona.getClearCopyEmail()).isEqualTo(UPDATED_CLEAR_COPY_EMAIL);
        assertThat(testSalesReceiptEmailPersona.getBlindCopyEmail()).isEqualTo(UPDATED_BLIND_COPY_EMAIL);
        assertThat(testSalesReceiptEmailPersona.getLanguageKeyCode()).isEqualTo(UPDATED_LANGUAGE_KEY_CODE);
        assertThat(testSalesReceiptEmailPersona.getPreferredGreeting()).isEqualTo(UPDATED_PREFERRED_GREETING);
        assertThat(testSalesReceiptEmailPersona.getPreferredGreetingDesignation()).isEqualTo(UPDATED_PREFERRED_GREETING_DESIGNATION);
        assertThat(testSalesReceiptEmailPersona.getPreferredPrefix()).isEqualTo(UPDATED_PREFERRED_PREFIX);
        assertThat(testSalesReceiptEmailPersona.getPreferredSuffix()).isEqualTo(UPDATED_PREFERRED_SUFFIX);
        assertThat(testSalesReceiptEmailPersona.getTimeBasedGreetings()).isEqualTo(UPDATED_TIME_BASED_GREETINGS);
        assertThat(testSalesReceiptEmailPersona.getSloganBasedGreeting()).isEqualTo(UPDATED_SLOGAN_BASED_GREETING);
        assertThat(testSalesReceiptEmailPersona.getAddPrefix()).isEqualTo(UPDATED_ADD_PREFIX);
        assertThat(testSalesReceiptEmailPersona.getAddSuffix()).isEqualTo(UPDATED_ADD_SUFFIX);
        assertThat(testSalesReceiptEmailPersona.getPreferredSignature()).isEqualTo(UPDATED_PREFERRED_SIGNATURE);
        assertThat(testSalesReceiptEmailPersona.getPreferredSignatureDesignation()).isEqualTo(UPDATED_PREFERRED_SIGNATURE_DESIGNATION);
        assertThat(testSalesReceiptEmailPersona.getIncludeServiceDetails()).isEqualTo(UPDATED_INCLUDE_SERVICE_DETAILS);
        assertThat(testSalesReceiptEmailPersona.getIncludeMessageOfTheDay()).isEqualTo(UPDATED_INCLUDE_MESSAGE_OF_THE_DAY);
        assertThat(testSalesReceiptEmailPersona.getIncludeTreasuryQuote()).isEqualTo(UPDATED_INCLUDE_TREASURY_QUOTE);
        assertThat(testSalesReceiptEmailPersona.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testSalesReceiptEmailPersona.getLastModifedAt()).isEqualTo(UPDATED_LAST_MODIFED_AT);
        assertThat(testSalesReceiptEmailPersona.getPersonaName()).isEqualTo(UPDATED_PERSONA_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SalesReceiptEmailPersona> salesReceiptEmailPersonaSearchList = IterableUtils.toList(
                    salesReceiptEmailPersonaSearchRepository.findAll()
                );
                SalesReceiptEmailPersona testSalesReceiptEmailPersonaSearch = salesReceiptEmailPersonaSearchList.get(
                    searchDatabaseSizeAfter - 1
                );
                assertThat(testSalesReceiptEmailPersonaSearch.getEmailIdentifier()).isEqualTo(UPDATED_EMAIL_IDENTIFIER);
                assertThat(testSalesReceiptEmailPersonaSearch.getMainEmail()).isEqualTo(UPDATED_MAIN_EMAIL);
                assertThat(testSalesReceiptEmailPersonaSearch.getClearCopyEmail()).isEqualTo(UPDATED_CLEAR_COPY_EMAIL);
                assertThat(testSalesReceiptEmailPersonaSearch.getBlindCopyEmail()).isEqualTo(UPDATED_BLIND_COPY_EMAIL);
                assertThat(testSalesReceiptEmailPersonaSearch.getLanguageKeyCode()).isEqualTo(UPDATED_LANGUAGE_KEY_CODE);
                assertThat(testSalesReceiptEmailPersonaSearch.getPreferredGreeting()).isEqualTo(UPDATED_PREFERRED_GREETING);
                assertThat(testSalesReceiptEmailPersonaSearch.getPreferredGreetingDesignation())
                    .isEqualTo(UPDATED_PREFERRED_GREETING_DESIGNATION);
                assertThat(testSalesReceiptEmailPersonaSearch.getPreferredPrefix()).isEqualTo(UPDATED_PREFERRED_PREFIX);
                assertThat(testSalesReceiptEmailPersonaSearch.getPreferredSuffix()).isEqualTo(UPDATED_PREFERRED_SUFFIX);
                assertThat(testSalesReceiptEmailPersonaSearch.getTimeBasedGreetings()).isEqualTo(UPDATED_TIME_BASED_GREETINGS);
                assertThat(testSalesReceiptEmailPersonaSearch.getSloganBasedGreeting()).isEqualTo(UPDATED_SLOGAN_BASED_GREETING);
                assertThat(testSalesReceiptEmailPersonaSearch.getAddPrefix()).isEqualTo(UPDATED_ADD_PREFIX);
                assertThat(testSalesReceiptEmailPersonaSearch.getAddSuffix()).isEqualTo(UPDATED_ADD_SUFFIX);
                assertThat(testSalesReceiptEmailPersonaSearch.getPreferredSignature()).isEqualTo(UPDATED_PREFERRED_SIGNATURE);
                assertThat(testSalesReceiptEmailPersonaSearch.getPreferredSignatureDesignation())
                    .isEqualTo(UPDATED_PREFERRED_SIGNATURE_DESIGNATION);
                assertThat(testSalesReceiptEmailPersonaSearch.getIncludeServiceDetails()).isEqualTo(UPDATED_INCLUDE_SERVICE_DETAILS);
                assertThat(testSalesReceiptEmailPersonaSearch.getIncludeMessageOfTheDay()).isEqualTo(UPDATED_INCLUDE_MESSAGE_OF_THE_DAY);
                assertThat(testSalesReceiptEmailPersonaSearch.getIncludeTreasuryQuote()).isEqualTo(UPDATED_INCLUDE_TREASURY_QUOTE);
                assertThat(testSalesReceiptEmailPersonaSearch.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
                assertThat(testSalesReceiptEmailPersonaSearch.getLastModifedAt()).isEqualTo(UPDATED_LAST_MODIFED_AT);
                assertThat(testSalesReceiptEmailPersonaSearch.getPersonaName()).isEqualTo(UPDATED_PERSONA_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingSalesReceiptEmailPersona() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        salesReceiptEmailPersona.setId(count.incrementAndGet());

        // Create the SalesReceiptEmailPersona
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesReceiptEmailPersonaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesReceiptEmailPersonaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptEmailPersona in the database
        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSalesReceiptEmailPersona() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        salesReceiptEmailPersona.setId(count.incrementAndGet());

        // Create the SalesReceiptEmailPersona
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptEmailPersonaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptEmailPersona in the database
        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSalesReceiptEmailPersona() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        salesReceiptEmailPersona.setId(count.incrementAndGet());

        // Create the SalesReceiptEmailPersona
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptEmailPersonaMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesReceiptEmailPersona in the database
        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSalesReceiptEmailPersonaWithPatch() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        int databaseSizeBeforeUpdate = salesReceiptEmailPersonaRepository.findAll().size();

        // Update the salesReceiptEmailPersona using partial update
        SalesReceiptEmailPersona partialUpdatedSalesReceiptEmailPersona = new SalesReceiptEmailPersona();
        partialUpdatedSalesReceiptEmailPersona.setId(salesReceiptEmailPersona.getId());

        partialUpdatedSalesReceiptEmailPersona
            .emailIdentifier(UPDATED_EMAIL_IDENTIFIER)
            .blindCopyEmail(UPDATED_BLIND_COPY_EMAIL)
            .languageKeyCode(UPDATED_LANGUAGE_KEY_CODE)
            .timeBasedGreetings(UPDATED_TIME_BASED_GREETINGS)
            .sloganBasedGreeting(UPDATED_SLOGAN_BASED_GREETING)
            .addPrefix(UPDATED_ADD_PREFIX)
            .addSuffix(UPDATED_ADD_SUFFIX)
            .createdAt(UPDATED_CREATED_AT)
            .lastModifedAt(UPDATED_LAST_MODIFED_AT)
            .personaName(UPDATED_PERSONA_NAME);

        restSalesReceiptEmailPersonaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesReceiptEmailPersona.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesReceiptEmailPersona))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceiptEmailPersona in the database
        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeUpdate);
        SalesReceiptEmailPersona testSalesReceiptEmailPersona = salesReceiptEmailPersonaList.get(salesReceiptEmailPersonaList.size() - 1);
        assertThat(testSalesReceiptEmailPersona.getEmailIdentifier()).isEqualTo(UPDATED_EMAIL_IDENTIFIER);
        assertThat(testSalesReceiptEmailPersona.getMainEmail()).isEqualTo(DEFAULT_MAIN_EMAIL);
        assertThat(testSalesReceiptEmailPersona.getClearCopyEmail()).isEqualTo(DEFAULT_CLEAR_COPY_EMAIL);
        assertThat(testSalesReceiptEmailPersona.getBlindCopyEmail()).isEqualTo(UPDATED_BLIND_COPY_EMAIL);
        assertThat(testSalesReceiptEmailPersona.getLanguageKeyCode()).isEqualTo(UPDATED_LANGUAGE_KEY_CODE);
        assertThat(testSalesReceiptEmailPersona.getPreferredGreeting()).isEqualTo(DEFAULT_PREFERRED_GREETING);
        assertThat(testSalesReceiptEmailPersona.getPreferredGreetingDesignation()).isEqualTo(DEFAULT_PREFERRED_GREETING_DESIGNATION);
        assertThat(testSalesReceiptEmailPersona.getPreferredPrefix()).isEqualTo(DEFAULT_PREFERRED_PREFIX);
        assertThat(testSalesReceiptEmailPersona.getPreferredSuffix()).isEqualTo(DEFAULT_PREFERRED_SUFFIX);
        assertThat(testSalesReceiptEmailPersona.getTimeBasedGreetings()).isEqualTo(UPDATED_TIME_BASED_GREETINGS);
        assertThat(testSalesReceiptEmailPersona.getSloganBasedGreeting()).isEqualTo(UPDATED_SLOGAN_BASED_GREETING);
        assertThat(testSalesReceiptEmailPersona.getAddPrefix()).isEqualTo(UPDATED_ADD_PREFIX);
        assertThat(testSalesReceiptEmailPersona.getAddSuffix()).isEqualTo(UPDATED_ADD_SUFFIX);
        assertThat(testSalesReceiptEmailPersona.getPreferredSignature()).isEqualTo(DEFAULT_PREFERRED_SIGNATURE);
        assertThat(testSalesReceiptEmailPersona.getPreferredSignatureDesignation()).isEqualTo(DEFAULT_PREFERRED_SIGNATURE_DESIGNATION);
        assertThat(testSalesReceiptEmailPersona.getIncludeServiceDetails()).isEqualTo(DEFAULT_INCLUDE_SERVICE_DETAILS);
        assertThat(testSalesReceiptEmailPersona.getIncludeMessageOfTheDay()).isEqualTo(DEFAULT_INCLUDE_MESSAGE_OF_THE_DAY);
        assertThat(testSalesReceiptEmailPersona.getIncludeTreasuryQuote()).isEqualTo(DEFAULT_INCLUDE_TREASURY_QUOTE);
        assertThat(testSalesReceiptEmailPersona.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testSalesReceiptEmailPersona.getLastModifedAt()).isEqualTo(UPDATED_LAST_MODIFED_AT);
        assertThat(testSalesReceiptEmailPersona.getPersonaName()).isEqualTo(UPDATED_PERSONA_NAME);
    }

    @Test
    @Transactional
    void fullUpdateSalesReceiptEmailPersonaWithPatch() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);

        int databaseSizeBeforeUpdate = salesReceiptEmailPersonaRepository.findAll().size();

        // Update the salesReceiptEmailPersona using partial update
        SalesReceiptEmailPersona partialUpdatedSalesReceiptEmailPersona = new SalesReceiptEmailPersona();
        partialUpdatedSalesReceiptEmailPersona.setId(salesReceiptEmailPersona.getId());

        partialUpdatedSalesReceiptEmailPersona
            .emailIdentifier(UPDATED_EMAIL_IDENTIFIER)
            .mainEmail(UPDATED_MAIN_EMAIL)
            .clearCopyEmail(UPDATED_CLEAR_COPY_EMAIL)
            .blindCopyEmail(UPDATED_BLIND_COPY_EMAIL)
            .languageKeyCode(UPDATED_LANGUAGE_KEY_CODE)
            .preferredGreeting(UPDATED_PREFERRED_GREETING)
            .preferredGreetingDesignation(UPDATED_PREFERRED_GREETING_DESIGNATION)
            .preferredPrefix(UPDATED_PREFERRED_PREFIX)
            .preferredSuffix(UPDATED_PREFERRED_SUFFIX)
            .timeBasedGreetings(UPDATED_TIME_BASED_GREETINGS)
            .sloganBasedGreeting(UPDATED_SLOGAN_BASED_GREETING)
            .addPrefix(UPDATED_ADD_PREFIX)
            .addSuffix(UPDATED_ADD_SUFFIX)
            .preferredSignature(UPDATED_PREFERRED_SIGNATURE)
            .preferredSignatureDesignation(UPDATED_PREFERRED_SIGNATURE_DESIGNATION)
            .includeServiceDetails(UPDATED_INCLUDE_SERVICE_DETAILS)
            .includeMessageOfTheDay(UPDATED_INCLUDE_MESSAGE_OF_THE_DAY)
            .includeTreasuryQuote(UPDATED_INCLUDE_TREASURY_QUOTE)
            .createdAt(UPDATED_CREATED_AT)
            .lastModifedAt(UPDATED_LAST_MODIFED_AT)
            .personaName(UPDATED_PERSONA_NAME);

        restSalesReceiptEmailPersonaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesReceiptEmailPersona.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesReceiptEmailPersona))
            )
            .andExpect(status().isOk());

        // Validate the SalesReceiptEmailPersona in the database
        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeUpdate);
        SalesReceiptEmailPersona testSalesReceiptEmailPersona = salesReceiptEmailPersonaList.get(salesReceiptEmailPersonaList.size() - 1);
        assertThat(testSalesReceiptEmailPersona.getEmailIdentifier()).isEqualTo(UPDATED_EMAIL_IDENTIFIER);
        assertThat(testSalesReceiptEmailPersona.getMainEmail()).isEqualTo(UPDATED_MAIN_EMAIL);
        assertThat(testSalesReceiptEmailPersona.getClearCopyEmail()).isEqualTo(UPDATED_CLEAR_COPY_EMAIL);
        assertThat(testSalesReceiptEmailPersona.getBlindCopyEmail()).isEqualTo(UPDATED_BLIND_COPY_EMAIL);
        assertThat(testSalesReceiptEmailPersona.getLanguageKeyCode()).isEqualTo(UPDATED_LANGUAGE_KEY_CODE);
        assertThat(testSalesReceiptEmailPersona.getPreferredGreeting()).isEqualTo(UPDATED_PREFERRED_GREETING);
        assertThat(testSalesReceiptEmailPersona.getPreferredGreetingDesignation()).isEqualTo(UPDATED_PREFERRED_GREETING_DESIGNATION);
        assertThat(testSalesReceiptEmailPersona.getPreferredPrefix()).isEqualTo(UPDATED_PREFERRED_PREFIX);
        assertThat(testSalesReceiptEmailPersona.getPreferredSuffix()).isEqualTo(UPDATED_PREFERRED_SUFFIX);
        assertThat(testSalesReceiptEmailPersona.getTimeBasedGreetings()).isEqualTo(UPDATED_TIME_BASED_GREETINGS);
        assertThat(testSalesReceiptEmailPersona.getSloganBasedGreeting()).isEqualTo(UPDATED_SLOGAN_BASED_GREETING);
        assertThat(testSalesReceiptEmailPersona.getAddPrefix()).isEqualTo(UPDATED_ADD_PREFIX);
        assertThat(testSalesReceiptEmailPersona.getAddSuffix()).isEqualTo(UPDATED_ADD_SUFFIX);
        assertThat(testSalesReceiptEmailPersona.getPreferredSignature()).isEqualTo(UPDATED_PREFERRED_SIGNATURE);
        assertThat(testSalesReceiptEmailPersona.getPreferredSignatureDesignation()).isEqualTo(UPDATED_PREFERRED_SIGNATURE_DESIGNATION);
        assertThat(testSalesReceiptEmailPersona.getIncludeServiceDetails()).isEqualTo(UPDATED_INCLUDE_SERVICE_DETAILS);
        assertThat(testSalesReceiptEmailPersona.getIncludeMessageOfTheDay()).isEqualTo(UPDATED_INCLUDE_MESSAGE_OF_THE_DAY);
        assertThat(testSalesReceiptEmailPersona.getIncludeTreasuryQuote()).isEqualTo(UPDATED_INCLUDE_TREASURY_QUOTE);
        assertThat(testSalesReceiptEmailPersona.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testSalesReceiptEmailPersona.getLastModifedAt()).isEqualTo(UPDATED_LAST_MODIFED_AT);
        assertThat(testSalesReceiptEmailPersona.getPersonaName()).isEqualTo(UPDATED_PERSONA_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingSalesReceiptEmailPersona() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        salesReceiptEmailPersona.setId(count.incrementAndGet());

        // Create the SalesReceiptEmailPersona
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesReceiptEmailPersonaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salesReceiptEmailPersonaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptEmailPersona in the database
        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSalesReceiptEmailPersona() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        salesReceiptEmailPersona.setId(count.incrementAndGet());

        // Create the SalesReceiptEmailPersona
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptEmailPersonaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesReceiptEmailPersona in the database
        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSalesReceiptEmailPersona() throws Exception {
        int databaseSizeBeforeUpdate = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        salesReceiptEmailPersona.setId(count.incrementAndGet());

        // Create the SalesReceiptEmailPersona
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = salesReceiptEmailPersonaMapper.toDto(salesReceiptEmailPersona);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesReceiptEmailPersonaMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesReceiptEmailPersonaDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesReceiptEmailPersona in the database
        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSalesReceiptEmailPersona() throws Exception {
        // Initialize the database
        salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);
        salesReceiptEmailPersonaRepository.save(salesReceiptEmailPersona);
        salesReceiptEmailPersonaSearchRepository.save(salesReceiptEmailPersona);

        int databaseSizeBeforeDelete = salesReceiptEmailPersonaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the salesReceiptEmailPersona
        restSalesReceiptEmailPersonaMockMvc
            .perform(delete(ENTITY_API_URL_ID, salesReceiptEmailPersona.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SalesReceiptEmailPersona> salesReceiptEmailPersonaList = salesReceiptEmailPersonaRepository.findAll();
        assertThat(salesReceiptEmailPersonaList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(salesReceiptEmailPersonaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSalesReceiptEmailPersona() throws Exception {
        // Initialize the database
        salesReceiptEmailPersona = salesReceiptEmailPersonaRepository.saveAndFlush(salesReceiptEmailPersona);
        salesReceiptEmailPersonaSearchRepository.save(salesReceiptEmailPersona);

        // Search the salesReceiptEmailPersona
        restSalesReceiptEmailPersonaMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + salesReceiptEmailPersona.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesReceiptEmailPersona.getId().intValue())))
            .andExpect(jsonPath("$.[*].emailIdentifier").value(hasItem(DEFAULT_EMAIL_IDENTIFIER.toString())))
            .andExpect(jsonPath("$.[*].mainEmail").value(hasItem(DEFAULT_MAIN_EMAIL)))
            .andExpect(jsonPath("$.[*].clearCopyEmail").value(hasItem(DEFAULT_CLEAR_COPY_EMAIL.booleanValue())))
            .andExpect(jsonPath("$.[*].blindCopyEmail").value(hasItem(DEFAULT_BLIND_COPY_EMAIL.booleanValue())))
            .andExpect(jsonPath("$.[*].languageKeyCode").value(hasItem(DEFAULT_LANGUAGE_KEY_CODE)))
            .andExpect(jsonPath("$.[*].preferredGreeting").value(hasItem(DEFAULT_PREFERRED_GREETING)))
            .andExpect(jsonPath("$.[*].preferredGreetingDesignation").value(hasItem(DEFAULT_PREFERRED_GREETING_DESIGNATION)))
            .andExpect(jsonPath("$.[*].preferredPrefix").value(hasItem(DEFAULT_PREFERRED_PREFIX)))
            .andExpect(jsonPath("$.[*].preferredSuffix").value(hasItem(DEFAULT_PREFERRED_SUFFIX)))
            .andExpect(jsonPath("$.[*].timeBasedGreetings").value(hasItem(DEFAULT_TIME_BASED_GREETINGS.booleanValue())))
            .andExpect(jsonPath("$.[*].sloganBasedGreeting").value(hasItem(DEFAULT_SLOGAN_BASED_GREETING.booleanValue())))
            .andExpect(jsonPath("$.[*].addPrefix").value(hasItem(DEFAULT_ADD_PREFIX.booleanValue())))
            .andExpect(jsonPath("$.[*].addSuffix").value(hasItem(DEFAULT_ADD_SUFFIX.booleanValue())))
            .andExpect(jsonPath("$.[*].preferredSignature").value(hasItem(DEFAULT_PREFERRED_SIGNATURE)))
            .andExpect(jsonPath("$.[*].preferredSignatureDesignation").value(hasItem(DEFAULT_PREFERRED_SIGNATURE_DESIGNATION)))
            .andExpect(jsonPath("$.[*].includeServiceDetails").value(hasItem(DEFAULT_INCLUDE_SERVICE_DETAILS.booleanValue())))
            .andExpect(jsonPath("$.[*].includeMessageOfTheDay").value(hasItem(DEFAULT_INCLUDE_MESSAGE_OF_THE_DAY.booleanValue())))
            .andExpect(jsonPath("$.[*].includeTreasuryQuote").value(hasItem(DEFAULT_INCLUDE_TREASURY_QUOTE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].lastModifedAt").value(hasItem(sameInstant(DEFAULT_LAST_MODIFED_AT))))
            .andExpect(jsonPath("$.[*].personaName").value(hasItem(DEFAULT_PERSONA_NAME)));
    }
}

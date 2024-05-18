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
import io.github.calvary.domain.ApplicationUser;
import io.github.calvary.domain.User;
import io.github.calvary.repository.ApplicationUserRepository;
import io.github.calvary.repository.search.ApplicationUserSearchRepository;
import io.github.calvary.service.ApplicationUserService;
import io.github.calvary.service.criteria.ApplicationUserCriteria;
import io.github.calvary.service.dto.ApplicationUserDTO;
import io.github.calvary.service.mapper.ApplicationUserMapper;
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
 * Integration tests for the {@link ApplicationUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ApplicationUserResourceIT {

    private static final String DEFAULT_APPLICATION_IDENTITY = "AAAAAAAAAA";
    private static final String UPDATED_APPLICATION_IDENTITY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LAST_LOGIN_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_LOGIN_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LAST_LOGIN_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_TIME_OF_CREATION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TIME_OF_CREATION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_TIME_OF_CREATION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_LAST_TIME_OF_MODIFICATION = ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(0L),
        ZoneOffset.UTC
    );
    private static final ZonedDateTime UPDATED_LAST_TIME_OF_MODIFICATION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_LAST_TIME_OF_MODIFICATION = ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(-1L),
        ZoneOffset.UTC
    );

    private static final UUID DEFAULT_USER_IDENTIFIER = UUID.randomUUID();
    private static final UUID UPDATED_USER_IDENTIFIER = UUID.randomUUID();

    private static final String ENTITY_API_URL = "/api/application-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/application-users";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Mock
    private ApplicationUserRepository applicationUserRepositoryMock;

    @Autowired
    private ApplicationUserMapper applicationUserMapper;

    @Mock
    private ApplicationUserService applicationUserServiceMock;

    @Autowired
    private ApplicationUserSearchRepository applicationUserSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApplicationUserMockMvc;

    private ApplicationUser applicationUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApplicationUser createEntity(EntityManager em) {
        ApplicationUser applicationUser = new ApplicationUser()
            .applicationIdentity(DEFAULT_APPLICATION_IDENTITY)
            .lastLoginTime(DEFAULT_LAST_LOGIN_TIME)
            .timeOfCreation(DEFAULT_TIME_OF_CREATION)
            .lastTimeOfModification(DEFAULT_LAST_TIME_OF_MODIFICATION)
            .userIdentifier(DEFAULT_USER_IDENTIFIER);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        applicationUser.setSystemIdentity(user);
        return applicationUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApplicationUser createUpdatedEntity(EntityManager em) {
        ApplicationUser applicationUser = new ApplicationUser()
            .applicationIdentity(UPDATED_APPLICATION_IDENTITY)
            .lastLoginTime(UPDATED_LAST_LOGIN_TIME)
            .timeOfCreation(UPDATED_TIME_OF_CREATION)
            .lastTimeOfModification(UPDATED_LAST_TIME_OF_MODIFICATION)
            .userIdentifier(UPDATED_USER_IDENTIFIER);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        applicationUser.setSystemIdentity(user);
        return applicationUser;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        applicationUserSearchRepository.deleteAll();
        assertThat(applicationUserSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        applicationUser = createEntity(em);
    }

    @Test
    @Transactional
    void createApplicationUser() throws Exception {
        int databaseSizeBeforeCreate = applicationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);
        restApplicationUserMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(applicationUserDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ApplicationUser in the database
        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ApplicationUser testApplicationUser = applicationUserList.get(applicationUserList.size() - 1);
        assertThat(testApplicationUser.getApplicationIdentity()).isEqualTo(DEFAULT_APPLICATION_IDENTITY);
        assertThat(testApplicationUser.getLastLoginTime()).isEqualTo(DEFAULT_LAST_LOGIN_TIME);
        assertThat(testApplicationUser.getTimeOfCreation()).isEqualTo(DEFAULT_TIME_OF_CREATION);
        assertThat(testApplicationUser.getLastTimeOfModification()).isEqualTo(DEFAULT_LAST_TIME_OF_MODIFICATION);
        assertThat(testApplicationUser.getUserIdentifier()).isEqualTo(DEFAULT_USER_IDENTIFIER);
    }

    @Test
    @Transactional
    void createApplicationUserWithExistingId() throws Exception {
        // Create the ApplicationUser with an existing ID
        applicationUser.setId(1L);
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        int databaseSizeBeforeCreate = applicationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restApplicationUserMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(applicationUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkApplicationIdentityIsRequired() throws Exception {
        int databaseSizeBeforeTest = applicationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        // set the field null
        applicationUser.setApplicationIdentity(null);

        // Create the ApplicationUser, which fails.
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        restApplicationUserMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(applicationUserDTO))
            )
            .andExpect(status().isBadRequest());

        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTimeOfCreationIsRequired() throws Exception {
        int databaseSizeBeforeTest = applicationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        // set the field null
        applicationUser.setTimeOfCreation(null);

        // Create the ApplicationUser, which fails.
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        restApplicationUserMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(applicationUserDTO))
            )
            .andExpect(status().isBadRequest());

        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUserIdentifierIsRequired() throws Exception {
        int databaseSizeBeforeTest = applicationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        // set the field null
        applicationUser.setUserIdentifier(null);

        // Create the ApplicationUser, which fails.
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        restApplicationUserMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(applicationUserDTO))
            )
            .andExpect(status().isBadRequest());

        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllApplicationUsers() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(applicationUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].applicationIdentity").value(hasItem(DEFAULT_APPLICATION_IDENTITY)))
            .andExpect(jsonPath("$.[*].lastLoginTime").value(hasItem(sameInstant(DEFAULT_LAST_LOGIN_TIME))))
            .andExpect(jsonPath("$.[*].timeOfCreation").value(hasItem(sameInstant(DEFAULT_TIME_OF_CREATION))))
            .andExpect(jsonPath("$.[*].lastTimeOfModification").value(hasItem(sameInstant(DEFAULT_LAST_TIME_OF_MODIFICATION))))
            .andExpect(jsonPath("$.[*].userIdentifier").value(hasItem(DEFAULT_USER_IDENTIFIER.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllApplicationUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(applicationUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restApplicationUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(applicationUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllApplicationUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(applicationUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restApplicationUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(applicationUserRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getApplicationUser() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get the applicationUser
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL_ID, applicationUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(applicationUser.getId().intValue()))
            .andExpect(jsonPath("$.applicationIdentity").value(DEFAULT_APPLICATION_IDENTITY))
            .andExpect(jsonPath("$.lastLoginTime").value(sameInstant(DEFAULT_LAST_LOGIN_TIME)))
            .andExpect(jsonPath("$.timeOfCreation").value(sameInstant(DEFAULT_TIME_OF_CREATION)))
            .andExpect(jsonPath("$.lastTimeOfModification").value(sameInstant(DEFAULT_LAST_TIME_OF_MODIFICATION)))
            .andExpect(jsonPath("$.userIdentifier").value(DEFAULT_USER_IDENTIFIER.toString()));
    }

    @Test
    @Transactional
    void getApplicationUsersByIdFiltering() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        Long id = applicationUser.getId();

        defaultApplicationUserShouldBeFound("id.equals=" + id);
        defaultApplicationUserShouldNotBeFound("id.notEquals=" + id);

        defaultApplicationUserShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultApplicationUserShouldNotBeFound("id.greaterThan=" + id);

        defaultApplicationUserShouldBeFound("id.lessThanOrEqual=" + id);
        defaultApplicationUserShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByApplicationIdentityIsEqualToSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where applicationIdentity equals to DEFAULT_APPLICATION_IDENTITY
        defaultApplicationUserShouldBeFound("applicationIdentity.equals=" + DEFAULT_APPLICATION_IDENTITY);

        // Get all the applicationUserList where applicationIdentity equals to UPDATED_APPLICATION_IDENTITY
        defaultApplicationUserShouldNotBeFound("applicationIdentity.equals=" + UPDATED_APPLICATION_IDENTITY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByApplicationIdentityIsInShouldWork() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where applicationIdentity in DEFAULT_APPLICATION_IDENTITY or UPDATED_APPLICATION_IDENTITY
        defaultApplicationUserShouldBeFound("applicationIdentity.in=" + DEFAULT_APPLICATION_IDENTITY + "," + UPDATED_APPLICATION_IDENTITY);

        // Get all the applicationUserList where applicationIdentity equals to UPDATED_APPLICATION_IDENTITY
        defaultApplicationUserShouldNotBeFound("applicationIdentity.in=" + UPDATED_APPLICATION_IDENTITY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByApplicationIdentityIsNullOrNotNull() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where applicationIdentity is not null
        defaultApplicationUserShouldBeFound("applicationIdentity.specified=true");

        // Get all the applicationUserList where applicationIdentity is null
        defaultApplicationUserShouldNotBeFound("applicationIdentity.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByApplicationIdentityContainsSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where applicationIdentity contains DEFAULT_APPLICATION_IDENTITY
        defaultApplicationUserShouldBeFound("applicationIdentity.contains=" + DEFAULT_APPLICATION_IDENTITY);

        // Get all the applicationUserList where applicationIdentity contains UPDATED_APPLICATION_IDENTITY
        defaultApplicationUserShouldNotBeFound("applicationIdentity.contains=" + UPDATED_APPLICATION_IDENTITY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByApplicationIdentityNotContainsSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where applicationIdentity does not contain DEFAULT_APPLICATION_IDENTITY
        defaultApplicationUserShouldNotBeFound("applicationIdentity.doesNotContain=" + DEFAULT_APPLICATION_IDENTITY);

        // Get all the applicationUserList where applicationIdentity does not contain UPDATED_APPLICATION_IDENTITY
        defaultApplicationUserShouldBeFound("applicationIdentity.doesNotContain=" + UPDATED_APPLICATION_IDENTITY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastLoginTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastLoginTime equals to DEFAULT_LAST_LOGIN_TIME
        defaultApplicationUserShouldBeFound("lastLoginTime.equals=" + DEFAULT_LAST_LOGIN_TIME);

        // Get all the applicationUserList where lastLoginTime equals to UPDATED_LAST_LOGIN_TIME
        defaultApplicationUserShouldNotBeFound("lastLoginTime.equals=" + UPDATED_LAST_LOGIN_TIME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastLoginTimeIsInShouldWork() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastLoginTime in DEFAULT_LAST_LOGIN_TIME or UPDATED_LAST_LOGIN_TIME
        defaultApplicationUserShouldBeFound("lastLoginTime.in=" + DEFAULT_LAST_LOGIN_TIME + "," + UPDATED_LAST_LOGIN_TIME);

        // Get all the applicationUserList where lastLoginTime equals to UPDATED_LAST_LOGIN_TIME
        defaultApplicationUserShouldNotBeFound("lastLoginTime.in=" + UPDATED_LAST_LOGIN_TIME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastLoginTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastLoginTime is not null
        defaultApplicationUserShouldBeFound("lastLoginTime.specified=true");

        // Get all the applicationUserList where lastLoginTime is null
        defaultApplicationUserShouldNotBeFound("lastLoginTime.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastLoginTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastLoginTime is greater than or equal to DEFAULT_LAST_LOGIN_TIME
        defaultApplicationUserShouldBeFound("lastLoginTime.greaterThanOrEqual=" + DEFAULT_LAST_LOGIN_TIME);

        // Get all the applicationUserList where lastLoginTime is greater than or equal to UPDATED_LAST_LOGIN_TIME
        defaultApplicationUserShouldNotBeFound("lastLoginTime.greaterThanOrEqual=" + UPDATED_LAST_LOGIN_TIME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastLoginTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastLoginTime is less than or equal to DEFAULT_LAST_LOGIN_TIME
        defaultApplicationUserShouldBeFound("lastLoginTime.lessThanOrEqual=" + DEFAULT_LAST_LOGIN_TIME);

        // Get all the applicationUserList where lastLoginTime is less than or equal to SMALLER_LAST_LOGIN_TIME
        defaultApplicationUserShouldNotBeFound("lastLoginTime.lessThanOrEqual=" + SMALLER_LAST_LOGIN_TIME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastLoginTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastLoginTime is less than DEFAULT_LAST_LOGIN_TIME
        defaultApplicationUserShouldNotBeFound("lastLoginTime.lessThan=" + DEFAULT_LAST_LOGIN_TIME);

        // Get all the applicationUserList where lastLoginTime is less than UPDATED_LAST_LOGIN_TIME
        defaultApplicationUserShouldBeFound("lastLoginTime.lessThan=" + UPDATED_LAST_LOGIN_TIME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastLoginTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastLoginTime is greater than DEFAULT_LAST_LOGIN_TIME
        defaultApplicationUserShouldNotBeFound("lastLoginTime.greaterThan=" + DEFAULT_LAST_LOGIN_TIME);

        // Get all the applicationUserList where lastLoginTime is greater than SMALLER_LAST_LOGIN_TIME
        defaultApplicationUserShouldBeFound("lastLoginTime.greaterThan=" + SMALLER_LAST_LOGIN_TIME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByTimeOfCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where timeOfCreation equals to DEFAULT_TIME_OF_CREATION
        defaultApplicationUserShouldBeFound("timeOfCreation.equals=" + DEFAULT_TIME_OF_CREATION);

        // Get all the applicationUserList where timeOfCreation equals to UPDATED_TIME_OF_CREATION
        defaultApplicationUserShouldNotBeFound("timeOfCreation.equals=" + UPDATED_TIME_OF_CREATION);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByTimeOfCreationIsInShouldWork() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where timeOfCreation in DEFAULT_TIME_OF_CREATION or UPDATED_TIME_OF_CREATION
        defaultApplicationUserShouldBeFound("timeOfCreation.in=" + DEFAULT_TIME_OF_CREATION + "," + UPDATED_TIME_OF_CREATION);

        // Get all the applicationUserList where timeOfCreation equals to UPDATED_TIME_OF_CREATION
        defaultApplicationUserShouldNotBeFound("timeOfCreation.in=" + UPDATED_TIME_OF_CREATION);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByTimeOfCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where timeOfCreation is not null
        defaultApplicationUserShouldBeFound("timeOfCreation.specified=true");

        // Get all the applicationUserList where timeOfCreation is null
        defaultApplicationUserShouldNotBeFound("timeOfCreation.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByTimeOfCreationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where timeOfCreation is greater than or equal to DEFAULT_TIME_OF_CREATION
        defaultApplicationUserShouldBeFound("timeOfCreation.greaterThanOrEqual=" + DEFAULT_TIME_OF_CREATION);

        // Get all the applicationUserList where timeOfCreation is greater than or equal to UPDATED_TIME_OF_CREATION
        defaultApplicationUserShouldNotBeFound("timeOfCreation.greaterThanOrEqual=" + UPDATED_TIME_OF_CREATION);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByTimeOfCreationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where timeOfCreation is less than or equal to DEFAULT_TIME_OF_CREATION
        defaultApplicationUserShouldBeFound("timeOfCreation.lessThanOrEqual=" + DEFAULT_TIME_OF_CREATION);

        // Get all the applicationUserList where timeOfCreation is less than or equal to SMALLER_TIME_OF_CREATION
        defaultApplicationUserShouldNotBeFound("timeOfCreation.lessThanOrEqual=" + SMALLER_TIME_OF_CREATION);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByTimeOfCreationIsLessThanSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where timeOfCreation is less than DEFAULT_TIME_OF_CREATION
        defaultApplicationUserShouldNotBeFound("timeOfCreation.lessThan=" + DEFAULT_TIME_OF_CREATION);

        // Get all the applicationUserList where timeOfCreation is less than UPDATED_TIME_OF_CREATION
        defaultApplicationUserShouldBeFound("timeOfCreation.lessThan=" + UPDATED_TIME_OF_CREATION);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByTimeOfCreationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where timeOfCreation is greater than DEFAULT_TIME_OF_CREATION
        defaultApplicationUserShouldNotBeFound("timeOfCreation.greaterThan=" + DEFAULT_TIME_OF_CREATION);

        // Get all the applicationUserList where timeOfCreation is greater than SMALLER_TIME_OF_CREATION
        defaultApplicationUserShouldBeFound("timeOfCreation.greaterThan=" + SMALLER_TIME_OF_CREATION);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastTimeOfModificationIsEqualToSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastTimeOfModification equals to DEFAULT_LAST_TIME_OF_MODIFICATION
        defaultApplicationUserShouldBeFound("lastTimeOfModification.equals=" + DEFAULT_LAST_TIME_OF_MODIFICATION);

        // Get all the applicationUserList where lastTimeOfModification equals to UPDATED_LAST_TIME_OF_MODIFICATION
        defaultApplicationUserShouldNotBeFound("lastTimeOfModification.equals=" + UPDATED_LAST_TIME_OF_MODIFICATION);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastTimeOfModificationIsInShouldWork() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastTimeOfModification in DEFAULT_LAST_TIME_OF_MODIFICATION or UPDATED_LAST_TIME_OF_MODIFICATION
        defaultApplicationUserShouldBeFound(
            "lastTimeOfModification.in=" + DEFAULT_LAST_TIME_OF_MODIFICATION + "," + UPDATED_LAST_TIME_OF_MODIFICATION
        );

        // Get all the applicationUserList where lastTimeOfModification equals to UPDATED_LAST_TIME_OF_MODIFICATION
        defaultApplicationUserShouldNotBeFound("lastTimeOfModification.in=" + UPDATED_LAST_TIME_OF_MODIFICATION);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastTimeOfModificationIsNullOrNotNull() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastTimeOfModification is not null
        defaultApplicationUserShouldBeFound("lastTimeOfModification.specified=true");

        // Get all the applicationUserList where lastTimeOfModification is null
        defaultApplicationUserShouldNotBeFound("lastTimeOfModification.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastTimeOfModificationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastTimeOfModification is greater than or equal to DEFAULT_LAST_TIME_OF_MODIFICATION
        defaultApplicationUserShouldBeFound("lastTimeOfModification.greaterThanOrEqual=" + DEFAULT_LAST_TIME_OF_MODIFICATION);

        // Get all the applicationUserList where lastTimeOfModification is greater than or equal to UPDATED_LAST_TIME_OF_MODIFICATION
        defaultApplicationUserShouldNotBeFound("lastTimeOfModification.greaterThanOrEqual=" + UPDATED_LAST_TIME_OF_MODIFICATION);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastTimeOfModificationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastTimeOfModification is less than or equal to DEFAULT_LAST_TIME_OF_MODIFICATION
        defaultApplicationUserShouldBeFound("lastTimeOfModification.lessThanOrEqual=" + DEFAULT_LAST_TIME_OF_MODIFICATION);

        // Get all the applicationUserList where lastTimeOfModification is less than or equal to SMALLER_LAST_TIME_OF_MODIFICATION
        defaultApplicationUserShouldNotBeFound("lastTimeOfModification.lessThanOrEqual=" + SMALLER_LAST_TIME_OF_MODIFICATION);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastTimeOfModificationIsLessThanSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastTimeOfModification is less than DEFAULT_LAST_TIME_OF_MODIFICATION
        defaultApplicationUserShouldNotBeFound("lastTimeOfModification.lessThan=" + DEFAULT_LAST_TIME_OF_MODIFICATION);

        // Get all the applicationUserList where lastTimeOfModification is less than UPDATED_LAST_TIME_OF_MODIFICATION
        defaultApplicationUserShouldBeFound("lastTimeOfModification.lessThan=" + UPDATED_LAST_TIME_OF_MODIFICATION);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastTimeOfModificationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastTimeOfModification is greater than DEFAULT_LAST_TIME_OF_MODIFICATION
        defaultApplicationUserShouldNotBeFound("lastTimeOfModification.greaterThan=" + DEFAULT_LAST_TIME_OF_MODIFICATION);

        // Get all the applicationUserList where lastTimeOfModification is greater than SMALLER_LAST_TIME_OF_MODIFICATION
        defaultApplicationUserShouldBeFound("lastTimeOfModification.greaterThan=" + SMALLER_LAST_TIME_OF_MODIFICATION);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByUserIdentifierIsEqualToSomething() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where userIdentifier equals to DEFAULT_USER_IDENTIFIER
        defaultApplicationUserShouldBeFound("userIdentifier.equals=" + DEFAULT_USER_IDENTIFIER);

        // Get all the applicationUserList where userIdentifier equals to UPDATED_USER_IDENTIFIER
        defaultApplicationUserShouldNotBeFound("userIdentifier.equals=" + UPDATED_USER_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByUserIdentifierIsInShouldWork() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where userIdentifier in DEFAULT_USER_IDENTIFIER or UPDATED_USER_IDENTIFIER
        defaultApplicationUserShouldBeFound("userIdentifier.in=" + DEFAULT_USER_IDENTIFIER + "," + UPDATED_USER_IDENTIFIER);

        // Get all the applicationUserList where userIdentifier equals to UPDATED_USER_IDENTIFIER
        defaultApplicationUserShouldNotBeFound("userIdentifier.in=" + UPDATED_USER_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByUserIdentifierIsNullOrNotNull() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where userIdentifier is not null
        defaultApplicationUserShouldBeFound("userIdentifier.specified=true");

        // Get all the applicationUserList where userIdentifier is null
        defaultApplicationUserShouldNotBeFound("userIdentifier.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByCreatedByIsEqualToSomething() throws Exception {
        ApplicationUser createdBy;
        if (TestUtil.findAll(em, ApplicationUser.class).isEmpty()) {
            applicationUserRepository.saveAndFlush(applicationUser);
            createdBy = ApplicationUserResourceIT.createEntity(em);
        } else {
            createdBy = TestUtil.findAll(em, ApplicationUser.class).get(0);
        }
        em.persist(createdBy);
        em.flush();
        applicationUser.setCreatedBy(createdBy);
        applicationUserRepository.saveAndFlush(applicationUser);
        Long createdById = createdBy.getId();

        // Get all the applicationUserList where createdBy equals to createdById
        defaultApplicationUserShouldBeFound("createdById.equals=" + createdById);

        // Get all the applicationUserList where createdBy equals to (createdById + 1)
        defaultApplicationUserShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastModifiedByIsEqualToSomething() throws Exception {
        ApplicationUser lastModifiedBy;
        if (TestUtil.findAll(em, ApplicationUser.class).isEmpty()) {
            applicationUserRepository.saveAndFlush(applicationUser);
            lastModifiedBy = ApplicationUserResourceIT.createEntity(em);
        } else {
            lastModifiedBy = TestUtil.findAll(em, ApplicationUser.class).get(0);
        }
        em.persist(lastModifiedBy);
        em.flush();
        applicationUser.setLastModifiedBy(lastModifiedBy);
        applicationUserRepository.saveAndFlush(applicationUser);
        Long lastModifiedById = lastModifiedBy.getId();

        // Get all the applicationUserList where lastModifiedBy equals to lastModifiedById
        defaultApplicationUserShouldBeFound("lastModifiedById.equals=" + lastModifiedById);

        // Get all the applicationUserList where lastModifiedBy equals to (lastModifiedById + 1)
        defaultApplicationUserShouldNotBeFound("lastModifiedById.equals=" + (lastModifiedById + 1));
    }

    @Test
    @Transactional
    void getAllApplicationUsersBySystemIdentityIsEqualToSomething() throws Exception {
        // Get already existing entity
        User systemIdentity = applicationUser.getSystemIdentity();
        applicationUserRepository.saveAndFlush(applicationUser);
        Long systemIdentityId = systemIdentity.getId();

        // Get all the applicationUserList where systemIdentity equals to systemIdentityId
        defaultApplicationUserShouldBeFound("systemIdentityId.equals=" + systemIdentityId);

        // Get all the applicationUserList where systemIdentity equals to (systemIdentityId + 1)
        defaultApplicationUserShouldNotBeFound("systemIdentityId.equals=" + (systemIdentityId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultApplicationUserShouldBeFound(String filter) throws Exception {
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(applicationUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].applicationIdentity").value(hasItem(DEFAULT_APPLICATION_IDENTITY)))
            .andExpect(jsonPath("$.[*].lastLoginTime").value(hasItem(sameInstant(DEFAULT_LAST_LOGIN_TIME))))
            .andExpect(jsonPath("$.[*].timeOfCreation").value(hasItem(sameInstant(DEFAULT_TIME_OF_CREATION))))
            .andExpect(jsonPath("$.[*].lastTimeOfModification").value(hasItem(sameInstant(DEFAULT_LAST_TIME_OF_MODIFICATION))))
            .andExpect(jsonPath("$.[*].userIdentifier").value(hasItem(DEFAULT_USER_IDENTIFIER.toString())));

        // Check, that the count call also returns 1
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultApplicationUserShouldNotBeFound(String filter) throws Exception {
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingApplicationUser() throws Exception {
        // Get the applicationUser
        restApplicationUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingApplicationUser() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        int databaseSizeBeforeUpdate = applicationUserRepository.findAll().size();
        applicationUserSearchRepository.save(applicationUser);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());

        // Update the applicationUser
        ApplicationUser updatedApplicationUser = applicationUserRepository.findById(applicationUser.getId()).get();
        // Disconnect from session so that the updates on updatedApplicationUser are not directly saved in db
        em.detach(updatedApplicationUser);
        updatedApplicationUser
            .applicationIdentity(UPDATED_APPLICATION_IDENTITY)
            .lastLoginTime(UPDATED_LAST_LOGIN_TIME)
            .timeOfCreation(UPDATED_TIME_OF_CREATION)
            .lastTimeOfModification(UPDATED_LAST_TIME_OF_MODIFICATION)
            .userIdentifier(UPDATED_USER_IDENTIFIER);
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(updatedApplicationUser);

        restApplicationUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicationUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(applicationUserDTO))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationUser in the database
        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeUpdate);
        ApplicationUser testApplicationUser = applicationUserList.get(applicationUserList.size() - 1);
        assertThat(testApplicationUser.getApplicationIdentity()).isEqualTo(UPDATED_APPLICATION_IDENTITY);
        assertThat(testApplicationUser.getLastLoginTime()).isEqualTo(UPDATED_LAST_LOGIN_TIME);
        assertThat(testApplicationUser.getTimeOfCreation()).isEqualTo(UPDATED_TIME_OF_CREATION);
        assertThat(testApplicationUser.getLastTimeOfModification()).isEqualTo(UPDATED_LAST_TIME_OF_MODIFICATION);
        assertThat(testApplicationUser.getUserIdentifier()).isEqualTo(UPDATED_USER_IDENTIFIER);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ApplicationUser> applicationUserSearchList = IterableUtils.toList(applicationUserSearchRepository.findAll());
                ApplicationUser testApplicationUserSearch = applicationUserSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testApplicationUserSearch.getApplicationIdentity()).isEqualTo(UPDATED_APPLICATION_IDENTITY);
                assertThat(testApplicationUserSearch.getLastLoginTime()).isEqualTo(UPDATED_LAST_LOGIN_TIME);
                assertThat(testApplicationUserSearch.getTimeOfCreation()).isEqualTo(UPDATED_TIME_OF_CREATION);
                assertThat(testApplicationUserSearch.getLastTimeOfModification()).isEqualTo(UPDATED_LAST_TIME_OF_MODIFICATION);
                assertThat(testApplicationUserSearch.getUserIdentifier()).isEqualTo(UPDATED_USER_IDENTIFIER);
            });
    }

    @Test
    @Transactional
    void putNonExistingApplicationUser() throws Exception {
        int databaseSizeBeforeUpdate = applicationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        applicationUser.setId(count.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicationUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(applicationUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchApplicationUser() throws Exception {
        int databaseSizeBeforeUpdate = applicationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        applicationUser.setId(count.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(applicationUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApplicationUser() throws Exception {
        int databaseSizeBeforeUpdate = applicationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        applicationUser.setId(count.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(applicationUserDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApplicationUser in the database
        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateApplicationUserWithPatch() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        int databaseSizeBeforeUpdate = applicationUserRepository.findAll().size();

        // Update the applicationUser using partial update
        ApplicationUser partialUpdatedApplicationUser = new ApplicationUser();
        partialUpdatedApplicationUser.setId(applicationUser.getId());

        partialUpdatedApplicationUser.lastLoginTime(UPDATED_LAST_LOGIN_TIME).timeOfCreation(UPDATED_TIME_OF_CREATION);

        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplicationUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedApplicationUser))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationUser in the database
        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeUpdate);
        ApplicationUser testApplicationUser = applicationUserList.get(applicationUserList.size() - 1);
        assertThat(testApplicationUser.getApplicationIdentity()).isEqualTo(DEFAULT_APPLICATION_IDENTITY);
        assertThat(testApplicationUser.getLastLoginTime()).isEqualTo(UPDATED_LAST_LOGIN_TIME);
        assertThat(testApplicationUser.getTimeOfCreation()).isEqualTo(UPDATED_TIME_OF_CREATION);
        assertThat(testApplicationUser.getLastTimeOfModification()).isEqualTo(DEFAULT_LAST_TIME_OF_MODIFICATION);
        assertThat(testApplicationUser.getUserIdentifier()).isEqualTo(DEFAULT_USER_IDENTIFIER);
    }

    @Test
    @Transactional
    void fullUpdateApplicationUserWithPatch() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);

        int databaseSizeBeforeUpdate = applicationUserRepository.findAll().size();

        // Update the applicationUser using partial update
        ApplicationUser partialUpdatedApplicationUser = new ApplicationUser();
        partialUpdatedApplicationUser.setId(applicationUser.getId());

        partialUpdatedApplicationUser
            .applicationIdentity(UPDATED_APPLICATION_IDENTITY)
            .lastLoginTime(UPDATED_LAST_LOGIN_TIME)
            .timeOfCreation(UPDATED_TIME_OF_CREATION)
            .lastTimeOfModification(UPDATED_LAST_TIME_OF_MODIFICATION)
            .userIdentifier(UPDATED_USER_IDENTIFIER);

        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplicationUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedApplicationUser))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationUser in the database
        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeUpdate);
        ApplicationUser testApplicationUser = applicationUserList.get(applicationUserList.size() - 1);
        assertThat(testApplicationUser.getApplicationIdentity()).isEqualTo(UPDATED_APPLICATION_IDENTITY);
        assertThat(testApplicationUser.getLastLoginTime()).isEqualTo(UPDATED_LAST_LOGIN_TIME);
        assertThat(testApplicationUser.getTimeOfCreation()).isEqualTo(UPDATED_TIME_OF_CREATION);
        assertThat(testApplicationUser.getLastTimeOfModification()).isEqualTo(UPDATED_LAST_TIME_OF_MODIFICATION);
        assertThat(testApplicationUser.getUserIdentifier()).isEqualTo(UPDATED_USER_IDENTIFIER);
    }

    @Test
    @Transactional
    void patchNonExistingApplicationUser() throws Exception {
        int databaseSizeBeforeUpdate = applicationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        applicationUser.setId(count.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, applicationUserDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(applicationUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApplicationUser() throws Exception {
        int databaseSizeBeforeUpdate = applicationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        applicationUser.setId(count.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(applicationUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApplicationUser() throws Exception {
        int databaseSizeBeforeUpdate = applicationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        applicationUser.setId(count.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(applicationUserDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApplicationUser in the database
        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteApplicationUser() throws Exception {
        // Initialize the database
        applicationUserRepository.saveAndFlush(applicationUser);
        applicationUserRepository.save(applicationUser);
        applicationUserSearchRepository.save(applicationUser);

        int databaseSizeBeforeDelete = applicationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the applicationUser
        restApplicationUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, applicationUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
        assertThat(applicationUserList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchApplicationUser() throws Exception {
        // Initialize the database
        applicationUser = applicationUserRepository.saveAndFlush(applicationUser);
        applicationUserSearchRepository.save(applicationUser);

        // Search the applicationUser
        restApplicationUserMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + applicationUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(applicationUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].applicationIdentity").value(hasItem(DEFAULT_APPLICATION_IDENTITY)))
            .andExpect(jsonPath("$.[*].lastLoginTime").value(hasItem(sameInstant(DEFAULT_LAST_LOGIN_TIME))))
            .andExpect(jsonPath("$.[*].timeOfCreation").value(hasItem(sameInstant(DEFAULT_TIME_OF_CREATION))))
            .andExpect(jsonPath("$.[*].lastTimeOfModification").value(hasItem(sameInstant(DEFAULT_LAST_TIME_OF_MODIFICATION))))
            .andExpect(jsonPath("$.[*].userIdentifier").value(hasItem(DEFAULT_USER_IDENTIFIER.toString())));
    }
}

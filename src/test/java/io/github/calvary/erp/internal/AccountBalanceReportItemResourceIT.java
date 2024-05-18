package io.github.calvary.erp.internal;

import static io.github.calvary.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.AccountBalanceReportItem;
import io.github.calvary.repository.AccountBalanceReportItemRepository;
import io.github.calvary.repository.search.AccountBalanceReportItemSearchRepository;
import io.github.calvary.service.dto.AccountBalanceReportItemDTO;
import io.github.calvary.service.mapper.AccountBalanceReportItemMapper;
import io.github.calvary.web.rest.AccountBalanceReportItemResource;
import io.github.calvary.web.rest.TestUtil;
import java.math.BigDecimal;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AccountBalanceReportItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AccountBalanceReportItemResourceIT {

    private static final String DEFAULT_ACCOUNT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ACCOUNT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_ACCOUNT_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_ACCOUNT_BALANCE = new BigDecimal(2);
    private static final BigDecimal SMALLER_ACCOUNT_BALANCE = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/app/account-balance-report-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/app/_search/account-balance-report-items";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AccountBalanceReportItemRepository accountBalanceReportItemRepository;

    @Autowired
    private AccountBalanceReportItemMapper accountBalanceReportItemMapper;

    @Autowired
    private AccountBalanceReportItemSearchRepository accountBalanceReportItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAccountBalanceReportItemMockMvc;

    private AccountBalanceReportItem accountBalanceReportItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountBalanceReportItem createEntity(EntityManager em) {
        return new AccountBalanceReportItem()
            .accountNumber(DEFAULT_ACCOUNT_NUMBER)
            .accountName(DEFAULT_ACCOUNT_NAME)
            .accountBalance(DEFAULT_ACCOUNT_BALANCE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountBalanceReportItem createUpdatedEntity(EntityManager em) {
        AccountBalanceReportItem accountBalanceReportItem = new AccountBalanceReportItem()
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .accountName(UPDATED_ACCOUNT_NAME)
            .accountBalance(UPDATED_ACCOUNT_BALANCE);
        return accountBalanceReportItem;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        accountBalanceReportItemSearchRepository.deleteAll();
        assertThat(accountBalanceReportItemSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        accountBalanceReportItem = createEntity(em);
    }

    @Test
    @Transactional
    void createAccountBalanceReportItem() throws Exception {
        int databaseSizeBeforeCreate = accountBalanceReportItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        // Create the AccountBalanceReportItem
        AccountBalanceReportItemDTO accountBalanceReportItemDTO = accountBalanceReportItemMapper.toDto(accountBalanceReportItem);
        restAccountBalanceReportItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.calvary.web.rest.TestUtil.convertObjectToJsonBytes(accountBalanceReportItemDTO))
            )
            .andExpect(status().isCreated());

        // Validate the AccountBalanceReportItem in the database
        List<AccountBalanceReportItem> accountBalanceReportItemList = accountBalanceReportItemRepository.findAll();
        assertThat(accountBalanceReportItemList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        AccountBalanceReportItem testAccountBalanceReportItem = accountBalanceReportItemList.get(accountBalanceReportItemList.size() - 1);
        assertThat(testAccountBalanceReportItem.getAccountNumber()).isEqualTo(DEFAULT_ACCOUNT_NUMBER);
        assertThat(testAccountBalanceReportItem.getAccountName()).isEqualTo(DEFAULT_ACCOUNT_NAME);
        assertThat(testAccountBalanceReportItem.getAccountBalance()).isEqualByComparingTo(DEFAULT_ACCOUNT_BALANCE);
    }

    @Test
    @Transactional
    void createAccountBalanceReportItemWithExistingId() throws Exception {
        // Create the AccountBalanceReportItem with an existing ID
        accountBalanceReportItem.setId(1L);
        AccountBalanceReportItemDTO accountBalanceReportItemDTO = accountBalanceReportItemMapper.toDto(accountBalanceReportItem);

        int databaseSizeBeforeCreate = accountBalanceReportItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restAccountBalanceReportItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.calvary.web.rest.TestUtil.convertObjectToJsonBytes(accountBalanceReportItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountBalanceReportItem in the database
        List<AccountBalanceReportItem> accountBalanceReportItemList = accountBalanceReportItemRepository.findAll();
        assertThat(accountBalanceReportItemList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItems() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList
        restAccountBalanceReportItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountBalanceReportItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].accountBalance").value(hasItem(sameNumber(DEFAULT_ACCOUNT_BALANCE))));
    }

    @Test
    @Transactional
    void getAccountBalanceReportItem() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get the accountBalanceReportItem
        restAccountBalanceReportItemMockMvc
            .perform(get(ENTITY_API_URL_ID, accountBalanceReportItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(accountBalanceReportItem.getId().intValue()))
            .andExpect(jsonPath("$.accountNumber").value(DEFAULT_ACCOUNT_NUMBER))
            .andExpect(jsonPath("$.accountName").value(DEFAULT_ACCOUNT_NAME))
            .andExpect(jsonPath("$.accountBalance").value(sameNumber(DEFAULT_ACCOUNT_BALANCE)));
    }

    @Test
    @Transactional
    void getAccountBalanceReportItemsByIdFiltering() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        Long id = accountBalanceReportItem.getId();

        defaultAccountBalanceReportItemShouldBeFound("id.equals=" + id);
        defaultAccountBalanceReportItemShouldNotBeFound("id.notEquals=" + id);

        defaultAccountBalanceReportItemShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAccountBalanceReportItemShouldNotBeFound("id.greaterThan=" + id);

        defaultAccountBalanceReportItemShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAccountBalanceReportItemShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountNumber equals to DEFAULT_ACCOUNT_NUMBER
        defaultAccountBalanceReportItemShouldBeFound("accountNumber.equals=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the accountBalanceReportItemList where accountNumber equals to UPDATED_ACCOUNT_NUMBER
        defaultAccountBalanceReportItemShouldNotBeFound("accountNumber.equals=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountNumberIsInShouldWork() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountNumber in DEFAULT_ACCOUNT_NUMBER or UPDATED_ACCOUNT_NUMBER
        defaultAccountBalanceReportItemShouldBeFound("accountNumber.in=" + DEFAULT_ACCOUNT_NUMBER + "," + UPDATED_ACCOUNT_NUMBER);

        // Get all the accountBalanceReportItemList where accountNumber equals to UPDATED_ACCOUNT_NUMBER
        defaultAccountBalanceReportItemShouldNotBeFound("accountNumber.in=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountNumber is not null
        defaultAccountBalanceReportItemShouldBeFound("accountNumber.specified=true");

        // Get all the accountBalanceReportItemList where accountNumber is null
        defaultAccountBalanceReportItemShouldNotBeFound("accountNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountNumberContainsSomething() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountNumber contains DEFAULT_ACCOUNT_NUMBER
        defaultAccountBalanceReportItemShouldBeFound("accountNumber.contains=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the accountBalanceReportItemList where accountNumber contains UPDATED_ACCOUNT_NUMBER
        defaultAccountBalanceReportItemShouldNotBeFound("accountNumber.contains=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountNumberNotContainsSomething() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountNumber does not contain DEFAULT_ACCOUNT_NUMBER
        defaultAccountBalanceReportItemShouldNotBeFound("accountNumber.doesNotContain=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the accountBalanceReportItemList where accountNumber does not contain UPDATED_ACCOUNT_NUMBER
        defaultAccountBalanceReportItemShouldBeFound("accountNumber.doesNotContain=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountNameIsEqualToSomething() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountName equals to DEFAULT_ACCOUNT_NAME
        defaultAccountBalanceReportItemShouldBeFound("accountName.equals=" + DEFAULT_ACCOUNT_NAME);

        // Get all the accountBalanceReportItemList where accountName equals to UPDATED_ACCOUNT_NAME
        defaultAccountBalanceReportItemShouldNotBeFound("accountName.equals=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountNameIsInShouldWork() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountName in DEFAULT_ACCOUNT_NAME or UPDATED_ACCOUNT_NAME
        defaultAccountBalanceReportItemShouldBeFound("accountName.in=" + DEFAULT_ACCOUNT_NAME + "," + UPDATED_ACCOUNT_NAME);

        // Get all the accountBalanceReportItemList where accountName equals to UPDATED_ACCOUNT_NAME
        defaultAccountBalanceReportItemShouldNotBeFound("accountName.in=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountName is not null
        defaultAccountBalanceReportItemShouldBeFound("accountName.specified=true");

        // Get all the accountBalanceReportItemList where accountName is null
        defaultAccountBalanceReportItemShouldNotBeFound("accountName.specified=false");
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountNameContainsSomething() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountName contains DEFAULT_ACCOUNT_NAME
        defaultAccountBalanceReportItemShouldBeFound("accountName.contains=" + DEFAULT_ACCOUNT_NAME);

        // Get all the accountBalanceReportItemList where accountName contains UPDATED_ACCOUNT_NAME
        defaultAccountBalanceReportItemShouldNotBeFound("accountName.contains=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountNameNotContainsSomething() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountName does not contain DEFAULT_ACCOUNT_NAME
        defaultAccountBalanceReportItemShouldNotBeFound("accountName.doesNotContain=" + DEFAULT_ACCOUNT_NAME);

        // Get all the accountBalanceReportItemList where accountName does not contain UPDATED_ACCOUNT_NAME
        defaultAccountBalanceReportItemShouldBeFound("accountName.doesNotContain=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountBalanceIsEqualToSomething() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountBalance equals to DEFAULT_ACCOUNT_BALANCE
        defaultAccountBalanceReportItemShouldBeFound("accountBalance.equals=" + DEFAULT_ACCOUNT_BALANCE);

        // Get all the accountBalanceReportItemList where accountBalance equals to UPDATED_ACCOUNT_BALANCE
        defaultAccountBalanceReportItemShouldNotBeFound("accountBalance.equals=" + UPDATED_ACCOUNT_BALANCE);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountBalanceIsInShouldWork() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountBalance in DEFAULT_ACCOUNT_BALANCE or UPDATED_ACCOUNT_BALANCE
        defaultAccountBalanceReportItemShouldBeFound("accountBalance.in=" + DEFAULT_ACCOUNT_BALANCE + "," + UPDATED_ACCOUNT_BALANCE);

        // Get all the accountBalanceReportItemList where accountBalance equals to UPDATED_ACCOUNT_BALANCE
        defaultAccountBalanceReportItemShouldNotBeFound("accountBalance.in=" + UPDATED_ACCOUNT_BALANCE);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountBalanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountBalance is not null
        defaultAccountBalanceReportItemShouldBeFound("accountBalance.specified=true");

        // Get all the accountBalanceReportItemList where accountBalance is null
        defaultAccountBalanceReportItemShouldNotBeFound("accountBalance.specified=false");
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountBalanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountBalance is greater than or equal to DEFAULT_ACCOUNT_BALANCE
        defaultAccountBalanceReportItemShouldBeFound("accountBalance.greaterThanOrEqual=" + DEFAULT_ACCOUNT_BALANCE);

        // Get all the accountBalanceReportItemList where accountBalance is greater than or equal to UPDATED_ACCOUNT_BALANCE
        defaultAccountBalanceReportItemShouldNotBeFound("accountBalance.greaterThanOrEqual=" + UPDATED_ACCOUNT_BALANCE);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountBalanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountBalance is less than or equal to DEFAULT_ACCOUNT_BALANCE
        defaultAccountBalanceReportItemShouldBeFound("accountBalance.lessThanOrEqual=" + DEFAULT_ACCOUNT_BALANCE);

        // Get all the accountBalanceReportItemList where accountBalance is less than or equal to SMALLER_ACCOUNT_BALANCE
        defaultAccountBalanceReportItemShouldNotBeFound("accountBalance.lessThanOrEqual=" + SMALLER_ACCOUNT_BALANCE);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountBalanceIsLessThanSomething() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountBalance is less than DEFAULT_ACCOUNT_BALANCE
        defaultAccountBalanceReportItemShouldNotBeFound("accountBalance.lessThan=" + DEFAULT_ACCOUNT_BALANCE);

        // Get all the accountBalanceReportItemList where accountBalance is less than UPDATED_ACCOUNT_BALANCE
        defaultAccountBalanceReportItemShouldBeFound("accountBalance.lessThan=" + UPDATED_ACCOUNT_BALANCE);
    }

    @Test
    @Transactional
    void getAllAccountBalanceReportItemsByAccountBalanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        // Get all the accountBalanceReportItemList where accountBalance is greater than DEFAULT_ACCOUNT_BALANCE
        defaultAccountBalanceReportItemShouldNotBeFound("accountBalance.greaterThan=" + DEFAULT_ACCOUNT_BALANCE);

        // Get all the accountBalanceReportItemList where accountBalance is greater than SMALLER_ACCOUNT_BALANCE
        defaultAccountBalanceReportItemShouldBeFound("accountBalance.greaterThan=" + SMALLER_ACCOUNT_BALANCE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAccountBalanceReportItemShouldBeFound(String filter) throws Exception {
        restAccountBalanceReportItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountBalanceReportItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].accountBalance").value(hasItem(sameNumber(DEFAULT_ACCOUNT_BALANCE))));

        // Check, that the count call also returns 1
        restAccountBalanceReportItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAccountBalanceReportItemShouldNotBeFound(String filter) throws Exception {
        restAccountBalanceReportItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAccountBalanceReportItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAccountBalanceReportItem() throws Exception {
        // Get the accountBalanceReportItem
        restAccountBalanceReportItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAccountBalanceReportItem() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        int databaseSizeBeforeUpdate = accountBalanceReportItemRepository.findAll().size();
        accountBalanceReportItemSearchRepository.save(accountBalanceReportItem);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());

        // Update the accountBalanceReportItem
        AccountBalanceReportItem updatedAccountBalanceReportItem = accountBalanceReportItemRepository
            .findById(accountBalanceReportItem.getId())
            .get();
        // Disconnect from session so that the updates on updatedAccountBalanceReportItem are not directly saved in db
        em.detach(updatedAccountBalanceReportItem);
        updatedAccountBalanceReportItem
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .accountName(UPDATED_ACCOUNT_NAME)
            .accountBalance(UPDATED_ACCOUNT_BALANCE);
        AccountBalanceReportItemDTO accountBalanceReportItemDTO = accountBalanceReportItemMapper.toDto(updatedAccountBalanceReportItem);

        restAccountBalanceReportItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accountBalanceReportItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.calvary.web.rest.TestUtil.convertObjectToJsonBytes(accountBalanceReportItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the AccountBalanceReportItem in the database
        List<AccountBalanceReportItem> accountBalanceReportItemList = accountBalanceReportItemRepository.findAll();
        assertThat(accountBalanceReportItemList).hasSize(databaseSizeBeforeUpdate);
        AccountBalanceReportItem testAccountBalanceReportItem = accountBalanceReportItemList.get(accountBalanceReportItemList.size() - 1);
        assertThat(testAccountBalanceReportItem.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
        assertThat(testAccountBalanceReportItem.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
        assertThat(testAccountBalanceReportItem.getAccountBalance()).isEqualByComparingTo(UPDATED_ACCOUNT_BALANCE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<AccountBalanceReportItem> accountBalanceReportItemSearchList = IterableUtils.toList(
                    accountBalanceReportItemSearchRepository.findAll()
                );
                AccountBalanceReportItem testAccountBalanceReportItemSearch = accountBalanceReportItemSearchList.get(
                    searchDatabaseSizeAfter - 1
                );
                assertThat(testAccountBalanceReportItemSearch.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
                assertThat(testAccountBalanceReportItemSearch.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
                assertThat(testAccountBalanceReportItemSearch.getAccountBalance()).isEqualByComparingTo(UPDATED_ACCOUNT_BALANCE);
            });
    }

    @Test
    @Transactional
    void putNonExistingAccountBalanceReportItem() throws Exception {
        int databaseSizeBeforeUpdate = accountBalanceReportItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        accountBalanceReportItem.setId(count.incrementAndGet());

        // Create the AccountBalanceReportItem
        AccountBalanceReportItemDTO accountBalanceReportItemDTO = accountBalanceReportItemMapper.toDto(accountBalanceReportItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountBalanceReportItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accountBalanceReportItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.calvary.web.rest.TestUtil.convertObjectToJsonBytes(accountBalanceReportItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountBalanceReportItem in the database
        List<AccountBalanceReportItem> accountBalanceReportItemList = accountBalanceReportItemRepository.findAll();
        assertThat(accountBalanceReportItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchAccountBalanceReportItem() throws Exception {
        int databaseSizeBeforeUpdate = accountBalanceReportItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        accountBalanceReportItem.setId(count.incrementAndGet());

        // Create the AccountBalanceReportItem
        AccountBalanceReportItemDTO accountBalanceReportItemDTO = accountBalanceReportItemMapper.toDto(accountBalanceReportItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountBalanceReportItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.calvary.web.rest.TestUtil.convertObjectToJsonBytes(accountBalanceReportItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountBalanceReportItem in the database
        List<AccountBalanceReportItem> accountBalanceReportItemList = accountBalanceReportItemRepository.findAll();
        assertThat(accountBalanceReportItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAccountBalanceReportItem() throws Exception {
        int databaseSizeBeforeUpdate = accountBalanceReportItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        accountBalanceReportItem.setId(count.incrementAndGet());

        // Create the AccountBalanceReportItem
        AccountBalanceReportItemDTO accountBalanceReportItemDTO = accountBalanceReportItemMapper.toDto(accountBalanceReportItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountBalanceReportItemMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.calvary.web.rest.TestUtil.convertObjectToJsonBytes(accountBalanceReportItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountBalanceReportItem in the database
        List<AccountBalanceReportItem> accountBalanceReportItemList = accountBalanceReportItemRepository.findAll();
        assertThat(accountBalanceReportItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateAccountBalanceReportItemWithPatch() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        int databaseSizeBeforeUpdate = accountBalanceReportItemRepository.findAll().size();

        // Update the accountBalanceReportItem using partial update
        AccountBalanceReportItem partialUpdatedAccountBalanceReportItem = new AccountBalanceReportItem();
        partialUpdatedAccountBalanceReportItem.setId(accountBalanceReportItem.getId());

        partialUpdatedAccountBalanceReportItem.accountNumber(UPDATED_ACCOUNT_NUMBER);

        restAccountBalanceReportItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountBalanceReportItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(io.github.calvary.web.rest.TestUtil.convertObjectToJsonBytes(partialUpdatedAccountBalanceReportItem))
            )
            .andExpect(status().isOk());

        // Validate the AccountBalanceReportItem in the database
        List<AccountBalanceReportItem> accountBalanceReportItemList = accountBalanceReportItemRepository.findAll();
        assertThat(accountBalanceReportItemList).hasSize(databaseSizeBeforeUpdate);
        AccountBalanceReportItem testAccountBalanceReportItem = accountBalanceReportItemList.get(accountBalanceReportItemList.size() - 1);
        assertThat(testAccountBalanceReportItem.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
        assertThat(testAccountBalanceReportItem.getAccountName()).isEqualTo(DEFAULT_ACCOUNT_NAME);
        assertThat(testAccountBalanceReportItem.getAccountBalance()).isEqualByComparingTo(DEFAULT_ACCOUNT_BALANCE);
    }

    @Test
    @Transactional
    void fullUpdateAccountBalanceReportItemWithPatch() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);

        int databaseSizeBeforeUpdate = accountBalanceReportItemRepository.findAll().size();

        // Update the accountBalanceReportItem using partial update
        AccountBalanceReportItem partialUpdatedAccountBalanceReportItem = new AccountBalanceReportItem();
        partialUpdatedAccountBalanceReportItem.setId(accountBalanceReportItem.getId());

        partialUpdatedAccountBalanceReportItem
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .accountName(UPDATED_ACCOUNT_NAME)
            .accountBalance(UPDATED_ACCOUNT_BALANCE);

        restAccountBalanceReportItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountBalanceReportItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(io.github.calvary.web.rest.TestUtil.convertObjectToJsonBytes(partialUpdatedAccountBalanceReportItem))
            )
            .andExpect(status().isOk());

        // Validate the AccountBalanceReportItem in the database
        List<AccountBalanceReportItem> accountBalanceReportItemList = accountBalanceReportItemRepository.findAll();
        assertThat(accountBalanceReportItemList).hasSize(databaseSizeBeforeUpdate);
        AccountBalanceReportItem testAccountBalanceReportItem = accountBalanceReportItemList.get(accountBalanceReportItemList.size() - 1);
        assertThat(testAccountBalanceReportItem.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
        assertThat(testAccountBalanceReportItem.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
        assertThat(testAccountBalanceReportItem.getAccountBalance()).isEqualByComparingTo(UPDATED_ACCOUNT_BALANCE);
    }

    @Test
    @Transactional
    void patchNonExistingAccountBalanceReportItem() throws Exception {
        int databaseSizeBeforeUpdate = accountBalanceReportItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        accountBalanceReportItem.setId(count.incrementAndGet());

        // Create the AccountBalanceReportItem
        AccountBalanceReportItemDTO accountBalanceReportItemDTO = accountBalanceReportItemMapper.toDto(accountBalanceReportItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountBalanceReportItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, accountBalanceReportItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(io.github.calvary.web.rest.TestUtil.convertObjectToJsonBytes(accountBalanceReportItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountBalanceReportItem in the database
        List<AccountBalanceReportItem> accountBalanceReportItemList = accountBalanceReportItemRepository.findAll();
        assertThat(accountBalanceReportItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAccountBalanceReportItem() throws Exception {
        int databaseSizeBeforeUpdate = accountBalanceReportItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        accountBalanceReportItem.setId(count.incrementAndGet());

        // Create the AccountBalanceReportItem
        AccountBalanceReportItemDTO accountBalanceReportItemDTO = accountBalanceReportItemMapper.toDto(accountBalanceReportItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountBalanceReportItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(io.github.calvary.web.rest.TestUtil.convertObjectToJsonBytes(accountBalanceReportItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountBalanceReportItem in the database
        List<AccountBalanceReportItem> accountBalanceReportItemList = accountBalanceReportItemRepository.findAll();
        assertThat(accountBalanceReportItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAccountBalanceReportItem() throws Exception {
        int databaseSizeBeforeUpdate = accountBalanceReportItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        accountBalanceReportItem.setId(count.incrementAndGet());

        // Create the AccountBalanceReportItem
        AccountBalanceReportItemDTO accountBalanceReportItemDTO = accountBalanceReportItemMapper.toDto(accountBalanceReportItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountBalanceReportItemMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountBalanceReportItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountBalanceReportItem in the database
        List<AccountBalanceReportItem> accountBalanceReportItemList = accountBalanceReportItemRepository.findAll();
        assertThat(accountBalanceReportItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteAccountBalanceReportItem() throws Exception {
        // Initialize the database
        accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);
        accountBalanceReportItemRepository.save(accountBalanceReportItem);
        accountBalanceReportItemSearchRepository.save(accountBalanceReportItem);

        int databaseSizeBeforeDelete = accountBalanceReportItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the accountBalanceReportItem
        restAccountBalanceReportItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, accountBalanceReportItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AccountBalanceReportItem> accountBalanceReportItemList = accountBalanceReportItemRepository.findAll();
        assertThat(accountBalanceReportItemList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountBalanceReportItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchAccountBalanceReportItem() throws Exception {
        // Initialize the database
        accountBalanceReportItem = accountBalanceReportItemRepository.saveAndFlush(accountBalanceReportItem);
        accountBalanceReportItemSearchRepository.save(accountBalanceReportItem);

        // Search the accountBalanceReportItem
        restAccountBalanceReportItemMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + accountBalanceReportItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountBalanceReportItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].accountBalance").value(hasItem(sameNumber(DEFAULT_ACCOUNT_BALANCE))));
    }
}

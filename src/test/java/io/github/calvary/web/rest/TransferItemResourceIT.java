package io.github.calvary.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.domain.TransactionClass;
import io.github.calvary.domain.TransferItem;
import io.github.calvary.repository.TransferItemRepository;
import io.github.calvary.repository.search.TransferItemSearchRepository;
import io.github.calvary.service.TransferItemService;
import io.github.calvary.service.criteria.TransferItemCriteria;
import io.github.calvary.service.dto.TransferItemDTO;
import io.github.calvary.service.mapper.TransferItemMapper;
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
 * Integration tests for the {@link TransferItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TransferItemResourceIT {

    private static final String DEFAULT_ITEM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transfer-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/transfer-items";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransferItemRepository transferItemRepository;

    @Mock
    private TransferItemRepository transferItemRepositoryMock;

    @Autowired
    private TransferItemMapper transferItemMapper;

    @Mock
    private TransferItemService transferItemServiceMock;

    @Autowired
    private TransferItemSearchRepository transferItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransferItemMockMvc;

    private TransferItem transferItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransferItem createEntity(EntityManager em) {
        TransferItem transferItem = new TransferItem().itemName(DEFAULT_ITEM_NAME).description(DEFAULT_DESCRIPTION);
        // Add required entity
        TransactionAccount transactionAccount;
        if (TestUtil.findAll(em, TransactionAccount.class).isEmpty()) {
            transactionAccount = TransactionAccountResourceIT.createEntity(em);
            em.persist(transactionAccount);
            em.flush();
        } else {
            transactionAccount = TestUtil.findAll(em, TransactionAccount.class).get(0);
        }
        transferItem.setTransactionAccount(transactionAccount);
        return transferItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransferItem createUpdatedEntity(EntityManager em) {
        TransferItem transferItem = new TransferItem().itemName(UPDATED_ITEM_NAME).description(UPDATED_DESCRIPTION);
        // Add required entity
        TransactionAccount transactionAccount;
        if (TestUtil.findAll(em, TransactionAccount.class).isEmpty()) {
            transactionAccount = TransactionAccountResourceIT.createUpdatedEntity(em);
            em.persist(transactionAccount);
            em.flush();
        } else {
            transactionAccount = TestUtil.findAll(em, TransactionAccount.class).get(0);
        }
        transferItem.setTransactionAccount(transactionAccount);
        return transferItem;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        transferItemSearchRepository.deleteAll();
        assertThat(transferItemSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        transferItem = createEntity(em);
    }

    @Test
    @Transactional
    void createTransferItem() throws Exception {
        int databaseSizeBeforeCreate = transferItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        // Create the TransferItem
        TransferItemDTO transferItemDTO = transferItemMapper.toDto(transferItem);
        restTransferItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transferItemDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TransferItem in the database
        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TransferItem testTransferItem = transferItemList.get(transferItemList.size() - 1);
        assertThat(testTransferItem.getItemName()).isEqualTo(DEFAULT_ITEM_NAME);
        assertThat(testTransferItem.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTransferItemWithExistingId() throws Exception {
        // Create the TransferItem with an existing ID
        transferItem.setId(1L);
        TransferItemDTO transferItemDTO = transferItemMapper.toDto(transferItem);

        int databaseSizeBeforeCreate = transferItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransferItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transferItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransferItem in the database
        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkItemNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = transferItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        // set the field null
        transferItem.setItemName(null);

        // Create the TransferItem, which fails.
        TransferItemDTO transferItemDTO = transferItemMapper.toDto(transferItem);

        restTransferItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transferItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTransferItems() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        // Get all the transferItemList
        restTransferItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transferItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemName").value(hasItem(DEFAULT_ITEM_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransferItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(transferItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransferItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(transferItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransferItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(transferItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransferItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(transferItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTransferItem() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        // Get the transferItem
        restTransferItemMockMvc
            .perform(get(ENTITY_API_URL_ID, transferItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transferItem.getId().intValue()))
            .andExpect(jsonPath("$.itemName").value(DEFAULT_ITEM_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getTransferItemsByIdFiltering() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        Long id = transferItem.getId();

        defaultTransferItemShouldBeFound("id.equals=" + id);
        defaultTransferItemShouldNotBeFound("id.notEquals=" + id);

        defaultTransferItemShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransferItemShouldNotBeFound("id.greaterThan=" + id);

        defaultTransferItemShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransferItemShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransferItemsByItemNameIsEqualToSomething() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        // Get all the transferItemList where itemName equals to DEFAULT_ITEM_NAME
        defaultTransferItemShouldBeFound("itemName.equals=" + DEFAULT_ITEM_NAME);

        // Get all the transferItemList where itemName equals to UPDATED_ITEM_NAME
        defaultTransferItemShouldNotBeFound("itemName.equals=" + UPDATED_ITEM_NAME);
    }

    @Test
    @Transactional
    void getAllTransferItemsByItemNameIsInShouldWork() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        // Get all the transferItemList where itemName in DEFAULT_ITEM_NAME or UPDATED_ITEM_NAME
        defaultTransferItemShouldBeFound("itemName.in=" + DEFAULT_ITEM_NAME + "," + UPDATED_ITEM_NAME);

        // Get all the transferItemList where itemName equals to UPDATED_ITEM_NAME
        defaultTransferItemShouldNotBeFound("itemName.in=" + UPDATED_ITEM_NAME);
    }

    @Test
    @Transactional
    void getAllTransferItemsByItemNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        // Get all the transferItemList where itemName is not null
        defaultTransferItemShouldBeFound("itemName.specified=true");

        // Get all the transferItemList where itemName is null
        defaultTransferItemShouldNotBeFound("itemName.specified=false");
    }

    @Test
    @Transactional
    void getAllTransferItemsByItemNameContainsSomething() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        // Get all the transferItemList where itemName contains DEFAULT_ITEM_NAME
        defaultTransferItemShouldBeFound("itemName.contains=" + DEFAULT_ITEM_NAME);

        // Get all the transferItemList where itemName contains UPDATED_ITEM_NAME
        defaultTransferItemShouldNotBeFound("itemName.contains=" + UPDATED_ITEM_NAME);
    }

    @Test
    @Transactional
    void getAllTransferItemsByItemNameNotContainsSomething() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        // Get all the transferItemList where itemName does not contain DEFAULT_ITEM_NAME
        defaultTransferItemShouldNotBeFound("itemName.doesNotContain=" + DEFAULT_ITEM_NAME);

        // Get all the transferItemList where itemName does not contain UPDATED_ITEM_NAME
        defaultTransferItemShouldBeFound("itemName.doesNotContain=" + UPDATED_ITEM_NAME);
    }

    @Test
    @Transactional
    void getAllTransferItemsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        // Get all the transferItemList where description equals to DEFAULT_DESCRIPTION
        defaultTransferItemShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the transferItemList where description equals to UPDATED_DESCRIPTION
        defaultTransferItemShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransferItemsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        // Get all the transferItemList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTransferItemShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the transferItemList where description equals to UPDATED_DESCRIPTION
        defaultTransferItemShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransferItemsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        // Get all the transferItemList where description is not null
        defaultTransferItemShouldBeFound("description.specified=true");

        // Get all the transferItemList where description is null
        defaultTransferItemShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTransferItemsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        // Get all the transferItemList where description contains DEFAULT_DESCRIPTION
        defaultTransferItemShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the transferItemList where description contains UPDATED_DESCRIPTION
        defaultTransferItemShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransferItemsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        // Get all the transferItemList where description does not contain DEFAULT_DESCRIPTION
        defaultTransferItemShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the transferItemList where description does not contain UPDATED_DESCRIPTION
        defaultTransferItemShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransferItemsByTransactionClassIsEqualToSomething() throws Exception {
        TransactionClass transactionClass;
        if (TestUtil.findAll(em, TransactionClass.class).isEmpty()) {
            transferItemRepository.saveAndFlush(transferItem);
            transactionClass = TransactionClassResourceIT.createEntity(em);
        } else {
            transactionClass = TestUtil.findAll(em, TransactionClass.class).get(0);
        }
        em.persist(transactionClass);
        em.flush();
        transferItem.setTransactionClass(transactionClass);
        transferItemRepository.saveAndFlush(transferItem);
        Long transactionClassId = transactionClass.getId();

        // Get all the transferItemList where transactionClass equals to transactionClassId
        defaultTransferItemShouldBeFound("transactionClassId.equals=" + transactionClassId);

        // Get all the transferItemList where transactionClass equals to (transactionClassId + 1)
        defaultTransferItemShouldNotBeFound("transactionClassId.equals=" + (transactionClassId + 1));
    }

    @Test
    @Transactional
    void getAllTransferItemsByTransactionAccountIsEqualToSomething() throws Exception {
        TransactionAccount transactionAccount;
        if (TestUtil.findAll(em, TransactionAccount.class).isEmpty()) {
            transferItemRepository.saveAndFlush(transferItem);
            transactionAccount = TransactionAccountResourceIT.createEntity(em);
        } else {
            transactionAccount = TestUtil.findAll(em, TransactionAccount.class).get(0);
        }
        em.persist(transactionAccount);
        em.flush();
        transferItem.setTransactionAccount(transactionAccount);
        transferItemRepository.saveAndFlush(transferItem);
        Long transactionAccountId = transactionAccount.getId();

        // Get all the transferItemList where transactionAccount equals to transactionAccountId
        defaultTransferItemShouldBeFound("transactionAccountId.equals=" + transactionAccountId);

        // Get all the transferItemList where transactionAccount equals to (transactionAccountId + 1)
        defaultTransferItemShouldNotBeFound("transactionAccountId.equals=" + (transactionAccountId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransferItemShouldBeFound(String filter) throws Exception {
        restTransferItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transferItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemName").value(hasItem(DEFAULT_ITEM_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restTransferItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransferItemShouldNotBeFound(String filter) throws Exception {
        restTransferItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransferItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransferItem() throws Exception {
        // Get the transferItem
        restTransferItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransferItem() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        int databaseSizeBeforeUpdate = transferItemRepository.findAll().size();
        transferItemSearchRepository.save(transferItem);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemSearchRepository.findAll());

        // Update the transferItem
        TransferItem updatedTransferItem = transferItemRepository.findById(transferItem.getId()).get();
        // Disconnect from session so that the updates on updatedTransferItem are not directly saved in db
        em.detach(updatedTransferItem);
        updatedTransferItem.itemName(UPDATED_ITEM_NAME).description(UPDATED_DESCRIPTION);
        TransferItemDTO transferItemDTO = transferItemMapper.toDto(updatedTransferItem);

        restTransferItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transferItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transferItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransferItem in the database
        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeUpdate);
        TransferItem testTransferItem = transferItemList.get(transferItemList.size() - 1);
        assertThat(testTransferItem.getItemName()).isEqualTo(UPDATED_ITEM_NAME);
        assertThat(testTransferItem.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TransferItem> transferItemSearchList = IterableUtils.toList(transferItemSearchRepository.findAll());
                TransferItem testTransferItemSearch = transferItemSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTransferItemSearch.getItemName()).isEqualTo(UPDATED_ITEM_NAME);
                assertThat(testTransferItemSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingTransferItem() throws Exception {
        int databaseSizeBeforeUpdate = transferItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        transferItem.setId(count.incrementAndGet());

        // Create the TransferItem
        TransferItemDTO transferItemDTO = transferItemMapper.toDto(transferItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransferItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transferItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transferItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransferItem in the database
        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransferItem() throws Exception {
        int databaseSizeBeforeUpdate = transferItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        transferItem.setId(count.incrementAndGet());

        // Create the TransferItem
        TransferItemDTO transferItemDTO = transferItemMapper.toDto(transferItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransferItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transferItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransferItem in the database
        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransferItem() throws Exception {
        int databaseSizeBeforeUpdate = transferItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        transferItem.setId(count.incrementAndGet());

        // Create the TransferItem
        TransferItemDTO transferItemDTO = transferItemMapper.toDto(transferItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransferItemMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transferItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransferItem in the database
        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTransferItemWithPatch() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        int databaseSizeBeforeUpdate = transferItemRepository.findAll().size();

        // Update the transferItem using partial update
        TransferItem partialUpdatedTransferItem = new TransferItem();
        partialUpdatedTransferItem.setId(transferItem.getId());

        restTransferItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransferItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransferItem))
            )
            .andExpect(status().isOk());

        // Validate the TransferItem in the database
        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeUpdate);
        TransferItem testTransferItem = transferItemList.get(transferItemList.size() - 1);
        assertThat(testTransferItem.getItemName()).isEqualTo(DEFAULT_ITEM_NAME);
        assertThat(testTransferItem.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateTransferItemWithPatch() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);

        int databaseSizeBeforeUpdate = transferItemRepository.findAll().size();

        // Update the transferItem using partial update
        TransferItem partialUpdatedTransferItem = new TransferItem();
        partialUpdatedTransferItem.setId(transferItem.getId());

        partialUpdatedTransferItem.itemName(UPDATED_ITEM_NAME).description(UPDATED_DESCRIPTION);

        restTransferItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransferItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransferItem))
            )
            .andExpect(status().isOk());

        // Validate the TransferItem in the database
        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeUpdate);
        TransferItem testTransferItem = transferItemList.get(transferItemList.size() - 1);
        assertThat(testTransferItem.getItemName()).isEqualTo(UPDATED_ITEM_NAME);
        assertThat(testTransferItem.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingTransferItem() throws Exception {
        int databaseSizeBeforeUpdate = transferItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        transferItem.setId(count.incrementAndGet());

        // Create the TransferItem
        TransferItemDTO transferItemDTO = transferItemMapper.toDto(transferItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransferItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transferItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transferItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransferItem in the database
        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransferItem() throws Exception {
        int databaseSizeBeforeUpdate = transferItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        transferItem.setId(count.incrementAndGet());

        // Create the TransferItem
        TransferItemDTO transferItemDTO = transferItemMapper.toDto(transferItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransferItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transferItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransferItem in the database
        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransferItem() throws Exception {
        int databaseSizeBeforeUpdate = transferItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        transferItem.setId(count.incrementAndGet());

        // Create the TransferItem
        TransferItemDTO transferItemDTO = transferItemMapper.toDto(transferItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransferItemMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transferItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransferItem in the database
        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTransferItem() throws Exception {
        // Initialize the database
        transferItemRepository.saveAndFlush(transferItem);
        transferItemRepository.save(transferItem);
        transferItemSearchRepository.save(transferItem);

        int databaseSizeBeforeDelete = transferItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transferItem
        restTransferItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, transferItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransferItem> transferItemList = transferItemRepository.findAll();
        assertThat(transferItemList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transferItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTransferItem() throws Exception {
        // Initialize the database
        transferItem = transferItemRepository.saveAndFlush(transferItem);
        transferItemSearchRepository.save(transferItem);

        // Search the transferItem
        restTransferItemMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transferItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transferItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemName").value(hasItem(DEFAULT_ITEM_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}

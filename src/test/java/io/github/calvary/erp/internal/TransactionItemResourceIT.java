package io.github.calvary.erp.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.domain.TransactionClass;
import io.github.calvary.domain.TransactionItem;
import io.github.calvary.repository.TransactionItemRepository;
import io.github.calvary.repository.search.TransactionItemSearchRepository;
import io.github.calvary.service.TransactionItemService;
import io.github.calvary.service.dto.TransactionItemDTO;
import io.github.calvary.service.mapper.TransactionItemMapper;
import io.github.calvary.web.rest.TransactionItemResource;
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
 * Integration tests for the {@link TransactionItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TransactionItemResourceIT {

    private static final String DEFAULT_ITEM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/app/transaction-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/app/_search/transaction-items";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Mock
    private TransactionItemRepository transactionItemRepositoryMock;

    @Autowired
    private TransactionItemMapper transactionItemMapper;

    @Mock
    private TransactionItemService transactionItemServiceMock;

    @Autowired
    private TransactionItemSearchRepository transactionItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionItemMockMvc;

    private TransactionItem transactionItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionItem createEntity(EntityManager em) {
        TransactionItem transactionItem = new TransactionItem().itemName(DEFAULT_ITEM_NAME).description(DEFAULT_DESCRIPTION);
        // Add required entity
        TransactionAccount transactionAccount;
        if (TestUtil.findAll(em, TransactionAccount.class).isEmpty()) {
            transactionAccount = TransactionAccountResourceIT.createEntity(em);
            em.persist(transactionAccount);
            em.flush();
        } else {
            transactionAccount = TestUtil.findAll(em, TransactionAccount.class).get(0);
        }
        transactionItem.setTransactionAccount(transactionAccount);
        return transactionItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionItem createUpdatedEntity(EntityManager em) {
        TransactionItem transactionItem = new TransactionItem().itemName(UPDATED_ITEM_NAME).description(UPDATED_DESCRIPTION);
        // Add required entity
        TransactionAccount transactionAccount;
        if (TestUtil.findAll(em, TransactionAccount.class).isEmpty()) {
            transactionAccount = TransactionAccountResourceIT.createUpdatedEntity(em);
            em.persist(transactionAccount);
            em.flush();
        } else {
            transactionAccount = TestUtil.findAll(em, TransactionAccount.class).get(0);
        }
        transactionItem.setTransactionAccount(transactionAccount);
        return transactionItem;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        transactionItemSearchRepository.deleteAll();
        assertThat(transactionItemSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        transactionItem = createEntity(em);
    }

    @Test
    @Transactional
    void createTransactionItem() throws Exception {
        int databaseSizeBeforeCreate = transactionItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        // Create the TransactionItem
        TransactionItemDTO transactionItemDTO = transactionItemMapper.toDto(transactionItem);
        restTransactionItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionItemDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TransactionItem in the database
        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TransactionItem testTransactionItem = transactionItemList.get(transactionItemList.size() - 1);
        assertThat(testTransactionItem.getItemName()).isEqualTo(DEFAULT_ITEM_NAME);
        assertThat(testTransactionItem.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTransactionItemWithExistingId() throws Exception {
        // Create the TransactionItem with an existing ID
        transactionItem.setId(1L);
        TransactionItemDTO transactionItemDTO = transactionItemMapper.toDto(transactionItem);

        int databaseSizeBeforeCreate = transactionItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItem in the database
        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkItemNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        // set the field null
        transactionItem.setItemName(null);

        // Create the TransactionItem, which fails.
        TransactionItemDTO transactionItemDTO = transactionItemMapper.toDto(transactionItem);

        restTransactionItemMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTransactionItems() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        // Get all the transactionItemList
        restTransactionItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemName").value(hasItem(DEFAULT_ITEM_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransactionItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(transactionItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransactionItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(transactionItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransactionItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(transactionItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransactionItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(transactionItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTransactionItem() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        // Get the transactionItem
        restTransactionItemMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionItem.getId().intValue()))
            .andExpect(jsonPath("$.itemName").value(DEFAULT_ITEM_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getTransactionItemsByIdFiltering() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        Long id = transactionItem.getId();

        defaultTransactionItemShouldBeFound("id.equals=" + id);
        defaultTransactionItemShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionItemShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionItemShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionItemShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionItemShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionItemsByItemNameIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        // Get all the transactionItemList where itemName equals to DEFAULT_ITEM_NAME
        defaultTransactionItemShouldBeFound("itemName.equals=" + DEFAULT_ITEM_NAME);

        // Get all the transactionItemList where itemName equals to UPDATED_ITEM_NAME
        defaultTransactionItemShouldNotBeFound("itemName.equals=" + UPDATED_ITEM_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionItemsByItemNameIsInShouldWork() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        // Get all the transactionItemList where itemName in DEFAULT_ITEM_NAME or UPDATED_ITEM_NAME
        defaultTransactionItemShouldBeFound("itemName.in=" + DEFAULT_ITEM_NAME + "," + UPDATED_ITEM_NAME);

        // Get all the transactionItemList where itemName equals to UPDATED_ITEM_NAME
        defaultTransactionItemShouldNotBeFound("itemName.in=" + UPDATED_ITEM_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionItemsByItemNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        // Get all the transactionItemList where itemName is not null
        defaultTransactionItemShouldBeFound("itemName.specified=true");

        // Get all the transactionItemList where itemName is null
        defaultTransactionItemShouldNotBeFound("itemName.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionItemsByItemNameContainsSomething() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        // Get all the transactionItemList where itemName contains DEFAULT_ITEM_NAME
        defaultTransactionItemShouldBeFound("itemName.contains=" + DEFAULT_ITEM_NAME);

        // Get all the transactionItemList where itemName contains UPDATED_ITEM_NAME
        defaultTransactionItemShouldNotBeFound("itemName.contains=" + UPDATED_ITEM_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionItemsByItemNameNotContainsSomething() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        // Get all the transactionItemList where itemName does not contain DEFAULT_ITEM_NAME
        defaultTransactionItemShouldNotBeFound("itemName.doesNotContain=" + DEFAULT_ITEM_NAME);

        // Get all the transactionItemList where itemName does not contain UPDATED_ITEM_NAME
        defaultTransactionItemShouldBeFound("itemName.doesNotContain=" + UPDATED_ITEM_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionItemsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        // Get all the transactionItemList where description equals to DEFAULT_DESCRIPTION
        defaultTransactionItemShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the transactionItemList where description equals to UPDATED_DESCRIPTION
        defaultTransactionItemShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionItemsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        // Get all the transactionItemList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTransactionItemShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the transactionItemList where description equals to UPDATED_DESCRIPTION
        defaultTransactionItemShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionItemsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        // Get all the transactionItemList where description is not null
        defaultTransactionItemShouldBeFound("description.specified=true");

        // Get all the transactionItemList where description is null
        defaultTransactionItemShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionItemsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        // Get all the transactionItemList where description contains DEFAULT_DESCRIPTION
        defaultTransactionItemShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the transactionItemList where description contains UPDATED_DESCRIPTION
        defaultTransactionItemShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionItemsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        // Get all the transactionItemList where description does not contain DEFAULT_DESCRIPTION
        defaultTransactionItemShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the transactionItemList where description does not contain UPDATED_DESCRIPTION
        defaultTransactionItemShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionItemsByTransactionClassIsEqualToSomething() throws Exception {
        TransactionClass transactionClass;
        if (TestUtil.findAll(em, TransactionClass.class).isEmpty()) {
            transactionItemRepository.saveAndFlush(transactionItem);
            transactionClass = TransactionClassResourceIT.createEntity(em);
        } else {
            transactionClass = TestUtil.findAll(em, TransactionClass.class).get(0);
        }
        em.persist(transactionClass);
        em.flush();
        transactionItem.setTransactionClass(transactionClass);
        transactionItemRepository.saveAndFlush(transactionItem);
        Long transactionClassId = transactionClass.getId();

        // Get all the transactionItemList where transactionClass equals to transactionClassId
        defaultTransactionItemShouldBeFound("transactionClassId.equals=" + transactionClassId);

        // Get all the transactionItemList where transactionClass equals to (transactionClassId + 1)
        defaultTransactionItemShouldNotBeFound("transactionClassId.equals=" + (transactionClassId + 1));
    }

    @Test
    @Transactional
    void getAllTransactionItemsByTransactionAccountIsEqualToSomething() throws Exception {
        TransactionAccount transactionAccount;
        if (TestUtil.findAll(em, TransactionAccount.class).isEmpty()) {
            transactionItemRepository.saveAndFlush(transactionItem);
            transactionAccount = TransactionAccountResourceIT.createEntity(em);
        } else {
            transactionAccount = TestUtil.findAll(em, TransactionAccount.class).get(0);
        }
        em.persist(transactionAccount);
        em.flush();
        transactionItem.setTransactionAccount(transactionAccount);
        transactionItemRepository.saveAndFlush(transactionItem);
        Long transactionAccountId = transactionAccount.getId();

        // Get all the transactionItemList where transactionAccount equals to transactionAccountId
        defaultTransactionItemShouldBeFound("transactionAccountId.equals=" + transactionAccountId);

        // Get all the transactionItemList where transactionAccount equals to (transactionAccountId + 1)
        defaultTransactionItemShouldNotBeFound("transactionAccountId.equals=" + (transactionAccountId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionItemShouldBeFound(String filter) throws Exception {
        restTransactionItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemName").value(hasItem(DEFAULT_ITEM_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restTransactionItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionItemShouldNotBeFound(String filter) throws Exception {
        restTransactionItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransactionItem() throws Exception {
        // Get the transactionItem
        restTransactionItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransactionItem() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        int databaseSizeBeforeUpdate = transactionItemRepository.findAll().size();
        transactionItemSearchRepository.save(transactionItem);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());

        // Update the transactionItem
        TransactionItem updatedTransactionItem = transactionItemRepository.findById(transactionItem.getId()).get();
        // Disconnect from session so that the updates on updatedTransactionItem are not directly saved in db
        em.detach(updatedTransactionItem);
        updatedTransactionItem.itemName(UPDATED_ITEM_NAME).description(UPDATED_DESCRIPTION);
        TransactionItemDTO transactionItemDTO = transactionItemMapper.toDto(updatedTransactionItem);

        restTransactionItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransactionItem in the database
        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeUpdate);
        TransactionItem testTransactionItem = transactionItemList.get(transactionItemList.size() - 1);
        assertThat(testTransactionItem.getItemName()).isEqualTo(UPDATED_ITEM_NAME);
        assertThat(testTransactionItem.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TransactionItem> transactionItemSearchList = IterableUtils.toList(transactionItemSearchRepository.findAll());
                TransactionItem testTransactionItemSearch = transactionItemSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTransactionItemSearch.getItemName()).isEqualTo(UPDATED_ITEM_NAME);
                assertThat(testTransactionItemSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingTransactionItem() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        transactionItem.setId(count.incrementAndGet());

        // Create the TransactionItem
        TransactionItemDTO transactionItemDTO = transactionItemMapper.toDto(transactionItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItem in the database
        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactionItem() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        transactionItem.setId(count.incrementAndGet());

        // Create the TransactionItem
        TransactionItemDTO transactionItemDTO = transactionItemMapper.toDto(transactionItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItem in the database
        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactionItem() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        transactionItem.setId(count.incrementAndGet());

        // Create the TransactionItem
        TransactionItemDTO transactionItemDTO = transactionItemMapper.toDto(transactionItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionItemMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionItem in the database
        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTransactionItemWithPatch() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        int databaseSizeBeforeUpdate = transactionItemRepository.findAll().size();

        // Update the transactionItem using partial update
        TransactionItem partialUpdatedTransactionItem = new TransactionItem();
        partialUpdatedTransactionItem.setId(transactionItem.getId());

        partialUpdatedTransactionItem.description(UPDATED_DESCRIPTION);

        restTransactionItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionItem))
            )
            .andExpect(status().isOk());

        // Validate the TransactionItem in the database
        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeUpdate);
        TransactionItem testTransactionItem = transactionItemList.get(transactionItemList.size() - 1);
        assertThat(testTransactionItem.getItemName()).isEqualTo(DEFAULT_ITEM_NAME);
        assertThat(testTransactionItem.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateTransactionItemWithPatch() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);

        int databaseSizeBeforeUpdate = transactionItemRepository.findAll().size();

        // Update the transactionItem using partial update
        TransactionItem partialUpdatedTransactionItem = new TransactionItem();
        partialUpdatedTransactionItem.setId(transactionItem.getId());

        partialUpdatedTransactionItem.itemName(UPDATED_ITEM_NAME).description(UPDATED_DESCRIPTION);

        restTransactionItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionItem))
            )
            .andExpect(status().isOk());

        // Validate the TransactionItem in the database
        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeUpdate);
        TransactionItem testTransactionItem = transactionItemList.get(transactionItemList.size() - 1);
        assertThat(testTransactionItem.getItemName()).isEqualTo(UPDATED_ITEM_NAME);
        assertThat(testTransactionItem.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingTransactionItem() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        transactionItem.setId(count.incrementAndGet());

        // Create the TransactionItem
        TransactionItemDTO transactionItemDTO = transactionItemMapper.toDto(transactionItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItem in the database
        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactionItem() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        transactionItem.setId(count.incrementAndGet());

        // Create the TransactionItem
        TransactionItemDTO transactionItemDTO = transactionItemMapper.toDto(transactionItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionItem in the database
        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactionItem() throws Exception {
        int databaseSizeBeforeUpdate = transactionItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        transactionItem.setId(count.incrementAndGet());

        // Create the TransactionItem
        TransactionItemDTO transactionItemDTO = transactionItemMapper.toDto(transactionItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionItemMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionItem in the database
        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTransactionItem() throws Exception {
        // Initialize the database
        transactionItemRepository.saveAndFlush(transactionItem);
        transactionItemRepository.save(transactionItem);
        transactionItemSearchRepository.save(transactionItem);

        int databaseSizeBeforeDelete = transactionItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transactionItem
        restTransactionItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransactionItem> transactionItemList = transactionItemRepository.findAll();
        assertThat(transactionItemList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTransactionItem() throws Exception {
        // Initialize the database
        transactionItem = transactionItemRepository.saveAndFlush(transactionItem);
        transactionItemSearchRepository.save(transactionItem);

        // Search the transactionItem
        restTransactionItemMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transactionItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemName").value(hasItem(DEFAULT_ITEM_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}

package io.github.calvary.erp.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.TransactionClass;
import io.github.calvary.repository.TransactionClassRepository;
import io.github.calvary.repository.search.TransactionClassSearchRepository;
import io.github.calvary.service.dto.TransactionClassDTO;
import io.github.calvary.service.mapper.TransactionClassMapper;
import io.github.calvary.web.rest.TransactionClassResource;
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
 * Integration tests for the {@link TransactionClassResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionClassResourceIT {

    private static final String DEFAULT_CLASS_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CLASS_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/app/transaction-classes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/app/_search/transaction-classes";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionClassRepository transactionClassRepository;

    @Autowired
    private TransactionClassMapper transactionClassMapper;

    @Autowired
    private TransactionClassSearchRepository transactionClassSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionClassMockMvc;

    private TransactionClass transactionClass;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionClass createEntity(EntityManager em) {
        TransactionClass transactionClass = new TransactionClass().className(DEFAULT_CLASS_NAME);
        return transactionClass;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionClass createUpdatedEntity(EntityManager em) {
        TransactionClass transactionClass = new TransactionClass().className(UPDATED_CLASS_NAME);
        return transactionClass;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        transactionClassSearchRepository.deleteAll();
        assertThat(transactionClassSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        transactionClass = createEntity(em);
    }

    @Test
    @Transactional
    void createTransactionClass() throws Exception {
        int databaseSizeBeforeCreate = transactionClassRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        // Create the TransactionClass
        TransactionClassDTO transactionClassDTO = transactionClassMapper.toDto(transactionClass);
        restTransactionClassMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionClassDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TransactionClass in the database
        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TransactionClass testTransactionClass = transactionClassList.get(transactionClassList.size() - 1);
        assertThat(testTransactionClass.getClassName()).isEqualTo(DEFAULT_CLASS_NAME);
    }

    @Test
    @Transactional
    void createTransactionClassWithExistingId() throws Exception {
        // Create the TransactionClass with an existing ID
        transactionClass.setId(1L);
        TransactionClassDTO transactionClassDTO = transactionClassMapper.toDto(transactionClass);

        int databaseSizeBeforeCreate = transactionClassRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionClassMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionClass in the database
        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkClassNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionClassRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        // set the field null
        transactionClass.setClassName(null);

        // Create the TransactionClass, which fails.
        TransactionClassDTO transactionClassDTO = transactionClassMapper.toDto(transactionClass);

        restTransactionClassMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionClassDTO))
            )
            .andExpect(status().isBadRequest());

        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTransactionClasses() throws Exception {
        // Initialize the database
        transactionClassRepository.saveAndFlush(transactionClass);

        // Get all the transactionClassList
        restTransactionClassMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionClass.getId().intValue())))
            .andExpect(jsonPath("$.[*].className").value(hasItem(DEFAULT_CLASS_NAME)));
    }

    @Test
    @Transactional
    void getTransactionClass() throws Exception {
        // Initialize the database
        transactionClassRepository.saveAndFlush(transactionClass);

        // Get the transactionClass
        restTransactionClassMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionClass.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionClass.getId().intValue()))
            .andExpect(jsonPath("$.className").value(DEFAULT_CLASS_NAME));
    }

    @Test
    @Transactional
    void getTransactionClassesByIdFiltering() throws Exception {
        // Initialize the database
        transactionClassRepository.saveAndFlush(transactionClass);

        Long id = transactionClass.getId();

        defaultTransactionClassShouldBeFound("id.equals=" + id);
        defaultTransactionClassShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionClassShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionClassShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionClassShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionClassShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionClassesByClassNameIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionClassRepository.saveAndFlush(transactionClass);

        // Get all the transactionClassList where className equals to DEFAULT_CLASS_NAME
        defaultTransactionClassShouldBeFound("className.equals=" + DEFAULT_CLASS_NAME);

        // Get all the transactionClassList where className equals to UPDATED_CLASS_NAME
        defaultTransactionClassShouldNotBeFound("className.equals=" + UPDATED_CLASS_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionClassesByClassNameIsInShouldWork() throws Exception {
        // Initialize the database
        transactionClassRepository.saveAndFlush(transactionClass);

        // Get all the transactionClassList where className in DEFAULT_CLASS_NAME or UPDATED_CLASS_NAME
        defaultTransactionClassShouldBeFound("className.in=" + DEFAULT_CLASS_NAME + "," + UPDATED_CLASS_NAME);

        // Get all the transactionClassList where className equals to UPDATED_CLASS_NAME
        defaultTransactionClassShouldNotBeFound("className.in=" + UPDATED_CLASS_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionClassesByClassNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionClassRepository.saveAndFlush(transactionClass);

        // Get all the transactionClassList where className is not null
        defaultTransactionClassShouldBeFound("className.specified=true");

        // Get all the transactionClassList where className is null
        defaultTransactionClassShouldNotBeFound("className.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionClassesByClassNameContainsSomething() throws Exception {
        // Initialize the database
        transactionClassRepository.saveAndFlush(transactionClass);

        // Get all the transactionClassList where className contains DEFAULT_CLASS_NAME
        defaultTransactionClassShouldBeFound("className.contains=" + DEFAULT_CLASS_NAME);

        // Get all the transactionClassList where className contains UPDATED_CLASS_NAME
        defaultTransactionClassShouldNotBeFound("className.contains=" + UPDATED_CLASS_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionClassesByClassNameNotContainsSomething() throws Exception {
        // Initialize the database
        transactionClassRepository.saveAndFlush(transactionClass);

        // Get all the transactionClassList where className does not contain DEFAULT_CLASS_NAME
        defaultTransactionClassShouldNotBeFound("className.doesNotContain=" + DEFAULT_CLASS_NAME);

        // Get all the transactionClassList where className does not contain UPDATED_CLASS_NAME
        defaultTransactionClassShouldBeFound("className.doesNotContain=" + UPDATED_CLASS_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionClassShouldBeFound(String filter) throws Exception {
        restTransactionClassMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionClass.getId().intValue())))
            .andExpect(jsonPath("$.[*].className").value(hasItem(DEFAULT_CLASS_NAME)));

        // Check, that the count call also returns 1
        restTransactionClassMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionClassShouldNotBeFound(String filter) throws Exception {
        restTransactionClassMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionClassMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransactionClass() throws Exception {
        // Get the transactionClass
        restTransactionClassMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransactionClass() throws Exception {
        // Initialize the database
        transactionClassRepository.saveAndFlush(transactionClass);

        int databaseSizeBeforeUpdate = transactionClassRepository.findAll().size();
        transactionClassSearchRepository.save(transactionClass);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());

        // Update the transactionClass
        TransactionClass updatedTransactionClass = transactionClassRepository.findById(transactionClass.getId()).get();
        // Disconnect from session so that the updates on updatedTransactionClass are not directly saved in db
        em.detach(updatedTransactionClass);
        updatedTransactionClass.className(UPDATED_CLASS_NAME);
        TransactionClassDTO transactionClassDTO = transactionClassMapper.toDto(updatedTransactionClass);

        restTransactionClassMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionClassDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionClassDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransactionClass in the database
        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeUpdate);
        TransactionClass testTransactionClass = transactionClassList.get(transactionClassList.size() - 1);
        assertThat(testTransactionClass.getClassName()).isEqualTo(UPDATED_CLASS_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TransactionClass> transactionClassSearchList = IterableUtils.toList(transactionClassSearchRepository.findAll());
                TransactionClass testTransactionClassSearch = transactionClassSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTransactionClassSearch.getClassName()).isEqualTo(UPDATED_CLASS_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingTransactionClass() throws Exception {
        int databaseSizeBeforeUpdate = transactionClassRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        transactionClass.setId(count.incrementAndGet());

        // Create the TransactionClass
        TransactionClassDTO transactionClassDTO = transactionClassMapper.toDto(transactionClass);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionClassMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionClassDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionClass in the database
        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactionClass() throws Exception {
        int databaseSizeBeforeUpdate = transactionClassRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        transactionClass.setId(count.incrementAndGet());

        // Create the TransactionClass
        TransactionClassDTO transactionClassDTO = transactionClassMapper.toDto(transactionClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionClassMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionClass in the database
        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactionClass() throws Exception {
        int databaseSizeBeforeUpdate = transactionClassRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        transactionClass.setId(count.incrementAndGet());

        // Create the TransactionClass
        TransactionClassDTO transactionClassDTO = transactionClassMapper.toDto(transactionClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionClassMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionClassDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionClass in the database
        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTransactionClassWithPatch() throws Exception {
        // Initialize the database
        transactionClassRepository.saveAndFlush(transactionClass);

        int databaseSizeBeforeUpdate = transactionClassRepository.findAll().size();

        // Update the transactionClass using partial update
        TransactionClass partialUpdatedTransactionClass = new TransactionClass();
        partialUpdatedTransactionClass.setId(transactionClass.getId());

        restTransactionClassMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionClass.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionClass))
            )
            .andExpect(status().isOk());

        // Validate the TransactionClass in the database
        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeUpdate);
        TransactionClass testTransactionClass = transactionClassList.get(transactionClassList.size() - 1);
        assertThat(testTransactionClass.getClassName()).isEqualTo(DEFAULT_CLASS_NAME);
    }

    @Test
    @Transactional
    void fullUpdateTransactionClassWithPatch() throws Exception {
        // Initialize the database
        transactionClassRepository.saveAndFlush(transactionClass);

        int databaseSizeBeforeUpdate = transactionClassRepository.findAll().size();

        // Update the transactionClass using partial update
        TransactionClass partialUpdatedTransactionClass = new TransactionClass();
        partialUpdatedTransactionClass.setId(transactionClass.getId());

        partialUpdatedTransactionClass.className(UPDATED_CLASS_NAME);

        restTransactionClassMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionClass.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionClass))
            )
            .andExpect(status().isOk());

        // Validate the TransactionClass in the database
        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeUpdate);
        TransactionClass testTransactionClass = transactionClassList.get(transactionClassList.size() - 1);
        assertThat(testTransactionClass.getClassName()).isEqualTo(UPDATED_CLASS_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingTransactionClass() throws Exception {
        int databaseSizeBeforeUpdate = transactionClassRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        transactionClass.setId(count.incrementAndGet());

        // Create the TransactionClass
        TransactionClassDTO transactionClassDTO = transactionClassMapper.toDto(transactionClass);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionClassMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionClassDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionClass in the database
        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactionClass() throws Exception {
        int databaseSizeBeforeUpdate = transactionClassRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        transactionClass.setId(count.incrementAndGet());

        // Create the TransactionClass
        TransactionClassDTO transactionClassDTO = transactionClassMapper.toDto(transactionClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionClassMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionClass in the database
        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactionClass() throws Exception {
        int databaseSizeBeforeUpdate = transactionClassRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        transactionClass.setId(count.incrementAndGet());

        // Create the TransactionClass
        TransactionClassDTO transactionClassDTO = transactionClassMapper.toDto(transactionClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionClassMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionClassDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionClass in the database
        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTransactionClass() throws Exception {
        // Initialize the database
        transactionClassRepository.saveAndFlush(transactionClass);
        transactionClassRepository.save(transactionClass);
        transactionClassSearchRepository.save(transactionClass);

        int databaseSizeBeforeDelete = transactionClassRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transactionClass
        restTransactionClassMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionClass.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransactionClass> transactionClassList = transactionClassRepository.findAll();
        assertThat(transactionClassList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTransactionClass() throws Exception {
        // Initialize the database
        transactionClass = transactionClassRepository.saveAndFlush(transactionClass);
        transactionClassSearchRepository.save(transactionClass);

        // Search the transactionClass
        restTransactionClassMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transactionClass.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionClass.getId().intValue())))
            .andExpect(jsonPath("$.[*].className").value(hasItem(DEFAULT_CLASS_NAME)));
    }
}

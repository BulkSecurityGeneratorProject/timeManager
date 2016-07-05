package ch.management.web.rest;

import ch.management.TimeManagerApp;
import ch.management.domain.Worker;
import ch.management.repository.WorkerRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the WorkerResource REST controller.
 *
 * @see WorkerResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TimeManagerApp.class)
@WebAppConfiguration
@IntegrationTest
public class WorkerResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final Double DEFAULT_CURRENT_MONTH_HOURS = 1D;
    private static final Double UPDATED_CURRENT_MONTH_HOURS = 2D;

    private static final Double DEFAULT_TOTAL_HOURS = 1D;
    private static final Double UPDATED_TOTAL_HOURS = 2D;

    private static final Double DEFAULT_TOTAL_EXPENSES = 1D;
    private static final Double UPDATED_TOTAL_EXPENSES = 2D;

    @Inject
    private WorkerRepository workerRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restWorkerMockMvc;

    private Worker worker;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WorkerResource workerResource = new WorkerResource();
        ReflectionTestUtils.setField(workerResource, "workerRepository", workerRepository);
        this.restWorkerMockMvc = MockMvcBuilders.standaloneSetup(workerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        worker = new Worker();
        worker.setName(DEFAULT_NAME);
        worker.setCurrentMonthHours(DEFAULT_CURRENT_MONTH_HOURS);
        worker.setTotalHours(DEFAULT_TOTAL_HOURS);
        worker.setTotalExpenses(DEFAULT_TOTAL_EXPENSES);
    }

    @Test
    @Transactional
    public void createWorker() throws Exception {
        int databaseSizeBeforeCreate = workerRepository.findAll().size();

        // Create the Worker

        restWorkerMockMvc.perform(post("/api/workers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(worker)))
                .andExpect(status().isCreated());

        // Validate the Worker in the database
        List<Worker> workers = workerRepository.findAll();
        assertThat(workers).hasSize(databaseSizeBeforeCreate + 1);
        Worker testWorker = workers.get(workers.size() - 1);
        assertThat(testWorker.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWorker.getCurrentMonthHours()).isEqualTo(DEFAULT_CURRENT_MONTH_HOURS);
        assertThat(testWorker.getTotalHours()).isEqualTo(DEFAULT_TOTAL_HOURS);
        assertThat(testWorker.getTotalExpenses()).isEqualTo(DEFAULT_TOTAL_EXPENSES);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = workerRepository.findAll().size();
        // set the field null
        worker.setName(null);

        // Create the Worker, which fails.

        restWorkerMockMvc.perform(post("/api/workers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(worker)))
                .andExpect(status().isBadRequest());

        List<Worker> workers = workerRepository.findAll();
        assertThat(workers).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWorkers() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workers
        restWorkerMockMvc.perform(get("/api/workers?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(worker.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].currentMonthHours").value(hasItem(DEFAULT_CURRENT_MONTH_HOURS.doubleValue())))
                .andExpect(jsonPath("$.[*].totalHours").value(hasItem(DEFAULT_TOTAL_HOURS.doubleValue())))
                .andExpect(jsonPath("$.[*].totalExpenses").value(hasItem(DEFAULT_TOTAL_EXPENSES.doubleValue())));
    }

    @Test
    @Transactional
    public void getWorker() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get the worker
        restWorkerMockMvc.perform(get("/api/workers/{id}", worker.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(worker.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.currentMonthHours").value(DEFAULT_CURRENT_MONTH_HOURS.doubleValue()))
            .andExpect(jsonPath("$.totalHours").value(DEFAULT_TOTAL_HOURS.doubleValue()))
            .andExpect(jsonPath("$.totalExpenses").value(DEFAULT_TOTAL_EXPENSES.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingWorker() throws Exception {
        // Get the worker
        restWorkerMockMvc.perform(get("/api/workers/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWorker() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);
        int databaseSizeBeforeUpdate = workerRepository.findAll().size();

        // Update the worker
        Worker updatedWorker = new Worker();
        updatedWorker.setId(worker.getId());
        updatedWorker.setName(UPDATED_NAME);
        updatedWorker.setCurrentMonthHours(UPDATED_CURRENT_MONTH_HOURS);
        updatedWorker.setTotalHours(UPDATED_TOTAL_HOURS);
        updatedWorker.setTotalExpenses(UPDATED_TOTAL_EXPENSES);

        restWorkerMockMvc.perform(put("/api/workers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedWorker)))
                .andExpect(status().isOk());

        // Validate the Worker in the database
        List<Worker> workers = workerRepository.findAll();
        assertThat(workers).hasSize(databaseSizeBeforeUpdate);
        Worker testWorker = workers.get(workers.size() - 1);
        assertThat(testWorker.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWorker.getCurrentMonthHours()).isEqualTo(UPDATED_CURRENT_MONTH_HOURS);
        assertThat(testWorker.getTotalHours()).isEqualTo(UPDATED_TOTAL_HOURS);
        assertThat(testWorker.getTotalExpenses()).isEqualTo(UPDATED_TOTAL_EXPENSES);
    }

    @Test
    @Transactional
    public void deleteWorker() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);
        int databaseSizeBeforeDelete = workerRepository.findAll().size();

        // Get the worker
        restWorkerMockMvc.perform(delete("/api/workers/{id}", worker.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Worker> workers = workerRepository.findAll();
        assertThat(workers).hasSize(databaseSizeBeforeDelete - 1);
    }
}

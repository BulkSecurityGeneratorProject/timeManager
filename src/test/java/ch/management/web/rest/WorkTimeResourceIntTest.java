package ch.management.web.rest;

import ch.management.TimeManagerApp;
import ch.management.domain.WorkTime;
import ch.management.repository.WorkTimeRepository;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the WorkTimeResource REST controller.
 *
 * @see WorkTimeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TimeManagerApp.class)
@WebAppConfiguration
@IntegrationTest
public class WorkTimeResourceIntTest {

    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_HOURS = 1D;
    private static final Double UPDATED_HOURS = 2D;

    @Inject
    private WorkTimeRepository workTimeRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restWorkTimeMockMvc;

    private WorkTime workTime;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WorkTimeResource workTimeResource = new WorkTimeResource();
        ReflectionTestUtils.setField(workTimeResource, "workTimeRepository", workTimeRepository);
        this.restWorkTimeMockMvc = MockMvcBuilders.standaloneSetup(workTimeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        workTime = new WorkTime();
        workTime.setDescription(DEFAULT_DESCRIPTION);
        workTime.setDate(DEFAULT_DATE);
        workTime.setHours(DEFAULT_HOURS);
    }

    @Test
    @Transactional
    public void createWorkTime() throws Exception {
        int databaseSizeBeforeCreate = workTimeRepository.findAll().size();

        // Create the WorkTime

        restWorkTimeMockMvc.perform(post("/api/work-times")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(workTime)))
                .andExpect(status().isCreated());

        // Validate the WorkTime in the database
        List<WorkTime> workTimes = workTimeRepository.findAll();
        assertThat(workTimes).hasSize(databaseSizeBeforeCreate + 1);
        WorkTime testWorkTime = workTimes.get(workTimes.size() - 1);
        assertThat(testWorkTime.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testWorkTime.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testWorkTime.getHours()).isEqualTo(DEFAULT_HOURS);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = workTimeRepository.findAll().size();
        // set the field null
        workTime.setDate(null);

        // Create the WorkTime, which fails.

        restWorkTimeMockMvc.perform(post("/api/work-times")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(workTime)))
                .andExpect(status().isBadRequest());

        List<WorkTime> workTimes = workTimeRepository.findAll();
        assertThat(workTimes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkHoursIsRequired() throws Exception {
        int databaseSizeBeforeTest = workTimeRepository.findAll().size();
        // set the field null
        workTime.setHours(null);

        // Create the WorkTime, which fails.

        restWorkTimeMockMvc.perform(post("/api/work-times")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(workTime)))
                .andExpect(status().isBadRequest());

        List<WorkTime> workTimes = workTimeRepository.findAll();
        assertThat(workTimes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWorkTimes() throws Exception {
        // Initialize the database
        workTimeRepository.saveAndFlush(workTime);

        // Get all the workTimes
        restWorkTimeMockMvc.perform(get("/api/work-times?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(workTime.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].hours").value(hasItem(DEFAULT_HOURS.doubleValue())));
    }

    @Test
    @Transactional
    public void getWorkTime() throws Exception {
        // Initialize the database
        workTimeRepository.saveAndFlush(workTime);

        // Get the workTime
        restWorkTimeMockMvc.perform(get("/api/work-times/{id}", workTime.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(workTime.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.hours").value(DEFAULT_HOURS.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingWorkTime() throws Exception {
        // Get the workTime
        restWorkTimeMockMvc.perform(get("/api/work-times/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWorkTime() throws Exception {
        // Initialize the database
        workTimeRepository.saveAndFlush(workTime);
        int databaseSizeBeforeUpdate = workTimeRepository.findAll().size();

        // Update the workTime
        WorkTime updatedWorkTime = new WorkTime();
        updatedWorkTime.setId(workTime.getId());
        updatedWorkTime.setDescription(UPDATED_DESCRIPTION);
        updatedWorkTime.setDate(UPDATED_DATE);
        updatedWorkTime.setHours(UPDATED_HOURS);

        restWorkTimeMockMvc.perform(put("/api/work-times")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedWorkTime)))
                .andExpect(status().isOk());

        // Validate the WorkTime in the database
        List<WorkTime> workTimes = workTimeRepository.findAll();
        assertThat(workTimes).hasSize(databaseSizeBeforeUpdate);
        WorkTime testWorkTime = workTimes.get(workTimes.size() - 1);
        assertThat(testWorkTime.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testWorkTime.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testWorkTime.getHours()).isEqualTo(UPDATED_HOURS);
    }

    @Test
    @Transactional
    public void deleteWorkTime() throws Exception {
        // Initialize the database
        workTimeRepository.saveAndFlush(workTime);
        int databaseSizeBeforeDelete = workTimeRepository.findAll().size();

        // Get the workTime
        restWorkTimeMockMvc.perform(delete("/api/work-times/{id}", workTime.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<WorkTime> workTimes = workTimeRepository.findAll();
        assertThat(workTimes).hasSize(databaseSizeBeforeDelete - 1);
    }
}

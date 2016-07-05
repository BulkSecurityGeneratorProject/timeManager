package ch.management.web.rest;

import ch.management.TimeManagerApp;
import ch.management.domain.Expense;
import ch.management.repository.ExpenseRepository;

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
 * Test class for the ExpenseResource REST controller.
 *
 * @see ExpenseResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TimeManagerApp.class)
@WebAppConfiguration
@IntegrationTest
public class ExpenseResourceIntTest {

    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_AMOUNT = 1D;
    private static final Double UPDATED_AMOUNT = 2D;

    @Inject
    private ExpenseRepository expenseRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restExpenseMockMvc;

    private Expense expense;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ExpenseResource expenseResource = new ExpenseResource();
        ReflectionTestUtils.setField(expenseResource, "expenseRepository", expenseRepository);
        this.restExpenseMockMvc = MockMvcBuilders.standaloneSetup(expenseResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        expense = new Expense();
        expense.setDescription(DEFAULT_DESCRIPTION);
        expense.setDate(DEFAULT_DATE);
        expense.setAmount(DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    public void createExpense() throws Exception {
        int databaseSizeBeforeCreate = expenseRepository.findAll().size();

        // Create the Expense

        restExpenseMockMvc.perform(post("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(expense)))
                .andExpect(status().isCreated());

        // Validate the Expense in the database
        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeCreate + 1);
        Expense testExpense = expenses.get(expenses.size() - 1);
        assertThat(testExpense.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testExpense.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testExpense.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenseRepository.findAll().size();
        // set the field null
        expense.setDate(null);

        // Create the Expense, which fails.

        restExpenseMockMvc.perform(post("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(expense)))
                .andExpect(status().isBadRequest());

        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenseRepository.findAll().size();
        // set the field null
        expense.setAmount(null);

        // Create the Expense, which fails.

        restExpenseMockMvc.perform(post("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(expense)))
                .andExpect(status().isBadRequest());

        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllExpenses() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenses
        restExpenseMockMvc.perform(get("/api/expenses?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(expense.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())));
    }

    @Test
    @Transactional
    public void getExpense() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get the expense
        restExpenseMockMvc.perform(get("/api/expenses/{id}", expense.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(expense.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingExpense() throws Exception {
        // Get the expense
        restExpenseMockMvc.perform(get("/api/expenses/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateExpense() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);
        int databaseSizeBeforeUpdate = expenseRepository.findAll().size();

        // Update the expense
        Expense updatedExpense = new Expense();
        updatedExpense.setId(expense.getId());
        updatedExpense.setDescription(UPDATED_DESCRIPTION);
        updatedExpense.setDate(UPDATED_DATE);
        updatedExpense.setAmount(UPDATED_AMOUNT);

        restExpenseMockMvc.perform(put("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedExpense)))
                .andExpect(status().isOk());

        // Validate the Expense in the database
        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeUpdate);
        Expense testExpense = expenses.get(expenses.size() - 1);
        assertThat(testExpense.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testExpense.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testExpense.getAmount()).isEqualTo(UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void deleteExpense() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);
        int databaseSizeBeforeDelete = expenseRepository.findAll().size();

        // Get the expense
        restExpenseMockMvc.perform(delete("/api/expenses/{id}", expense.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeDelete - 1);
    }
}

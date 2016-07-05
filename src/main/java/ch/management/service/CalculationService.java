package ch.management.service;

import java.time.LocalDate;
import java.time.Month;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.management.domain.Expense;
import ch.management.domain.WorkTime;
import ch.management.domain.Worker;
import ch.management.repository.ExpenseRepository;
import ch.management.repository.ProjectRepository;
import ch.management.repository.WorkerRepository;

@Service
@Transactional
public class CalculationService {

    private final Logger log = LoggerFactory.getLogger(CalculationService.class);

    @Inject
    private ProjectRepository projectRepository;
    
    @Inject
    private WorkerRepository workerRepository;
    
    @Inject
    private ExpenseRepository expenseRepository;
    
	public void calculateProjectWorkHours() {
		projectRepository.findAll().stream().forEach(project -> {
			double totalHours = 0;
			double monthHours = 0;
			Month currentMonth = LocalDate.now().getMonth();
			for (WorkTime work : project.getWorkTimes()){
				totalHours += work.getHours();
				if (work.getDate().getMonth() == currentMonth){
					monthHours += work.getHours();
				}
			}
			project.setTotalHours(totalHours);
			project.setCurrentMonthHours(monthHours);
        });
	}

	public void calculateWorkerWorkHours() {
		workerRepository.findAll().stream().forEach(worker -> {
			double totalHours = 0;
			double monthHours = 0;
			Month currentMonth = LocalDate.now().getMonth();
			for (WorkTime work : worker.getWorkTimes()){
				totalHours += work.getHours();
				if (work.getDate().getMonth() == currentMonth){
					monthHours += work.getHours();
				}
			}
			worker.setTotalHours(totalHours);
			worker.setCurrentMonthHours(monthHours);
        });
	}

	public void addExpense(Expense expense) {
		Worker worker = workerRepository.findOne(expense.getWorker().getId());
		double totalExpenses = (worker.getTotalExpenses() == null) ? 0 : worker.getTotalExpenses();
		worker.setTotalExpenses(totalExpenses + expense.getAmount());
	}

	public void removeExpense(Long id) {
		Expense expense = expenseRepository.findOne(id);
		Worker worker = workerRepository.findOne(expense.getWorker().getId());
		worker.setTotalExpenses(worker.getTotalExpenses() - expense.getAmount());
	}

	public void updateExpense(Expense expense) {
		Worker worker = workerRepository.findOne(expense.getWorker().getId());
		worker.setTotalExpenses(worker.getTotalExpenses() - expenseRepository.findOne(expense.getId()).getAmount() + expense.getAmount());
	}

}

package ch.management.repository;

import ch.management.domain.Expense;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Expense entity.
 */
@SuppressWarnings("unused")
public interface ExpenseRepository extends JpaRepository<Expense,Long> {

}

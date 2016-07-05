package ch.management.repository;

import ch.management.domain.WorkTime;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the WorkTime entity.
 */
@SuppressWarnings("unused")
public interface WorkTimeRepository extends JpaRepository<WorkTime,Long> {

}

package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.DailyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyTaskRepository extends JpaRepository<DailyTask, Long> {

    List<DailyTask> findByTaskDateOrderByPriorityDescCreatedAtAsc(LocalDate taskDate);

    List<DailyTask> findByTaskDateAndStatus(LocalDate taskDate, String status);
}

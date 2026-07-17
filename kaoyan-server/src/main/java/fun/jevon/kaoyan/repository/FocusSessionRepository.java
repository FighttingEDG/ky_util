package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.FocusSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {

    List<FocusSession> findByStartTimeBetweenOrderByStartTimeDesc(LocalDateTime start, LocalDateTime end);

    @Query("SELECT fs.subject.name as subjectName, COALESCE(SUM(fs.durationMinutes), 0) as totalMinutes " +
           "FROM FocusSession fs " +
           "WHERE fs.startTime BETWEEN :start AND :end " +
           "GROUP BY fs.subject.name")
    List<Map<String, Object>> groupBySubject(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(fs.durationMinutes), 0) FROM FocusSession fs " +
           "WHERE fs.startTime BETWEEN :start AND :end")
    Integer sumDurationBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

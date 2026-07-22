package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.WordReviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WordReviewRecordRepository extends JpaRepository<WordReviewRecord, Long> {

    @Query("SELECT r.reviewDate, COUNT(r), SUM(CASE WHEN r.result IN ('good','easy') THEN 1 ELSE 0 END) " +
           "FROM WordReviewRecord r WHERE r.reviewDate >= :startDate GROUP BY r.reviewDate ORDER BY r.reviewDate")
    List<Object[]> countByDateSince(@Param("startDate") LocalDate startDate);
}

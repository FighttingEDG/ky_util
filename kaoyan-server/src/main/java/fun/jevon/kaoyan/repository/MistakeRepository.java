package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.Mistake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MistakeRepository extends JpaRepository<Mistake, Long> {

    @Query("SELECT m FROM Mistake m WHERE (:subjectId IS NULL OR m.subject.id = :subjectId) " +
           "AND (:wrongReason IS NULL OR m.wrongReason = :wrongReason) " +
           "AND (:tag IS NULL OR m.tags LIKE %:tag%) " +
           "AND (:keyword IS NULL OR m.question LIKE %:keyword% OR m.analysis LIKE %:keyword%)")
    Page<Mistake> search(@Param("subjectId") Long subjectId,
                         @Param("wrongReason") String wrongReason,
                         @Param("tag") String tag,
                         @Param("keyword") String keyword,
                         Pageable pageable);

    List<Mistake> findByNextReviewDateLessThanEqualOrderByNextReviewDateAsc(LocalDate date);

    long countByNextReviewDateLessThanEqual(LocalDate date);

    long countByStage(Integer stage);

    @Query(value = "SELECT tag, COUNT(*) as cnt FROM (" +
                   "SELECT TRIM(value) as tag FROM mistake CROSS JOIN UNNEST(SPLIT(tags, ',')) AS t(value)" +
                   ") WHERE tag <> '' GROUP BY tag ORDER BY cnt DESC LIMIT :limit",
           nativeQuery = true)
    List<Object[]> findWeakPoints(@Param("limit") int limit);
}

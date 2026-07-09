package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("SELECT c FROM Card c WHERE (:subjectId IS NULL OR c.subject.id = :subjectId) " +
           "AND (:tag IS NULL OR c.tags LIKE %:tag%) " +
           "AND (:keyword IS NULL OR c.front LIKE %:keyword% OR c.back LIKE %:keyword%)")
    Page<Card> search(@Param("subjectId") Long subjectId,
                      @Param("tag") String tag,
                      @Param("keyword") String keyword,
                      Pageable pageable);

    List<Card> findByNextReviewDateLessThanEqualOrderByNextReviewDateAsc(LocalDate date);

    long countByNextReviewDateLessThanEqual(LocalDate date);

    long countByStage(Integer stage);
}

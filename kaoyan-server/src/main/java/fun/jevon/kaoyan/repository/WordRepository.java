package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    Optional<Word> findByWord(String word);

    @Query("SELECT w FROM Word w WHERE (:wordType IS NULL OR w.wordType = :wordType) " +
           "AND (:keyword IS NULL OR w.word LIKE %:keyword% OR w.meaning LIKE %:keyword%) " +
           "AND (:stage IS NULL OR w.stage = :stage)")
    Page<Word> search(@Param("wordType") String wordType,
                      @Param("keyword") String keyword,
                      @Param("stage") Integer stage,
                      Pageable pageable);

    List<Word> findByNextReviewDateLessThanEqualOrderByStageAscNextReviewDateAsc(LocalDate date);

    long countByNextReviewDateLessThanEqual(LocalDate date);

    long countByStage(Integer stage);

    long countByWordType(String wordType);

    @Query("SELECT w.stage, COUNT(w) FROM Word w GROUP BY w.stage")
    List<Object[]> countByStageGroup();
}

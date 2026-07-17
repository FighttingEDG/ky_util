package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.KnowledgeNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KnowledgeNodeRepository extends JpaRepository<KnowledgeNode, Long> {

    List<KnowledgeNode> findBySubjectIdOrderByLevelAscCodeAsc(Long subjectId);

    List<KnowledgeNode> findByParentIdOrderByCodeAsc(Long parentId);

    List<KnowledgeNode> findByNextReviewDateLessThanEqualOrderByNextReviewDateAsc(LocalDate date);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.knowledgeNodeId = :nodeId")
    Long countCardsByNodeId(@Param("nodeId") Long nodeId);

    @Query("SELECT COUNT(m) FROM Mistake m WHERE m.knowledgeNodeId = :nodeId")
    Long countMistakesByNodeId(@Param("nodeId") Long nodeId);
}

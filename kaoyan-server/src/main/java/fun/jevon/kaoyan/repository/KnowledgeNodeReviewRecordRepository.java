package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.KnowledgeNodeReviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeNodeReviewRecordRepository extends JpaRepository<KnowledgeNodeReviewRecord, Long> {

    @Query("SELECT r.result as result, COUNT(r) as cnt FROM KnowledgeNodeReviewRecord r WHERE r.node.id = :nodeId GROUP BY r.result")
    List<Object[]> countByResult(@Param("nodeId") Long nodeId);
}

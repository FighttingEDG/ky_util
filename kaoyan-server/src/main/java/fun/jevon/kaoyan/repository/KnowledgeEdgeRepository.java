package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.KnowledgeEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeEdgeRepository extends JpaRepository<KnowledgeEdge, Long> {

    List<KnowledgeEdge> findBySourceId(Long sourceId);

    List<KnowledgeEdge> findByTargetId(Long targetId);
}

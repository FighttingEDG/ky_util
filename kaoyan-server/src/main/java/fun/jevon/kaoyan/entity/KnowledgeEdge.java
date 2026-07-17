package fun.jevon.kaoyan.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "knowledge_edge")
@Data
public class KnowledgeEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "relation_type", length = 32)
    private String relationType;
}

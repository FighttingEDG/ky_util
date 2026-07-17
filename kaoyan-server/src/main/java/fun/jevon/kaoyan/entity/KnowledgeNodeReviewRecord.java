package fun.jevon.kaoyan.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "knowledge_node_review_record")
public class KnowledgeNodeReviewRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "node_id", nullable = false)
    private KnowledgeNode node;

    @Column(name = "result", nullable = false, length = 16)
    private String result;

    @Column(name = "review_date", nullable = false)
    private LocalDate reviewDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

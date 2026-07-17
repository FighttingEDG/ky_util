package fun.jevon.kaoyan.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "front", nullable = false, length = 2000)
    private String front;

    @Column(name = "back", nullable = false, length = 4000)
    private String back;

    @Column(name = "tags", length = 255)
    private String tags;

    @Column(name = "difficulty")
    private Integer difficulty = 3;

    @Column(name = "stage")
    private Integer stage = 0;

    @Column(name = "next_review_date", nullable = false)
    private LocalDate nextReviewDate;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "knowledge_node_id")
    private Long knowledgeNodeId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

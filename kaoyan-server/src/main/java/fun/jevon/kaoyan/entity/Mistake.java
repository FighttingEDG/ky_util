package fun.jevon.kaoyan.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mistake")
public class Mistake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "question", nullable = false, length = 4000)
    private String question;

    @Column(name = "correct_answer", nullable = false, length = 4000)
    private String correctAnswer;

    @Column(name = "wrong_answer", length = 4000)
    private String wrongAnswer;

    @Column(name = "analysis", length = 4000)
    private String analysis;

    @Column(name = "wrong_reason", length = 64)
    private String wrongReason;

    @Column(name = "tags", length = 255)
    private String tags;

    @Column(name = "difficulty")
    private Integer difficulty = 3;

    @Column(name = "source", length = 128)
    private String source;

    @Column(name = "stage")
    private Integer stage = 0;

    @Column(name = "next_review_date", nullable = false)
    private LocalDate nextReviewDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

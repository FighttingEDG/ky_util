package fun.jevon.kaoyan.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "daily_task")
public class DailyTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_date", nullable = false)
    private LocalDate taskDate;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", length = 2000)
    private String content;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes = 25;

    @Column(name = "actual_minutes")
    private Integer actualMinutes = 0;

    @Column(name = "priority")
    private Integer priority = 2;

    @Column(name = "status", length = 16)
    private String status = "todo";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

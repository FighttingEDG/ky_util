package fun.jevon.kaoyan.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "word_stats")
public class WordStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_xp")
    private Integer totalXp = 0;

    @Column(name = "streak_days")
    private Integer streakDays = 0;

    @Column(name = "last_review_date")
    private LocalDate lastReviewDate;
}

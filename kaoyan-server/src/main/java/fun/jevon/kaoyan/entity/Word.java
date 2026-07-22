package fun.jevon.kaoyan.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "word")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word", nullable = false, unique = true, length = 128)
    private String word;

    @Column(name = "phonetic", length = 128)
    private String phonetic;

    @Column(name = "word_class", length = 64)
    private String wordClass;

    @Column(name = "meaning", length = 2000)
    private String meaning;

    @Column(name = "word_type", length = 16)
    private String wordType;

    @Column(name = "root_memo", length = 2000)
    private String rootMemo;

    @Column(name = "exam_meaning", length = 4000)
    private String examMeaning;

    @Column(name = "extensions", length = 4000)
    private String extensions;

    @Column(name = "exam_context", length = 4000)
    private String examContext;

    @Column(name = "synonyms", length = 255)
    private String synonyms;

    @Column(name = "antonyms", length = 255)
    private String antonyms;

    @Column(name = "derivatives", length = 2000)
    private String derivatives;

    @Column(name = "stage")
    private Integer stage = 0;

    @Column(name = "next_review_date", nullable = false)
    private LocalDate nextReviewDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

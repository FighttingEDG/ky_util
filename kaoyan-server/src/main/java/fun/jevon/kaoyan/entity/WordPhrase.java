package fun.jevon.kaoyan.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "word_phrase")
@Data
public class WordPhrase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "phrase_text", length = 255)
    private String phraseText;
}

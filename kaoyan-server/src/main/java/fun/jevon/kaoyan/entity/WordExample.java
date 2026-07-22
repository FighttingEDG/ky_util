package fun.jevon.kaoyan.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "word_example")
@Data
public class WordExample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "example_text", length = 1000)
    private String exampleText;
}

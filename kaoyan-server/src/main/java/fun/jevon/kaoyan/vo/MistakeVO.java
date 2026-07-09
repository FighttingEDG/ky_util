package fun.jevon.kaoyan.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MistakeVO {

    private Long id;
    private Long subjectId;
    private String subjectName;
    private String question;
    private String correctAnswer;
    private String wrongAnswer;
    private String analysis;
    private String wrongReason;
    private String tags;
    private Integer difficulty;
    private String source;
    private Integer stage;
    private LocalDate nextReviewDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package fun.jevon.kaoyan.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CardVO {

    private Long id;
    private Long subjectId;
    private String subjectName;
    private String front;
    private String back;
    private String tags;
    private Integer difficulty;
    private Integer stage;
    private LocalDate nextReviewDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

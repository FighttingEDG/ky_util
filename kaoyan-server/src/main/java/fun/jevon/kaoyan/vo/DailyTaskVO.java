package fun.jevon.kaoyan.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyTaskVO {

    private Long id;
    private LocalDate taskDate;
    private Long subjectId;
    private String subjectName;
    private String title;
    private String content;
    private Integer estimatedMinutes;
    private Integer actualMinutes;
    private Integer priority;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

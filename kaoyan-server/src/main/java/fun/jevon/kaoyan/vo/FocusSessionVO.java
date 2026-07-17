package fun.jevon.kaoyan.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FocusSessionVO {

    private Long id;
    private Long taskId;
    private Long subjectId;
    private String subjectName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private String note;
    private LocalDateTime createdAt;
}

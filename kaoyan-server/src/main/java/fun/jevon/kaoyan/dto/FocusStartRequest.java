package fun.jevon.kaoyan.dto;

import lombok.Data;

@Data
public class FocusStartRequest {

    private Long taskId;
    private Long subjectId;
    private String note;
}

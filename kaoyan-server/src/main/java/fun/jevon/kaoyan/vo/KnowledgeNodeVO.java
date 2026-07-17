package fun.jevon.kaoyan.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class KnowledgeNodeVO {

    private Long id;
    private Long subjectId;
    private String subjectName;
    private String code;
    private String name;
    private String description;
    private Long parentId;
    private Integer level;
    private Integer weight;
    private Integer stage;
    private LocalDate nextReviewDate;
    private Integer masteryRate;
    private Long cardCount;
    private Long mistakeCount;
    private String imageUrl;
    private Integer score;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<KnowledgeNodeVO> children;
}

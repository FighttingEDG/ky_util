package fun.jevon.kaoyan.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class KnowledgeGraphVO {

    private List<Node> nodes;
    private List<Edge> edges;

    @Data
    public static class Node {
        private Long id;
        private String name;
        private String code;
        private String subjectName;
        private Integer level;
        private Integer masteryRate;
        private Integer stage;
    }

    @Data
    public static class Edge {
        private Long source;
        private Long target;
        private String relationType;
    }
}

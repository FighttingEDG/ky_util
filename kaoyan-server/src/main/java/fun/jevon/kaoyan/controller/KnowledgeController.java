package fun.jevon.kaoyan.controller;

import fun.jevon.kaoyan.dto.ApiResponse;
import fun.jevon.kaoyan.dto.ReviewRequest;
import fun.jevon.kaoyan.service.KnowledgeService;
import fun.jevon.kaoyan.vo.KnowledgeGraphVO;
import fun.jevon.kaoyan.vo.KnowledgeNodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @GetMapping("/nodes")
    public ApiResponse<List<KnowledgeNodeVO>> listNodes(@RequestParam(required = false) Long subjectId) {
        return ApiResponse.success(knowledgeService.listNodes(subjectId));
    }

    @GetMapping("/nodes/{id}")
    public ApiResponse<KnowledgeNodeVO> getNode(@PathVariable Long id) {
        return ApiResponse.success(knowledgeService.getNode(id));
    }

    @PostMapping("/nodes")
    public ApiResponse<KnowledgeNodeVO> createNode(@RequestBody KnowledgeNodeVO vo) {
        return ApiResponse.success(knowledgeService.createNode(vo));
    }

    @PutMapping("/nodes/{id}")
    public ApiResponse<KnowledgeNodeVO> updateNode(@PathVariable Long id, @RequestBody KnowledgeNodeVO vo) {
        return ApiResponse.success(knowledgeService.updateNode(id, vo));
    }

    @DeleteMapping("/nodes/{id}")
    public ApiResponse<Void> deleteNode(@PathVariable Long id) {
        knowledgeService.deleteNode(id);
        return ApiResponse.success();
    }

    @GetMapping("/graph")
    public ApiResponse<KnowledgeGraphVO> getGraph() {
        return ApiResponse.success(knowledgeService.getGraph());
    }

    @GetMapping("/nodes/today")
    public ApiResponse<List<KnowledgeNodeVO>> getTodayNodes() {
        return ApiResponse.success(knowledgeService.getTodayNodes());
    }

    @PostMapping("/nodes/{id}/review")
    public ApiResponse<KnowledgeNodeVO> reviewNode(@PathVariable Long id, @RequestBody ReviewRequest request) {
        return ApiResponse.success(knowledgeService.reviewNode(id, request));
    }

    @GetMapping("/recommend")
    public ApiResponse<List<KnowledgeNodeVO>> recommendNodes() {
        return ApiResponse.success(knowledgeService.recommendNodes());
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.success(knowledgeService.getStats());
    }
}

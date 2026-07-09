package fun.jevon.kaoyan.controller;

import fun.jevon.kaoyan.dto.ApiResponse;
import fun.jevon.kaoyan.dto.ReviewRequest;
import fun.jevon.kaoyan.service.MistakeService;
import fun.jevon.kaoyan.vo.MistakeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MistakeController {

    private final MistakeService mistakeService;

    @GetMapping("/mistakes")
    public ApiResponse<Page<MistakeVO>> listMistakes(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String wrongReason,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success(mistakeService.listMistakes(subjectId, wrongReason, tag, keyword, pageable));
    }

    @GetMapping("/mistakes/{id}")
    public ApiResponse<MistakeVO> getMistake(@PathVariable Long id) {
        return ApiResponse.success(mistakeService.getMistake(id));
    }

    @PostMapping("/mistakes")
    public ApiResponse<MistakeVO> createMistake(@RequestBody MistakeVO vo) {
        return ApiResponse.success(mistakeService.createMistake(vo));
    }

    @PutMapping("/mistakes/{id}")
    public ApiResponse<MistakeVO> updateMistake(@PathVariable Long id, @RequestBody MistakeVO vo) {
        return ApiResponse.success(mistakeService.updateMistake(id, vo));
    }

    @DeleteMapping("/mistakes/{id}")
    public ApiResponse<Void> deleteMistake(@PathVariable Long id) {
        mistakeService.deleteMistake(id);
        return ApiResponse.success();
    }

    @GetMapping("/mistakes/today")
    public ApiResponse<List<MistakeVO>> getTodayMistakes() {
        return ApiResponse.success(mistakeService.getTodayMistakes());
    }

    @PostMapping("/mistakes/{id}/review")
    public ApiResponse<MistakeVO> reviewMistake(@PathVariable Long id, @RequestBody ReviewRequest request) {
        return ApiResponse.success(mistakeService.reviewMistake(id, request));
    }

    @GetMapping("/mistakes/stats")
    public ApiResponse<Map<String, Long>> getStats() {
        return ApiResponse.success(mistakeService.getStats());
    }

    @GetMapping("/mistakes/weak-points")
    public ApiResponse<List<Map<String, Object>>> getWeakPoints(
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(mistakeService.getWeakPoints(limit));
    }
}

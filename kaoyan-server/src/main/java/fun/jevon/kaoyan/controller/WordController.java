package fun.jevon.kaoyan.controller;

import fun.jevon.kaoyan.dto.ApiResponse;
import fun.jevon.kaoyan.dto.ReviewRequest;
import fun.jevon.kaoyan.service.WordService;
import fun.jevon.kaoyan.vo.WordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @GetMapping
    public ApiResponse<Page<WordVO>> listWords(
            @RequestParam(required = false) String wordType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer stage,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("word"));
        return ApiResponse.success(wordService.listWords(wordType, keyword, stage, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<WordVO> getWord(@PathVariable Long id) {
        return ApiResponse.success(wordService.getWord(id));
    }

    @GetMapping("/study")
    public ApiResponse<List<WordVO>> getStudyQueue() {
        return ApiResponse.success(wordService.getStudyQueue());
    }

    @PostMapping("/{id}/review")
    public ApiResponse<Map<String, Object>> reviewWord(@PathVariable Long id, @RequestBody ReviewRequest request) {
        return ApiResponse.success(wordService.reviewWord(id, request));
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.success(wordService.getStats());
    }

    @GetMapping("/weekly")
    public ApiResponse<Map<String, Object>> getWeeklyStats() {
        return ApiResponse.success(wordService.getWeeklyStats());
    }

    @GetMapping("/import-status")
    public ApiResponse<Map<String, Object>> getImportStatus() {
        return ApiResponse.success(wordService.getImportStatus());
    }

    @PostMapping("/reimport")
    public ApiResponse<Void> reimport() throws Exception {
        wordService.clearAll();
        wordService.doImport();
        return ApiResponse.success();
    }
}

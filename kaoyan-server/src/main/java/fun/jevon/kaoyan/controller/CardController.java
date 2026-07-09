package fun.jevon.kaoyan.controller;

import fun.jevon.kaoyan.dto.ApiResponse;
import fun.jevon.kaoyan.dto.ReviewRequest;
import fun.jevon.kaoyan.service.CardService;
import fun.jevon.kaoyan.service.SubjectService;
import fun.jevon.kaoyan.vo.CardVO;
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
public class CardController {

    private final CardService cardService;
    private final SubjectService subjectService;

    @GetMapping("/subjects")
    public ApiResponse<List> listSubjects() {
        return ApiResponse.success(subjectService.listAll());
    }

    @GetMapping("/cards")
    public ApiResponse<Page<CardVO>> listCards(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success(cardService.listCards(subjectId, tag, keyword, pageable));
    }

    @GetMapping("/cards/{id}")
    public ApiResponse<CardVO> getCard(@PathVariable Long id) {
        return ApiResponse.success(cardService.getCard(id));
    }

    @PostMapping("/cards")
    public ApiResponse<CardVO> createCard(@RequestBody CardVO vo) {
        return ApiResponse.success(cardService.createCard(vo));
    }

    @PutMapping("/cards/{id}")
    public ApiResponse<CardVO> updateCard(@PathVariable Long id, @RequestBody CardVO vo) {
        return ApiResponse.success(cardService.updateCard(id, vo));
    }

    @DeleteMapping("/cards/{id}")
    public ApiResponse<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ApiResponse.success();
    }

    @GetMapping("/cards/today")
    public ApiResponse<List<CardVO>> getTodayCards() {
        return ApiResponse.success(cardService.getTodayCards());
    }

    @PostMapping("/cards/{id}/review")
    public ApiResponse<CardVO> reviewCard(@PathVariable Long id, @RequestBody ReviewRequest request) {
        return ApiResponse.success(cardService.reviewCard(id, request));
    }

    @GetMapping("/cards/stats")
    public ApiResponse<Map<String, Long>> getStats() {
        return ApiResponse.success(cardService.getStats());
    }
}

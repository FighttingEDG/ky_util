package fun.jevon.kaoyan.controller;

import fun.jevon.kaoyan.dto.ApiResponse;
import fun.jevon.kaoyan.dto.FocusStartRequest;
import fun.jevon.kaoyan.service.FocusService;
import fun.jevon.kaoyan.vo.FocusSessionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FocusController {

    private final FocusService focusService;

    @PostMapping("/focus/start")
    public ApiResponse<FocusSessionVO> startFocus(@RequestBody FocusStartRequest request) {
        return ApiResponse.success(focusService.startFocus(request));
    }

    @PostMapping("/focus/{id}/stop")
    public ApiResponse<FocusSessionVO> stopFocus(@PathVariable Long id) {
        return ApiResponse.success(focusService.stopFocus(id));
    }

    @GetMapping("/focus/sessions")
    public ApiResponse<List<FocusSessionVO>> listSessions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long subjectId) {
        if (date == null) {
            date = LocalDate.now();
        }
        return ApiResponse.success(focusService.listSessions(date, subjectId));
    }

    @GetMapping("/focus/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.success(focusService.getStats());
    }
}

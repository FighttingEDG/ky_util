package fun.jevon.kaoyan.controller;

import fun.jevon.kaoyan.dto.ApiResponse;
import fun.jevon.kaoyan.dto.FocusStartRequest;
import fun.jevon.kaoyan.service.DailyTaskService;
import fun.jevon.kaoyan.vo.DailyTaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DailyTaskController {

    private final DailyTaskService dailyTaskService;

    @GetMapping("/tasks")
    public ApiResponse<List<DailyTaskVO>> listTasks(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String status) {
        if (date == null) {
            date = LocalDate.now();
        }
        return ApiResponse.success(dailyTaskService.listTasks(date, subjectId, status));
    }

    @GetMapping("/tasks/{id}")
    public ApiResponse<DailyTaskVO> getTask(@PathVariable Long id) {
        return ApiResponse.success(dailyTaskService.getTask(id));
    }

    @PostMapping("/tasks")
    public ApiResponse<DailyTaskVO> createTask(@RequestBody DailyTaskVO vo) {
        return ApiResponse.success(dailyTaskService.createTask(vo));
    }

    @PutMapping("/tasks/{id}")
    public ApiResponse<DailyTaskVO> updateTask(@PathVariable Long id, @RequestBody DailyTaskVO vo) {
        return ApiResponse.success(dailyTaskService.updateTask(id, vo));
    }

    @DeleteMapping("/tasks/{id}")
    public ApiResponse<Void> deleteTask(@PathVariable Long id) {
        dailyTaskService.deleteTask(id);
        return ApiResponse.success();
    }

    @PutMapping("/tasks/{id}/status")
    public ApiResponse<DailyTaskVO> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ApiResponse.success(dailyTaskService.updateStatus(id, status));
    }

    @GetMapping("/tasks/stats")
    public ApiResponse<Map<String, Object>> getDayStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return ApiResponse.success(dailyTaskService.getDayStats(date));
    }
}

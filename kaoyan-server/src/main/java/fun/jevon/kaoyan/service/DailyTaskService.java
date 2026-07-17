package fun.jevon.kaoyan.service;

import fun.jevon.kaoyan.dto.FocusStartRequest;
import fun.jevon.kaoyan.entity.DailyTask;
import fun.jevon.kaoyan.entity.FocusSession;
import fun.jevon.kaoyan.entity.Subject;
import fun.jevon.kaoyan.repository.DailyTaskRepository;
import fun.jevon.kaoyan.repository.FocusSessionRepository;
import fun.jevon.kaoyan.repository.SubjectRepository;
import fun.jevon.kaoyan.vo.DailyTaskVO;
import fun.jevon.kaoyan.vo.FocusSessionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyTaskService {

    private final DailyTaskRepository dailyTaskRepository;
    private final FocusSessionRepository focusSessionRepository;
    private final SubjectRepository subjectRepository;

    public List<DailyTaskVO> listTasks(LocalDate date, Long subjectId, String status) {
        List<DailyTask> tasks;
        if (subjectId != null && status != null) {
            tasks = dailyTaskRepository.findByTaskDateAndStatus(date, status).stream()
                    .filter(t -> t.getSubject() != null && t.getSubject().getId().equals(subjectId))
                    .collect(Collectors.toList());
        } else if (subjectId != null) {
            tasks = dailyTaskRepository.findByTaskDateOrderByPriorityDescCreatedAtAsc(date).stream()
                    .filter(t -> t.getSubject() != null && t.getSubject().getId().equals(subjectId))
                    .collect(Collectors.toList());
        } else if (status != null) {
            tasks = dailyTaskRepository.findByTaskDateAndStatus(date, status);
        } else {
            tasks = dailyTaskRepository.findByTaskDateOrderByPriorityDescCreatedAtAsc(date);
        }
        return tasks.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    public DailyTaskVO getTask(Long id) {
        return convertToVO(findById(id));
    }

    @Transactional
    public DailyTaskVO createTask(DailyTaskVO vo) {
        DailyTask task = new DailyTask();
        BeanUtils.copyProperties(vo, task);
        if (task.getTaskDate() == null) {
            task.setTaskDate(LocalDate.now());
        }
        if (vo.getSubjectId() != null) {
            task.setSubject(getSubject(vo.getSubjectId()));
        }
        return convertToVO(dailyTaskRepository.save(task));
    }

    @Transactional
    public DailyTaskVO updateTask(Long id, DailyTaskVO vo) {
        DailyTask task = findById(id);
        task.setTitle(vo.getTitle());
        task.setContent(vo.getContent());
        task.setEstimatedMinutes(vo.getEstimatedMinutes());
        task.setPriority(vo.getPriority());
        task.setStatus(vo.getStatus());
        if (vo.getSubjectId() != null) {
            task.setSubject(getSubject(vo.getSubjectId()));
        } else {
            task.setSubject(null);
        }
        return convertToVO(dailyTaskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        dailyTaskRepository.deleteById(id);
    }

    @Transactional
    public DailyTaskVO updateStatus(Long id, String status) {
        DailyTask task = findById(id);
        task.setStatus(status);
        return convertToVO(dailyTaskRepository.save(task));
    }

    public Map<String, Object> getDayStats(LocalDate date) {
        List<DailyTask> tasks = dailyTaskRepository.findByTaskDateOrderByPriorityDescCreatedAtAsc(date);
        int total = tasks.size();
        int done = (int) tasks.stream().filter(t -> "done".equals(t.getStatus())).count();
        int totalEstimated = tasks.stream().mapToInt(t -> t.getEstimatedMinutes() == null ? 0 : t.getEstimatedMinutes()).sum();
        int totalActual = tasks.stream().mapToInt(t -> t.getActualMinutes() == null ? 0 : t.getActualMinutes()).sum();
        return Map.of(
                "total", total,
                "done", done,
                "totalEstimated", totalEstimated,
                "totalActual", totalActual
        );
    }

    private DailyTask findById(Long id) {
        return dailyTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));
    }

    private Subject getSubject(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));
    }

    private DailyTaskVO convertToVO(DailyTask task) {
        DailyTaskVO vo = new DailyTaskVO();
        BeanUtils.copyProperties(task, vo);
        if (task.getSubject() != null) {
            vo.setSubjectId(task.getSubject().getId());
            vo.setSubjectName(task.getSubject().getName());
        }
        return vo;
    }
}

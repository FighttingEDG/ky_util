package fun.jevon.kaoyan.service;

import fun.jevon.kaoyan.dto.FocusStartRequest;
import fun.jevon.kaoyan.entity.DailyTask;
import fun.jevon.kaoyan.entity.FocusSession;
import fun.jevon.kaoyan.entity.Subject;
import fun.jevon.kaoyan.repository.DailyTaskRepository;
import fun.jevon.kaoyan.repository.FocusSessionRepository;
import fun.jevon.kaoyan.repository.SubjectRepository;
import fun.jevon.kaoyan.vo.FocusSessionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FocusService {

    private final FocusSessionRepository focusSessionRepository;
    private final DailyTaskRepository dailyTaskRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public FocusSessionVO startFocus(FocusStartRequest request) {
        FocusSession session = new FocusSession();
        session.setStartTime(LocalDateTime.now());
        if (request.getTaskId() != null) {
            DailyTask task = dailyTaskRepository.findById(request.getTaskId())
                    .orElseThrow(() -> new RuntimeException("Task not found: " + request.getTaskId()));
            session.setTask(task);
        }
        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found: " + request.getSubjectId()));
            session.setSubject(subject);
        }
        session.setNote(request.getNote());
        return convertToVO(focusSessionRepository.save(session));
    }

    @Transactional
    public FocusSessionVO stopFocus(Long id) {
        FocusSession session = focusSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Focus session not found: " + id));
        session.setEndTime(LocalDateTime.now());
        int minutes = (int) ChronoUnit.MINUTES.between(session.getStartTime(), session.getEndTime());
        session.setDurationMinutes(Math.max(1, minutes));
        focusSessionRepository.save(session);

        if (session.getTask() != null) {
            DailyTask task = session.getTask();
            int actual = task.getActualMinutes() == null ? 0 : task.getActualMinutes();
            task.setActualMinutes(actual + session.getDurationMinutes());
            dailyTaskRepository.save(task);
        }

        return convertToVO(session);
    }

    public List<FocusSessionVO> listSessions(LocalDate date, Long subjectId) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return focusSessionRepository.findByStartTimeBetweenOrderByStartTimeDesc(start, end).stream()
                .filter(fs -> subjectId == null || (fs.getSubject() != null && fs.getSubject().getId().equals(subjectId)))
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);

        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDateTime weekStartTime = weekStart.atStartOfDay();
        LocalDateTime weekEndTime = today.atTime(LocalTime.MAX);

        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDateTime monthStartTime = monthStart.atStartOfDay();

        int todayMinutes = focusSessionRepository.sumDurationBetween(todayStart, todayEnd);
        int weekMinutes = focusSessionRepository.sumDurationBetween(weekStartTime, weekEndTime);
        int monthMinutes = focusSessionRepository.sumDurationBetween(monthStartTime, todayEnd);

        List<Map<String, Object>> todayBySubject = focusSessionRepository.groupBySubject(todayStart, todayEnd);

        Map<String, Integer> last7Days = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            int minutes = focusSessionRepository.sumDurationBetween(d.atStartOfDay(), d.atTime(LocalTime.MAX));
            last7Days.put(d.toString(), minutes);
        }

        return Map.of(
                "today", todayMinutes,
                "week", weekMinutes,
                "month", monthMinutes,
                "todayBySubject", todayBySubject,
                "last7Days", last7Days
        );
    }

    private FocusSessionVO convertToVO(FocusSession session) {
        FocusSessionVO vo = new FocusSessionVO();
        BeanUtils.copyProperties(session, vo);
        if (session.getTask() != null) {
            vo.setTaskId(session.getTask().getId());
        }
        if (session.getSubject() != null) {
            vo.setSubjectId(session.getSubject().getId());
            vo.setSubjectName(session.getSubject().getName());
        }
        return vo;
    }
}

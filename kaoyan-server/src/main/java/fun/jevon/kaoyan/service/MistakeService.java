package fun.jevon.kaoyan.service;

import fun.jevon.kaoyan.dto.ReviewRequest;
import fun.jevon.kaoyan.entity.Mistake;
import fun.jevon.kaoyan.entity.MistakeReviewRecord;
import fun.jevon.kaoyan.entity.Subject;
import fun.jevon.kaoyan.repository.MistakeRepository;
import fun.jevon.kaoyan.repository.MistakeReviewRecordRepository;
import fun.jevon.kaoyan.repository.SubjectRepository;
import fun.jevon.kaoyan.vo.MistakeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MistakeService {

    private final MistakeRepository mistakeRepository;
    private final MistakeReviewRecordRepository reviewRecordRepository;
    private final SubjectRepository subjectRepository;

    private static final int[] INTERVAL_DAYS = {1, 2, 4, 7, 15, 30, 90};

    public Page<MistakeVO> listMistakes(Long subjectId, String wrongReason, String tag, String keyword, Pageable pageable) {
        Page<Mistake> page = mistakeRepository.search(subjectId, wrongReason, tag, keyword, pageable);
        List<MistakeVO> vos = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new PageImpl<>(vos, pageable, page.getTotalElements());
    }

    public MistakeVO getMistake(Long id) {
        return convertToVO(findById(id));
    }

    @Transactional
    public MistakeVO createMistake(MistakeVO vo) {
        Mistake mistake = new Mistake();
        BeanUtils.copyProperties(vo, mistake);
        mistake.setSubject(getSubject(vo.getSubjectId()));
        if (mistake.getNextReviewDate() == null) {
            mistake.setNextReviewDate(LocalDate.now().plusDays(1));
        }
        if (mistake.getStage() == null) {
            mistake.setStage(0);
        }
        return convertToVO(mistakeRepository.save(mistake));
    }

    @Transactional
    public MistakeVO updateMistake(Long id, MistakeVO vo) {
        Mistake mistake = findById(id);
        mistake.setSubject(getSubject(vo.getSubjectId()));
        mistake.setQuestion(vo.getQuestion());
        mistake.setCorrectAnswer(vo.getCorrectAnswer());
        mistake.setWrongAnswer(vo.getWrongAnswer());
        mistake.setAnalysis(vo.getAnalysis());
        mistake.setWrongReason(vo.getWrongReason());
        mistake.setTags(vo.getTags());
        mistake.setDifficulty(vo.getDifficulty());
        mistake.setSource(vo.getSource());
        return convertToVO(mistakeRepository.save(mistake));
    }

    @Transactional
    public void deleteMistake(Long id) {
        mistakeRepository.deleteById(id);
    }

    public List<MistakeVO> getTodayMistakes() {
        return mistakeRepository.findByNextReviewDateLessThanEqualOrderByNextReviewDateAsc(LocalDate.now())
                .stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MistakeVO reviewMistake(Long id, ReviewRequest request) {
        Mistake mistake = findById(id);
        String result = request.getResult();
        int stage = mistake.getStage() == null ? 0 : mistake.getStage();

        switch (result) {
            case "again" -> stage = 0;
            case "hard" -> stage = Math.max(0, stage - 1);
            case "good" -> stage = Math.min(7, stage + 1);
            case "easy" -> stage = Math.min(7, stage + 2);
            default -> throw new IllegalArgumentException("Invalid review result: " + result);
        }

        mistake.setStage(stage);
        if (stage >= 7) {
            mistake.setNextReviewDate(LocalDate.of(9999, 12, 31));
        } else {
            mistake.setNextReviewDate(LocalDate.now().plusDays(INTERVAL_DAYS[stage]));
        }
        mistakeRepository.save(mistake);

        MistakeReviewRecord record = new MistakeReviewRecord();
        record.setMistake(mistake);
        record.setResult(result);
        record.setReviewDate(LocalDate.now());
        reviewRecordRepository.save(record);

        return convertToVO(mistake);
    }

    public Map<String, Long> getStats() {
        LocalDate today = LocalDate.now();
        return Map.of(
                "total", mistakeRepository.count(),
                "today", mistakeRepository.countByNextReviewDateLessThanEqual(today),
                "mastered", mistakeRepository.countByStage(7),
                "learning", mistakeRepository.count() - mistakeRepository.countByStage(7)
        );
    }

    public List<Map<String, Object>> getWeakPoints(int limit) {
        return mistakeRepository.findWeakPoints(limit).stream()
                .map(row -> Map.of("tag", row[0], "count", row[1]))
                .collect(Collectors.toList());
    }

    private Mistake findById(Long id) {
        return mistakeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mistake not found: " + id));
    }

    private Subject getSubject(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));
    }

    private MistakeVO convertToVO(Mistake mistake) {
        MistakeVO vo = new MistakeVO();
        BeanUtils.copyProperties(mistake, vo);
        vo.setSubjectId(mistake.getSubject().getId());
        vo.setSubjectName(mistake.getSubject().getName());
        return vo;
    }
}

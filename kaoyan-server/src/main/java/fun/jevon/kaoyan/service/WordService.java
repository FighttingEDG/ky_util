package fun.jevon.kaoyan.service;

import fun.jevon.kaoyan.dto.ReviewRequest;
import fun.jevon.kaoyan.entity.Word;
import fun.jevon.kaoyan.entity.WordReviewRecord;
import fun.jevon.kaoyan.entity.WordStats;
import fun.jevon.kaoyan.repository.*;
import fun.jevon.kaoyan.vo.WordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final WordExampleRepository exampleRepository;
    private final WordPhraseRepository phraseRepository;
    private final WordReviewRecordRepository reviewRecordRepository;
    private final WordStatsRepository statsRepository;
    private final WordImportService wordImportService;

    private static final int[] INTERVAL_DAYS = {1, 2, 4, 7, 15, 30, 90};
    private static final Map<String, Integer> XP_MAP = Map.of(
            "again", 5,
            "hard", 10,
            "good", 15,
            "easy", 20
    );

    public Page<WordVO> listWords(String wordType, String keyword, Integer stage, Pageable pageable) {
        Page<Word> page = wordRepository.search(wordType, keyword, stage, pageable);
        List<WordVO> vos = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new PageImpl<>(vos, pageable, page.getTotalElements());
    }

    public WordVO getWord(Long id) {
        Word word = findById(id);
        return convertToVO(word, true);
    }

    public List<WordVO> getStudyQueue() {
        return wordRepository.findByNextReviewDateLessThanEqualOrderByStageAscNextReviewDateAsc(LocalDate.now())
                .stream()
                .map(w -> convertToVO(w, true))
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> reviewWord(Long id, ReviewRequest request) {
        Word word = findById(id);
        String result = request.getResult();
        int stage = word.getStage() == null ? 0 : word.getStage();

        switch (result) {
            case "again" -> stage = 0;
            case "hard" -> stage = Math.max(0, stage - 1);
            case "good" -> stage = Math.min(7, stage + 1);
            case "easy" -> stage = Math.min(7, stage + 2);
            default -> throw new IllegalArgumentException("Invalid review result: " + result);
        }

        word.setStage(stage);
        if (stage >= 7) {
            word.setNextReviewDate(LocalDate.of(9999, 12, 31));
        } else {
            word.setNextReviewDate(LocalDate.now().plusDays(INTERVAL_DAYS[stage]));
        }
        wordRepository.save(word);

        int xp = XP_MAP.getOrDefault(result, 10);
        WordReviewRecord record = new WordReviewRecord();
        record.setWord(word);
        record.setResult(result);
        record.setReviewDate(LocalDate.now());
        record.setXpEarned(xp);
        reviewRecordRepository.save(record);

        WordStats stats = getOrCreateStats();
        stats.setTotalXp(stats.getTotalXp() + xp);
        LocalDate today = LocalDate.now();
        if (stats.getLastReviewDate() == null) {
            stats.setStreakDays(1);
        } else if (stats.getLastReviewDate().equals(today)) {
            // 今天已打卡，streak 不变
        } else if (stats.getLastReviewDate().equals(today.minusDays(1))) {
            stats.setStreakDays(stats.getStreakDays() + 1);
        } else {
            stats.setStreakDays(1);
        }
        stats.setLastReviewDate(today);
        statsRepository.save(stats);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("word", convertToVO(word, true));
        resultMap.put("xp", xp);
        resultMap.put("totalXp", stats.getTotalXp());
        resultMap.put("streakDays", stats.getStreakDays());
        return resultMap;
    }

    @Transactional
    public void clearAll() {
        reviewRecordRepository.deleteAll();
        exampleRepository.deleteAll();
        phraseRepository.deleteAll();
        wordRepository.deleteAll();
        statsRepository.deleteAll();
    }

    public void doImport() throws Exception {
        wordImportService.doImport();
    }

    public Map<String, Object> getStats() {
        LocalDate today = LocalDate.now();
        WordStats stats = getOrCreateStats();
        List<Object[]> stageGroups = wordRepository.countByStageGroup();
        Map<Integer, Long> stageMap = new TreeMap<>();
        for (Object[] row : stageGroups) {
            stageMap.put((Integer) row[0], (Long) row[1]);
        }
        return Map.of(
                "total", wordRepository.count(),
                "mustCount", wordRepository.countByWordType("must"),
                "examCount", wordRepository.countByWordType("exam"),
                "today", wordRepository.countByNextReviewDateLessThanEqual(today),
                "mastered", wordRepository.countByStage(7),
                "totalXp", stats.getTotalXp(),
                "level", stats.getTotalXp() / 100,
                "streakDays", stats.getStreakDays(),
                "stageDistribution", stageMap
        );
    }

    public Map<String, Object> getWeeklyStats() {
        LocalDate weekAgo = LocalDate.now().minusDays(6);
        List<Object[]> rows = reviewRecordRepository.countByDateSince(weekAgo);
        Map<String, Map<String, Object>> daily = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = weekAgo.plusDays(i);
            daily.put(d.toString(), Map.of("count", 0, "correct", 0));
        }
        for (Object[] row : rows) {
            LocalDate date = (LocalDate) row[0];
            long count = (Long) row[1];
            long correct = (Long) row[2];
            daily.put(date.toString(), Map.of("count", count, "correct", correct));
        }
        return Map.of("daily", daily);
    }

    public Map<String, Object> getImportStatus() {
        return Map.of(
                "imported", wordRepository.count() > 0,
                "count", wordRepository.count()
        );
    }

    private WordStats getOrCreateStats() {
        return statsRepository.findAll().stream().findFirst()
                .orElseGet(() -> statsRepository.save(new WordStats()));
    }

    private Word findById(Long id) {
        return wordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Word not found: " + id));
    }

    private WordVO convertToVO(Word word) {
        return convertToVO(word, false);
    }

    private WordVO convertToVO(Word word, boolean withDetail) {
        WordVO vo = new WordVO();
        BeanUtils.copyProperties(word, vo);
        if (withDetail) {
            vo.setExamples(exampleRepository.findByWordId(word.getId()).stream()
                    .map(e -> e.getExampleText()).collect(Collectors.toList()));
            vo.setPhrases(phraseRepository.findByWordId(word.getId()).stream()
                    .map(p -> p.getPhraseText()).collect(Collectors.toList()));
        } else {
            vo.setExamples(List.of());
            vo.setPhrases(List.of());
        }
        return vo;
    }
}

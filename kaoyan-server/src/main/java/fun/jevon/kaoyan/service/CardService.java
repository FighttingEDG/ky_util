package fun.jevon.kaoyan.service;

import fun.jevon.kaoyan.dto.ReviewRequest;
import fun.jevon.kaoyan.entity.Card;
import fun.jevon.kaoyan.entity.ReviewRecord;
import fun.jevon.kaoyan.entity.Subject;
import fun.jevon.kaoyan.repository.CardRepository;
import fun.jevon.kaoyan.repository.ReviewRecordRepository;
import fun.jevon.kaoyan.repository.SubjectRepository;
import fun.jevon.kaoyan.vo.CardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final ReviewRecordRepository reviewRecordRepository;
    private final SubjectRepository subjectRepository;

    private static final int[] INTERVAL_DAYS = {1, 2, 4, 7, 15, 30, 90};

    public Page<CardVO> listCards(Long subjectId, String tag, String keyword, Pageable pageable) {
        Page<Card> page = cardRepository.search(subjectId, tag, keyword, pageable);
        List<CardVO> vos = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new PageImpl<>(vos, pageable, page.getTotalElements());
    }

    public CardVO getCard(Long id) {
        Card card = findById(id);
        return convertToVO(card);
    }

    @Transactional
    public CardVO createCard(CardVO vo) {
        Card card = new Card();
        BeanUtils.copyProperties(vo, card);
        card.setSubject(getSubject(vo.getSubjectId()));
        if (card.getNextReviewDate() == null) {
            card.setNextReviewDate(LocalDate.now().plusDays(1));
        }
        if (card.getStage() == null) {
            card.setStage(0);
        }
        Card saved = cardRepository.save(card);
        return convertToVO(saved);
    }

    @Transactional
    public CardVO updateCard(Long id, CardVO vo) {
        Card card = findById(id);
        card.setSubject(getSubject(vo.getSubjectId()));
        card.setFront(vo.getFront());
        card.setBack(vo.getBack());
        card.setTags(vo.getTags());
        card.setDifficulty(vo.getDifficulty());
        card.setKnowledgeNodeId(vo.getKnowledgeNodeId());
        card.setImageUrl(vo.getImageUrl());
        return convertToVO(cardRepository.save(card));
    }

    @Transactional
    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }

    public List<CardVO> getTodayCards() {
        return cardRepository.findByNextReviewDateLessThanEqualOrderByNextReviewDateAsc(LocalDate.now())
                .stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CardVO reviewCard(Long id, ReviewRequest request) {
        Card card = findById(id);
        String result = request.getResult();
        int stage = card.getStage() == null ? 0 : card.getStage();

        switch (result) {
            case "again" -> stage = 0;
            case "hard" -> stage = Math.max(0, stage - 1);
            case "good" -> stage = Math.min(7, stage + 1);
            case "easy" -> stage = Math.min(7, stage + 2);
            default -> throw new IllegalArgumentException("Invalid review result: " + result);
        }

        card.setStage(stage);
        if (stage >= 7) {
            card.setNextReviewDate(LocalDate.of(9999, 12, 31));
        } else {
            card.setNextReviewDate(LocalDate.now().plusDays(INTERVAL_DAYS[stage]));
        }
        cardRepository.save(card);

        ReviewRecord record = new ReviewRecord();
        record.setCard(card);
        record.setResult(result);
        record.setReviewDate(LocalDate.now());
        reviewRecordRepository.save(record);

        return convertToVO(card);
    }

    public Map<String, Long> getStats() {
        LocalDate today = LocalDate.now();
        return Map.of(
                "total", cardRepository.count(),
                "today", cardRepository.countByNextReviewDateLessThanEqual(today),
                "mastered", cardRepository.countByStage(7),
                "learning", cardRepository.count() - cardRepository.countByStage(7)
        );
    }

    private Card findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found: " + id));
    }

    private Subject getSubject(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));
    }

    private CardVO convertToVO(Card card) {
        CardVO vo = new CardVO();
        BeanUtils.copyProperties(card, vo);
        vo.setSubjectId(card.getSubject().getId());
        vo.setSubjectName(card.getSubject().getName());
        return vo;
    }
}

package fun.jevon.kaoyan.service;

import fun.jevon.kaoyan.dto.ReviewRequest;
import fun.jevon.kaoyan.entity.*;
import fun.jevon.kaoyan.repository.*;
import fun.jevon.kaoyan.vo.KnowledgeGraphVO;
import fun.jevon.kaoyan.vo.KnowledgeNodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeNodeRepository nodeRepository;
    private final KnowledgeEdgeRepository edgeRepository;
    private final KnowledgeNodeReviewRecordRepository reviewRecordRepository;
    private final SubjectRepository subjectRepository;
    private final CardRepository cardRepository;
    private final MistakeRepository mistakeRepository;

    private static final int[] INTERVAL_DAYS = {1, 2, 4, 7, 15, 30, 90};

    public List<KnowledgeNodeVO> listNodes(Long subjectId) {
        List<KnowledgeNode> nodes;
        if (subjectId != null) {
            nodes = nodeRepository.findBySubjectIdOrderByLevelAscCodeAsc(subjectId);
        } else {
            nodes = nodeRepository.findAll();
        }
        return buildTree(nodes);
    }

    private List<KnowledgeNodeVO> buildTree(List<KnowledgeNode> nodes) {
        Map<Long, KnowledgeNodeVO> voMap = nodes.stream()
                .map(this::convertToVO)
                .collect(Collectors.toMap(KnowledgeNodeVO::getId, n -> n));
        List<KnowledgeNodeVO> roots = new ArrayList<>();
        for (KnowledgeNodeVO vo : voMap.values()) {
            if (vo.getParentId() == null) {
                roots.add(vo);
            } else {
                KnowledgeNodeVO parent = voMap.get(vo.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                } else {
                    roots.add(vo);
                }
            }
        }
        return roots;
    }

    public KnowledgeNodeVO getNode(Long id) {
        KnowledgeNode node = findNodeById(id);
        KnowledgeNodeVO vo = convertToVO(node);
        vo.setChildren(buildTree(nodeRepository.findByParentIdOrderByCodeAsc(id)));
        return vo;
    }

    @Transactional
    public KnowledgeNodeVO createNode(KnowledgeNodeVO vo) {
        KnowledgeNode node = new KnowledgeNode();
        BeanUtils.copyProperties(vo, node);
        if (vo.getSubjectId() != null) {
            node.setSubject(getSubject(vo.getSubjectId()));
        }
        if (node.getNextReviewDate() == null) {
            node.setNextReviewDate(LocalDate.now().plusDays(1));
        }
        return convertToVO(nodeRepository.save(node));
    }

    @Transactional
    public KnowledgeNodeVO updateNode(Long id, KnowledgeNodeVO vo) {
        KnowledgeNode node = findNodeById(id);
        node.setName(vo.getName());
        node.setCode(vo.getCode());
        node.setDescription(vo.getDescription());
        node.setParentId(vo.getParentId());
        node.setLevel(vo.getLevel());
        node.setWeight(vo.getWeight());
        node.setImageUrl(vo.getImageUrl());
        if (vo.getSubjectId() != null) {
            node.setSubject(getSubject(vo.getSubjectId()));
        }
        return convertToVO(nodeRepository.save(node));
    }

    @Transactional
    public void deleteNode(Long id) {
        nodeRepository.deleteById(id);
    }

    public KnowledgeGraphVO getGraph() {
        List<KnowledgeNode> nodes = nodeRepository.findAll();
        List<KnowledgeEdge> edges = edgeRepository.findAll();

        KnowledgeGraphVO graph = new KnowledgeGraphVO();
        graph.setNodes(nodes.stream().map(n -> {
            KnowledgeGraphVO.Node node = new KnowledgeGraphVO.Node();
            BeanUtils.copyProperties(n, node);
            node.setSubjectName(n.getSubject() != null ? n.getSubject().getName() : null);
            return node;
        }).collect(Collectors.toList()));
        graph.setEdges(edges.stream().map(e -> {
            KnowledgeGraphVO.Edge edge = new KnowledgeGraphVO.Edge();
            edge.setSource(e.getSourceId());
            edge.setTarget(e.getTargetId());
            edge.setRelationType(e.getRelationType());
            return edge;
        }).collect(Collectors.toList()));
        return graph;
    }

    public List<KnowledgeNodeVO> getTodayNodes() {
        return nodeRepository.findByNextReviewDateLessThanEqualOrderByNextReviewDateAsc(LocalDate.now())
                .stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Transactional
    public KnowledgeNodeVO reviewNode(Long id, ReviewRequest request) {
        KnowledgeNode node = findNodeById(id);
        String result = request.getResult();
        int stage = node.getStage() == null ? 0 : node.getStage();

        switch (result) {
            case "again" -> stage = 0;
            case "hard" -> stage = Math.max(0, stage - 1);
            case "good" -> stage = Math.min(7, stage + 1);
            case "easy" -> stage = Math.min(7, stage + 2);
            default -> throw new IllegalArgumentException("Invalid review result: " + result);
        }

        node.setStage(stage);
        node.setMasteryRate(stageToMastery(stage));
        if (stage >= 7) {
            node.setNextReviewDate(LocalDate.of(9999, 12, 31));
        } else {
            node.setNextReviewDate(LocalDate.now().plusDays(INTERVAL_DAYS[stage]));
        }
        nodeRepository.save(node);

        KnowledgeNodeReviewRecord record = new KnowledgeNodeReviewRecord();
        record.setNode(node);
        record.setResult(result);
        record.setReviewDate(LocalDate.now());
        reviewRecordRepository.save(record);

        return convertToVO(node);
    }

    public List<KnowledgeNodeVO> recommendNodes() {
        List<KnowledgeNode> all = nodeRepository.findAll();
        return all.stream()
                .map(n -> {
                    KnowledgeNodeVO vo = convertToVO(n);
                    vo.setScore(calculateWeakScore(n));
                    return vo;
                })
                .sorted(Comparator.comparingInt(KnowledgeNodeVO::getScore).reversed())
                .limit(20)
                .collect(Collectors.toList());
    }

    private int calculateWeakScore(KnowledgeNode node) {
        int score = 0;
        int mastery = node.getMasteryRate() == null ? 0 : node.getMasteryRate();
        int stage = node.getStage() == null ? 0 : node.getStage();
        Long mistakes = nodeRepository.countMistakesByNodeId(node.getId());

        score += (100 - mastery) * 30 / 100;
        if (stage <= 2) score += 20;
        score += Math.min(mistakes.intValue() * 5, 20);
        if (node.getNextReviewDate() != null && node.getNextReviewDate().isBefore(LocalDate.now())) {
            score += 15;
        }
        return score;
    }

    public Map<String, Object> getStats() {
        List<KnowledgeNode> all = nodeRepository.findAll();
        long total = all.size();
        long mastered = all.stream().filter(n -> n.getStage() != null && n.getStage() >= 7).count();
        long learning = total - mastered;
        long today = all.stream().filter(n -> n.getNextReviewDate() != null && !n.getNextReviewDate().isAfter(LocalDate.now())).count();
        double avgMastery = all.stream().mapToInt(n -> n.getMasteryRate() == null ? 0 : n.getMasteryRate()).average().orElse(0);
        return Map.of(
                "total", total,
                "mastered", mastered,
                "learning", learning,
                "today", today,
                "avgMastery", Math.round(avgMastery)
        );
    }

    private int stageToMastery(int stage) {
        return switch (stage) {
            case 0 -> 0;
            case 1 -> 15;
            case 2 -> 30;
            case 3 -> 50;
            case 4 -> 70;
            case 5 -> 85;
            case 6 -> 95;
            default -> 100;
        };
    }

    private KnowledgeNode findNodeById(Long id) {
        return nodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Knowledge node not found: " + id));
    }

    private Subject getSubject(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));
    }

    private KnowledgeNodeVO convertToVO(KnowledgeNode node) {
        KnowledgeNodeVO vo = new KnowledgeNodeVO();
        BeanUtils.copyProperties(node, vo);
        if (node.getSubject() != null) {
            vo.setSubjectId(node.getSubject().getId());
            vo.setSubjectName(node.getSubject().getName());
        }
        vo.setCardCount(nodeRepository.countCardsByNodeId(node.getId()));
        vo.setMistakeCount(nodeRepository.countMistakesByNodeId(node.getId()));
        if (vo.getChildren() == null) {
            vo.setChildren(new ArrayList<>());
        }
        return vo;
    }
}

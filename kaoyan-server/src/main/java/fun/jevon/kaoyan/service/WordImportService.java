package fun.jevon.kaoyan.service;

import fun.jevon.kaoyan.entity.Word;
import fun.jevon.kaoyan.entity.WordExample;
import fun.jevon.kaoyan.entity.WordPhrase;
import fun.jevon.kaoyan.repository.WordExampleRepository;
import fun.jevon.kaoyan.repository.WordPhraseRepository;
import fun.jevon.kaoyan.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WordImportService implements ApplicationRunner {

    private final WordRepository wordRepository;
    private final WordExampleRepository exampleRepository;
    private final WordPhraseRepository phraseRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (wordRepository.count() > 0) {
            log.info("单词数据已存在（{} 条），跳过导入", wordRepository.count());
            return;
        }
        doImport();
    }

    @Transactional
    public void doImport() throws Exception {
        log.info("开始导入单词数据...");
        Map<String, Word> wordMap = new HashMap<>();
        Map<String, List<String>> examplesMap = new HashMap<>();
        Map<String, List<String>> phrasesMap = new HashMap<>();

        importMustWords(wordMap, examplesMap, phrasesMap);
        importExamWords(wordMap, examplesMap);

        List<Word> words = new ArrayList<>(wordMap.values());
        words.forEach(w -> {
            w.setStage(0);
            w.setNextReviewDate(LocalDate.now());
        });
        wordRepository.saveAll(words);
        log.info("单词主表导入完成：{} 条", words.size());

        List<WordExample> examples = new ArrayList<>();
        List<WordPhrase> phrases = new ArrayList<>();
        for (Word w : words) {
            String key = w.getWord();
            for (String text : examplesMap.getOrDefault(key, List.of())) {
                WordExample e = new WordExample();
                e.setWord(w);
                e.setExampleText(text);
                examples.add(e);
            }
            for (String text : phrasesMap.getOrDefault(key, List.of())) {
                WordPhrase p = new WordPhrase();
                p.setWord(w);
                p.setPhraseText(text);
                phrases.add(p);
            }
        }
        exampleRepository.saveAll(examples);
        phraseRepository.saveAll(phrases);
        log.info("例句导入完成：{} 条，词组导入完成：{} 条", examples.size(), phrases.size());
    }

    private void importMustWords(Map<String, Word> wordMap,
                                 Map<String, List<String>> examplesMap,
                                 Map<String, List<String>> phrasesMap) throws Exception {
        ClassPathResource resource = new ClassPathResource("必背词_clean.xlsx");
        try (InputStream in = resource.getInputStream();
             Workbook workbook = new XSSFWorkbook(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String wordText = cell(row, 0);
                if (wordText == null || wordText.isBlank()) continue;

                Word w = new Word();
                w.setWord(wordText.trim());
                w.setPhonetic(cell(row, 1));
                w.setWordClass(cell(row, 2));
                w.setMeaning(cell(row, 3));
                w.setWordType("must");
                w.setDerivatives(cell(row, 6));
                w.setSynonyms(cell(row, 7));
                w.setAntonyms(cell(row, 8));
                wordMap.put(w.getWord(), w);

                String examplesText = cell(row, 4);
                if (examplesText != null && !examplesText.isBlank()) {
                    examplesMap.put(w.getWord(), splitLines(examplesText));
                }
                String phrasesText = cell(row, 5);
                if (phrasesText != null && !phrasesText.isBlank()) {
                    phrasesMap.put(w.getWord(), splitLines(phrasesText));
                }
            }
        }
        log.info("必背词读取完成：{} 条", wordMap.size());
    }

    private void importExamWords(Map<String, Word> wordMap,
                                 Map<String, List<String>> examplesMap) throws Exception {
        ClassPathResource resource = new ClassPathResource("主考词_clean.xlsx");
        int count = 0;
        try (InputStream in = resource.getInputStream();
             Workbook workbook = new XSSFWorkbook(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String wordText = cell(row, 0);
                if (wordText == null || wordText.isBlank()) continue;
                wordText = wordText.trim();

                Word w = wordMap.get(wordText);
                if (w == null) {
                    w = new Word();
                    w.setWord(wordText);
                    w.setPhonetic(cell(row, 1));
                    w.setWordClass(cell(row, 2));
                    w.setMeaning(cell(row, 3));
                    wordMap.put(wordText, w);
                }
                w.setWordType("exam");
                w.setRootMemo(cell(row, 4));
                w.setExamMeaning(cell(row, 6));
                w.setExtensions(cell(row, 7));
                w.setExamContext(cell(row, 8));

                String baseExample = cell(row, 5);
                if (baseExample != null && !baseExample.isBlank()) {
                    examplesMap.computeIfAbsent(wordText, k -> new ArrayList<>()).addAll(splitLines(baseExample));
                }
                count++;
            }
        }
        log.info("主考词读取完成：{} 条", count);
    }

    private String cell(Row row, int index) {
        if (row.getCell(index) == null) return null;
        return switch (row.getCell(index).getCellType()) {
            case STRING -> row.getCell(index).getStringCellValue();
            case NUMERIC -> String.valueOf(row.getCell(index).getNumericCellValue());
            default -> null;
        };
    }

    private List<String> splitLines(String text) {
        List<String> lines = new ArrayList<>();
        for (String line : text.split("\\r?\\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                lines.add(trimmed);
            }
        }
        return lines;
    }
}

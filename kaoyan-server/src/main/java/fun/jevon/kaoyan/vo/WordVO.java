package fun.jevon.kaoyan.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class WordVO {

    private Long id;
    private String word;
    private String phonetic;
    private String wordClass;
    private String meaning;
    private String wordType;
    private String rootMemo;
    private String examMeaning;
    private String extensions;
    private String examContext;
    private String synonyms;
    private String antonyms;
    private String derivatives;
    private Integer stage;
    private LocalDate nextReviewDate;
    private List<String> examples;
    private List<String> phrases;
}

package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.WordPhrase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordPhraseRepository extends JpaRepository<WordPhrase, Long> {

    List<WordPhrase> findByWordId(Long wordId);
}

package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.WordStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordStatsRepository extends JpaRepository<WordStats, Long> {
}

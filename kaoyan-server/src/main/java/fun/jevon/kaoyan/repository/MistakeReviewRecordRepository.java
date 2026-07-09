package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.MistakeReviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MistakeReviewRecordRepository extends JpaRepository<MistakeReviewRecord, Long> {
}

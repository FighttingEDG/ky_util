package fun.jevon.kaoyan.repository;

import fun.jevon.kaoyan.entity.ReviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRecordRepository extends JpaRepository<ReviewRecord, Long> {
}

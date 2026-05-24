package iuh.fit.se.repository;

import iuh.fit.se.entity.NhatKyAi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface NhatKyAiRepository extends JpaRepository<NhatKyAi, Integer> {
    // Tìm kiếm lịch sử theo ngày để Admin dễ theo dõi
    List<NhatKyAi> findByNgayPhanTichOrderByThoiGianTaoDesc(LocalDate ngay);

    // Lấy tất cả lịch sử phân tích AI
    List<NhatKyAi> findAllByOrderByThoiGianTaoDesc();
}
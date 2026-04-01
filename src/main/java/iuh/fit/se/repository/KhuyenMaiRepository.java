package iuh.fit.se.repository;

import iuh.fit.se.entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {

    Optional<KhuyenMai> findByMaCodeAndThoiGianXoa(String maCode, Long thoiGianXoa);

    boolean existsByMaCodeAndThoiGianXoa(String maCode, Long thoiGianXoa);

    @Query("SELECT k FROM KhuyenMai k WHERE k.thoiGianXoa = 0 " +
            "AND k.ngayBatDau <= :now AND k.ngayHetHan >= :now")
    List<KhuyenMai> findActivePromotions(LocalDateTime now);

    List<KhuyenMai> findByThoiGianXoaOrderByNgayBatDauDesc(Long thoiGianXoa);
}
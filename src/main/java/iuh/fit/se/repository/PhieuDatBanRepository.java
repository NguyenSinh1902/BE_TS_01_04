package iuh.fit.se.repository;

import iuh.fit.se.entity.ChiTietDatBan;
import iuh.fit.se.entity.PhieuDatBan;
import iuh.fit.se.enums.TrangThaiDatBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PhieuDatBanRepository extends JpaRepository<PhieuDatBan, Integer> {
    @Query("SELECT p FROM PhieuDatBan p WHERE p.idPhieuDat = :id AND p.thoiGianXoa = 0")
    Optional<PhieuDatBan> findActiveById(Integer id);

    List<PhieuDatBan> findByTrangThaiDatAndThoiGianDatBefore(TrangThaiDatBan trangThai, LocalDateTime time);
}


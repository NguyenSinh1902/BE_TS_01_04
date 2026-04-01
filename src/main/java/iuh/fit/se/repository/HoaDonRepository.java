package iuh.fit.se.repository;

import iuh.fit.se.entity.HoaDon;
import iuh.fit.se.enums.LoaiDonHang;
import iuh.fit.se.enums.TrangThaiHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    Optional<HoaDon> findByPhieuDatBan_IdPhieuDatAndTrangThaiNot(Integer idPhieu, TrangThaiHoaDon trangThai);

    @Query("SELECT h FROM HoaDon h " +
            "JOIN h.phieuDatBan p " +
            "JOIN ChiTietDatBan ct ON ct.phieuDatBan.idPhieuDat = p.idPhieuDat " +
            "WHERE ct.ban.idBan = :idBan AND h.trangThai != 'DA_THANH_TOAN'")
    Optional<HoaDon> findActiveInvoiceByBanId(@Param("idBan") Integer idBan);

    List<HoaDon> findByThoiGianTaoBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByPhieuDatBan_IdPhieuDatAndThoiGianXoa(Integer idPhieuDat, Long thoiGianXoa);

    @Query("SELECT DISTINCT h FROM HoaDon h " +
            "LEFT JOIN FETCH h.danhSachChiTiet ct " +
            "LEFT JOIN FETCH ct.danhSachTopping tp " +
            "WHERE h.idHoaDon = :id")
    Optional<HoaDon> findByIdWithChiTietsAndToppings(@Param("id") Integer id);

    @Query("SELECT DISTINCT h FROM HoaDon h " +
            "LEFT JOIN FETCH h.danhSachChiTiet ct " +
            "LEFT JOIN FETCH ct.danhSachTopping tp " +
            "WHERE h.loaiDonHang = :loai AND h.trangThai <> 'DA_HUY' " +
            "ORDER BY h.thoiGianTao DESC")
    List<HoaDon> findByLoaiDonHang(@Param("loai") LoaiDonHang loai);
}

package iuh.fit.se.repository;

import iuh.fit.se.entity.HoaDon;
import iuh.fit.se.enums.LoaiDonHang;
import iuh.fit.se.enums.TrangThaiHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    //Lấy danh sách hóa đơn theo khoảng thời gian
    @Query("SELECT DISTINCT h FROM HoaDon h " +
            "LEFT JOIN FETCH h.danhSachChiTiet " +
            "WHERE h.thoiGianTao BETWEEN :start AND :end " +
            "ORDER BY h.thoiGianTao DESC")
    List<HoaDon> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    //Tính tổng doanh thu
    @Query("SELECT SUM(h.tongThanhToan) FROM HoaDon h " +
            "WHERE h.thoiGianTao BETWEEN :start AND :end " +
            "AND h.trangThai IN (iuh.fit.se.enums.TrangThaiHoaDon.DA_THANH_TOAN, iuh.fit.se.enums.TrangThaiHoaDon.HOAN_TAT)")
    BigDecimal tinhTongDoanhThu(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    //Đếm số đơn hàng
    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.thoiGianTao BETWEEN :start AND :end")
    long demSoDonHang(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    //Tìm món bán chạy nhất
    @Query("SELECT ct.bienThe.sanPham.tenSanPham, SUM(ct.soLuong) as total " +
            "FROM ChiTietHoaDon ct " +
            "WHERE ct.hoaDon.thoiGianThanhToan BETWEEN :start AND :end " +
            "GROUP BY ct.bienThe.sanPham.idSanPham " +
            "ORDER BY total DESC")
    List<Object[]> findTopProduct(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    //Thống kê doanh thu theo giờ(bieu do)
    @Query("SELECT HOUR(h.thoiGianThanhToan), SUM(h.tongThanhToan) " +
            "FROM HoaDon h " +
            "WHERE h.thoiGianThanhToan BETWEEN :start AND :end " +
            "AND h.trangThai IN (iuh.fit.se.enums.TrangThaiHoaDon.DA_THANH_TOAN, iuh.fit.se.enums.TrangThaiHoaDon.HOAN_TAT) " +
            "GROUP BY HOUR(h.thoiGianThanhToan) " +
            "ORDER BY HOUR(h.thoiGianThanhToan)")
    List<Object[]> getDoanhThuTheoGio(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

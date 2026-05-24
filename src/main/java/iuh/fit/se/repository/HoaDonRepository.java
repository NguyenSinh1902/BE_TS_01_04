package iuh.fit.se.repository;

import iuh.fit.se.entity.HoaDon;
import iuh.fit.se.enums.LoaiDonHang;
import iuh.fit.se.enums.TrangThaiHoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    // Tìm món bán chạy nhất (Trả về ID sản phẩm và Số lượng)
    @Query("SELECT ct.bienThe.sanPham.idSanPham, SUM(ct.soLuong) as total " +
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

    @Query("SELECT h.loaiDonHang, COUNT(h) FROM HoaDon h " +
            "WHERE h.thoiGianTao BETWEEN :start AND :end AND h.thoiGianXoa = 0 " +
            "GROUP BY h.loaiDonHang")
    List<Object[]> countOrderByLoai(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT HOUR(h.thoiGianTao), COUNT(h) FROM HoaDon h " +
            "WHERE h.thoiGianTao BETWEEN :start AND :end AND h.thoiGianXoa = 0 " +
            "GROUP BY HOUR(h.thoiGianTao)")
    List<Object[]> countOrderByHour(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 1. TOP 5 BÁN CHẠY NHẤT
    //(Chỉ lấy món nước, loại bỏ Topping)
    @Query("SELECT sp.tenSanPham, SUM(ct.soLuong) as total " +
            "FROM ChiTietHoaDon ct " +
            "JOIN ct.bienThe bt " +
            "JOIN bt.sanPham sp " +
            "WHERE ct.hoaDon.thoiGianTao BETWEEN :start AND :end " +
            "AND ct.hoaDon.thoiGianXoa = 0 " +
            "AND sp.laTopping = false " + //Chỉ lấy sản phẩm KHÔNG PHẢI là topping
            "GROUP BY sp.idSanPham, sp.tenSanPham " +
            "ORDER BY total DESC")
    List<Object[]> findTop5BestSellers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

    // 2. TOP 5 BÁN CHẬM NHẤT (Chỉ lấy món nước, loại bỏ Topping)
    @Query("SELECT sp.tenSanPham, COALESCE(SUM(ct.soLuong), 0) as total " +
            "FROM SanPham sp " +
            "LEFT JOIN sp.danhSachBienThe bt " +
            "LEFT JOIN ChiTietHoaDon ct ON ct.bienThe = bt " +
            "  AND ct.hoaDon.thoiGianTao BETWEEN :start AND :end " +
            "  AND ct.hoaDon.thoiGianXoa = 0 " +
            "WHERE sp.thoiGianXoa = 0 " +
            "AND sp.laTopping = false " + //Loại bỏ hoàn toàn các mặt hàng topping ra khỏi bảng bán chậm
            "GROUP BY sp.idSanPham, sp.tenSanPham " +
            "ORDER BY total ASC")
    List<Object[]> findTop5WorstSellers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);
    // Lấy lịch sử hóa đơn của 1 khách hàng
    Page<HoaDon> findByKhachHang_IdKhachHangOrderByThoiGianTaoDesc(Integer idKhachHang, Pageable pageable);

    @Query("SELECT tp.topping.idSanPham, tp.topping.tenSanPham, tp.topping.duongDanAnh, COUNT(tp) " +
            "FROM ChiTietHoaDon ct " +
            "JOIN ct.danhSachTopping tp " +
            "WHERE ct.bienThe.sanPham.idSanPham = :idSpChinh " +
            "GROUP BY tp.topping.idSanPham, tp.topping.tenSanPham, tp.topping.duongDanAnh " +
            "ORDER BY COUNT(tp) DESC")
    List<Object[]> findTopToppingsForProduct(@Param("idSpChinh") Integer idSpChinh, Pageable pageable);

    @Query("SELECT h.phuongThucThanhToan, COUNT(h) FROM HoaDon h " +
            "WHERE h.thoiGianTao BETWEEN :start AND :end " +
            "AND h.thoiGianXoa = 0 " +
            "AND h.trangThai IN (iuh.fit.se.enums.TrangThaiHoaDon.DA_THANH_TOAN, iuh.fit.se.enums.TrangThaiHoaDon.HOAN_TAT) " +
            "AND h.phuongThucThanhToan IS NOT NULL " +
            "GROUP BY h.phuongThucThanhToan")
    List<Object[]> countOrderByPhuongThucThanhToan(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Dùng nativeQuery = true để bỏ qua điều kiện thoi_gian_xoa = 0 của Hibernate
    @Query(value = "SELECT * FROM hoa_don ORDER BY thoi_gian_tao DESC", nativeQuery = true)
    List<HoaDon> findAllInvoicesIncludingDeleted();
}

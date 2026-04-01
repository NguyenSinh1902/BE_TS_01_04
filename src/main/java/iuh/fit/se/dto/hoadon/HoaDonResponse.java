package iuh.fit.se.dto.hoadon;

import iuh.fit.se.dto.chitiethoadon.ChiTietHoaDonResponse;
import iuh.fit.se.dto.thuephi.HoaDonThuePhiResponse;
import iuh.fit.se.enums.LoaiDonHang;
import iuh.fit.se.enums.PhuongThucThanhToan;
import iuh.fit.se.enums.TrangThaiHoaDon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record HoaDonResponse(
        Integer idHoaDon,
        Integer idPhieuDat,
        List<String> danhSachTenBan,
        LoaiDonHang loaiDonHang,
        String tenNhanVien,
        String tenKhachHang,
        String maKhuyenMai,
        BigDecimal tongTienHang,
        BigDecimal giamGiaKhuyenMai,
        BigDecimal giamGiaThanhVien,
        Integer diemSuDung,
        List<HoaDonThuePhiResponse> danhSachThuePhi,
        BigDecimal tongTienThue,
        BigDecimal tongThanhToan,
        PhuongThucThanhToan phuongThucThanhToan,
        TrangThaiHoaDon trangThai,
        LocalDateTime thoiGianTao,
        LocalDateTime thoiGianYeuCau,
        LocalDateTime thoiGianThanhToan,
        String thongTinChiTiet,
        List<ChiTietHoaDonResponse> danhSachChiTiet
) {}
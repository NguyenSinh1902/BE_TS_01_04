package iuh.fit.se.service;

import iuh.fit.se.dto.thongke.*;

import java.time.LocalDate;
import java.util.List;

public interface ThongKeService {
    // Nhóm 1: Tổng quan hôm nay
    TongQuanHomNayResponse getTongQuanHomNay();
    BieuDoHomNayResponse getBieuDoHomNay();

    // Nhóm 2: Thống kê chi tiết theo bộ lọc thời gian
    ChiTietThongKeResponse getChiTietTheoThoiGian(LocalDate tuNgay, LocalDate denNgay);
    List<BieuDoDoanhThuResponse> getBieuDoDoanhThuTheoThoiGian(LocalDate tuNgay, LocalDate denNgay, String donVi);
    TopSanPhamResponse getTopSanPhamTheoThoiGian(LocalDate tuNgay, LocalDate denNgay);
    List<PhuongThucThanhToanResponse> getThongKePhuongThucThanhToan(LocalDate tuNgay, LocalDate denNgay);
}

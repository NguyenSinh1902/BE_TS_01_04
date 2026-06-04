package iuh.fit.se.dto.khachhang;

import iuh.fit.se.dto.chitiethoadon.ChiTietHoaDonRequest;
import iuh.fit.se.enums.LoaiDonHang;
import iuh.fit.se.enums.PhuongThucThanhToan;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record DatMonKhachRequest(
        @NotNull(message = "Loại đơn hàng không được để trống (GIAO_HANG / MANG_VE)")
        LoaiDonHang loaiDonHang,

        @NotNull(message = "Phương thức thanh toán không được để trống (TIEN_MAT / CHUYEN_KHOAN)")
        PhuongThucThanhToan phuongThucThanhToan,

        String diaChiGiaoHang,
        LocalDateTime thoiGianHenLay,
        String ghiChuKhachHang,

        Integer idKhuyenMai, // Khách hàng có thể truyền ID Khuyến Mãi nếu có áp dụng voucher

        @NotEmpty(message = "Giỏ hàng không được để trống")
        List<ChiTietHoaDonRequest> danhSachMon
) {}

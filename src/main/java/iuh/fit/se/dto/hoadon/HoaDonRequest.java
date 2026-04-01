package iuh.fit.se.dto.hoadon;

import iuh.fit.se.enums.LoaiDonHang;
import jakarta.validation.constraints.*;

public record HoaDonRequest(
        @NotNull(message = "Nhân viên không được để trống")
        Integer idNhanVien,
        Integer idPhieuDat,
        @NotNull(message = "Loại đơn hàng không được để trống")
        LoaiDonHang loaiDonHang,
        Integer idKhachHang,
        Integer soKhachHienTai,
        Integer idKhuyenMai,
        Integer diemSuDung
) {}
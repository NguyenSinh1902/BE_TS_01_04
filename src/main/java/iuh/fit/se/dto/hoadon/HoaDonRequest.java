package iuh.fit.se.dto.hoadon;

import iuh.fit.se.enums.LoaiDonHang;
import jakarta.validation.constraints.*;

public record HoaDonRequest(
//        @NotNull(message = "ID Thu ngân không được để trống")
//        Integer idThuNgan,
        Integer idPhieuDat,
        @NotNull(message = "Loại đơn hàng không được để trống")
        LoaiDonHang loaiDonHang,
        Integer idKhachHang,
        Integer soKhachHienTai,
        Integer idKhuyenMai,
        Integer diemSuDung
) {}
package iuh.fit.se.dto.khachhang;

import iuh.fit.se.enums.*;

public record KhachHangResponse(
        Integer idKhachHang,
        String hoTen,
        String soDienThoai,
        GioiTinh gioiTinh,
        Integer tongDiemDaTichLuy,
        Integer diemTichLuy,
        HangThanhVien hangThanhVien,
        TrangThaiKhachHang trangThai
) {}
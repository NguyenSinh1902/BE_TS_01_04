package iuh.fit.se.dto.hoadon;

import iuh.fit.se.enums.PhuongThucThanhToan;

import java.util.List;

public record ThanhToanRequest(
        Integer idKhachHang,
        String maCode,
        Integer diemSuDung,
        List<Integer> danhSachIdThuePhi,
        PhuongThucThanhToan phuongThuc
) {}

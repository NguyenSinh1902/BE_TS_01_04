package iuh.fit.se.dto.thuephi;

import iuh.fit.se.enums.LoaiGiaTriThuePhi;

import java.math.BigDecimal;

public record HoaDonThuePhiResponse(
        String tenThuePhi,
        Float giaTriTaiThoiDiemBan,
        LoaiGiaTriThuePhi loaiGiaTri,
        BigDecimal soTienQuyDoi
) {}

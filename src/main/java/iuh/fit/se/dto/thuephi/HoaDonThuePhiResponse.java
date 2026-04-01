package iuh.fit.se.dto.thuephi;

import java.math.BigDecimal;

public record HoaDonThuePhiResponse(
        String tenThuePhi,
        Float giaTriTaiThoiDiemBan,
        BigDecimal soTienQuyDoi
) {}

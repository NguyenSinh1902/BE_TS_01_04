package iuh.fit.se.dto.hoantra;

import iuh.fit.se.enums.TrangThaiPhieuHoanTra;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PhieuHoanTraResponse(
    Integer idPhieuHoanTra,
    Integer idHoaDon,
    BigDecimal soTienHoan,
    String lyDo,
    TrangThaiPhieuHoanTra trangThai,
    String tenThuNganTao,
    String tenAdminDuyet,
    LocalDateTime thoiGianTao,
    LocalDateTime thoiGianDuyet,
    LocalDateTime thoiGianHoanThanh
) {}

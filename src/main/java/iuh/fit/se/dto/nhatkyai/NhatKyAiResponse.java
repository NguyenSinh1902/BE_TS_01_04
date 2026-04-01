package iuh.fit.se.dto.nhatkyai;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record NhatKyAiResponse(
        Integer idNhatKy,
        String tenQuanLy,
        LocalDate ngayPhanTich,
        String loiKhuyenAi,
        String duLieuDauVao,
        LocalDateTime thoiGianTao
) {}
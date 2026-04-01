package iuh.fit.se.dto.nhatkyai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record NhatKyAiRequest(
        @NotNull(message = "Người quản lý không được để trống")
        Integer idQuanLy,

        @NotNull(message = "Ngày phân tích không được để trống")
        LocalDate ngayPhanTich,

        @NotBlank(message = "Lời khuyên AI không được để trống")
        String loiKhuyenAi,

        String duLieuDauVao
) {}
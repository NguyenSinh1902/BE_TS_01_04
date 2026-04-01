package iuh.fit.se.dto.ban;

import iuh.fit.se.enums.TrangThaiDatBan;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record DatBanRequest(
        @NotBlank(message = "Tên khách đặt không được để trống")
        String tenKhachDat,

        @NotBlank(message = "Số điện thoại khách không được để trống")
        @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có 10 chữ số")
        String sdtKhachDat,

        @NotNull(message = "Giờ hẹn không được để trống")
        @Future(message = "Giờ hẹn phải là một thời điểm trong tương lai")
        LocalDateTime gioHen,

        TrangThaiDatBan trangThaiDatBan // Mặc định thường là CHO_DEN
) {}
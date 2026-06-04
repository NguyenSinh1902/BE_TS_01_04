package iuh.fit.se.dto.khachhang;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DatBanRequest(
        String tenKhachHang,

        String sdtKhachHang,

        @NotNull(message = "Thời gian đặt không được để trống")
        @Future(message = "Thời gian đặt phải trong tương lai")
        LocalDateTime thoiGianDat,

        @NotNull(message = "Số lượng người không được để trống")
        @Min(value = 1, message = "Số lượng người phải lớn hơn 0")
        Integer soLuongNguoi,

        String ghiChu
) {}

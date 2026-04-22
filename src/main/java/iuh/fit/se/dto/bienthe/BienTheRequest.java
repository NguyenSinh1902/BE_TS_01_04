package iuh.fit.se.dto.bienthe;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record BienTheRequest(

        Integer idBienThe,

        @NotBlank(message = "Tên kích cỡ không được để trống")
        String tenKichCo,

        @NotNull(message = "Giá bán không được để trống")
        @Min(value = 0, message = "Giá bán không được âm")
        BigDecimal giaBan,

        @Min(value = 0) @Max(value = 100)
        Integer phanTramGiamGia,

        Integer soLuongTonKho
) {}

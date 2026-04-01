package iuh.fit.se.dto.goiy;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GoiYSanPhamRequest(
        @NotNull(message = "ID sản phẩm chính không được để trống")
        Integer idSanPhamChinh,

        @NotNull(message = "ID sản phẩm gợi ý không được để trống")
        Integer idSanPhamGoiY,

        @Min(0) @Max(1)
        Float diemTinCay
) {}
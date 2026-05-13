package iuh.fit.se.dto.thuephi;

import iuh.fit.se.enums.LoaiGiaTriThuePhi;
import jakarta.validation.constraints.*;

public record ThuePhiRequest(
        @NotBlank(message = "Tên thuế/phí không được để trống")
        String tenThuePhi,

        @NotNull(message = "Giá trị không được để trống")
        @Min(0)
        Float giaTri,

        @NotNull(message = "Loại giá trị không được để trống")
        LoaiGiaTriThuePhi loaiGiaTri,

        Boolean laMacDinh
) {}

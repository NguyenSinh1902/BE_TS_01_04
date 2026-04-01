package iuh.fit.se.dto.khuyenmai;

import iuh.fit.se.enums.LoaiKhuyenMai;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record KhuyenMaiRequest(
        @NotBlank(message = "Mã code không được để trống")
        String maCode,

        @NotNull(message = "Loại khuyến mãi không được để trống")
        LoaiKhuyenMai loaiKhuyenMai, // GIAM_PHAN_TRAM hoặc GIAM_TIEN_MAT

        @NotNull(message = "Giá trị giảm không được để trống")
        @Positive(message = "Giá trị giảm phải lớn hơn 0")
        BigDecimal giaTriGiam,

        @NotNull(message = "Cấu hình thuế không được để trống")
        Boolean laGiamGiaSauThue,

        BigDecimal donToiThieu,

        @NotNull(message = "Ngày bắt đầu không được để trống")
        LocalDateTime ngayBatDau,

        @NotNull(message = "Ngày hết hạn không được để trống")
        LocalDateTime ngayHetHan
) {}
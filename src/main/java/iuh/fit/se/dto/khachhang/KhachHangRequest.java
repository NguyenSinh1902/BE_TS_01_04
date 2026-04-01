package iuh.fit.se.dto.khachhang;

import iuh.fit.se.enums.GioiTinh;
import jakarta.validation.constraints.*;

public record KhachHangRequest(
        @NotBlank(message = "Tên khách hàng không được để trống")
        String hoTen,

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải có 10 số và bắt đầu bằng số 0")
        String soDienThoai,

        GioiTinh gioiTinh
) {}
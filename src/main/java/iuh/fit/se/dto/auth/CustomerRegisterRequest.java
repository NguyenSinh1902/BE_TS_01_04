package iuh.fit.se.dto.auth;

import iuh.fit.se.enums.GioiTinh;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerRegisterRequest(
        @NotBlank(message = "Họ tên không được để trống")
        String hoTen,

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không hợp lệ")
        String soDienThoai,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        String matKhau,

        GioiTinh gioiTinh
) {}

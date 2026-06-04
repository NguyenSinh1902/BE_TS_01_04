package iuh.fit.se.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerLoginRequest(
        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không hợp lệ")
        String soDienThoai,

        @NotBlank(message = "Mật khẩu không được để trống")
        String matKhau
) {}

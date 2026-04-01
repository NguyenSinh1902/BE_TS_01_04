package iuh.fit.se.dto.auth;

import jakarta.validation.constraints.*;

public record QuenMatKhauRequest(
        @NotBlank(message = "Email không được để trống") String email,
        @NotBlank(message = "Mã OTP không được để trống") String otp,
        @NotBlank(message = "Mật khẩu mới không được để trống") String matKhauMoi
) {}
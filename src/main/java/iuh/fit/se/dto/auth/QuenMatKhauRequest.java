package iuh.fit.se.dto.auth;

import jakarta.validation.constraints.*;

public record QuenMatKhauRequest(
        @NotBlank(message = "Email không được để trống") String email,

        @NotBlank(message = "Mã OTP không được để trống") String otp,

        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Mật khẩu mới phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, chữ số và ký tự đặc biệt"
        )
        String matKhauMoi
) {}
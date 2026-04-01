package iuh.fit.se.dto.auth;

import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank(message = "Email không được để trống") String email,
        @NotBlank(message = "Mật khẩu không được để trống") String matKhau
) {}
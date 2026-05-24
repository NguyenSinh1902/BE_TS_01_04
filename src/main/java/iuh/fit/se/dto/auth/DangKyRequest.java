package iuh.fit.se.dto.auth;

import iuh.fit.se.enums.VaiTroNhanVien;
import jakarta.validation.constraints.*;

public record DangKyRequest(
        @NotBlank(message = "Email không được để trống") @Email String email,

        @NotBlank(message = "Mật khẩu không được để trống") @Size(min = 6)
        @NotBlank(message = "Mật khẩu không được để trống")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, chữ số và ký tự đặc biệt"
        )
        String matKhau,

        @NotBlank(message = "Họ tên không được để trống") String hoTen,

        String soDienThoai,
        VaiTroNhanVien vaiTro
) {}
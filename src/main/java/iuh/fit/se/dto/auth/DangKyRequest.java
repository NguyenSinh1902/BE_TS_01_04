package iuh.fit.se.dto.auth;

import iuh.fit.se.enums.VaiTroNhanVien;
import jakarta.validation.constraints.*;

public record DangKyRequest(
        @NotBlank(message = "Email không được để trống") @Email String email,
        @NotBlank(message = "Mật khẩu không được để trống") @Size(min = 6) String matKhau,
        @NotBlank(message = "Họ tên không được để trống") String hoTen,
        String soDienThoai,
        VaiTroNhanVien vaiTro
) {}
package iuh.fit.se.dto.nhanvien;

import iuh.fit.se.enums.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record NhanVienRequest(
        @NotBlank(message = "Họ tên không được để trống")
        String hoTen,
        GioiTinh gioiTinh,
        LocalDate ngaySinh,
        @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại không hợp lệ")
        String soDienThoai,
        String avatar
) {}
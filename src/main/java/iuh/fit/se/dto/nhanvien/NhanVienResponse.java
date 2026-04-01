package iuh.fit.se.dto.nhanvien;

import iuh.fit.se.enums.*;
import java.time.LocalDate;

public record NhanVienResponse(
        Integer idNhanVien,
        String email,
        String hoTen,
        GioiTinh gioiTinh,
        LocalDate ngaySinh,
        String soDienThoai,
        VaiTroNhanVien vaiTro,
        TrangThaiNhanVien trangThai
) {}
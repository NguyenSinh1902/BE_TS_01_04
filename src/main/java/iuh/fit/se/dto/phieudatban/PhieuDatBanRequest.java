package iuh.fit.se.dto.phieudatban;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record PhieuDatBanRequest(
        @NotBlank(message = "Tên khách hàng không được để trống")
        @Size(min = 2, max = 100, message = "Tên khách hàng từ 2 đến 100 ký tự")
        String tenKhachHang,

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có 10 chữ số")
        String sdtKhachHang,

        @NotNull(message = "Thời gian không được để trống")
        @FutureOrPresent(message = "Thời gian không được ở quá khứ")
        LocalDateTime thoiGianDat,

        @NotNull(message = "Số lượng người không được để trống")
        @Min(value = 1, message = "Phải có ít nhất 1 người")
        Integer soLuongNguoi,

        String ghiChu,

//        @NotNull(message = "Nhân viên phục vụ không được để trống")
//        Integer idNhanVienPhucVu,

        @NotEmpty(message = "Phải chọn ít nhất 1 bàn")
        List<Integer> danhSachIdBan
) {}

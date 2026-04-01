package iuh.fit.se.dto.chitiethoadon;

import jakarta.validation.constraints.*;

import java.util.List;

public record ChiTietHoaDonRequest(
        @NotNull(message = "Phải chọn sản phẩm/biến thể")
        Integer idBienThe,

        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng tối thiểu là 1")
        Integer soLuong,

        String tuyChonJson, // Chỉ dùng cho Đường/Đá (50% đường, ít đá...)

        List<Integer> danhSachIdTopping // Danh sách ID SanPham (loại topping)
) {}

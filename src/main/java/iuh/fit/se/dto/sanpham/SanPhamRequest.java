package iuh.fit.se.dto.sanpham;
import iuh.fit.se.dto.bienthe.BienTheRequest;
import jakarta.validation.constraints.*;

import java.util.List;

public record SanPhamRequest(
        @NotBlank(message = "Tên sản phẩm không được để trống")
        String tenSanPham,

        @NotNull(message = "Phải chọn danh mục cho sản phẩm")
        Integer idDanhMuc,

        String duongDanAnh,
        Boolean laTopping,
        List<BienTheRequest> danhSachBienThe
) {}
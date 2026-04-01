package iuh.fit.se.dto.danhmuc;

import jakarta.validation.constraints.*;

public record DanhMucRequest(
        @NotBlank(message = "Tên danh mục không được để trống")
        @Size(max = 100, message = "Tên danh mục không được quá 100 ký tự")
        String tenDanhMuc,

        @Size(max = 500, message = "Đường dẫn ảnh quá dài")
        String duongDanAnh,

        Boolean laHeThong
) {}
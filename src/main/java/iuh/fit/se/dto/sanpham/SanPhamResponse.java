package iuh.fit.se.dto.sanpham;

import iuh.fit.se.dto.bienthe.BienTheResponse;

import java.util.List;

public record SanPhamResponse(
        Integer idSanPham,
        String tenSanPham,
        Integer idDanhMuc,
        String tenDanhMuc, // Thêm tên danh mục để UI hiển thị luôn
        String moTa,
        String duongDanAnh,
        Boolean laTopping,
        List<BienTheResponse> danhSachBienThe
) {}
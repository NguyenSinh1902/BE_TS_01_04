package iuh.fit.se.dto.sanpham;

import java.util.List;

public record HomeResponse(
        List<SanPhamResponse> sanPhamHot,
        List<SanPhamResponse> sanPhamGiamGia,
        List<SanPhamResponse> sanPhamMoi
) {}
package iuh.fit.se.dto.goiy;

public record GoiYSanPhamResponse(
        Integer idGoiY,
        Integer idSanPhamGoiY,
        String tenSanPhamGoiY,
        String duongDanAnhGoiY,
        Float diemTinCay
) {}
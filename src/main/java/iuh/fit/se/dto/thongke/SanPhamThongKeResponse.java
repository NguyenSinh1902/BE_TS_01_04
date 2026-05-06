package iuh.fit.se.dto.thongke;

public record SanPhamThongKeResponse(
        String tenSanPham,
        long soLuong,
        String xuHuong // "Tang", "Giam" hoặc "On dinh"
) {}

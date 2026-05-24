package iuh.fit.se.dto.thongke;

public record MonBanChayResponse(
        String tenSanPham,
        String duongDanAnh,
        java.math.BigDecimal giaBanMacDinh, // Giá của size đầu tiên
        long soLuongDaBan
) {}
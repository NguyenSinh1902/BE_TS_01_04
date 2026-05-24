package iuh.fit.se.dto.thongke;

// 1. DTO cho Thẻ tổng quan hôm nay
public record TongQuanHomNayResponse(
        java.math.BigDecimal doanhThuHomNay,
        double phanTramTangTruongDoanhThu,
        long soDonHang,
        double phanTramTangTruongDonHang,
        MonBanChayResponse monBanChayNhat // Thay đổi ở đây
) {}

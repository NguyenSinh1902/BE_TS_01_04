package iuh.fit.se.dto.thongke;

import java.math.BigDecimal;

public record DashboardResponse(
        BigDecimal doanhThuHomNay,
        double phanTramTangTruongDoanhThu,
        long soDonHang,
        double phanTramTangTruongDonHang,
        String monBanChayNhat,
        long soLuongMonBanChay
) {}

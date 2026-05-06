package iuh.fit.se.dto.thongke;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        BigDecimal doanhThuHomNay,
        double phanTramTangTruongDoanhThu,
        long soDonHang,
        double phanTramTangTruongDonHang,
        String monBanChayNhat,
        long soLuongMonBanChay,
        List<OrderSourceResponse> orderSources,
        List<PeakHourResponse> peakHours,
        List<SanPhamThongKeResponse> top5BanChay,
        List<SanPhamThongKeResponse> top5BanCham
) {}

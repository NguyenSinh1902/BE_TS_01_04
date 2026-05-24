package iuh.fit.se.dto.thongke;

public record PhuongThucThanhToanResponse(
        String code,
        String label,
        long soLuongDon,
        double phanTram
) {}

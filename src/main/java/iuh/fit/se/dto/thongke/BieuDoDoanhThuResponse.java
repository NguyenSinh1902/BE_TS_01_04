package iuh.fit.se.dto.thongke;

// 4. DTO cho Biểu đồ Doanh thu (Dùng chung cho cả ngày/tuần/tháng/năm)
public record BieuDoDoanhThuResponse(
        String nhan,
        java.math.BigDecimal giaTri
) {}

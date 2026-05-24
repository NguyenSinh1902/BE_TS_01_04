package iuh.fit.se.dto.thongke;

// 3. DTO cho Chỉ số chi tiết theo khoảng thời gian
public record ChiTietThongKeResponse(
        java.math.BigDecimal tongDoanhThu,
        long tongSoDonHang,
        String monBanChayNhat
) {}

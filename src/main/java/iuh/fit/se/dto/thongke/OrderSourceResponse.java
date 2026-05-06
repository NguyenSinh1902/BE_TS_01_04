package iuh.fit.se.dto.thongke;

public record OrderSourceResponse(
        String sourceCode,
        String label,
        long totalOrders,
        double percentage
) {}
package iuh.fit.se.dto.thongke;

import java.util.List;

// 2. DTO cho Cụm biểu đồ hôm nay
public record BieuDoHomNayResponse(
        List<OrderSourceResponse> orderSources,
        List<PeakHourResponse> peakHours
) {}

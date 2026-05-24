package iuh.fit.se.dto.thongke;

import java.util.List;

// 5. DTO cho Top sản phẩm bán chạy / bán chậm
public record TopSanPhamResponse(
        List<SanPhamThongKeResponse> top5BanChay,
        List<SanPhamThongKeResponse> top5BanCham
) {}

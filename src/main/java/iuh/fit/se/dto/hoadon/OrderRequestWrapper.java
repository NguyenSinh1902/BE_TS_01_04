package iuh.fit.se.dto.hoadon;

import iuh.fit.se.dto.chitiethoadon.ChiTietHoaDonRequest;
import java.util.List;

public record OrderRequestWrapper(
        HoaDonRequest request,
        List<ChiTietHoaDonRequest> chiTiets
) {}
package iuh.fit.se.dto.chitiethoadon;

import java.math.BigDecimal;
import java.util.List;

public record ChiTietHoaDonResponse(
        Integer idChiTiet,
        Integer idBienThe,
        String tenSanPham,
        String tenKichCo,
        Integer soLuong,
        BigDecimal giaThoiDiemBan,
        String tuyChonJson,
        List<ToppingResponse> danhSachTopping,
        BigDecimal thanhTien
) {}


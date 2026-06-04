package iuh.fit.se.dto.bep;

import iuh.fit.se.enums.LoaiNguyenLieu;
import java.math.BigDecimal;

public record NguyenLieuResponse(
    Integer idNguyenLieu,
    String tenNguyenLieu,
    BigDecimal soLuongTon,
    String donViTinh,
    LoaiNguyenLieu loaiNguyenLieu,
    BigDecimal giaNhapDonVi
) {}

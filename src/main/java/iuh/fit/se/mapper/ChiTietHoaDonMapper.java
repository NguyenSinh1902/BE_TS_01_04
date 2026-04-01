package iuh.fit.se.mapper;

import iuh.fit.se.dto.chitiethoadon.ChiTietHoaDonRequest;
import iuh.fit.se.dto.chitiethoadon.ChiTietHoaDonResponse;
import iuh.fit.se.entity.ChiTietHoaDon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChiTietHoaDonMapper {

    @Mapping(source = "bienThe.idBienThe", target = "idBienThe")
    @Mapping(source = "bienThe.sanPham.tenSanPham", target = "tenSanPham")
    @Mapping(source = "bienThe.tenKichCo", target = "tenKichCo")

    @Mapping(target = "thanhTien", expression = "java(entity.getGiaThoiDiemBan().multiply(java.math.BigDecimal.valueOf(entity.getSoLuong())))")
    ChiTietHoaDonResponse toResponse(ChiTietHoaDon entity);

    @Mapping(target = "idChiTiet", ignore = true)
    @Mapping(target = "hoaDon", ignore = true)
    @Mapping(target = "bienThe", ignore = true)
    @Mapping(target = "thoiGianXoa", constant = "0L")
    ChiTietHoaDon toEntity(ChiTietHoaDonRequest request);
}

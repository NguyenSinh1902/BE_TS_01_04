package iuh.fit.se.mapper;

import iuh.fit.se.dto.sanpham.SanPhamRequest;
import iuh.fit.se.dto.sanpham.SanPhamResponse;
import iuh.fit.se.entity.SanPham;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {BienTheMapper.class})
public interface SanPhamMapper {
    @Mapping(source = "danhMuc.idDanhMuc", target = "idDanhMuc")
    @Mapping(source = "danhMuc.tenDanhMuc", target = "tenDanhMuc")
    SanPhamResponse toResponse(SanPham entity);

    @Mapping(target = "idSanPham", ignore = true)
    @Mapping(target = "danhMuc", ignore = true)
    @Mapping(target = "thoiGianXoa", constant = "0L")
    SanPham toEntity(SanPhamRequest request);

    @Mapping(target = "idSanPham", ignore = true)
    @Mapping(target = "danhMuc", ignore = true)
    @Mapping(target = "thoiGianXoa", ignore = true)
    @Mapping(target = "danhSachBienThe", ignore = true) // Biến thể xử lý riêng
    void updateEntityFromRequest(SanPhamRequest request, @MappingTarget SanPham entity);
}
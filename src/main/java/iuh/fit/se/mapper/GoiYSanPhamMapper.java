package iuh.fit.se.mapper;

import iuh.fit.se.dto.goiy.GoiYSanPhamRequest;
import iuh.fit.se.dto.goiy.GoiYSanPhamResponse;
import iuh.fit.se.entity.GoiYSanPham;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoiYSanPhamMapper {

    @Mapping(source = "sanPhamGoiY.idSanPham", target = "idSanPhamGoiY")
    @Mapping(source = "sanPhamGoiY.tenSanPham", target = "tenSanPhamGoiY")
    @Mapping(source = "sanPhamGoiY.duongDanAnh", target = "duongDanAnhGoiY")
    GoiYSanPhamResponse toResponse(GoiYSanPham entity);

    @Mapping(target = "idGoiY", ignore = true)
    @Mapping(target = "sanPhamChinh", ignore = true)
    @Mapping(target = "sanPhamGoiY", ignore = true)
    GoiYSanPham toEntity(GoiYSanPhamRequest request);
}
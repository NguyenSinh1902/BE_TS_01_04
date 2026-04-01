package iuh.fit.se.mapper;

import iuh.fit.se.dto.danhmuc.DanhMucRequest;
import iuh.fit.se.dto.danhmuc.DanhMucResponse;
import iuh.fit.se.entity.DanhMuc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DanhMucMapper {

    DanhMucResponse toResponse(DanhMuc entity);

    @Mapping(target = "idDanhMuc", ignore = true)
    @Mapping(target = "danhSachSanPham", ignore = true)
    @Mapping(target = "thoiGianXoa", constant = "0L")
    @Mapping(target = "laHeThong", source = "laHeThong", defaultValue = "false")
    DanhMuc toEntity(DanhMucRequest request);

    @Mapping(target = "idDanhMuc", ignore = true)
    @Mapping(target = "laHeThong", ignore = true)
    @Mapping(target = "thoiGianXoa", ignore = true)
    void updateEntityFromRequest(DanhMucRequest request, @MappingTarget DanhMuc entity);
}
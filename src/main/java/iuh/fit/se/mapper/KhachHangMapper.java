package iuh.fit.se.mapper;

import iuh.fit.se.dto.khachhang.*;
import iuh.fit.se.entity.KhachHang;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface KhachHangMapper {

    KhachHangResponse toResponse(KhachHang entity);

    @Mapping(target = "idKhachHang", ignore = true)
    @Mapping(target = "diemTichLuy", constant = "0")
    @Mapping(target = "hangThanhVien", constant = "MOI")
    @Mapping(target = "trangThai", constant = "DANG_HOAT_DONG")
    @Mapping(target = "thoiGianXoa", constant = "0L")
    KhachHang toEntity(KhachHangRequest request);

    @Mapping(target = "idKhachHang", ignore = true)
    @Mapping(target = "diemTichLuy", ignore = true)
    @Mapping(target = "hangThanhVien", ignore = true)
    @Mapping(target = "thoiGianXoa", ignore = true)
    void updateEntityFromRequest(KhachHangRequest request, @MappingTarget KhachHang entity);
}
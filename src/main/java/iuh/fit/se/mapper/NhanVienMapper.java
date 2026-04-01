package iuh.fit.se.mapper;

import iuh.fit.se.dto.nhanvien.*;
import iuh.fit.se.entity.NhanVien;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NhanVienMapper {

    NhanVienResponse toResponse(NhanVien entity);

    @Mapping(target = "idNhanVien", ignore = true)
    @Mapping(target = "maOtp", ignore = true)
    @Mapping(target = "thoiHanOtp", ignore = true)
    @Mapping(target = "trangThai", constant = "CHO_DUYET")
    @Mapping(target = "thoiGianXoa", constant = "0L")
    NhanVien toEntity(NhanVienRequest request);

    void updateEntityFromRequest(NhanVienRequest request, @MappingTarget NhanVien entity);


}
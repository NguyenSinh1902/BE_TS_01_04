package iuh.fit.se.mapper;

import iuh.fit.se.dto.khuyenmai.KhuyenMaiRequest;
import iuh.fit.se.dto.khuyenmai.KhuyenMaiResponse;
import iuh.fit.se.entity.KhuyenMai;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface KhuyenMaiMapper {
    KhuyenMaiResponse toResponse(KhuyenMai entity);

    @Mapping(target = "idKhuyenMai", ignore = true)
    @Mapping(target = "thoiGianXoa", constant = "0L")
    KhuyenMai toEntity(KhuyenMaiRequest request);

    @Mapping(target = "idKhuyenMai", ignore = true)
    @Mapping(target = "thoiGianXoa", ignore = true)
    void updateEntityFromRequest(KhuyenMaiRequest request, @MappingTarget KhuyenMai entity);
}
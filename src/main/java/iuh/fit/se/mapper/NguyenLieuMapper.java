package iuh.fit.se.mapper;

import iuh.fit.se.dto.bep.NguyenLieuRequest;
import iuh.fit.se.dto.bep.NguyenLieuResponse;
import iuh.fit.se.entity.NguyenLieu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NguyenLieuMapper {
    
    NguyenLieuResponse toResponse(NguyenLieu entity);
    
    @Mapping(target = "idNguyenLieu", ignore = true)
    @Mapping(target = "thoiGianXoa", constant = "0L")
    NguyenLieu toEntity(NguyenLieuRequest request);
}

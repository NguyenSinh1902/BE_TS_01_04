package iuh.fit.se.mapper;

import iuh.fit.se.dto.bienthe.BienTheRequest;
import iuh.fit.se.dto.bienthe.BienTheResponse;
import iuh.fit.se.entity.BienTheSanPham;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BienTheMapper {
    BienTheResponse toResponse(BienTheSanPham entity);

    @Mapping(target = "idBienThe", ignore = true)
    @Mapping(target = "sanPham", ignore = true)
    @Mapping(target = "thoiGianXoa", constant = "0L")
    BienTheSanPham toEntity(BienTheRequest request);
}

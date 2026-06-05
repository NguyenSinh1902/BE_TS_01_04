package iuh.fit.se.mapper;

import iuh.fit.se.dto.hoantra.PhieuHoanTraResponse;
import iuh.fit.se.entity.PhieuHoanTra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PhieuHoanTraMapper {
    @Mapping(target = "idHoaDon", source = "hoaDon.idHoaDon")
    @Mapping(target = "tenThuNganTao", source = "thuNganTao.hoTen")
    @Mapping(target = "tenAdminDuyet", source = "adminDuyet.hoTen")
    PhieuHoanTraResponse toResponse(PhieuHoanTra entity);
}

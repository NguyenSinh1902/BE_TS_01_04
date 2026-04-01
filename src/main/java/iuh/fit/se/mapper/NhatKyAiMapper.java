package iuh.fit.se.mapper;

import iuh.fit.se.dto.nhatkyai.NhatKyAiRequest;
import iuh.fit.se.dto.nhatkyai.NhatKyAiResponse;
import iuh.fit.se.entity.NhatKyAi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NhatKyAiMapper {

    @Mapping(source = "quanLy.hoTen", target = "tenQuanLy")
    @Mapping(source = "duLieuDauVao", target = "duLieuDauVao")
    NhatKyAiResponse toResponse(NhatKyAi entity);

    @Mapping(target = "idNhatKy", ignore = true)
    @Mapping(target = "quanLy", ignore = true)
    @Mapping(target = "thoiGianTao", expression = "java(java.time.LocalDateTime.now())")
    NhatKyAi toEntity(NhatKyAiRequest request);
}
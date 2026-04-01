package iuh.fit.se.mapper;

import iuh.fit.se.dto.ban.BanRequest;
import iuh.fit.se.dto.ban.BanResponse;
import iuh.fit.se.entity.Ban;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BanMapper {
    BanResponse toResponse(Ban entity);

    @Mapping(target = "idBan", ignore = true)
    @Mapping(target = "tinhTrangBan", constant = "TRONG")
    @Mapping(target = "thoiGianXoa", constant = "0L")
    Ban toEntity(BanRequest request);
}
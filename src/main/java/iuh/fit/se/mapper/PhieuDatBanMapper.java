package iuh.fit.se.mapper;

import iuh.fit.se.dto.phieudatban.PhieuDatBanRequest;
import iuh.fit.se.dto.phieudatban.PhieuDatBanResponse;
import iuh.fit.se.entity.PhieuDatBan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BanMapper.class})
public interface PhieuDatBanMapper {

    @Mapping(source = "nhanVienPhucVu.hoTen", target = "tenNhanVienPhucVu")
    @Mapping(target = "danhSachBan", ignore = true)
    PhieuDatBanResponse toResponse(PhieuDatBan entity);

    @Mapping(target = "nhanVienPhucVu", ignore = true)
    @Mapping(target = "idPhieuDat", ignore = true)
    @Mapping(target = "trangThaiDat", constant = "CHO_DEN")
    @Mapping(target = "thoiGianXoa", constant = "0L")
    PhieuDatBan toEntity(PhieuDatBanRequest request);
}

package iuh.fit.se.mapper;

import iuh.fit.se.dto.thuephi.HoaDonThuePhiResponse;
import iuh.fit.se.dto.thuephi.ThuePhiRequest;
import iuh.fit.se.dto.thuephi.ThuePhiResponse;
import iuh.fit.se.entity.HoaDonThuePhi;
import iuh.fit.se.entity.ThuePhi;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ThuePhiMapper {

    ThuePhi toEntity(ThuePhiRequest request);

    ThuePhiResponse toResponse(ThuePhi thuePhi);

    HoaDonThuePhiResponse toHoaDonResponse(HoaDonThuePhi entity);
}

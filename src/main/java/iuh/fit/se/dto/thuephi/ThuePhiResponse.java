package iuh.fit.se.dto.thuephi;

import iuh.fit.se.enums.LoaiGiaTriThuePhi;

public record ThuePhiResponse(
        Integer idThuePhi,
        String tenThuePhi,
        Float giaTri,
        LoaiGiaTriThuePhi loaiGiaTri,
        Boolean laMacDinh
) {}

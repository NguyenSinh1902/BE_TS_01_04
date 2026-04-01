package iuh.fit.se.dto.ban;

import iuh.fit.se.enums.TinhTrangBan;

public record BanResponse(
        Integer idBan,
        String tenBan,
        Integer sucChua,
        TinhTrangBan tinhTrangBan
) {}
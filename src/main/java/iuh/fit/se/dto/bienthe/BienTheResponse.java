package iuh.fit.se.dto.bienthe;

import java.math.BigDecimal;

public record BienTheResponse(
        Integer idBienThe,
        String tenKichCo,
        BigDecimal giaBan,
        Integer phanTramGiamGia,
        Integer soLuongTonKho
) {}

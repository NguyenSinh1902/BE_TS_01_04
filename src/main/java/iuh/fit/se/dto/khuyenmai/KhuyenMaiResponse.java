package iuh.fit.se.dto.khuyenmai;

import iuh.fit.se.enums.LoaiKhuyenMai;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record KhuyenMaiResponse(
        Integer idKhuyenMai,
        String maCode,
        LoaiKhuyenMai loaiKhuyenMai,
        BigDecimal giaTriGiam,
        Boolean laGiamGiaSauThue,
        BigDecimal donToiThieu,
        LocalDateTime ngayBatDau,
        LocalDateTime ngayHetHan
) {}
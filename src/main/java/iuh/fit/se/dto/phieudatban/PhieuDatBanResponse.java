package iuh.fit.se.dto.phieudatban;

import iuh.fit.se.dto.ban.BanResponse;
import iuh.fit.se.enums.TrangThaiDatBan;

import java.time.LocalDateTime;
import java.util.List;

public record PhieuDatBanResponse(
        Integer idPhieuDat,
        String tenKhachHang,
        String sdtKhachHang,
        LocalDateTime thoiGianDat,
        Integer soLuongNguoi,
        TrangThaiDatBan trangThaiDat,
        String ghiChu,
        List<BanResponse> danhSachBan
) {}

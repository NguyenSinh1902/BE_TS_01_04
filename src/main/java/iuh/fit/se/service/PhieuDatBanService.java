package iuh.fit.se.service;

import iuh.fit.se.dto.phieudatban.PhieuDatBanRequest;
import iuh.fit.se.dto.phieudatban.PhieuDatBanResponse;
import java.util.List;

public interface PhieuDatBanService {

    PhieuDatBanResponse taoPhieuDatMoi(PhieuDatBanRequest request);

    void doiBan(Integer idPhieu, Integer idBanCu, Integer idBanMoi);

    void gopThemBan(Integer idPhieu, List<Integer> idBansMoi);

    void huyPhieu(Integer idPhieu);

    void xuLyHuyPhieuQuaHan();

    void hoanTatPhieu(Integer idPhieu);

    List<PhieuDatBanResponse> layTatCaPhieu();

    List<PhieuDatBanResponse> layPhieuDangHoatDong();

    PhieuDatBanResponse checkIn(Integer idPhieu);

    void giaiPhongBanKhiHuyHoaDon(Integer idPhieu);
}
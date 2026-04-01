package iuh.fit.se.service;

import iuh.fit.se.dto.khuyenmai.KhuyenMaiRequest;
import iuh.fit.se.dto.khuyenmai.KhuyenMaiResponse;
import java.math.BigDecimal;
import java.util.List;

public interface KhuyenMaiService {
    KhuyenMaiResponse taoMoi(KhuyenMaiRequest request);
    KhuyenMaiResponse capNhat(Integer id, KhuyenMaiRequest request);
    List<KhuyenMaiResponse> layTatCa();
    List<KhuyenMaiResponse> layDanhSachHoatDong();
    KhuyenMaiResponse kiemTraMaCode(String maCode, BigDecimal tongTienHang);
    void xoa(Integer id);
}
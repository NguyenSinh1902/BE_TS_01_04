package iuh.fit.se.service;

import iuh.fit.se.dto.khachhang.KhachHangRequest;
import iuh.fit.se.dto.khachhang.KhachHangResponse;
import iuh.fit.se.enums.TrangThaiKhachHang;

import java.util.List;

public interface KhachHangService {

    List<KhachHangResponse> layTatCa();

    KhachHangResponse layChiTiet(Integer id);

    KhachHangResponse taoMoi(KhachHangRequest request);
    KhachHangResponse capNhat(Integer id, KhachHangRequest request);
    void xoa(Integer id);

    void congDiem(Integer id, Integer diem);

    void capNhatTrangThai(Integer id, TrangThaiKhachHang trangThaiMoi);

    void tichDiemVaThangHang(Integer idKhachHang, int diemMoi);
}
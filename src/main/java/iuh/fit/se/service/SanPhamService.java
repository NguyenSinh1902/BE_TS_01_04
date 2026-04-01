package iuh.fit.se.service;

import iuh.fit.se.dto.sanpham.HomeResponse;
import iuh.fit.se.dto.sanpham.SanPhamRequest;
import iuh.fit.se.dto.sanpham.SanPhamResponse;
import java.util.List;

public interface SanPhamService {
    List<SanPhamResponse> layTatCa();

    SanPhamResponse taoMoi(SanPhamRequest request);

    SanPhamResponse capNhat(Integer id, SanPhamRequest request);

    void xoa(Integer id);

    List<SanPhamResponse> layMenuChinh();

    List<SanPhamResponse> layDanhSachTopping();

    List<SanPhamResponse> layTheoDanhMuc(Integer idDanhMuc);

    HomeResponse layDuLieuTrangChu();
}
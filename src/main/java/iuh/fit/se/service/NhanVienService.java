package iuh.fit.se.service;

import iuh.fit.se.dto.nhanvien.NhanVienRequest;
import iuh.fit.se.dto.nhanvien.NhanVienResponse;
import iuh.fit.se.enums.TrangThaiNhanVien;
import iuh.fit.se.enums.VaiTroNhanVien;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NhanVienService {
    List<NhanVienResponse> layTatCaNhanVien();
    List<NhanVienResponse> layNhanVienChoDuyet();
    List<NhanVienResponse> layDanhSachVanHanh();
    void capNhatTrangThai(Integer id, TrangThaiNhanVien trangThai);
    void xoaNhanVien(Integer id); // Xóa mềm
    NhanVienResponse layChiTiet(Integer id);
    NhanVienResponse capNhatThongTin(Integer id, NhanVienRequest request, MultipartFile file);
    void doiVaiTro(Integer id, VaiTroNhanVien vaiTroMoi);
}
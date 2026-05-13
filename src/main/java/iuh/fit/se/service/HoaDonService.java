package iuh.fit.se.service;

import iuh.fit.se.dto.hoadon.HoaDonRequest;
import iuh.fit.se.dto.hoadon.HoaDonResponse;
import iuh.fit.se.dto.chitiethoadon.ChiTietHoaDonRequest;
import iuh.fit.se.dto.hoadon.ThanhToanRequest;
import iuh.fit.se.enums.LoaiDonHang;
import iuh.fit.se.enums.PhuongThucThanhToan;
import iuh.fit.se.enums.TrangThaiHoaDon;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface HoaDonService {
    HoaDonResponse taoDonHangMoi(HoaDonRequest request, List<ChiTietHoaDonRequest> chiTiets);

    HoaDonResponse xuatHoaDonTamTinh(Integer id, ThanhToanRequest request);

    public HoaDonResponse xacNhanThanhToan(Integer id, ThanhToanRequest request);

    void hoanTatDonHang(Integer id);

    HoaDonResponse layChiTiet(Integer idHoaDon);

    void huyHoaDon(Integer idHoaDon);

    HoaDonResponse themMonVaoHoaDon(Integer idHoaDon, List<ChiTietHoaDonRequest> danhSachMoi);

    List<HoaDonResponse> layTatCaHoaDon();

    List<HoaDonResponse> layHoaDonTrongKhoangNgay(LocalDate tuNgay, LocalDate denNgay);

    void capNhatDanhSachThuePhi(Integer idHoaDon, List<Integer> idThuePhis);

    HoaDonResponse yeuCauThanhToan(Integer idHoaDon);

    HoaDonResponse suaChiTietMon(Integer idHoaDon, Integer idChiTiet, ChiTietHoaDonRequest request);

    List<HoaDonResponse> layHoaDonTheoLoai(LoaiDonHang loai);

    HoaDonResponse xoaMonKhoiHoaDon(Integer idHoaDon, Integer idChiTiet);

    HoaDonResponse capNhatTrangThai(Integer idHoaDon, TrangThaiHoaDon trangThaiMoi);

    Page<HoaDonResponse> layLichSuHoaDonKhachHang(Integer idKhachHang, int page, int size);
}
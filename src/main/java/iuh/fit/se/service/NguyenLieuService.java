package iuh.fit.se.service;

import iuh.fit.se.dto.bep.CheBienRequest;
import iuh.fit.se.dto.bep.NguyenLieuRequest;
import iuh.fit.se.dto.bep.NguyenLieuResponse;
import java.util.List;

public interface NguyenLieuService {
    List<NguyenLieuResponse> layKhoNguyenLieu();
    List<NguyenLieuResponse> layKhoNguyenLieuTheoLoai(iuh.fit.se.enums.LoaiNguyenLieu loai);
    NguyenLieuResponse themNguyenLieu(NguyenLieuRequest request);
    
    // CRUD & Kiểm Kê
    NguyenLieuResponse capNhatNguyenLieu(Integer id, NguyenLieuRequest request);
    NguyenLieuResponse nhapThemHang(Integer id, java.math.BigDecimal soLuongNhap);
    void xuatHuyKho(Integer idNguyenLieu, java.math.BigDecimal soLuongHuy, String lyDo);

    
    // API dành riêng cho Bếp làm form nấu Bán thành phẩm (Ví dụ: Nấu 2.5L cốt trà)
    void cheBienBanThanhPham(CheBienRequest request);
    
    // API tính toán xem một Sản phẩm (hoặc Topping) hiện tại còn làm được tối đa bao nhiêu ly
    java.util.Map<String, Object> tinhSoLuongLyConLai(Integer idSanPham);
    
    // API kích hoạt khi Bếp bấm "XONG_MÓN" -> Trừ kho và bắn Firebase Realtime
    void bepXongMon(Integer idChiTietDonHang);

    // API cài đặt công thức
    void caiDatRecipeChuyenDoi(Integer idBanThanhPham, Integer idNguyenLieuTho, java.math.BigDecimal hamLuongCan, java.math.BigDecimal soLuongThuDuoc);
    void caiDatDinhLuongMonAn(Integer idBienThe, Integer idNguyenLieu, java.math.BigDecimal soLuongTieuHao);
    void xoaRecipeChuyenDoi(Integer idRecipe);
    void xoaDinhLuongMonAn(Integer idDinhLuong);

    // API lấy dữ liệu cấu hình
    List<java.util.Map<String, Object>> layRecipeChuyenDoi(Integer idBanThanhPham);
    List<java.util.Map<String, Object>> layDinhLuongMonAn(Integer idBienThe);
    List<java.util.Map<String, Object>> layTatCaDinhLuongMonAn();
}

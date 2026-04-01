package iuh.fit.se.mapper;

import iuh.fit.se.dto.hoadon.HoaDonRequest;
import iuh.fit.se.dto.hoadon.HoaDonResponse;
import iuh.fit.se.entity.HoaDon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ChiTietHoaDonMapper.class, ThuePhiMapper.class})
public interface HoaDonMapper {

    @Mapping(source = "phieuDatBan.idPhieuDat", target = "idPhieuDat")
    @Mapping(target = "danhSachTenBan", ignore = true)
    @Mapping(source = "nhanVien.hoTen", target = "tenNhanVien")
    @Mapping(source = "khachHang.hoTen", target = "tenKhachHang")
    @Mapping(source = "khuyenMai.maCode", target = "maKhuyenMai")
    HoaDonResponse toResponse(HoaDon entity);

    @Mapping(target = "idHoaDon", ignore = true)
    @Mapping(target = "phieuDatBan", ignore = true)
    @Mapping(target = "nhanVien", ignore = true)
    @Mapping(target = "khachHang", ignore = true)
    @Mapping(target = "khuyenMai", ignore = true)
    @Mapping(target = "danhSachChiTiet", ignore = true)
    @Mapping(target = "danhSachThuePhi", ignore = true)
    @Mapping(target = "phuongThucThanhToan", ignore = true)
    @Mapping(target = "thoiGianYeuCau", ignore = true)
    @Mapping(target = "thoiGianThanhToan", ignore = true)
    @Mapping(target = "thoiGianXoa", constant = "0L")
    @Mapping(target = "trangThai", constant = "CHO_XAC_NHAN")
    @Mapping(target = "thoiGianTao", expression = "java(java.time.LocalDateTime.now())")

    // Đảm bảo các trường tài chính khởi tạo là ZERO
    @Mapping(target = "tongTienHang", constant = "0")
    @Mapping(target = "giamGiaKhuyenMai", constant = "0")
    @Mapping(target = "giamGiaThanhVien", constant = "0")
    @Mapping(target = "tongTienThue", constant = "0")
    @Mapping(target = "tongThanhToan", constant = "0")
    @Mapping(target = "thongTinChiTiet", ignore = true)
    @Mapping(target = "diemSuDung", source = "diemSuDung", defaultValue = "0")
    HoaDon toEntity(HoaDonRequest request);
}
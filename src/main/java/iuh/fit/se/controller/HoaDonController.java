package iuh.fit.se.controller;

import iuh.fit.se.dto.chitiethoadon.ChiTietHoaDonRequest;
import iuh.fit.se.dto.hoadon.*;
import iuh.fit.se.enums.LoaiDonHang;
import iuh.fit.se.enums.PhuongThucThanhToan;
import iuh.fit.se.enums.TrangThaiHoaDon;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.service.HoaDonService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {

    private final HoaDonService hoaDonService;

    // Constructor
    public HoaDonController(HoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    @PostMapping("/tao-moi")
    public ResponseEntity<HoaDonResponse> openOrder(@RequestBody @Valid OrderRequestWrapper wrapper) {
        HoaDonResponse response = hoaDonService.taoDonHangMoi(wrapper.request(), wrapper.chiTiets());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HoaDonResponse> getDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(hoaDonService.layChiTiet(id));
    }

    @PutMapping("/{id}/them-mon")
    public ResponseEntity<HoaDonResponse> themMon(@PathVariable Integer id, @RequestBody List<ChiTietHoaDonRequest> requests) {
        return ResponseEntity.ok(hoaDonService.themMonVaoHoaDon(id, requests));
    }

    @PatchMapping("/{id}/sua-mon/{idChiTiet}")
    public ResponseEntity<HoaDonResponse> suaMon(
            @PathVariable Integer id,
            @PathVariable Integer idChiTiet,
            @RequestBody ChiTietHoaDonRequest request) {
        return ResponseEntity.ok(hoaDonService.suaChiTietMon(id, idChiTiet, request));
    }

    @PostMapping("/{id}/xuat-tam-tinh")
    public ResponseEntity<HoaDonResponse> xuatTamTinh(
            @PathVariable Integer id,
            @RequestBody @Valid ThanhToanRequest request) {
        return ResponseEntity.ok(hoaDonService.xuatHoaDonTamTinh(id, request));
    }

    @PostMapping("/{id}/xac-nhan-thanh-toan")
    public ResponseEntity<HoaDonResponse> xacNhanThanhToan(
            @PathVariable Integer id,
            @RequestBody @Valid ThanhToanRequest request) {
        return ResponseEntity.ok(hoaDonService.xacNhanThanhToan(id, request));
    }

    @PostMapping("/{id}/hoan-tat")
    public ResponseEntity<Void> hoanTat(@PathVariable Integer id) {
        hoaDonService.hoanTatDonHang(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/huy")
    public ResponseEntity<Void> cancel(@PathVariable Integer id) {
        hoaDonService.huyHoaDon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<HoaDonResponse>> getAll() {
        return ResponseEntity.ok(hoaDonService.layTatCaHoaDon());
    }

    @GetMapping("/loc-theo-khoang-ngay")
    public ResponseEntity<List<HoaDonResponse>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {

        if (tuNgay.isAfter(denNgay)) {
            throw new BadRequestException("Ngày bắt đầu không được sau ngày kết thúc!");
        }

        return ResponseEntity.ok(hoaDonService.layHoaDonTrongKhoangNgay(tuNgay, denNgay));
    }

    @PutMapping("/{id}/thue-phi")
    public ResponseEntity<String> updateThuePhi(
            @PathVariable Integer id,
            @RequestBody List<Integer> idThuePhis) {
        hoaDonService.capNhatDanhSachThuePhi(id, idThuePhis);
        return ResponseEntity.ok("Cập nhật thuế phí thành công!");
    }

    @PatchMapping("/{id}/yeu-cau-thanh-toan")
    public ResponseEntity<HoaDonResponse> yeuCauThanhToan(@PathVariable Integer id) {
        return ResponseEntity.ok(hoaDonService.yeuCauThanhToan(id));
    }

    @GetMapping("/loc-theo-loai")
    public ResponseEntity<List<HoaDonResponse>> locTheoLoai(@RequestParam LoaiDonHang loai) {
        return ResponseEntity.ok(hoaDonService.layHoaDonTheoLoai(loai));
    }

    @DeleteMapping("/{id}/xoa-mon/{idChiTiet}")
    public ResponseEntity<HoaDonResponse> xoaMon(
            @PathVariable Integer id,
            @PathVariable Integer idChiTiet) {
        return ResponseEntity.ok(hoaDonService.xoaMonKhoiHoaDon(id, idChiTiet));
    }

    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<HoaDonResponse> capNhatTrangThai(
            @PathVariable Integer id,
            @RequestParam TrangThaiHoaDon trangThai) {
        return ResponseEntity.ok(hoaDonService.capNhatTrangThai(id, trangThai));
    }
}

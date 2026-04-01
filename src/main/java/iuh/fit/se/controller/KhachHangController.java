package iuh.fit.se.controller;

import iuh.fit.se.dto.khachhang.KhachHangRequest;
import iuh.fit.se.dto.khachhang.KhachHangResponse;
import iuh.fit.se.enums.TrangThaiKhachHang;
import iuh.fit.se.service.KhachHangService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {

    private final KhachHangService khachHangService;

    // Constructor
    public KhachHangController(KhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    @GetMapping
    public ResponseEntity<List<KhachHangResponse>> getAll() {
        return ResponseEntity.ok(khachHangService.layTatCa());
    }

    @PostMapping
    public ResponseEntity<KhachHangResponse> create(@RequestBody @Valid KhachHangRequest request) {
        return new ResponseEntity<>(khachHangService.taoMoi(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KhachHangResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid KhachHangRequest request) {
        return ResponseEntity.ok(khachHangService.capNhat(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        khachHangService.xoa(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cong-diem")
    public ResponseEntity<Void> addPoints(
            @PathVariable Integer id,
            @RequestParam Integer diem) {
        khachHangService.congDiem(id, diem);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Integer id,
            @RequestParam TrangThaiKhachHang status) {
        khachHangService.capNhatTrangThai(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<KhachHangResponse> getDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(khachHangService.layChiTiet(id));
    }
}
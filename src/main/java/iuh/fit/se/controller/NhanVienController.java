package iuh.fit.se.controller;

import iuh.fit.se.dto.nhanvien.NhanVienRequest;
import iuh.fit.se.dto.nhanvien.NhanVienResponse;
import iuh.fit.se.enums.TrangThaiNhanVien;
import iuh.fit.se.enums.VaiTroNhanVien;
import iuh.fit.se.service.NhanVienService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/nhan-vien")
public class NhanVienController {

    private final NhanVienService nhanVienService;

    public NhanVienController(NhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    @GetMapping
    public ResponseEntity<List<NhanVienResponse>> getAll() {
        return ResponseEntity.ok(nhanVienService.layTatCaNhanVien());
    }

    @GetMapping("/cho-duyet")
    public ResponseEntity<List<NhanVienResponse>> getPending() {
        return ResponseEntity.ok(nhanVienService.layNhanVienChoDuyet());
    }

    @GetMapping("/van-hanh")
    public ResponseEntity<List<NhanVienResponse>> getOperatingList() {
        return ResponseEntity.ok(nhanVienService.layDanhSachVanHanh());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NhanVienResponse> getDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(nhanVienService.layChiTiet(id));
    }

    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Integer id,
            @RequestParam TrangThaiNhanVien status) {
        nhanVienService.capNhatTrangThai(id, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        nhanVienService.xoaNhanVien(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NhanVienResponse> updateProfile(
            @PathVariable Integer id,
            @RequestPart("request") @Valid NhanVienRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        return ResponseEntity.ok(nhanVienService.capNhatThongTin(id, request, file));
    }

    @PatchMapping("/{id}/vai-tro")
    public ResponseEntity<Void> changeRole(
            @PathVariable Integer id,
            @RequestParam VaiTroNhanVien role) {
        nhanVienService.doiVaiTro(id, role);
        return ResponseEntity.noContent().build();
    }
}
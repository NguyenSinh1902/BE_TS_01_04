package iuh.fit.se.controller;

import iuh.fit.se.dto.khuyenmai.KhuyenMaiRequest;
import iuh.fit.se.dto.khuyenmai.KhuyenMaiResponse;
import iuh.fit.se.service.KhuyenMaiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/khuyen-mai")
public class KhuyenMaiController {

    private final KhuyenMaiService khuyenMaiService;

    // Constructor
    public KhuyenMaiController(KhuyenMaiService khuyenMaiService) {
        this.khuyenMaiService = khuyenMaiService;
    }

    @GetMapping
    public ResponseEntity<List<KhuyenMaiResponse>> getAll() {
        return ResponseEntity.ok(khuyenMaiService.layTatCa());
    }

    @GetMapping("/dang-hoat-dong")
    public ResponseEntity<List<KhuyenMaiResponse>> getActivePromotions() {
        return ResponseEntity.ok(khuyenMaiService.layDanhSachHoatDong());
    }

    @GetMapping("/kiem-tra")
    public ResponseEntity<KhuyenMaiResponse> checkVoucher(
            @RequestParam String maCode,
            @RequestParam BigDecimal tongTien) {
        return ResponseEntity.ok(khuyenMaiService.kiemTraMaCode(maCode, tongTien));
    }

    @PostMapping
    public ResponseEntity<KhuyenMaiResponse> create(@RequestBody @Valid KhuyenMaiRequest request) {
        return new ResponseEntity<>(khuyenMaiService.taoMoi(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KhuyenMaiResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid KhuyenMaiRequest request) {
        return ResponseEntity.ok(khuyenMaiService.capNhat(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        khuyenMaiService.xoa(id);
        return ResponseEntity.noContent().build();
    }
}
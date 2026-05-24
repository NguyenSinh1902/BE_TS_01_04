package iuh.fit.se.controller;

import iuh.fit.se.dto.thongke.*;
import iuh.fit.se.service.ThongKeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/thong-ke")
public class ThongKeController {

    private final ThongKeService thongKeService;

    public ThongKeController(ThongKeService thongKeService) {
        this.thongKeService = thongKeService;
    }

    // 1. GET /api/thong-ke/tong-quan-hom-nay
    @GetMapping("/tong-quan-hom-nay")
    public ResponseEntity<TongQuanHomNayResponse> getTongQuanHomNay() {
        return ResponseEntity.ok(thongKeService.getTongQuanHomNay());
    }

    // 2. GET /api/thong-ke/bieu-do-hom-nay
    @GetMapping("/bieu-do-hom-nay")
    public ResponseEntity<BieuDoHomNayResponse> getBieuDoHomNay() {
        return ResponseEntity.ok(thongKeService.getBieuDoHomNay());
    }

    // 3. GET /api/thong-ke/chi-tiet?tuNgay=2026-05-01&denNgay=2026-05-14
    @GetMapping("/chi-tiet")
    public ResponseEntity<ChiTietThongKeResponse> getChiTietTheoThoiGian(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        return ResponseEntity.ok(thongKeService.getChiTietTheoThoiGian(tuNgay, denNgay));
    }

    // 4. GET /api/thong-ke/bieu-do-doanh-thu?tuNgay=2026-05-01&denNgay=2026-05-14&donVi=ngay
    @GetMapping("/bieu-do-doanh-thu")
    public ResponseEntity<List<BieuDoDoanhThuResponse>> getBieuDoDoanhThu(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(defaultValue = "ngay") String donVi) {
        return ResponseEntity.ok(thongKeService.getBieuDoDoanhThuTheoThoiGian(tuNgay, denNgay, donVi));
    }

    // 5. GET /api/thong-ke/top-san-pham?tuNgay=2026-05-01&denNgay=2026-05-14
    @GetMapping("/top-san-pham")
    public ResponseEntity<TopSanPhamResponse> getTopSanPham(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        return ResponseEntity.ok(thongKeService.getTopSanPhamTheoThoiGian(tuNgay, denNgay));
    }

    // 6. GET /api/thong-ke/phuong-thuc-thanh-toan?tuNgay=2026-05-01&denNgay=2026-05-23
    @GetMapping("/phuong-thuc-thanh-toan")
    public ResponseEntity<List<PhuongThucThanhToanResponse>> getPhuongThucThanhToan(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        return ResponseEntity.ok(thongKeService.getThongKePhuongThucThanhToan(tuNgay, denNgay));
    }
}
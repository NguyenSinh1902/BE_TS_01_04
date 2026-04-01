package iuh.fit.se.controller;

import iuh.fit.se.dto.sanpham.HomeResponse;
import iuh.fit.se.dto.sanpham.SanPhamRequest;
import iuh.fit.se.dto.sanpham.SanPhamResponse;
import iuh.fit.se.service.SanPhamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/san-pham")
public class SanPhamController {

    private final SanPhamService sanPhamService;

    // Constructor
    public SanPhamController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @GetMapping
    public ResponseEntity<List<SanPhamResponse>> getAll() {
        return ResponseEntity.ok(sanPhamService.layTatCa());
    }

    @PostMapping
    public ResponseEntity<SanPhamResponse> create(@RequestBody @Valid SanPhamRequest request) {
        SanPhamResponse response = sanPhamService.taoMoi(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SanPhamResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid SanPhamRequest request) {
        return ResponseEntity.ok(sanPhamService.capNhat(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        sanPhamService.xoa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/menu")
    public ResponseEntity<List<SanPhamResponse>> getMenu() {
        return ResponseEntity.ok(sanPhamService.layMenuChinh());
    }

    @GetMapping("/toppings")
    public ResponseEntity<List<SanPhamResponse>> getToppings() {
        return ResponseEntity.ok(sanPhamService.layDanhSachTopping());
    }

    @GetMapping("/danh-muc/{id}")
    public ResponseEntity<List<SanPhamResponse>> getByDanhMuc(@PathVariable Integer id) {
        return ResponseEntity.ok(sanPhamService.layTheoDanhMuc(id));
    }

    @GetMapping("/home")
    public ResponseEntity<HomeResponse> getHomeData() {
        return ResponseEntity.ok(sanPhamService.layDuLieuTrangChu());
    }
}
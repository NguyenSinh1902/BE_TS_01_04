package iuh.fit.se.controller;

import iuh.fit.se.dto.sanpham.HomeResponse;
import iuh.fit.se.dto.sanpham.SanPhamRequest;
import iuh.fit.se.dto.sanpham.SanPhamResponse;
import iuh.fit.se.service.SanPhamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SanPhamResponse> create(
            @RequestPart("request") @Valid SanPhamRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        SanPhamResponse response = sanPhamService.taoMoi(request, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SanPhamResponse> update(
            @PathVariable Integer id,
            @RequestPart("request") @Valid SanPhamRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        return ResponseEntity.ok(sanPhamService.capNhat(id, request, file));
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
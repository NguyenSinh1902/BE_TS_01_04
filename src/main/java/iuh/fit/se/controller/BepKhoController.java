package iuh.fit.se.controller;

import iuh.fit.se.dto.bep.CheBienRequest;
import iuh.fit.se.dto.bep.NguyenLieuRequest;
import iuh.fit.se.dto.bep.NguyenLieuResponse;
import iuh.fit.se.service.NguyenLieuService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bep-kho")
public class BepKhoController {

    private final NguyenLieuService nguyenLieuService;

    public BepKhoController(NguyenLieuService nguyenLieuService) {
        this.nguyenLieuService = nguyenLieuService;
    }

    @GetMapping("/nguyen-lieu")
    public ResponseEntity<List<NguyenLieuResponse>> layKho(@RequestParam(required = false) iuh.fit.se.enums.LoaiNguyenLieu loaiNguyenLieu) {
        if (loaiNguyenLieu != null) {
            return ResponseEntity.ok(nguyenLieuService.layKhoNguyenLieuTheoLoai(loaiNguyenLieu));
        }
        return ResponseEntity.ok(nguyenLieuService.layKhoNguyenLieu());
    }

    @PostMapping("/nguyen-lieu")
    public ResponseEntity<NguyenLieuResponse> themMoi(@Valid @RequestBody NguyenLieuRequest request) {
        return ResponseEntity.ok(nguyenLieuService.themNguyenLieu(request));
    }

    @PutMapping("/nguyen-lieu/{id}")
    public ResponseEntity<NguyenLieuResponse> capNhat(@PathVariable Integer id, @Valid @RequestBody NguyenLieuRequest request) {
        return ResponseEntity.ok(nguyenLieuService.capNhatNguyenLieu(id, request));
    }

    @PatchMapping("/nguyen-lieu/{id}/nhap-them")
    public ResponseEntity<NguyenLieuResponse> nhapThem(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        java.math.BigDecimal soLuongNhap = new java.math.BigDecimal(body.get("soLuongNhap").toString());
        return ResponseEntity.ok(nguyenLieuService.nhapThemHang(id, soLuongNhap));
    }

    @PostMapping("/nguyen-lieu/xuat-huy")
    public ResponseEntity<Map<String, String>> xuatHuy(@RequestBody Map<String, Object> body) {
        Integer idNguyenLieu = Integer.valueOf(body.get("idNguyenLieu").toString());
        java.math.BigDecimal soLuongHuy = new java.math.BigDecimal(body.get("soLuongHuy").toString());
        String lyDo = body.getOrDefault("lyDo", "").toString();
        
        nguyenLieuService.xuatHuyKho(idNguyenLieu, soLuongHuy, lyDo);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Xuất hủy nguyên liệu thành công và đã lưu lịch sử!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/che-bien")
    public ResponseEntity<Map<String, String>> cheBien(@Valid @RequestBody CheBienRequest request) {
        nguyenLieuService.cheBienBanThanhPham(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Chế biến bán thành phẩm thành công, hệ thống đã cập nhật khấu hao kho thô!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/san-pham/{id}/ton-ly")
    public ResponseEntity<Map<String, Object>> layTonLySanPham(@PathVariable Integer id) {
        return ResponseEntity.ok(nguyenLieuService.tinhSoLuongLyConLai(id));
    }

    @PatchMapping("/don-chi-tiet/{id}/xong-mon")
    public ResponseEntity<Map<String, String>> xongMon(@PathVariable Integer id) {
        nguyenLieuService.bepXongMon(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Xác nhận xong món! Đã tự động trừ kho thành phẩm và đồng bộ Realtime.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/config/recipe-chuyen-doi")
    public ResponseEntity<Map<String, String>> caidatRecipeChuyenDoi(@RequestBody Map<String, Object> body) {
        Integer idBanThanhPham = Integer.valueOf(body.get("idBanThanhPham").toString());
        Integer idNguyenLieuTho = Integer.valueOf(body.get("idNguyenLieuTho").toString());
        java.math.BigDecimal hamLuongCan = new java.math.BigDecimal(body.get("hamLuongCan").toString());
        
        java.math.BigDecimal soLuongThuDuoc = java.math.BigDecimal.ONE;
        if (body.containsKey("soLuongThuDuoc")) {
            soLuongThuDuoc = new java.math.BigDecimal(body.get("soLuongThuDuoc").toString());
        }
        
        nguyenLieuService.caiDatRecipeChuyenDoi(idBanThanhPham, idNguyenLieuTho, hamLuongCan, soLuongThuDuoc);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cấu hình công thức chuyển đổi bán thành phẩm thành công!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/config/dinh-luong-mon-an")
    public ResponseEntity<Map<String, String>> caidatDinhLuongMonAn(@RequestBody Map<String, Object> body) {
        Integer idBienThe = Integer.valueOf(body.get("idBienThe").toString());
        Integer idNguyenLieu = Integer.valueOf(body.get("idNguyenLieu").toString());
        java.math.BigDecimal soLuongTieuHao = new java.math.BigDecimal(body.get("soLuongTieuHao").toString());
        
        nguyenLieuService.caiDatDinhLuongMonAn(idBienThe, idNguyenLieu, soLuongTieuHao);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cấu hình định lượng tiêu hao món ăn thành công!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/config/recipe-chuyen-doi/{idBanThanhPham}")
    public ResponseEntity<List<Map<String, Object>>> layRecipe(@PathVariable Integer idBanThanhPham) {
        return ResponseEntity.ok(nguyenLieuService.layRecipeChuyenDoi(idBanThanhPham));
    }

    @GetMapping("/config/dinh-luong-mon-an/{idBienThe}")
    public ResponseEntity<List<Map<String, Object>>> layDinhLuong(@PathVariable Integer idBienThe) {
        return ResponseEntity.ok(nguyenLieuService.layDinhLuongMonAn(idBienThe));
    }

    @GetMapping("/config/dinh-luong-mon-an")
    public ResponseEntity<List<Map<String, Object>>> layTatCaDinhLuong() {
        return ResponseEntity.ok(nguyenLieuService.layTatCaDinhLuongMonAn());
    }

    @DeleteMapping("/config/recipe-chuyen-doi/{id}")
    public ResponseEntity<Map<String, String>> xoaRecipe(@PathVariable Integer id) {
        nguyenLieuService.xoaRecipeChuyenDoi(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã xóa công thức thành công!");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/config/dinh-luong-mon-an/{id}")
    public ResponseEntity<Map<String, String>> xoaDinhLuong(@PathVariable Integer id) {
        nguyenLieuService.xoaDinhLuongMonAn(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã xóa định lượng thành công!");
        return ResponseEntity.ok(response);
    }
}

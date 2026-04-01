package iuh.fit.se.controller;

import iuh.fit.se.dto.phieudatban.PhieuDatBanRequest;
import iuh.fit.se.dto.phieudatban.PhieuDatBanResponse;
import iuh.fit.se.service.PhieuDatBanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phieu-dat-ban")
public class PhieuDatBanController {

    private final PhieuDatBanService phieuDatBanService;

    public PhieuDatBanController(PhieuDatBanService phieuDatBanService) {
        this.phieuDatBanService = phieuDatBanService;
    }

    @PostMapping("/tao-moi")
    public ResponseEntity<PhieuDatBanResponse> taoMoi(@Valid @RequestBody PhieuDatBanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(phieuDatBanService.taoPhieuDatMoi(request));
    }

    @PutMapping("/{idPhieu}/doi-ban")
    public ResponseEntity<String> doiBan(
            @PathVariable Integer idPhieu,
            @RequestParam Integer idBanCu,
            @RequestParam Integer idBanMoi) {
        phieuDatBanService.doiBan(idPhieu, idBanCu, idBanMoi);
        return ResponseEntity.ok("Đổi bàn thành công từ " + idBanCu + " sang " + idBanMoi);
    }

    @PutMapping("/{idPhieu}/gop-them")
    public ResponseEntity<String> gopThemBan(
            @PathVariable Integer idPhieu,
            @RequestBody List<Integer> idBansMoi) {
        phieuDatBanService.gopThemBan(idPhieu, idBansMoi);
        return ResponseEntity.ok("Đã gộp thêm " + idBansMoi.size() + " bàn vào phiếu.");
    }

    @DeleteMapping("/{idPhieu}/huy")
    public ResponseEntity<Void> huyPhieu(@PathVariable Integer idPhieu) {
        phieuDatBanService.huyPhieu(idPhieu);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PhieuDatBanResponse>> getAll() {
        return ResponseEntity.ok(phieuDatBanService.layTatCaPhieu());
    }

    @GetMapping("/dang-hoat-dong")
    public ResponseEntity<List<PhieuDatBanResponse>> getActive() {
        return ResponseEntity.ok(phieuDatBanService.layPhieuDangHoatDong());
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<PhieuDatBanResponse> checkIn(@PathVariable Integer id) {
        return ResponseEntity.ok(phieuDatBanService.checkIn(id));
    }
}

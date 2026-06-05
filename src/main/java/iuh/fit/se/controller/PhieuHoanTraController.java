package iuh.fit.se.controller;

import iuh.fit.se.dto.hoantra.PhieuHoanTraRequest;
import iuh.fit.se.dto.hoantra.PhieuHoanTraResponse;
import iuh.fit.se.service.PhieuHoanTraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phieu-hoan-tra")
@CrossOrigin(origins = "*")
public class PhieuHoanTraController {

    private final PhieuHoanTraService phieuHoanTraService;

    public PhieuHoanTraController(PhieuHoanTraService phieuHoanTraService) {
        this.phieuHoanTraService = phieuHoanTraService;
    }

    @PostMapping
    public ResponseEntity<PhieuHoanTraResponse> taoPhieu(@Valid @RequestBody PhieuHoanTraRequest request) {
        return ResponseEntity.ok(phieuHoanTraService.taoPhieuHoanTra(request));
    }

    @PatchMapping("/{id}/phe-duyet")
    public ResponseEntity<PhieuHoanTraResponse> pheDuyet(
            @PathVariable Integer id, 
            @RequestParam boolean isDuyet) {
        return ResponseEntity.ok(phieuHoanTraService.pheDuyetPhieu(id, isDuyet));
    }

    @PatchMapping("/{id}/hoan-thanh")
    public ResponseEntity<PhieuHoanTraResponse> hoanThanh(@PathVariable Integer id) {
        return ResponseEntity.ok(phieuHoanTraService.hoanThanhPhieu(id));
    }

    @GetMapping
    public ResponseEntity<List<PhieuHoanTraResponse>> layTatCa() {
        return ResponseEntity.ok(phieuHoanTraService.layTatCaPhieu());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhieuHoanTraResponse> layChiTiet(@PathVariable Integer id) {
        return ResponseEntity.ok(phieuHoanTraService.layChiTietPhieu(id));
    }
}

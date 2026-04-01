package iuh.fit.se.controller;

import iuh.fit.se.dto.ban.BanRequest;
import iuh.fit.se.dto.ban.BanResponse;
import iuh.fit.se.service.BanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ban")
public class BanController {

    private final BanService banService;

    public BanController(BanService banService) {
        this.banService = banService;
    }

    @GetMapping
    public ResponseEntity<List<BanResponse>> layTatCa() {
        return ResponseEntity.ok(banService.layTatCaBan());
    }

    @PostMapping
    public ResponseEntity<BanResponse> taoMoi(@Valid @RequestBody BanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(banService.taoMoi(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BanResponse> capNhat(@PathVariable Integer id, @Valid @RequestBody BanRequest request) {
        return ResponseEntity.ok(banService.capNhat(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoa(@PathVariable Integer id) {
        banService.xoa(id);
        return ResponseEntity.noContent().build();
    }
}
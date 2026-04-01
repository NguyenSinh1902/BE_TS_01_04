package iuh.fit.se.controller;

import iuh.fit.se.dto.danhmuc.DanhMucRequest;
import iuh.fit.se.dto.danhmuc.DanhMucResponse;
import iuh.fit.se.service.DanhMucService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/danh-muc")
public class DanhMucController {

    private final DanhMucService danhMucService;

    public DanhMucController(DanhMucService danhMucService) {
        this.danhMucService = danhMucService;
    }

    @GetMapping
    public ResponseEntity<List<DanhMucResponse>> layTatCa() {
        return ResponseEntity.ok(danhMucService.layTatCa());
    }

    @PostMapping
    public ResponseEntity<DanhMucResponse> taoMoi(@Valid @RequestBody DanhMucRequest request) {
        return new ResponseEntity<>(danhMucService.taoMoi(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DanhMucResponse> capNhat(
            @PathVariable Integer id,
            @Valid @RequestBody DanhMucRequest request) {
        return ResponseEntity.ok(danhMucService.capNhat(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoa(@PathVariable Integer id) {
        danhMucService.xoa(id);
        return ResponseEntity.noContent().build();
    }
}
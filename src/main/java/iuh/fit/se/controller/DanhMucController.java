package iuh.fit.se.controller;

import iuh.fit.se.dto.danhmuc.DanhMucRequest;
import iuh.fit.se.dto.danhmuc.DanhMucResponse;
import iuh.fit.se.service.DanhMucService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DanhMucResponse> taoMoi(
            @RequestPart("request") @Valid DanhMucRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        return new ResponseEntity<>(danhMucService.taoMoi(request, file), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DanhMucResponse> capNhat(
            @PathVariable Integer id,
            @RequestPart("request") @Valid DanhMucRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        return ResponseEntity.ok(danhMucService.capNhat(id, request, file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoa(@PathVariable Integer id) {
        danhMucService.xoa(id);
        return ResponseEntity.noContent().build();
    }
}
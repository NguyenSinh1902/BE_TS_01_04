package iuh.fit.se.controller;

import iuh.fit.se.dto.goiy.GoiYSanPhamResponse;
import iuh.fit.se.service.GoiYSanPhamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/goi-y") // Đảm bảo đúng path này
public class GoiYSanPhamController {

    private final GoiYSanPhamService goiYSanPhamService;

    public GoiYSanPhamController(GoiYSanPhamService goiYSanPhamService) {
        this.goiYSanPhamService = goiYSanPhamService;
    }

    @GetMapping("/instant/{id}") // Chỗ này không được có dấu gạch chéo thừa
    public ResponseEntity<List<GoiYSanPhamResponse>> getInstantGoiY(@PathVariable Integer id) {
        return ResponseEntity.ok(goiYSanPhamService.layGoiYNgayLapTuc(id));
    }
}

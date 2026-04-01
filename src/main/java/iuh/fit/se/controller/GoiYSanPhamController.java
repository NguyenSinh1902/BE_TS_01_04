package iuh.fit.se.controller;

import iuh.fit.se.dto.goiy.GoiYSanPhamResponse;
import iuh.fit.se.service.GoiYSanPhamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goi-y")
public class GoiYSanPhamController {

    private final GoiYSanPhamService goiYSanPhamService;

    public GoiYSanPhamController(GoiYSanPhamService goiYSanPhamService) {
        this.goiYSanPhamService = goiYSanPhamService;
    }
    @GetMapping("/san-pham/{id}")
    public ResponseEntity<List<GoiYSanPhamResponse>> getSuggestions(@PathVariable Integer id) {
        List<GoiYSanPhamResponse> responses = goiYSanPhamService.layGoiYChoSanPham(id);

        return ResponseEntity.ok(responses);
    }
}
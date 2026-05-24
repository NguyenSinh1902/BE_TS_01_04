package iuh.fit.se.controller;

import iuh.fit.se.dto.nhatkyai.NhatKyAiResponse;
import iuh.fit.se.service.AiStrategyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ai-strategy")
public class AiStrategyController {

    private final AiStrategyService aiStrategyService;

    public AiStrategyController(AiStrategyService aiStrategyService) {
        this.aiStrategyService = aiStrategyService;
    }

    //phân tích và lưu nhật ký AI
    @PostMapping("/analyze")
    public ResponseEntity<NhatKyAiResponse> analyze(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay) {
        return ResponseEntity.ok(aiStrategyService.thucHienPhanTich(ngay));
    }

//    @GetMapping("/history")
//    public ResponseEntity<List<NhatKyAiResponse>> getHistory(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay) {
//        return ResponseEntity.ok(aiStrategyService.layLichSuTheoNgay(ngay));
//    }

    @GetMapping("/history")
    public ResponseEntity<List<NhatKyAiResponse>> getHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay) {
        return ResponseEntity.ok(aiStrategyService.layLichSuTheoNgay(ngay));
    }
}
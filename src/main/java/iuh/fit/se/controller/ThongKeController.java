package iuh.fit.se.controller;

import iuh.fit.se.dto.thongke.BieuDoResponse;
import iuh.fit.se.dto.thongke.DashboardResponse;
import iuh.fit.se.service.ThongKeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/thong-ke")
public class ThongKeController {

    private final ThongKeService thongKeService;

    public ThongKeController(ThongKeService thongKeService) {
        this.thongKeService = thongKeService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(thongKeService.getDashboardHomNay());
    }

    @GetMapping("/bieu-do-ngay")
    public ResponseEntity<List<BieuDoResponse>> getDailyChart() {
        return ResponseEntity.ok(thongKeService.getBieuDoTheoNgay());
    }
}

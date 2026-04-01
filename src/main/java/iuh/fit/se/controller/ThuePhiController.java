package iuh.fit.se.controller;

import iuh.fit.se.dto.thuephi.ThuePhiRequest;
import iuh.fit.se.dto.thuephi.ThuePhiResponse;
import iuh.fit.se.service.ThuePhiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thue-phi")
public class ThuePhiController {

    private final ThuePhiService thuePhiService;

    public ThuePhiController(ThuePhiService thuePhiService) {
        this.thuePhiService = thuePhiService;
    }

    @GetMapping
    public ResponseEntity<List<ThuePhiResponse>> getAll() {
        return ResponseEntity.ok(thuePhiService.layTatCa());
    }

    @PostMapping
    public ResponseEntity<ThuePhiResponse> create(@Valid @RequestBody ThuePhiRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(thuePhiService.taoMoi(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThuePhiResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody ThuePhiRequest request) {
        return ResponseEntity.ok(thuePhiService.capNhat(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        thuePhiService.xoa(id);
        return ResponseEntity.noContent().build();
    }
}
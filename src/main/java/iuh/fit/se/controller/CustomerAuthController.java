package iuh.fit.se.controller;

import iuh.fit.se.dto.auth.*;
import iuh.fit.se.entity.KhachHang;
import iuh.fit.se.service.CustomerAuthService;
import iuh.fit.se.config.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/khach-hang")
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;
    private final JwtUtil jwtUtil;

    public CustomerAuthController(CustomerAuthService customerAuthService, JwtUtil jwtUtil) {
        this.customerAuthService = customerAuthService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/check-sdt")
    public ResponseEntity<Map<String, String>> checkPhone(@Valid @RequestBody CheckPhoneRequest request) {
        String status = customerAuthService.checkPhone(request.soDienThoai());
        Map<String, String> response = new HashMap<>();
        response.put("status", status); // "EXISTS" or "NEW"
        return ResponseEntity.ok(response);
    }

    @PostMapping("/dang-ky")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody CustomerRegisterRequest request) {
        customerAuthService.register(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đăng ký thành công!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody CustomerLoginRequest request) {
        KhachHang kh = customerAuthService.login(request);

        String token = jwtUtil.generateToken(
                kh.getIdKhachHang(),
                kh.getSoDienThoai(), // Dùng SĐT làm subject
                "KHACH_HANG"         // Vai trò khách hàng
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", token);
        response.put("user", kh);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/request-otp")
    public ResponseEntity<Map<String, String>> requestOtp(@RequestParam String soDienThoai, @RequestParam String email) {
        customerAuthService.requestOtp(soDienThoai, email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Mã OTP đã được gửi về email của bạn");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody QuenMatKhauRequest request) {
        customerAuthService.resetPassword(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đổi mật khẩu thành công!");
        return ResponseEntity.ok(response);
    }
}

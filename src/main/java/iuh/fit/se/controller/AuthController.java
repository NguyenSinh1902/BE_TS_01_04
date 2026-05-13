package iuh.fit.se.controller;

import iuh.fit.se.dto.auth.*;
import iuh.fit.se.dto.nhanvien.NhanVienResponse;
import iuh.fit.se.service.AuthService;
import iuh.fit.se.config.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // Constructor
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<NhanVienResponse> register(@Valid @RequestBody DangKyRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/verify-register")
    public ResponseEntity<Map<String, String>> verifyRegister(@Valid @RequestBody VerifyRequest request) {
        authService.verifyRegister(request.email(), request.otp());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Xác thực thành công! Tài khoản đã sẵn sàng.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        NhanVienResponse nv = authService.login(request);

        String token = jwtUtil.generateToken(
                nv.idNhanVien(),
                nv.email(),
                nv.vaiTro().name()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", token);
        response.put("user", nv);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/request-otp")
    public ResponseEntity<Map<String, String>> requestOtp(@RequestParam String email) {
        authService.requestOtp(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Mã OTP đã được gửi về email của bạn");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody QuenMatKhauRequest request) {
        authService.resetPassword(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đổi mật khẩu thành công!");
        return ResponseEntity.ok(response);
    }
}
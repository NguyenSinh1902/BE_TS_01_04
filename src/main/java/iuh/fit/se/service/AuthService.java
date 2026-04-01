package iuh.fit.se.service;

import iuh.fit.se.dto.auth.*;
import iuh.fit.se.dto.nhanvien.NhanVienResponse;

public interface AuthService {
    NhanVienResponse register(DangKyRequest request);
    void verifyRegister(String email, String otp);
    NhanVienResponse login(LoginRequest request);
    void requestOtp(String email);
    void resetPassword(QuenMatKhauRequest request);
}
package iuh.fit.se.service;

import iuh.fit.se.dto.auth.CustomerLoginRequest;
import iuh.fit.se.dto.auth.CustomerRegisterRequest;
import iuh.fit.se.dto.auth.QuenMatKhauRequest;
import iuh.fit.se.entity.KhachHang;

public interface CustomerAuthService {
    String checkPhone(String soDienThoai);
    void register(CustomerRegisterRequest request);
    KhachHang login(CustomerLoginRequest request);
    void requestOtp(String soDienThoai, String email);
    void resetPassword(QuenMatKhauRequest request);
}

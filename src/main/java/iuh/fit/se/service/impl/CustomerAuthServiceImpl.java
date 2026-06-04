package iuh.fit.se.service.impl;

import iuh.fit.se.dto.auth.CustomerLoginRequest;
import iuh.fit.se.dto.auth.CustomerRegisterRequest;
import iuh.fit.se.dto.auth.QuenMatKhauRequest;
import iuh.fit.se.entity.KhachHang;
import iuh.fit.se.enums.TrangThaiKhachHang;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.repository.KhachHangRepository;
import iuh.fit.se.service.CustomerAuthService;
import iuh.fit.se.service.MailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class CustomerAuthServiceImpl implements CustomerAuthService {

    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public CustomerAuthServiceImpl(KhachHangRepository khachHangRepository,
                                   PasswordEncoder passwordEncoder,
                                   MailService mailService) {
        this.khachHangRepository = khachHangRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Override
    public String checkPhone(String soDienThoai) {
        return khachHangRepository.findBySoDienThoai(soDienThoai).isPresent() ? "EXISTS" : "NEW";
    }

    @Override
    public void register(CustomerRegisterRequest request) {
        if (khachHangRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("Email này đã được sử dụng!");
        }

        Optional<KhachHang> khOpt = khachHangRepository.findBySoDienThoai(request.soDienThoai());
        KhachHang kh;
        if (khOpt.isPresent()) {
            kh = khOpt.get();
            if (kh.getMatKhau() != null) {
                throw new BadRequestException("Số điện thoại này đã có tài khoản App!");
            }
            // Khách walk-in từng đến quán, giờ tải App -> chỉ cần đắp thêm thông tin
            kh.setEmail(request.email());
            kh.setMatKhau(passwordEncoder.encode(request.matKhau()));
            kh.setHoTen(request.hoTen());
            if (request.gioiTinh() != null) kh.setGioiTinh(request.gioiTinh());
        } else {
            // Khách mới toanh
            kh = new KhachHang();
            kh.setHoTen(request.hoTen());
            kh.setSoDienThoai(request.soDienThoai());
            kh.setEmail(request.email());
            kh.setMatKhau(passwordEncoder.encode(request.matKhau()));
            kh.setGioiTinh(request.gioiTinh());
        }
        khachHangRepository.save(kh);
    }

    @Override
    public KhachHang login(CustomerLoginRequest request) {
        KhachHang kh = khachHangRepository.findBySoDienThoai(request.soDienThoai())
                .orElseThrow(() -> new ResourceNotFoundException("Số điện thoại chưa được đăng ký!"));

        if (kh.getMatKhau() == null) {
            throw new BadRequestException("Tài khoản chưa có mật khẩu, vui lòng chọn 'Kích hoạt tài khoản' để đặt mật khẩu.");
        }

        if (!passwordEncoder.matches(request.matKhau(), kh.getMatKhau())) {
            throw new BadRequestException("Mật khẩu không chính xác!");
        }

        if (kh.getTrangThai() == TrangThaiKhachHang.BI_KHOA) {
            throw new BadRequestException("Tài khoản của bạn đã bị khóa, vui lòng liên hệ quán!");
        }

        return kh;
    }

    @Override
    public void requestOtp(String soDienThoai, String email) {
        KhachHang kh = khachHangRepository.findBySoDienThoai(soDienThoai)
                .orElseThrow(() -> new ResourceNotFoundException("Số điện thoại chưa được đăng ký!"));

        // Nếu khách hàng chưa có email (khách do thu ngân tạo)
        if (kh.getEmail() == null) {
            // Kiểm tra xem email mới này có bị ai khác xài chưa
            if (khachHangRepository.findByEmail(email).isPresent()) {
                throw new BadRequestException("Email này đã được tài khoản khác sử dụng!");
            }
            kh.setEmail(email); // Tạm thời link email này vào tài khoản
        } else if (!kh.getEmail().equals(email)) {
            throw new BadRequestException("Email không khớp với email đã đăng ký của tài khoản này!");
        }

        String otp = generateOtp();
        kh.setMaOtp(otp); // Lưu trực tiếp thay vì băm để vừa với độ dài 6 ký tự
        kh.setThoiHanOtp(LocalDateTime.now().plusMinutes(5));
        khachHangRepository.save(kh);

        mailService.guiOtp(kh.getEmail(), otp);
    }

    @Override
    public void resetPassword(QuenMatKhauRequest request) {
        KhachHang kh = khachHangRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Email chưa được đăng ký!"));

        if (kh.getThoiHanOtp() == null || kh.getThoiHanOtp().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP đã hết hạn, vui lòng yêu cầu lại!");
        }

        if (!request.otp().equals(kh.getMaOtp())) {
            throw new BadRequestException("Mã OTP không chính xác!");
        }

        kh.setMatKhau(passwordEncoder.encode(request.matKhauMoi()));
        kh.setMaOtp(null);
        kh.setThoiHanOtp(null);
        khachHangRepository.save(kh);
    }

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}

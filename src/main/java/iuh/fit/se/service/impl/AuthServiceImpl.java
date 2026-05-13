package iuh.fit.se.service.impl;

import iuh.fit.se.dto.auth.*;
import iuh.fit.se.dto.nhanvien.NhanVienResponse;
import iuh.fit.se.entity.NhanVien;
import iuh.fit.se.enums.*;
import iuh.fit.se.exception.*;
import iuh.fit.se.mapper.NhanVienMapper;
import iuh.fit.se.repository.NhanVienRepository;
import iuh.fit.se.service.AuthService;
import iuh.fit.se.service.MailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private final NhanVienRepository nhanVienRepository;
    private final NhanVienMapper nhanVienMapper;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public AuthServiceImpl(NhanVienRepository nhanVienRepository,
                           NhanVienMapper nhanVienMapper,
                           PasswordEncoder passwordEncoder,
                           MailService mailService) {
        this.nhanVienRepository = nhanVienRepository;
        this.nhanVienMapper = nhanVienMapper;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }


    @Override
    @Transactional
    public NhanVienResponse register(DangKyRequest request) {

        Optional<NhanVien> existingUser = nhanVienRepository.findActiveByEmail(request.email());

        if (existingUser.isPresent()) {
            NhanVien nv = existingUser.get();

            if (nv.getTrangThai() == TrangThaiNhanVien.HOAT_DONG) {
                throw new BadRequestException("Email đã tồn tại và đang hoạt động!");
            }
            if (nv.getTrangThai() == TrangThaiNhanVien.CHO_DUYET) {
                throw new BadRequestException("Tài khoản đã xác thực email, vui lòng chờ Admin phê duyệt.");
            }
            if (nv.getTrangThai() == TrangThaiNhanVien.BI_KHOA) {
                throw new BadRequestException("Tài khoản đã bị khóa!");
            }

            // Chỉ khi trạng thái là CHO_XAC_THUC (đăng ký chưa xong) thì mới cho lưu đè & gửi lại OTP mới
            return nhanVienMapper.toResponse(saveWithNewOtp(nv, request));
        }


        if (request.soDienThoai() != null && !request.soDienThoai().trim().isEmpty()) {
            if (nhanVienRepository.existsBySoDienThoaiAndThoiGianXoa(request.soDienThoai(), 0L)) {
                throw new BadRequestException("Số điện thoại này đã được đăng ký bởi nhân viên khác!");
            }
        }

        NhanVien moi = new NhanVien();
        moi.setEmail(request.email());
        moi.setTrangThai(TrangThaiNhanVien.CHO_XAC_THUC);
        moi.setEmailVerified(false);
        moi.setThoiGianXoa(0L);

        return nhanVienMapper.toResponse(saveWithNewOtp(moi, request));
    }

    private NhanVien saveWithNewOtp(NhanVien nv, DangKyRequest request) {

        nv.setMatKhau(passwordEncoder.encode(request.matKhau()));
        nv.setHoTen(request.hoTen());
        nv.setSoDienThoai(request.soDienThoai());

        if (request.vaiTro() != null) {
            if (request.vaiTro() == VaiTroNhanVien.ADMIN) {
                nv.setVaiTro(VaiTroNhanVien.PHUC_VU);
            } else {
                nv.setVaiTro(request.vaiTro());
            }
        } else if (nv.getVaiTro() == null) {
            nv.setVaiTro(VaiTroNhanVien.PHUC_VU);
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        nv.setMaOtp(otp);
        nv.setThoiHanOtp(LocalDateTime.now().plusMinutes(5));

        NhanVien saved = nhanVienRepository.save(nv);
        mailService.guiOtp(saved.getEmail(), otp);

        return saved;
    }

    @Override
    @Transactional
    public void verifyRegister(String email, String otp) {
        NhanVien nv = nhanVienRepository.findActiveByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin đăng ký"));

        if (nv.isEmailVerified()) {
            throw new BadRequestException("Email này đã được xác thực trước đó.");
        }

        if (nv.getMaOtp() == null || !nv.getMaOtp().equals(otp)) {
            throw new BadRequestException("Mã xác thực không chính xác");
        }

        if (nv.getThoiHanOtp().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã xác thực đã hết hạn, vui lòng thực hiện gửi lại mã");
        }

        nv.setEmailVerified(true);
        nv.setTrangThai(TrangThaiNhanVien.CHO_DUYET);
        nv.setMaOtp(null);
        nv.setThoiHanOtp(null);
        nhanVienRepository.save(nv);
    }

    @Override
    public NhanVienResponse login(LoginRequest request) {
        NhanVien nv = nhanVienRepository.findActiveByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Email hoặc mật khẩu không đúng"));

        if (!passwordEncoder.matches(request.matKhau(), nv.getMatKhau())) {
            throw new BadRequestException("Email hoặc mật khẩu không đúng");
        }

        switch (nv.getTrangThai()) {
            case CHO_XAC_THUC:
                throw new BadRequestException("Tài khoản chưa xác thực. Vui lòng kiểm tra email để lấy mã OTP!");
            case CHO_DUYET:
                throw new BadRequestException("Tài khoản đã xác thực thành công. Vui lòng đợi quản lý phê duyệt!");
            case BI_KHOA:
                throw new BadRequestException("Tài khoản của bạn đã bị khóa!");
            case HOAT_DONG:
                return nhanVienMapper.toResponse(nv);
            default:
                throw new BadRequestException("Trạng thái tài khoản không hợp lệ!");
        }
    }

    @Override
    @Transactional
    public void requestOtp(String email) {
        NhanVien nv = nhanVienRepository.findActiveByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy email"));

        if (nv.getTrangThai() == TrangThaiNhanVien.CHO_XAC_THUC) {
            throw new BadRequestException("Tài khoản này chưa hoàn tất đăng ký ban đầu!");
        }
        if (nv.getTrangThai() == TrangThaiNhanVien.BI_KHOA) {
            throw new BadRequestException("Tài khoản đã bị khóa, không thể lấy lại mật khẩu!");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        nv.setMaOtp(otp);
        nv.setThoiHanOtp(LocalDateTime.now().plusMinutes(5));

        nhanVienRepository.save(nv);
        mailService.guiOtp(email, otp);
    }

    @Override
    @Transactional
    public void resetPassword(QuenMatKhauRequest request) {

        NhanVien nv = nhanVienRepository.findActiveByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại"));

        if (nv.getMaOtp() == null || !nv.getMaOtp().equals(request.otp())) {
            throw new BadRequestException("Mã OTP không chính xác");
        }

        if (nv.getThoiHanOtp().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP đã hết hạn");
        }

        nv.setMatKhau(passwordEncoder.encode(request.matKhauMoi()));
        nv.setMaOtp(null);
        nv.setThoiHanOtp(null);
        nhanVienRepository.save(nv);
    }
}
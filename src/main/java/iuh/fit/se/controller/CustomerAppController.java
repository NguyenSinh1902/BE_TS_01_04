package iuh.fit.se.controller;

import iuh.fit.se.dto.khachhang.DatBanRequest;
import iuh.fit.se.entity.PhieuDatBan;
import iuh.fit.se.enums.TrangThaiDatBan;
import iuh.fit.se.repository.PhieuDatBanRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app-khach")
public class CustomerAppController {

    private final PhieuDatBanRepository phieuDatBanRepository;
    private final iuh.fit.se.repository.KhachHangRepository khachHangRepository;
    private final iuh.fit.se.service.FirebaseMessagingService firebaseMessagingService;
    private final iuh.fit.se.service.HoaDonService hoaDonService;
    private final iuh.fit.se.repository.HoaDonRepository hoaDonRepository;

    public CustomerAppController(PhieuDatBanRepository phieuDatBanRepository, 
                                 iuh.fit.se.repository.KhachHangRepository khachHangRepository,
                                 iuh.fit.se.service.FirebaseMessagingService firebaseMessagingService,
                                 iuh.fit.se.service.HoaDonService hoaDonService,
                                 iuh.fit.se.repository.HoaDonRepository hoaDonRepository) {
        this.phieuDatBanRepository = phieuDatBanRepository;
        this.khachHangRepository = khachHangRepository;
        this.firebaseMessagingService = firebaseMessagingService;
        this.hoaDonService = hoaDonService;
        this.hoaDonRepository = hoaDonRepository;
    }

    @PostMapping("/dat-ban")
    public ResponseEntity<Map<String, Object>> datBan(@Valid @RequestBody DatBanRequest request, org.springframework.security.core.Authentication auth) {
        PhieuDatBan phieu = new PhieuDatBan();
        
        String sdt = request.sdtKhachHang();
        String ten = request.tenKhachHang();
        
        if (sdt == null || sdt.trim().isEmpty() || ten == null || ten.trim().isEmpty()) {
            // Lấy SĐT từ Token
            String tokenSdt = auth.getName();
            iuh.fit.se.entity.KhachHang kh = khachHangRepository.findBySoDienThoai(tokenSdt).orElse(null);
            if (kh != null) {
                if (sdt == null || sdt.trim().isEmpty()) sdt = kh.getSoDienThoai();
                if (ten == null || ten.trim().isEmpty()) ten = kh.getHoTen();
            }
        }
        
        if (sdt == null || sdt.trim().isEmpty()) {
            throw new iuh.fit.se.exception.BadRequestException("Không xác định được Số điện thoại người đặt");
        }
        if (ten == null || ten.trim().isEmpty()) {
            throw new iuh.fit.se.exception.BadRequestException("Không xác định được Tên người đặt");
        }

        phieu.setTenKhachHang(ten);
        phieu.setSdtKhachHang(sdt);
        phieu.setThoiGianDat(request.thoiGianDat());
        phieu.setSoLuongNguoi(request.soLuongNguoi());
        phieu.setGhiChu(request.ghiChu());
        
        // Đơn đặt bàn qua app luôn bắt đầu bằng trạng thái CHO_XAC_NHAN
        phieu.setTrangThaiDat(TrangThaiDatBan.CHO_XAC_NHAN);
        
        phieuDatBanRepository.save(phieu);

        // Bắn FCM Notification cho Thu Ngân / Phục Vụ biết có đơn đặt bàn mới
        String title = "🔔 Yêu cầu đặt bàn mới!";
        String body = "Khách hàng " + ten + " (" + sdt + ") vừa đặt bàn cho " + request.soLuongNguoi() + " người lúc " + request.thoiGianDat().toString();
        firebaseMessagingService.sendNotificationToTopic("PHUC_VU", title, body);
        firebaseMessagingService.sendNotificationToTopic("THU_NGAN", title, body);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Gửi yêu cầu đặt bàn thành công! Vui lòng chờ quán xác nhận.");
        response.put("phieuDatBan", phieu);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lich-su-dat-ban")
    public ResponseEntity<List<PhieuDatBan>> getLichSuDatBan(
            @RequestParam(required = false) String sdt,
            org.springframework.security.core.Authentication auth) {
        
        if (sdt == null || sdt.trim().isEmpty()) {
            sdt = auth.getName(); // Lấy SĐT từ Token
        }
        
        List<PhieuDatBan> danhSach = phieuDatBanRepository.findBySdtKhachHangOrderByThoiGianDatDesc(sdt);
        return ResponseEntity.ok(danhSach);
    }

    @PostMapping("/dat-mon")
    public ResponseEntity<Map<String, Object>> datMon(
            @Valid @RequestBody iuh.fit.se.dto.khachhang.DatMonKhachRequest request,
            org.springframework.security.core.Authentication auth) {
        
        // Lấy SĐT từ Token để tìm idKhachHang
        String sdt = auth.getName();
        iuh.fit.se.entity.KhachHang kh = khachHangRepository.findBySoDienThoai(sdt).orElse(null);
        Integer idKhachHang = (kh != null) ? kh.getIdKhachHang() : null;

        // Gọi Service tạo Hóa đơn thật lưu vào Database
        iuh.fit.se.dto.hoadon.HoaDonResponse hdRes = hoaDonService.taoHoaDonKhachHang(request, idKhachHang);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Khởi tạo đơn hàng thành công!");
        
        if (request.phuongThucThanhToan() == iuh.fit.se.enums.PhuongThucThanhToan.CHUYEN_KHOAN) {
            response.put("vnpayRequired", true);
            response.put("idHoaDon", hdRes.idHoaDon());
        } else {
            response.put("vnpayRequired", false);
            response.put("idHoaDon", hdRes.idHoaDon());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/xac-nhan-nhan-hang/{idHoaDon}")
    public ResponseEntity<Map<String, Object>> xacNhanNhanHang(@PathVariable Integer idHoaDon, org.springframework.security.core.Authentication auth) {
        // Lấy SĐT từ Token
        String sdt = auth.getName();
        iuh.fit.se.entity.KhachHang kh = khachHangRepository.findBySoDienThoai(sdt).orElse(null);
        if (kh == null) {
            throw new iuh.fit.se.exception.BadRequestException("Khách hàng không tồn tại");
        }

        iuh.fit.se.entity.HoaDon hd = hoaDonRepository.findById(idHoaDon).orElseThrow(() -> new iuh.fit.se.exception.ResourceNotFoundException("Hóa đơn không tồn tại"));
        if (hd.getKhachHang() == null || !hd.getKhachHang().getIdKhachHang().equals(kh.getIdKhachHang())) {
            throw new iuh.fit.se.exception.BadRequestException("Bạn không có quyền xác nhận đơn hàng này");
        }

        iuh.fit.se.dto.hoadon.HoaDonResponse hdRes = hoaDonService.capNhatTrangThai(idHoaDon, iuh.fit.se.enums.TrangThaiHoaDon.HOAN_TAT);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đã xác nhận nhận hàng thành công!");
        response.put("idHoaDon", hdRes.idHoaDon());
        return ResponseEntity.ok(response);
    }
}

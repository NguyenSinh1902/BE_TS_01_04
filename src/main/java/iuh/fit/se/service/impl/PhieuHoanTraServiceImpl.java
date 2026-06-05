package iuh.fit.se.service.impl;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import iuh.fit.se.dto.hoantra.PhieuHoanTraRequest;
import iuh.fit.se.dto.hoantra.PhieuHoanTraResponse;
import iuh.fit.se.entity.*;
import iuh.fit.se.enums.TrangThaiHoaDon;
import iuh.fit.se.enums.TrangThaiPhieuHoanTra;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.mapper.PhieuHoanTraMapper;
import iuh.fit.se.repository.*;
import iuh.fit.se.service.FirebaseMessagingService;
import iuh.fit.se.service.PhieuHoanTraService;
import iuh.fit.se.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhieuHoanTraServiceImpl implements PhieuHoanTraService {

    private final PhieuHoanTraRepository phieuHoanTraRepository;
    private final HoaDonRepository hoaDonRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PhieuHoanTraMapper phieuHoanTraMapper;
    private final FirebaseMessagingService firebaseMessagingService;
    private final DinhLuongMonAnRepository dinhLuongMonAnRepository;
    private final LichSuXuatHuyRepository lichSuXuatHuyRepository;
    private final NguyenLieuRepository nguyenLieuRepository;

    public PhieuHoanTraServiceImpl(PhieuHoanTraRepository phieuHoanTraRepository, HoaDonRepository hoaDonRepository, NhanVienRepository nhanVienRepository, PhieuHoanTraMapper phieuHoanTraMapper, FirebaseMessagingService firebaseMessagingService, DinhLuongMonAnRepository dinhLuongMonAnRepository, LichSuXuatHuyRepository lichSuXuatHuyRepository, NguyenLieuRepository nguyenLieuRepository) {
        this.phieuHoanTraRepository = phieuHoanTraRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.phieuHoanTraMapper = phieuHoanTraMapper;
        this.firebaseMessagingService = firebaseMessagingService;
        this.dinhLuongMonAnRepository = dinhLuongMonAnRepository;
        this.lichSuXuatHuyRepository = lichSuXuatHuyRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
    }

    @Override
    @Transactional
    public PhieuHoanTraResponse taoPhieuHoanTra(PhieuHoanTraRequest request) {
        HoaDon hd = hoaDonRepository.findById(request.idHoaDon())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn"));

        if (hd.getTrangThai() == TrangThaiHoaDon.HOAN_TIEN) {
            throw new BadRequestException("Hóa đơn này đã được hoàn tiền rồi!");
        }

        Integer idThuNgan = SecurityUtils.getCurrentIdNhanVien();
        NhanVien thuNgan = idThuNgan != null ? nhanVienRepository.getReferenceById(idThuNgan) : null;

        PhieuHoanTra phieu = new PhieuHoanTra();
        phieu.setHoaDon(hd);
        phieu.setSoTienHoan(request.soTienHoan());
        phieu.setLyDo(request.lyDo());
        phieu.setTrangThai(TrangThaiPhieuHoanTra.CHO_DUYET);
        phieu.setThuNganTao(thuNgan);
        phieu.setThoiGianTao(LocalDateTime.now());

        PhieuHoanTra saved = phieuHoanTraRepository.save(phieu);

        // Bắn thông báo FCM cho Admin
        try {
            firebaseMessagingService.sendNotificationToTopic("admin", 
                "Cảnh báo: Yêu cầu hoàn tiền", 
                "Hóa đơn #" + hd.getIdHoaDon() + " yêu cầu hoàn " + request.soTienHoan() + "đ do: " + request.lyDo());
        } catch (Exception e) {
            System.err.println("FCM Error: " + e.getMessage());
        }

        // Cập nhật Firebase Realtime để các app khác thấy trạng thái chờ duyệt
        updateRealtime(saved);

        return phieuHoanTraMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PhieuHoanTraResponse pheDuyetPhieu(Integer id, boolean isDuyet) {
        PhieuHoanTra phieu = phieuHoanTraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phiếu hoàn trả không tồn tại"));

        if (phieu.getTrangThai() != TrangThaiPhieuHoanTra.CHO_DUYET) {
            throw new BadRequestException("Chỉ phiếu đang chờ duyệt mới có thể phê duyệt/từ chối");
        }

        Integer idAdmin = SecurityUtils.getCurrentIdNhanVien();
        NhanVien admin = idAdmin != null ? nhanVienRepository.getReferenceById(idAdmin) : null;

        phieu.setTrangThai(isDuyet ? TrangThaiPhieuHoanTra.DA_DUYET : TrangThaiPhieuHoanTra.TU_CHOI);
        phieu.setAdminDuyet(admin);
        phieu.setThoiGianDuyet(LocalDateTime.now());

        PhieuHoanTra saved = phieuHoanTraRepository.save(phieu);

        // Notify Cashier
        try {
            String statusStr = isDuyet ? "ĐÃ DUYỆT" : "TỪ CHỐI";
            firebaseMessagingService.sendNotificationToTopic("THU_NGAN", 
                "Kết quả hoàn tiền", 
                "Phiếu hoàn trả Hóa đơn #" + phieu.getHoaDon().getIdHoaDon() + " đã bị " + statusStr);
        } catch (Exception e) {}

        updateRealtime(saved);
        return phieuHoanTraMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PhieuHoanTraResponse hoanThanhPhieu(Integer id) {
        PhieuHoanTra phieu = phieuHoanTraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phiếu hoàn trả không tồn tại"));

        if (phieu.getTrangThai() != TrangThaiPhieuHoanTra.DA_DUYET) {
            throw new BadRequestException("Phiếu chưa được duyệt, không thể hoàn thành");
        }

        phieu.setTrangThai(TrangThaiPhieuHoanTra.HOAN_THANH);
        phieu.setThoiGianHoanThanh(LocalDateTime.now());

        HoaDon hd = phieu.getHoaDon();
        hd.setTrangThai(TrangThaiHoaDon.HOAN_TIEN);
        hoaDonRepository.save(hd);

        // Xử lý khấu hao kho
        for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
            List<DinhLuongMonAn> dinhLuongs = dinhLuongMonAnRepository.findByBienThe_IdBienThe(ct.getBienThe().getIdBienThe());
            for (DinhLuongMonAn dl : dinhLuongs) {
                java.math.BigDecimal tieuHao = dl.getSoLuongTieuHao().multiply(java.math.BigDecimal.valueOf(ct.getSoLuong()));
                LichSuXuatHuy ls = new LichSuXuatHuy(dl.getNguyenLieu(), tieuHao, "Khấu hao hoàn tiền Hóa đơn #" + hd.getIdHoaDon());
                lichSuXuatHuyRepository.save(ls);
            }
            
            // Topping
            if (ct.getDanhSachTopping() != null) {
                for (ChiTietHoaDonTopping tp : ct.getDanhSachTopping()) {
                    List<DinhLuongMonAn> dlToppings = dinhLuongMonAnRepository.findByBienThe_IdBienThe(tp.getBienTheTopping().getIdBienThe());
                    for (DinhLuongMonAn dlTp : dlToppings) {
                        java.math.BigDecimal tieuHaoTp = dlTp.getSoLuongTieuHao().multiply(java.math.BigDecimal.valueOf(ct.getSoLuong()));
                        LichSuXuatHuy lsTp = new LichSuXuatHuy(dlTp.getNguyenLieu(), tieuHaoTp, "Khấu hao topping hoàn tiền HD #" + hd.getIdHoaDon());
                        lichSuXuatHuyRepository.save(lsTp);
                    }
                }
            }
        }

        PhieuHoanTra saved = phieuHoanTraRepository.save(phieu);
        updateRealtime(saved);
        return phieuHoanTraMapper.toResponse(saved);
    }

    @Override
    public List<PhieuHoanTraResponse> layTatCaPhieu() {
        return phieuHoanTraRepository.findAll().stream()
                .map(phieuHoanTraMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PhieuHoanTraResponse layChiTietPhieu(Integer id) {
        PhieuHoanTra phieu = phieuHoanTraRepository.findById(id).orElseThrow();
        return phieuHoanTraMapper.toResponse(phieu);
    }

    private void updateRealtime(PhieuHoanTra phieu) {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("refund_orders").child(phieu.getIdPhieuHoanTra().toString());
            ref.child("idPhieu").setValueAsync(phieu.getIdPhieuHoanTra());
            ref.child("idHoaDon").setValueAsync(phieu.getHoaDon().getIdHoaDon());
            ref.child("trangThai").setValueAsync(phieu.getTrangThai().name());
            ref.child("soTienHoan").setValueAsync(phieu.getSoTienHoan().longValue());
            ref.child("lastUpdate").setValueAsync(System.currentTimeMillis());
        } catch (Exception e) {
            System.err.println("Firebase Realtime Error: " + e.getMessage());
        }
    }
}

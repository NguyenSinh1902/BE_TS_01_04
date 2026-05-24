package iuh.fit.se.service.impl;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import iuh.fit.se.dto.chitiethoadon.ChiTietHoaDonRequest;
import iuh.fit.se.dto.chitiethoadon.ChiTietHoaDonResponse;
import iuh.fit.se.dto.chitiethoadon.ToppingResponse;
import iuh.fit.se.dto.hoadon.*;
import iuh.fit.se.dto.thuephi.HoaDonThuePhiResponse;
import iuh.fit.se.entity.*;
import iuh.fit.se.enums.*;
import iuh.fit.se.exception.*;
import iuh.fit.se.mapper.HoaDonMapper;
import iuh.fit.se.repository.*;
import iuh.fit.se.service.*;
import iuh.fit.se.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final PhieuDatBanRepository phieuDatBanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;
    private final BienTheSanPhamRepository bienTheRepository;
    private final KhachHangService khachHangService;
    private final HoaDonMapper hoaDonMapper;
    private final KhuyenMaiService khuyenMaiService;
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final PhieuDatBanService phieuDatBanService;
    private final ChiTietDatBanRepository chiTietDatBanRepository;
    private final SanPhamRepository sanPhamRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ThuePhiRepository thuePhiRepository;
    private final FirebaseRealtimeService firebaseRealtimeService;
    private final FirebaseMessagingService firebaseMessagingService;

    public HoaDonServiceImpl(HoaDonRepository hoaDonRepository, PhieuDatBanRepository phieuDatBanRepository, NhanVienRepository nhanVienRepository, KhachHangRepository khachHangRepository, BienTheSanPhamRepository bienTheRepository, KhachHangService khachHangService, HoaDonMapper hoaDonMapper, KhuyenMaiService khuyenMaiService, KhuyenMaiRepository khuyenMaiRepository, PhieuDatBanService phieuDatBanService, ChiTietDatBanRepository chiTietDatBanRepository, SanPhamRepository sanPhamRepository, ThuePhiRepository thuePhiRepository, FirebaseRealtimeService firebaseRealtimeService, FirebaseMessagingService firebaseMessagingService) {
        this.hoaDonRepository = hoaDonRepository;
        this.phieuDatBanRepository = phieuDatBanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khachHangRepository = khachHangRepository;
        this.bienTheRepository = bienTheRepository;
        this.khachHangService = khachHangService;
        this.hoaDonMapper = hoaDonMapper;
        this.khuyenMaiService = khuyenMaiService;
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.phieuDatBanService = phieuDatBanService;
        this.chiTietDatBanRepository = chiTietDatBanRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.thuePhiRepository = thuePhiRepository;
        this.firebaseRealtimeService = firebaseRealtimeService;
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDonResponse> layTatCaHoaDon() {
        // Thay vì dùng findAll() mặc định, gọi hàm Native Query của mình
        return hoaDonRepository.findAllInvoicesIncludingDeleted()
                .stream()
                .map(this::mapToResponseFull)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDonResponse> layHoaDonTheoLoai(LoaiDonHang loai) {
        return hoaDonRepository.findByLoaiDonHang(loai)
                .stream()
                .map(this::mapToResponseFull)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDonResponse> layHoaDonTrongKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        // 00:00:00 của ngày bắt đầu
        LocalDateTime start = tuNgay.atStartOfDay();
        // 23:59:59 của ngày kết thúc
        LocalDateTime end = denNgay.atTime(LocalTime.MAX);

        return hoaDonRepository.findByDateRange(start, end)
                .stream()
                .map(this::mapToResponseFull)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HoaDonResponse taoDonHangMoi(HoaDonRequest request, List<ChiTietHoaDonRequest> chiTiets) {

        HoaDon hd = hoaDonMapper.toEntity(request);
        apDungThuePhiMacDinh(hd);

//        // LẤY ID TỪ SECURITY CONTEXT
//        Integer idThuNgan = SecurityUtils.getCurrentIdNhanVien();
//        if (idThuNgan == null) throw new AccessDeniedException("Vui lòng đăng nhập!");
//
//        // Gán thu ngân
//        hd.setThuNgan(nhanVienRepository.getReferenceById(idThuNgan));

        if (request.loaiDonHang() == LoaiDonHang.TAI_BAN) {
            if (request.idPhieuDat() == null) throw new BadRequestException("Đơn tại bàn phải có ID Phiếu đặt!");

            PhieuDatBan phieu = phieuDatBanRepository.findActiveById(request.idPhieuDat())
                    .orElseThrow(() -> new ResourceNotFoundException("Phiếu đặt không tồn tại hoặc đã bị hủy!"));

            Optional<HoaDon> hdCu = hoaDonRepository.findByPhieuDatBan_IdPhieuDatAndTrangThaiNot(
                    phieu.getIdPhieuDat(), TrangThaiHoaDon.DA_THANH_TOAN);
            if (hdCu.isPresent()) {
                throw new BadRequestException("Phiếu đặt này đã có hóa đơn đang chờ! Hãy dùng hàm cập nhật món.");
            }

            hd.setPhieuDatBan(phieu);
        }

        if (request.idKhachHang() != null) {
            hd.setKhachHang(khachHangRepository.findById(request.idKhachHang()).orElse(null));
        }

        napDanhSachMonVaoHoaDon(hd, chiTiets);
        tinhToanTaiChinh(hd);

        HoaDon savedHd = hoaDonRepository.save(hd);

        // REALTIME: Đẩy đơn mới lên màn hình Thu ngân/Bếp
        updateOrderRealtime(savedHd);

        // THÔNG BÁO: Báo cho topic 'staff' là có đơn hàng mới cần làm
        firebaseMessagingService.sendNotificationToTopic("staff", "Đơn hàng mới", "Bàn " + getTenBans(savedHd) + " vừa gọi món.");
        return this.layChiTiet(savedHd.getIdHoaDon());
    }

    private void apDungThuePhiMacDinh(HoaDon hd) {
        List<ThuePhi> dsMacDinh = thuePhiRepository.findByLaMacDinhTrue();
        for (ThuePhi tp : dsMacDinh) {
            hd.getDanhSachThuePhi().add(new HoaDonThuePhi(hd, tp.getTenThuePhi(), tp.getGiaTri(), tp.getLoaiGiaTri()));
        }
    }

    @Override
    @Transactional
    public void capNhatDanhSachThuePhi(Integer idHoaDon, List<Integer> idThuePhisChonThem) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại"));

        hd.getDanhSachThuePhi().clear();
        List<ThuePhi> dsChonThem = thuePhiRepository.findAllById(idThuePhisChonThem);

        for (ThuePhi tp : dsChonThem) {
            hd.getDanhSachThuePhi().add(new HoaDonThuePhi(hd, tp.getTenThuePhi(), tp.getGiaTri(), tp.getLoaiGiaTri()));
        }

        tinhToanTaiChinh(hd);
        hoaDonRepository.save(hd);
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDonResponse layChiTiet(Integer id) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại"));
        return mapToResponseFull(hd);
    }

    private HoaDonResponse mapToResponseFull(HoaDon hd) {
        HoaDonResponse res = hoaDonMapper.toResponse(hd);
        List<String> tenBans = new ArrayList<>();
        if (hd.getPhieuDatBan() != null) {
            tenBans = chiTietDatBanRepository.findByPhieuDatBan_IdPhieuDat(hd.getPhieuDatBan().getIdPhieuDat())
                    .stream()
                    .map(ct -> ct.getBan().getTenBan())
                    .collect(Collectors.toList());
        }

        List<HoaDonThuePhiResponse> dsThuePhiRes = hd.getDanhSachThuePhi().stream()
                .map(tp -> new HoaDonThuePhiResponse(
                        tp.getTenThuePhi(),
                        tp.getGiaTriTaiThoiDiemBan(),
                        tp.getLoaiGiaTri(),
                        tp.getSoTienQuyDoi()
                ))
                .collect(Collectors.toList());

        List<ChiTietHoaDonResponse> danhSachChiTietMoi = hd.getDanhSachChiTiet().stream().map(ct -> {
            List<ToppingResponse> toppingRes = ct.getDanhSachTopping().stream()
                    .map(tp -> new ToppingResponse(tp.getTopping().getTenSanPham(), tp.getGiaThoiDiemBan()))
                    .collect(Collectors.toList());

            BigDecimal tongGiaTopping = ct.getDanhSachTopping().stream()
                    .map(ChiTietHoaDonTopping::getGiaThoiDiemBan)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal thanhTienChuan = ct.getGiaThoiDiemBan().add(tongGiaTopping)
                    .multiply(BigDecimal.valueOf(ct.getSoLuong()))
                    .setScale(2, RoundingMode.HALF_UP);

            return new ChiTietHoaDonResponse(
                    ct.getIdChiTiet(),
                    ct.getBienThe().getIdBienThe(),
                    ct.getBienThe().getSanPham().getTenSanPham(),
                    ct.getBienThe().getTenKichCo(),
                    ct.getSoLuong(),
                    ct.getGiaThoiDiemBan().longValue(),
                    ct.getTuyChonJson(),
                    toppingRes,
                    thanhTienChuan.longValue()
            );
        }).collect(Collectors.toList());

        return new HoaDonResponse(
                res.idHoaDon(),
                res.idPhieuDat(),
                tenBans,
                res.loaiDonHang(),
                res.tenThuNgan(),
                res.tenPhucVu(),
                res.tenKhachHang(),
                res.maKhuyenMai(),
                res.tongTienHang(),
                res.giamGiaKhuyenMai(),
                res.giamGiaThanhVien(),
                res.diemSuDung(),
                dsThuePhiRes,
                res.tongTienThue(),
                res.tongThanhToan(),
                res.phuongThucThanhToan(),
                res.trangThai(),
                res.thoiGianTao(),
                res.thoiGianYeuCau(),
                res.thoiGianThanhToan(),
                res.thongTinChiTiet(),
                danhSachChiTietMoi
        );
    }

    @Override
    @Transactional
    public HoaDonResponse capNhatTrangThai(Integer idHoaDon, TrangThaiHoaDon trangThaiMoi) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại"));

        if (hd.getTrangThai() == TrangThaiHoaDon.HOAN_TAT || hd.getTrangThai() == TrangThaiHoaDon.DA_HUY) {
            throw new BadRequestException("Hóa đơn đã đóng hoặc đã hủy, không thể thay đổi trạng thái!");
        }

        hd.setTrangThai(trangThaiMoi);

        // CHẶN: Không cho phép cập nhật các trạng thái nghiệp vụ quan trọng qua hàm này
        List<TrangThaiHoaDon> forbiddenStates = List.of(
                TrangThaiHoaDon.DA_THANH_TOAN,
                TrangThaiHoaDon.HOAN_TAT,
                TrangThaiHoaDon.DA_HUY
        );

        if (forbiddenStates.contains(trangThaiMoi)) {
            throw new BadRequestException("Không thể cập nhật trạng thái này trực tiếp. Vui lòng thực hiện qua chức năng Thanh toán hoặc Hủy đơn!");
        }

        // 1. Nếu hoàn tất hóa đơn -> Giải phóng bàn (Realtime Database)
        if (trangThaiMoi == TrangThaiHoaDon.HOAN_TAT && hd.getPhieuDatBan() != null) {
            phieuDatBanService.hoanTatPhieu(hd.getPhieuDatBan().getIdPhieuDat());
        }

        // 2. THÔNG BÁO ĐẨY: Nếu món đã pha chế xong
        if (trangThaiMoi == TrangThaiHoaDon.CHO_LAY_MON) {
            String title = "☕ Món đã xong!";
            String body = "Đơn hàng tại bàn " + getTenBans(hd) + " đã pha chế xong. Hãy tới quầy lấy món!";

            // GIẢI PHÁP MỚI: Không thèm lấy idThuNgan nữa, gửi thông báo theo nhóm quyền (Topic)
            // Tất cả máy app chạy quyền PHUC_VU đăng ký vào topic này sẽ nhận được hết
            String topicName = "PHUC_VU";

            try {
                // Thay vì truyền fcmToken cá nhân, ông dùng hàm gửi theo Topic của Firebase
                firebaseMessagingService.sendNotificationToTopic(topicName, title, body);
            } catch (Exception e) {
                System.out.println("⚠️ Lỗi gửi thông báo Topic: " + e.getMessage());
            }
        }

        hoaDonRepository.save(hd);

        // 3. REALTIME: Cập nhật trạng thái đơn hàng để các máy khác thấy ngay
        updateOrderRealtime(hd);

        return layChiTiet(idHoaDon);
    }

    @Override
    @Transactional
    public void huyHoaDon(Integer id) {
        String finalHd1;
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại"));

        EnumSet<TrangThaiHoaDon> camHuy = EnumSet.of(
                TrangThaiHoaDon.DANG_PHA_CHE,
                TrangThaiHoaDon.CHO_LAY_MON,
                TrangThaiHoaDon.DANG_PHUC_VU,
                TrangThaiHoaDon.DA_THANH_TOAN,
                TrangThaiHoaDon.HOAN_TAT
        );

        if (camHuy.contains(hd.getTrangThai())) {
            throw new BadRequestException("Không thể hủy hóa đơn khi đang ở trạng thái: " + hd.getTrangThai());
        }

        if (hd.getPhieuDatBan() != null) {
            phieuDatBanService.giaiPhongBanKhiHuyHoaDon(hd.getPhieuDatBan().getIdPhieuDat());
        }

        hd.setTrangThai(TrangThaiHoaDon.DA_HUY);
        hd.setThoiGianXoa(System.currentTimeMillis());
        HoaDon finalHd = hoaDonRepository.save(hd);

        // --- 🌟 THÊM DÒNG NÀY ĐỂ REALTIME HOẠT ĐỘNG 🌟 ---
        // Hàm này sẽ đẩy trạng thái "DA_HUY" lên Firebase giúp danh sách đơn hàng bên FE tự biến mất/cập nhật lại
        updateOrderRealtime(finalHd);

        // 🔔 🚨 THÔNG BÁO KHẨN CẤP ĐẾN TOÀN BỘ CÁC BÊN (FCM)
        try {
            String tenBan = finalHd.getPhieuDatBan() != null ? getTenBans(finalHd) : "Mang về";
            String tieuDe = "🚨 CẢNH BÁO: HỦY ĐƠN HÀNG!";
            String noiDung = "Hóa đơn #" + finalHd.getIdHoaDon() + " tại " + tenBan + " đã bị HỦY!";

            firebaseMessagingService.sendNotificationToTopic("admin", tieuDe, noiDung);
            firebaseMessagingService.sendNotificationToTopic("THU_NGAN", tieuDe, noiDung);
            firebaseMessagingService.sendNotificationToTopic("PHUC_VU", tieuDe, noiDung);
        } catch (Exception e) {
            System.out.println("⚠️ Lỗi phát tán thông báo hủy đơn: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public HoaDonResponse themMonVaoHoaDon(Integer idHoaDon, List<ChiTietHoaDonRequest> requests) {
        HoaDon hd = hoaDonRepository.findByIdWithChiTietsAndToppings(idHoaDon)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại"));

        if (hd.getTrangThai().ordinal() >= TrangThaiHoaDon.CHO_THANH_TOAN.ordinal()) {
            throw new BadRequestException("Hóa đơn đã chốt thanh toán, không thể thêm món!");
        }

        napDanhSachMonVaoHoaDon(hd, requests);
        tinhToanTaiChinh(hd);

        HoaDon finalHd = hoaDonRepository.save(hd);

        // --- 🌟 THÊM DÒNG REALTIME CHIẾN LƯỢC NÀY 🌟 ---
        // Đẩy danh sách món mới + tổng tiền mới lên Firebase để màn hình Thu ngân tự nhảy số lập tức
        updateOrderRealtime(finalHd);

        return layChiTiet(idHoaDon);
    }

    @Override
    @Transactional
    public HoaDonResponse suaChiTietMon(Integer idHoaDon, Integer idChiTiet, ChiTietHoaDonRequest request) {
        HoaDon hd = hoaDonRepository.findByIdWithChiTietsAndToppings(idHoaDon)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại"));

        if (hd.getTrangThai().ordinal() >= TrangThaiHoaDon.CHO_THANH_TOAN.ordinal()) {
            throw new BadRequestException("Hóa đơn đã chốt, không thể chỉnh sửa món!");
        }

        ChiTietHoaDon ct = hd.getDanhSachChiTiet().stream()
                .filter(item -> item.getIdChiTiet().equals(idChiTiet))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Món này không có trong hóa đơn!"));

        BienTheSanPham btMoi = bienTheRepository.findById(request.idBienThe())
                .orElseThrow(() -> new ResourceNotFoundException("Biến thể mới không tồn tại"));
        ct.setBienThe(btMoi);
        ct.setGiaThoiDiemBan(btMoi.getGiaBan());
        ct.setSoLuong(request.soLuong());
        ct.setTuyChonJson(request.tuyChonJson());

        ct.getDanhSachTopping().clear();
        if (request.danhSachIdTopping() != null) {
            for (Integer idTp : request.danhSachIdTopping()) {
                BienTheSanPham btTp = bienTheRepository.findById(idTp).orElseThrow();
                ChiTietHoaDonTopping cttp = new ChiTietHoaDonTopping();
                cttp.setChiTietHoaDon(ct);
                cttp.setTopping(btTp.getSanPham());
                cttp.setBienTheTopping(btTp);
                cttp.setGiaThoiDiemBan(btTp.getGiaBan());
                ct.getDanhSachTopping().add(cttp);
            }
        }

        tinhToanTaiChinh(hd);
        hoaDonRepository.save(hd);
        return layChiTiet(idHoaDon);
    }

    @Override
    @Transactional
    public HoaDonResponse xoaMonKhoiHoaDon(Integer idHoaDon, Integer idChiTiet) {
        HoaDon hd = hoaDonRepository.findByIdWithChiTietsAndToppings(idHoaDon)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại"));

        if (hd.getTrangThai().ordinal() >= TrangThaiHoaDon.CHO_THANH_TOAN.ordinal()) {
            throw new BadRequestException("Hóa đơn đã chốt, không thể xóa món!");
        }

        boolean removed = hd.getDanhSachChiTiet().removeIf(ct -> ct.getIdChiTiet().equals(idChiTiet));

        if (!removed) {
            throw new ResourceNotFoundException("Không tìm thấy món cần xóa trong hóa đơn này!");
        }

        tinhToanTaiChinh(hd);
        hoaDonRepository.save(hd);

        return layChiTiet(idHoaDon);
    }

    @Override
    @Transactional
    public HoaDonResponse yeuCauThanhToan(Integer idHoaDon) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn"));

        if (hd.getTrangThai().ordinal() >= TrangThaiHoaDon.DA_THANH_TOAN.ordinal()) {
            throw new BadRequestException("Hóa đơn đã thanh toán hoặc đã đóng!");
        }

        hd.setTrangThai(TrangThaiHoaDon.CHO_THANH_TOAN);
        hd.setThoiGianYeuCau(LocalDateTime.now());

        HoaDon finalHd = hoaDonRepository.save(hd);
        hoaDonRepository.flush();

        // 🔔 THÔNG BÁO ĐẾN THU NGÂN ĐỂ IN BILL
        try {
            String tenBan = finalHd.getPhieuDatBan() != null ? getTenBans(finalHd) : "Mang về";
            firebaseMessagingService.sendNotificationToTopic("THU_NGAN",
                    "💵 Khách gọi thanh toán!",
                    tenBan + " đang yêu cầu thanh toán hóa đơn #" + finalHd.getIdHoaDon());
        } catch (Exception e) {
            System.out.println("⚠️ Lỗi thông báo yêu cầu thanh toán: " + e.getMessage());
        }

        return layChiTiet(idHoaDon);
    }

    @Override
    @Transactional
    public HoaDonResponse xuatHoaDonTamTinh(Integer id, ThanhToanRequest request) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn id = " + id));

        // LẤY ID THU NGÂN TỪ TOKEN
        Integer idThuNgan = SecurityUtils.getCurrentIdNhanVien();
        if (idThuNgan == null) throw new AccessDeniedException("Chưa đăng nhập thu ngân!");

        // Gán/Cập nhật thu ngân là người vừa nhấn xuất hóa đơn
        hd.setThuNgan(nhanVienRepository.getReferenceById(idThuNgan));

        if (hd.getTrangThai().ordinal() >= TrangThaiHoaDon.DA_THANH_TOAN.ordinal()) {
            throw new BadRequestException("Hóa đơn đã thanh toán, không thể thay đổi thông tin tạm tính!");
        }

        //GÁN KHÁCH HÀNG NẾU CÓ
        if (request.idKhachHang() != null) {
            KhachHang kh = khachHangRepository.findById(request.idKhachHang())
                    .orElseThrow(() -> new NotFoundException("Khách hàng không tồn tại"));
            hd.setKhachHang(kh);
        }

        capNhatCauHinhGiamGiaVaThuePhi(hd, request);
        tinhToanTaiChinh(hd);
        hd.setTrangThai(TrangThaiHoaDon.CHO_THANH_TOAN);

        return mapToResponseFull(hoaDonRepository.save(hd));
    }

    @Override
    @Transactional
    public HoaDonResponse xacNhanThanhToan(Integer id, ThanhToanRequest request) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn id = " + id));

        // LẤY ID THU NGÂN TỪ TOKEN
        Integer idThuNgan = SecurityUtils.getCurrentIdNhanVien();
        if (idThuNgan == null) throw new AccessDeniedException("Chưa đăng nhập thu ngân!");

        // Gán/Cập nhật thu ngân là người vừa nhấn xuất hóa đơn
        hd.setThuNgan(nhanVienRepository.getReferenceById(idThuNgan));

        if (hd.getTrangThai() == TrangThaiHoaDon.DA_THANH_TOAN || hd.getTrangThai() == TrangThaiHoaDon.HOAN_TAT) {
            throw new BadRequestException("Hóa đơn này đã được thanh toán rồi!");
        }

        // 1. Cập nhật khách hàng
        if (request.idKhachHang() != null) {
            KhachHang kh = khachHangRepository.findById(request.idKhachHang())
                    .orElseThrow(() -> new NotFoundException("Khách hàng không tồn tại"));
            hd.setKhachHang(kh);
        }

        // 2. Logic trừ điểm tích lũy
        if (request.diemSuDung() != null && request.diemSuDung() > 0) {
            KhachHang kh = hd.getKhachHang();
            if (kh == null) throw new BadRequestException("Khách vãng lai không thể sử dụng điểm!");

            if (kh.getDiemTichLuy() < request.diemSuDung()) {
                throw new BadRequestException("Khách hàng không đủ điểm để sử dụng!");
            }

            kh.setDiemTichLuy(kh.getDiemTichLuy() - request.diemSuDung());
            hd.setDiemSuDung(request.diemSuDung());
            khachHangRepository.save(kh);
        }

        // 3.Tính lại tiền
        capNhatCauHinhGiamGiaVaThuePhi(hd, request);
        tinhToanTaiChinh(hd);

        // 4. Cập nhật thông tin thanh toán
        hd.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        hd.setPhuongThucThanhToan(request.phuongThuc());
        hd.setThoiGianThanhToan(LocalDateTime.now());

        // 5. TÍCH ĐIỂM (Quy đổi: 50.000đ = 1 điểm)
        if (hd.getKhachHang() != null) {
            BigDecimal mocTichDiem = new BigDecimal("50000");
            int diemMoi = hd.getTongThanhToan().divide(mocTichDiem, 0, RoundingMode.DOWN).intValue();

            if (diemMoi > 0) {
                khachHangService.tichDiemVaThangHang(hd.getKhachHang().getIdKhachHang(), diemMoi);
            }
        }

        hd.setThongTinChiTiet(taoSnapshotText(hd));

        // 6. TỰ ĐỘNG GIẢI PHÓNG BÀN (REALTIME DATABASE)
        // Nếu là đơn tại bàn, gọi hàm hoanTatPhieu để đổi màu bàn sang XANH trên sơ đồ
        if (hd.getPhieuDatBan() != null) {
            phieuDatBanService.hoanTatPhieu(hd.getPhieuDatBan().getIdPhieuDat());
        }

        if (hd.getLoaiDonHang() == LoaiDonHang.MANG_VE) {
            hd.setTrangThai(TrangThaiHoaDon.HOAN_TAT);
        }

        // 7. Lưu vào SQL
        HoaDon finalHd = hoaDonRepository.save(hd);

        // 8. CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG REALTIME
        // Để App Thu ngân/Quản lý cập nhật lại danh sách đơn hàng mà không cần F5
        updateOrderRealtime(finalHd);

        // 9. 🔔 THÔNG BÁO ĐẨY (FCM) - CHỈ GỬI CHO PHỤC VỤ
        try {
            String tenBan = finalHd.getPhieuDatBan() != null ? getTenBans(finalHd) : "Mang về";
            firebaseMessagingService.sendNotificationToTopic("PHUC_VU",
                    "✅ Bàn đã thanh toán",
                    "Đơn hàng tại " + tenBan + " đã được thanh toán xong. Phục vụ chuẩn bị dọn bàn!");
        } catch (Exception e) {
            System.out.println("⚠️ Lỗi thông báo thanh toán cho Phục vụ: " + e.getMessage());
        }

        return mapToResponseFull(finalHd);
    }

    @Override
    @Transactional
    public void hoanTatDonHang(Integer id) {
        HoaDon hd = hoaDonRepository.findById(id).orElseThrow();

        if (hd.getTrangThai() != TrangThaiHoaDon.DA_THANH_TOAN) {
            throw new BadRequestException("Phải xác nhận thanh toán trước khi hoàn tất đơn hàng!");
        }

        hd.setTrangThai(TrangThaiHoaDon.HOAN_TAT);

        if (hd.getPhieuDatBan() != null) {
            phieuDatBanService.hoanTatPhieu(hd.getPhieuDatBan().getIdPhieuDat());
        }

        HoaDon finalHd = hoaDonRepository.save(hd);

        // 🔔 🌟 THÔNG BÁO TỔNG KẾT HOÀN TẤT ĐẾN TOÀN BỘ HỆ THỐNG
        try {
            String tenBan = finalHd.getPhieuDatBan() != null ? getTenBans(finalHd) : "Mang về";
            String formatTien = String.format("%,.0f", finalHd.getTongThanhToan());

            String tieuDe = "📈 Đơn hàng hoàn tất!";
            String noiDung = "Hóa đơn #" + finalHd.getIdHoaDon() + " tại " + tenBan + " đã đóng ca. (Thu: " + formatTien + "đ)";

            // Bắn thông báo đồng loạt cho cả 3 nhóm quầy
            firebaseMessagingService.sendNotificationToTopic("admin", tieuDe, noiDung);
            firebaseMessagingService.sendNotificationToTopic("THU_NGAN", tieuDe, "Hóa đơn #" + finalHd.getIdHoaDon() + " (" + tenBan + ") đã đóng ca thành công.");
            firebaseMessagingService.sendNotificationToTopic("PHUC_VU", tieuDe, "Hệ thống đã giải phóng " + tenBan + ". Sẵn sàng đón lượt khách mới!");

        } catch (Exception e) {
            System.out.println("⚠️ Lỗi phát tán thông báo hoàn tất đơn: " + e.getMessage());
        }
    }

    private void capNhatCauHinhGiamGiaVaThuePhi(HoaDon hd, ThanhToanRequest request) {
        if (request.maCode() != null && !request.maCode().isBlank()) {
            khuyenMaiService.kiemTraMaCode(request.maCode(), hd.getTongTienHang());

            KhuyenMai km = khuyenMaiRepository.findByMaCodeAndThoiGianXoa(request.maCode(), 0L)
                    .orElseThrow(() -> new BadRequestException("Mã khuyến mãi không tồn tại"));
            hd.setKhuyenMai(km);
        } else {
            hd.setKhuyenMai(null);
        }

        if (request.diemSuDung() != null && request.diemSuDung() > 0) {
            if (hd.getKhachHang() == null) throw new BadRequestException("Khách vãng lai không có điểm!");
            if (hd.getKhachHang().getDiemTichLuy() < request.diemSuDung()) {
                throw new BadRequestException("Không đủ điểm! Hiện có: " + hd.getKhachHang().getDiemTichLuy());
            }
            hd.setDiemSuDung(request.diemSuDung());
        } else {
            hd.setDiemSuDung(0);
        }

        Set<Integer> finalIds = thuePhiRepository.findByLaMacDinhTrue()
                .stream().map(ThuePhi::getIdThuePhi).collect(Collectors.toSet());

        if (request.danhSachIdThuePhi() != null) {
            finalIds.addAll(request.danhSachIdThuePhi());
        }

        hd.getDanhSachThuePhi().clear();
        List<ThuePhi> dsCanApDung = thuePhiRepository.findAllById(finalIds);
        for (ThuePhi tp : dsCanApDung) {
            hd.getDanhSachThuePhi().add(new HoaDonThuePhi(hd, tp.getTenThuePhi(), tp.getGiaTri(), tp.getLoaiGiaTri()));
        }
    }

    private String taoSnapshotText(HoaDon hd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("=== MATCHTEA BILL ===\n");
        sb.append("Ngày: ").append(LocalDateTime.now().format(formatter)).append("\n");
        // Sửa tên Thu ngân (người chốt bill)
        sb.append("Thu ngân: ").append(hd.getThuNgan().getHoTen()).append("\n");

        // Thêm tên Phục vụ (người mở bàn) nếu là đơn tại bàn
        if (hd.getPhieuDatBan() != null && hd.getPhieuDatBan().getNhanVienPhucVu() != null) {
            sb.append("Phục vụ : ").append(hd.getPhieuDatBan().getNhanVienPhucVu().getHoTen()).append("\n");
        }
        sb.append("---------------------\n");

        hd.getDanhSachChiTiet().forEach(ct -> {
            BigDecimal giaTopping = ct.getDanhSachTopping().stream()
                    .map(ChiTietHoaDonTopping::getGiaThoiDiemBan)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal thanhTienDong = ct.getGiaThoiDiemBan().add(giaTopping)
                    .multiply(BigDecimal.valueOf(ct.getSoLuong()));

            sb.append(String.format("%-15s x%d %s\n",
                    ct.getBienThe().getSanPham().getTenSanPham(),
                    ct.getSoLuong(),
                    thanhTienDong.setScale(0, RoundingMode.HALF_UP).toString()));
        });

        sb.append("---------------------\n");
        sb.append("Tiền hàng: ").append(hd.getTongTienHang().setScale(0, RoundingMode.HALF_UP)).append("\n");
        sb.append("Giảm giá: -").append(hd.getGiamGiaKhuyenMai().add(hd.getGiamGiaThanhVien()).setScale(0, RoundingMode.HALF_UP)).append("\n");
        sb.append("Thuế phí: ").append(hd.getTongTienThue().setScale(0, RoundingMode.HALF_UP)).append("\n");
        sb.append("THANH TOÁN: ").append(hd.getTongThanhToan().setScale(0, RoundingMode.HALF_UP)).append("\n");
        sb.append("PTTT: ").append(hd.getPhuongThucThanhToan() != null ? hd.getPhuongThucThanhToan() : "Chưa chọn").append("\n");
        return sb.toString();
    }

    private void napDanhSachMonVaoHoaDon(HoaDon hd, List<ChiTietHoaDonRequest> requests) {
        if (hd.getDanhSachChiTiet() == null) hd.setDanhSachChiTiet(new LinkedHashSet<>());

        for (ChiTietHoaDonRequest req : requests) {
            BienTheSanPham bt = bienTheRepository.findById(req.idBienThe())
                    .orElseThrow(() -> new ResourceNotFoundException("Biến thể không tồn tại"));

            Optional<ChiTietHoaDon> trungMon = hd.getDanhSachChiTiet().stream()
                    .filter(ct -> ct.getBienThe().getIdBienThe().equals(req.idBienThe()))
                    .filter(ct -> isSameOptions(ct.getTuyChonJson(), req.tuyChonJson()))
                    .filter(ct -> isSameToppings(ct, req.danhSachIdTopping()))
                    .findFirst();

            if (trungMon.isPresent()) {
                ChiTietHoaDon existingCt = trungMon.get();
                existingCt.setSoLuong(existingCt.getSoLuong() + req.soLuong());

                existingCt.setPhanTramGiamGia(bt.getPhanTramGiamGia());
            } else {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setHoaDon(hd);
                ct.setBienThe(bt);
                ct.setSoLuong(req.soLuong());
                ct.setGiaThoiDiemBan(bt.getGiaBan());
                ct.setPhanTramGiamGia(bt.getPhanTramGiamGia()); // Lấy phần trăm giảm giá mới nhất từ biến thể
                ct.setTuyChonJson(req.tuyChonJson());

                if (req.danhSachIdTopping() != null && !req.danhSachIdTopping().isEmpty()) {
                    Set<ChiTietHoaDonTopping> listTopping = new LinkedHashSet<>();
                    for (Integer idTpBienThe : req.danhSachIdTopping()) {
                        BienTheSanPham btTopping = bienTheRepository.findById(idTpBienThe)
                                .orElseThrow(() -> new ResourceNotFoundException("Topping không tồn tại"));

                        ChiTietHoaDonTopping cttp = new ChiTietHoaDonTopping();
                        cttp.setChiTietHoaDon(ct);
                        cttp.setTopping(btTopping.getSanPham());
                        cttp.setBienTheTopping(btTopping);
                        cttp.setGiaThoiDiemBan(btTopping.getGiaBan());
                        listTopping.add(cttp);
                    }
                    ct.setDanhSachTopping(listTopping);
                }
                hd.getDanhSachChiTiet().add(ct);
            }
        }
    }

    private boolean isSameOptions(String json1, String json2) {
        if (json1 == null && json2 == null) return true;
        if (json1 == null || json2 == null) return false;

        try {
            JsonNode tree1 = objectMapper.readTree(json1);
            JsonNode tree2 = objectMapper.readTree(json2);
            return tree1.equals(tree2);
        } catch (Exception e) {
            return json1.trim().equals(json2.trim());
        }
    }

    private boolean isSameToppings(ChiTietHoaDon existingItem, List<Integer> requestedToppingIds) {
        Set<ChiTietHoaDonTopping> currentToppings = existingItem.getDanhSachTopping();

        boolean currentEmpty = (currentToppings == null || currentToppings.isEmpty());
        boolean reqEmpty = (requestedToppingIds == null || requestedToppingIds.isEmpty());
        if (currentEmpty && reqEmpty) return true;

        if (currentEmpty != reqEmpty || currentToppings.size() != requestedToppingIds.size()) return false;

        List<Integer> currentIds = currentToppings.stream()
                .map(tp -> tp.getBienTheTopping().getIdBienThe())
                .sorted().toList();

        List<Integer> requestedIds = requestedToppingIds.stream().sorted().toList();

        return currentIds.equals(requestedIds);
    }

    private void tinhToanTaiChinh(HoaDon hd) {
        BigDecimal tongTienHang = hd.getDanhSachChiTiet().stream()
                .map(ct -> {
                    // 1. Lấy giá gốc tại thời điểm bán
                    BigDecimal giaGocMon = ct.getGiaThoiDiemBan();

                    // 2. SỬA TẠI ĐÂY: Kiểm tra null trước khi gán vào biến int
                    // Nếu null thì coi như giảm giá bằng 0
                    int phanTramGiam = (ct.getPhanTramGiamGia() != null) ? ct.getPhanTramGiamGia() : 0;

                    // 3. Tính toán giá sau giảm của 1 món
                    BigDecimal giaSauGiamMon = giaGocMon;
                    if (phanTramGiam > 0) {
                        BigDecimal tienGiam = giaGocMon.multiply(BigDecimal.valueOf(phanTramGiam))
                                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                        giaSauGiamMon = giaGocMon.subtract(tienGiam);
                    }

                    // 4. Cộng Topping
                    BigDecimal tongGiaTopping = BigDecimal.ZERO;
                    if (ct.getDanhSachTopping() != null && !ct.getDanhSachTopping().isEmpty()) {
                        tongGiaTopping = ct.getDanhSachTopping().stream()
                                .map(ChiTietHoaDonTopping::getGiaThoiDiemBan)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                    }

                    // 5. Nhân với số lượng
                    return giaSauGiamMon.add(tongGiaTopping).multiply(BigDecimal.valueOf(ct.getSoLuong()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        hd.setTongTienHang(tongTienHang);

        BigDecimal giamGiaTV = BigDecimal.ZERO;
        if (hd.getKhachHang() != null) {
            BigDecimal tiLeGiam = BigDecimal.ZERO;
            if (hd.getKhachHang().getHangThanhVien() == HangThanhVien.VANG) {
                tiLeGiam = new BigDecimal("0.10");
            } else if (hd.getKhachHang().getHangThanhVien() == HangThanhVien.BAC) {
                tiLeGiam = new BigDecimal("0.05");
            }
            giamGiaTV = tongTienHang.multiply(tiLeGiam).setScale(2, RoundingMode.HALF_UP);
        }
        hd.setGiamGiaThanhVien(giamGiaTV);

        BigDecimal giamGiaKM = BigDecimal.ZERO;
        if (hd.getKhuyenMai() != null) {
            KhuyenMai km = hd.getKhuyenMai();
            if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.GIAM_PHAN_TRAM) {
                giamGiaKM = tongTienHang.multiply(km.getGiaTriGiam())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            } else {
                giamGiaKM = km.getGiaTriGiam().setScale(2, RoundingMode.HALF_UP);
            }
        }
        hd.setGiamGiaKhuyenMai(giamGiaKM);

        BigDecimal soTienSauGiam = tongTienHang.subtract(giamGiaTV).subtract(giamGiaKM);
        if (soTienSauGiam.compareTo(BigDecimal.ZERO) < 0) soTienSauGiam = BigDecimal.ZERO;

        BigDecimal tongTienThuePhi = BigDecimal.ZERO;

        if (hd.getDanhSachThuePhi() != null) {
            for (HoaDonThuePhi tp : hd.getDanhSachThuePhi()) {
                BigDecimal giaTri = BigDecimal.valueOf(tp.getGiaTriTaiThoiDiemBan());
                BigDecimal tienQuyDoi;

                // KIỂM TRA LOẠI GIÁ TRỊ (PHAN_TRAM hay TIEN_MAT)
                if (tp.getLoaiGiaTri() == LoaiGiaTriThuePhi.PHAN_TRAM) {
                    // Nếu là phần trăm, tính dựa trên số tiền sau giảm (hoặc trước giảm tùy loại KM)
                    BigDecimal baseAmount;
                    if (hd.getKhuyenMai() != null && hd.getKhuyenMai().getLaGiamGiaSauThue()) {
                        baseAmount = tongTienHang.subtract(giamGiaTV);
                    } else {
                        baseAmount = soTienSauGiam;
                    }

                    // Công thức: (Số tiền * Phần trăm) / 100
                    tienQuyDoi = baseAmount.multiply(giaTri)
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                } else {
                    // Nếu là tiền mặt (VND), giữ nguyên giá trị đó cộng vào bill
                    tienQuyDoi = giaTri.setScale(2, RoundingMode.HALF_UP);
                }

                tp.setSoTienQuyDoi(tienQuyDoi);
                tongTienThuePhi = tongTienThuePhi.add(tienQuyDoi);
            }
        }
        hd.setTongTienThue(tongTienThuePhi);

        BigDecimal tienDiem = BigDecimal.ZERO;
        if (hd.getDiemSuDung() != null && hd.getDiemSuDung() > 0) {
            tienDiem = BigDecimal.valueOf(hd.getDiemSuDung())
                    .multiply(new BigDecimal("1000"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal tongCuoi = soTienSauGiam.add(hd.getTongTienThue()).subtract(tienDiem);

        hd.setTongThanhToan(tongCuoi.compareTo(BigDecimal.ZERO) < 0
                ? BigDecimal.ZERO
                : tongCuoi.setScale(2, RoundingMode.HALF_UP));
    }

    // 1. Hàm lấy danh sách tên bàn từ hóa đơn để hiển thị trong nội dung thông báo
    private String getTenBans(HoaDon hd) {
        if (hd.getPhieuDatBan() == null) return "Mang về";
        return chiTietDatBanRepository.findByPhieuDatBan_IdPhieuDat(hd.getPhieuDatBan().getIdPhieuDat())
                .stream()
                .map(ct -> ct.getBan().getTenBan())
                .collect(Collectors.joining(", "));
    }

    // 2. Hàm cập nhật trạng thái đơn hàng lên Firebase Realtime Database
    private void updateOrderRealtime(HoaDon hd) {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders/" + hd.getIdHoaDon());

            Map<String, Object> data = new HashMap<>();
            data.put("idHoaDon", hd.getIdHoaDon());
            data.put("trangThai", hd.getTrangThai().name());

            // SỬA TẠI ĐÂY: Chuyển BigDecimal thành double
            if (hd.getTongThanhToan() != null) {
                data.put("tongThanhToan", hd.getTongThanhToan().doubleValue());
            } else {
                data.put("tongThanhToan", 0.0);
            }

            data.put("lastUpdate", System.currentTimeMillis());

            ref.setValueAsync(data);
            System.out.println(">>> Đã cập nhật Realtime cho hóa đơn: " + hd.getIdHoaDon());
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật Realtime Order: " + e.getMessage());
        }
    }

    @Override
    public Page<HoaDonResponse> layLichSuHoaDonKhachHang(Integer idKhachHang, int page, int size) {

        if (!khachHangRepository.existsById(idKhachHang)) {
            throw new ResourceNotFoundException("Khách hàng không tồn tại");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<HoaDon> hoaDons = hoaDonRepository.findByKhachHang_IdKhachHangOrderByThoiGianTaoDesc(idKhachHang, pageable);

        // Chuyển đổi từ Page<Entity> sang Page<Response>
        return hoaDons.map(this::mapToResponseFull);
    }
}
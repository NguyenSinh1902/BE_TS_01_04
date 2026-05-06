package iuh.fit.se.service.impl;

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
import iuh.fit.se.service.HoaDonService;
import iuh.fit.se.service.KhachHangService;
import iuh.fit.se.service.KhuyenMaiService;
import iuh.fit.se.service.PhieuDatBanService;
import org.springframework.data.domain.Sort;
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

    public HoaDonServiceImpl(HoaDonRepository hoaDonRepository, PhieuDatBanRepository phieuDatBanRepository, NhanVienRepository nhanVienRepository, KhachHangRepository khachHangRepository, BienTheSanPhamRepository bienTheRepository, KhachHangService khachHangService, HoaDonMapper hoaDonMapper, KhuyenMaiService khuyenMaiService, KhuyenMaiRepository khuyenMaiRepository, PhieuDatBanService phieuDatBanService, ChiTietDatBanRepository chiTietDatBanRepository, SanPhamRepository sanPhamRepository, ThuePhiRepository thuePhiRepository) {
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
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDonResponse> layTatCaHoaDon() {
        return hoaDonRepository.findAll(Sort.by(Sort.Direction.DESC, "thoiGianTao"))
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

        NhanVien nv = nhanVienRepository.findById(request.idNhanVien())
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên không tồn tại"));
        hd.setNhanVien(nv);

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
        return this.layChiTiet(savedHd.getIdHoaDon());
    }

    private void apDungThuePhiMacDinh(HoaDon hd) {
        List<ThuePhi> dsMacDinh = thuePhiRepository.findByLaMacDinhTrue();
        for (ThuePhi tp : dsMacDinh) {
            hd.getDanhSachThuePhi().add(new HoaDonThuePhi(hd, tp.getTenThuePhi(), tp.getGiaTri()));
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
            hd.getDanhSachThuePhi().add(new HoaDonThuePhi(hd, tp.getTenThuePhi(), tp.getGiaTri()));
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
                    ct.getGiaThoiDiemBan(),
                    ct.getTuyChonJson(),
                    toppingRes,
                    thanhTienChuan
            );
        }).collect(Collectors.toList());

        return new HoaDonResponse(
                res.idHoaDon(),
                res.idPhieuDat(),
                tenBans,
                res.loaiDonHang(),
                res.tenNhanVien(),
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

        if (trangThaiMoi == TrangThaiHoaDon.HOAN_TAT && hd.getPhieuDatBan() != null) {
            phieuDatBanService.hoanTatPhieu(hd.getPhieuDatBan().getIdPhieuDat());
        }

        hoaDonRepository.save(hd);
        return layChiTiet(idHoaDon);
    }

    @Override
    @Transactional
    public void huyHoaDon(Integer id) {
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
        hoaDonRepository.save(hd);
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

        hoaDonRepository.save(hd);
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

        hoaDonRepository.save(hd);
        hoaDonRepository.flush();

        return layChiTiet(idHoaDon);
    }

    @Override
    @Transactional
    public HoaDonResponse xuatHoaDonTamTinh(Integer id, ThanhToanRequest request) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn id = " + id));

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

        // 5. Tích điểm
        if (hd.getKhachHang() != null) {
            int diemMoi = hd.getTongThanhToan().divide(new BigDecimal("10000"), 0, RoundingMode.DOWN).intValue();
            if (diemMoi > 0) {
                khachHangService.tichDiemVaThangHang(hd.getKhachHang().getIdKhachHang(), diemMoi);
            }
        }

        hd.setThongTinChiTiet(taoSnapshotText(hd));

        if (hd.getLoaiDonHang() == LoaiDonHang.MANG_VE) {
            hd.setTrangThai(TrangThaiHoaDon.HOAN_TAT);
        }

        return mapToResponseFull(hoaDonRepository.save(hd));
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

        hoaDonRepository.save(hd);
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
            hd.getDanhSachThuePhi().add(new HoaDonThuePhi(hd, tp.getTenThuePhi(), tp.getGiaTri()));
        }
    }

    private String taoSnapshotText(HoaDon hd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("=== MATCHTEA BILL ===\n");
        sb.append("Ngày: ").append(LocalDateTime.now().format(formatter)).append("\n");
        sb.append("NV: ").append(hd.getNhanVien().getHoTen()).append("\n");
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
                // Cập nhật lại phần trăm giảm giá mới nhất từ DB nếu cần (tùy nghiệp vụ)
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
                BigDecimal tiLe = BigDecimal.valueOf(tp.getGiaTriTaiThoiDiemBan());
                BigDecimal tienQuyDoi;

                if (hd.getKhuyenMai() != null && hd.getKhuyenMai().getLaGiamGiaSauThue()) {
                    tienQuyDoi = tongTienHang.subtract(giamGiaTV).multiply(tiLe);
                } else {
                    tienQuyDoi = soTienSauGiam.multiply(tiLe);
                }

                BigDecimal tienTron = tienQuyDoi.setScale(2, RoundingMode.HALF_UP);
                tp.setSoTienQuyDoi(tienTron);
                tongTienThuePhi = tongTienThuePhi.add(tienTron);
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
}
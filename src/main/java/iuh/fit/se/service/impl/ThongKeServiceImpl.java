package iuh.fit.se.service.impl;

import iuh.fit.se.dto.thongke.*;
import iuh.fit.se.enums.LoaiDonHang;
import iuh.fit.se.repository.HoaDonRepository;
import iuh.fit.se.repository.SanPhamRepository;
import iuh.fit.se.service.ThongKeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ThongKeServiceImpl implements ThongKeService {

    private final HoaDonRepository hoaDonRepository;
    private final SanPhamRepository sanPhamRepository;

    public ThongKeServiceImpl(HoaDonRepository hoaDonRepository, SanPhamRepository sanPhamRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.sanPhamRepository = sanPhamRepository;
    }

    // ==========================================
    // NHÓM 1: TỔNG QUAN HÔM NAY
    // ==========================================

    @Override
    public TongQuanHomNayResponse getTongQuanHomNay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startToday = LocalDate.now().atStartOfDay();

        // 🌟 SỬA MỐC THỜI GIAN: Tính trọn vẹn từ 00:00:00 đến 23:59:59 của ngày hôm qua
        LocalDateTime startYesterday = startToday.minusDays(1);
        LocalDateTime endYesterday = startToday.minusNanos(1); // Tương đương 23:59:59.999 của hôm qua

        BigDecimal dtHomNay = Optional.ofNullable(hoaDonRepository.tinhTongDoanhThu(startToday, now)).orElse(BigDecimal.ZERO);
        long sdHomNay = hoaDonRepository.demSoDonHang(startToday, now);

        BigDecimal dtHomQua = Optional.ofNullable(hoaDonRepository.tinhTongDoanhThu(startYesterday, endYesterday)).orElse(BigDecimal.ZERO);
        long sdHomQua = hoaDonRepository.demSoDonHang(startYesterday, endYesterday);

        // 🌟 SỬA LOGIC TÍNH PHẦN TRĂM TĂNG TRƯỞNG (HỖ TRỢ SỐ ÂM KHI DOANH THU GIẢM)
        double growthDT = 0.0;
        if (dtHomQua.compareTo(BigDecimal.ZERO) > 0) {
            // Công thức chuẩn: ((Hôm nay - Hôm qua) / Hôm qua) * 100
            growthDT = dtHomNay.subtract(dtHomQua)
                    .divide(dtHomQua, 4, RoundingMode.HALF_UP)
                    .doubleValue() * 100;
        } else if (dtHomNay.compareTo(BigDecimal.ZERO) > 0) {
            growthDT = 100.0; // Hôm qua không bán được gì, hôm nay bán được thì tính tăng 100%
        }

        double growthSD = 0.0;
        if (sdHomQua > 0) {
            // Công thức chuẩn: ((Hôm nay - Hôm qua) / Hôm qua) * 100
            growthSD = (double) (sdHomNay - sdHomQua) / sdHomQua * 100;
        } else if (sdHomNay > 0) {
            growthSD = 100.0;
        }

        // XỬ LÝ LOGIC LẤY THÔNG TIN CHO MÓN BÁN CHẠY - GIỮ NGUYÊN CODE CHUẨN CỦA ÔNG
        List<Object[]> topProd = hoaDonRepository.findTopProduct(startToday, now);
        MonBanChayResponse monBanChayDto = new MonBanChayResponse("Chưa có đơn", null, BigDecimal.ZERO, 0L);

        if (!topProd.isEmpty()) {
            Integer idSanPham = (Integer) topProd.get(0)[0];
            long soLuongDaBan = ((Number) topProd.get(0)[1]).longValue();

            iuh.fit.se.entity.SanPham sp = sanPhamRepository.findById(idSanPham).orElse(null);
            if (sp != null) {
                BigDecimal giaSizeDau = BigDecimal.ZERO;
                if (sp.getDanhSachBienThe() != null && !sp.getDanhSachBienThe().isEmpty()) {
                    giaSizeDau = sp.getDanhSachBienThe().iterator().next().getGiaBan();
                }
                monBanChayDto = new MonBanChayResponse(
                        sp.getTenSanPham(),
                        sp.getDuongDanAnh(),
                        giaSizeDau,
                        soLuongDaBan
                );
            }
        }

        // Làm tròn lấy 1 chữ số thập phân trước khi trả về cho giống format DTO của ông cũ
        growthDT = Double.parseDouble(String.format("%.1f", growthDT));
        growthSD = Double.parseDouble(String.format("%.1f", growthSD));

        return new TongQuanHomNayResponse(dtHomNay, growthDT, sdHomNay, growthSD, monBanChayDto);
    }

    @Override
    public BieuDoHomNayResponse getBieuDoHomNay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startToday = LocalDate.now().atStartOfDay();
        long sdHomNay = hoaDonRepository.demSoDonHang(startToday, now);

        // [MỤC 1]: Nguồn đơn hàng (Pie Chart) - GIỮ NGUYÊN 100% CODE CŨ CỦA ÔNG
        List<OrderSourceResponse> orderSources = new ArrayList<>();
        List<Object[]> sourcesRaw = hoaDonRepository.countOrderByLoai(startToday, now);
        for (Object[] row : sourcesRaw) {
            LoaiDonHang loai = (LoaiDonHang) row[0];
            long count = (long) row[1];
            double percent = (sdHomNay > 0) ? (double) count / sdHomNay * 100 : 0;
            orderSources.add(new OrderSourceResponse(loai.name(), loai == LoaiDonHang.TAI_BAN ? "Tại chỗ" : "Mang về", count, Double.parseDouble(String.format("%.1f", percent))));
        }

        // [MỤC 2]: Khung giờ cao điểm (Bar Chart) - 🌟 NÂNG CẤP FULL 24 TIẾNG 🌟
        Map<Integer, Long> hourMap = new TreeMap<>();

        // Thay vì chạy từ 8 đến 22, mình cho chạy từ 0 đến 23 để hứng trọn ca đêm ông test
        for (int i = 0; i <= 23; i++) {
            hourMap.put(i, 0L);
        }

        // Phần lấy data từ Repository và map ngược vào Map - Giữ nguyên logic cũ của ông
        List<Object[]> hoursRaw = hoaDonRepository.countOrderByHour(startToday, now);
        for (Object[] row : hoursRaw) {
            if (row[0] != null) { // Bọc nhẹ thêm điều kiện phòng hờ dữ liệu lỗi
                int hour = ((Number) row[0]).intValue();
                if (hourMap.containsKey(hour)) {
                    hourMap.put(hour, ((Number) row[1]).longValue());
                }
            }
        }

        // Trả dữ liệu ra DTO cho FE vẽ biểu đồ - Giữ nguyên logic cũ của ông
        List<PeakHourResponse> peakHours = hourMap.entrySet().stream()
                .map(entry -> new PeakHourResponse(String.format("%02d:00", entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());

        return new BieuDoHomNayResponse(orderSources, peakHours);
    }

    // ==========================================
    // NHÓM 2: THỐNG KÊ CHI TIẾT THEO KHOẢNG THỜI GIAN
    // ==========================================

    @Override
    public ChiTietThongKeResponse getChiTietTheoThoiGian(LocalDate tuNgay, LocalDate denNgay) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.atTime(23, 59, 59);

        BigDecimal tongDoanhThu = Optional.ofNullable(hoaDonRepository.tinhTongDoanhThu(start, end)).orElse(BigDecimal.ZERO);
        long tongSoDonHang = hoaDonRepository.demSoDonHang(start, end);

        // XỬ LÝ LỖI CAST TYPE Ở ĐÂY
        List<Object[]> topProd = hoaDonRepository.findTopProduct(start, end);
        String monBanChay = "Chưa có dữ liệu";

        if (!topProd.isEmpty()) {
            // row[0] bây giờ là Integer (idSanPham) chứ không phải String nữa nhé!
            Integer idSanPham = (Integer) topProd.get(0)[0];

            // Dùng idSanPham tìm ngược lại tên từ DB để hiển thị
            iuh.fit.se.entity.SanPham sp = sanPhamRepository.findById(idSanPham).orElse(null);
            if (sp != null) {
                monBanChay = sp.getTenSanPham();
            }
        }

        return new ChiTietThongKeResponse(tongDoanhThu, tongSoDonHang, monBanChay);
    }

    @Override
    public List<BieuDoDoanhThuResponse> getBieuDoDoanhThuTheoThoiGian(LocalDate tuNgay, LocalDate denNgay, String donVi) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.atTime(23, 59, 59);

        // Lấy toàn bộ hóa đơn thành công trong khoảng thời gian
        List<iuh.fit.se.entity.HoaDon> hoaDons = hoaDonRepository.findByDateRange(start, end).stream()
                .filter(h -> h.getTrangThai() == iuh.fit.se.enums.TrangThaiHoaDon.DA_THANH_TOAN || h.getTrangThai() == iuh.fit.se.enums.TrangThaiHoaDon.HOAN_TAT)
                .collect(Collectors.toList());

        Map<String, BigDecimal> groupMap = new LinkedHashMap<>();

        // THÀNH PHẦN MỚI: Xử lý gom nhóm theo TUẦN
        if ("tuan".equalsIgnoreCase(donVi)) {
            // Tạo các nhãn tuần mẫu trống từ tuNgay đến denNgay
            LocalDate temp = tuNgay;
            while (!temp.isAfter(denNgay)) {
                // Lấy số tuần trong tháng (Tuần 1, Tuần 2...)
                int weekNum = temp.get(java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH);
                String label = "Tuần " + weekNum + " - Thg " + temp.getMonthValue();
                groupMap.put(label, BigDecimal.ZERO);
                temp = temp.plusDays(7); // Nhảy theo block 7 ngày để khởi tạo nhãn
            }
            // Đảm bảo ngày cuối cùng cũng có nhãn nếu lỡ chu kỳ nhảy 7 ngày vượt qua denNgay
            int finalWeekNum = denNgay.get(java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH);
            groupMap.put("Tuần " + finalWeekNum + " - Thg " + denNgay.getMonthValue(), BigDecimal.ZERO);

            // Điền dữ liệu thật vào tuần tương ứng
            for (iuh.fit.se.entity.HoaDon hd : hoaDons) {
                LocalDateTime time = hd.getThoiGianTao();
                int weekNum = time.get(java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH);
                String label = "Tuần " + weekNum + " - Thg " + time.getMonthValue();

                if (groupMap.containsKey(label)) {
                    groupMap.put(label, groupMap.get(label).add(hd.getTongThanhToan()));
                }
            }

        } else {
            // GIỮ NGUYÊN LOGIC CHO NGÀY VÀ THÁNG
            DateTimeFormatter formatter;
            if ("thang".equalsIgnoreCase(donVi)) {
                formatter = DateTimeFormatter.ofPattern("MM/yyyy");
            } else { // Mặc định là "ngay"
                formatter = DateTimeFormatter.ofPattern("dd/MM");
            }

            // Tạo mảng mẫu rỗng
            LocalDate temp = tuNgay;
            while (!temp.isAfter(denNgay)) {
                String label = temp.format(formatter);
                groupMap.put(label, BigDecimal.ZERO);
                temp = "thang".equalsIgnoreCase(donVi) ? temp.plusMonths(1) : temp.plusDays(1);
            }

            // Điền dữ liệu thật
            for (iuh.fit.se.entity.HoaDon hd : hoaDons) {
                String label = hd.getThoiGianTao().format(formatter);
                if (groupMap.containsKey(label)) {
                    groupMap.put(label, groupMap.get(label).add(hd.getTongThanhToan()));
                }
            }
        }

        return groupMap.entrySet().stream()
                .map(e -> new BieuDoDoanhThuResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public TopSanPhamResponse getTopSanPhamTheoThoiGian(LocalDate tuNgay, LocalDate denNgay) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.atTime(23, 59, 59);

        List<Object[]> top5ChayRaw = hoaDonRepository.findTop5BestSellers(start, end, PageRequest.of(0, 5));
        List<SanPhamThongKeResponse> top5BanChay = top5ChayRaw.stream()
                .map(row -> new SanPhamThongKeResponse((String) row[0], (long) row[1], "Tang"))
                .collect(Collectors.toList());

        List<Object[]> top5ChamRaw = hoaDonRepository.findTop5WorstSellers(start, end, PageRequest.of(0, 5));
        List<SanPhamThongKeResponse> top5BanCham = top5ChamRaw.stream()
                .map(row -> new SanPhamThongKeResponse((String) row[0], (long) row[1], "Giam"))
                .collect(Collectors.toList());

        return new TopSanPhamResponse(top5BanChay, top5BanCham);
    }

    private double tinhPhanTramTangTruong(BigDecimal hnay, BigDecimal hqua) {
        if (hqua == null || hqua.compareTo(BigDecimal.ZERO) == 0) {
            return hnay.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return hnay.subtract(hqua)
                .divide(hqua, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }

    @Override
    public List<PhuongThucThanhToanResponse> getThongKePhuongThucThanhToan(LocalDate tuNgay, LocalDate denNgay) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.atTime(23, 59, 59);

        List<Object[]> rawData = hoaDonRepository.countOrderByPhuongThucThanhToan(start, end);

        // Tính tổng số đơn của tất cả phương thức để làm mẫu chia phần trăm
        long tongSoDon = rawData.stream().mapToLong(row -> (long) row[1]).sum();

        List<PhuongThucThanhToanResponse> danhSachResponse = new ArrayList<>();

        for (Object[] row : rawData) {
            iuh.fit.se.enums.PhuongThucThanhToan pttt = (iuh.fit.se.enums.PhuongThucThanhToan) row[0];
            long count = (long) row[1];
            double percent = (tongSoDon > 0) ? (double) count / tongSoDon * 100 : 0;

            // Map label tiếng Việt hiển thị trên UI Glassmorphism
            String label = pttt.name().equalsIgnoreCase("TIEN_MAT") ? "Tiền mặt" : "Chuyển khoản";

            danhSachResponse.add(new PhuongThucThanhToanResponse(
                    pttt.name(),
                    label,
                    count,
                    Double.parseDouble(String.format("%.1f", percent))
            ));
        }

        return danhSachResponse;
    }
}
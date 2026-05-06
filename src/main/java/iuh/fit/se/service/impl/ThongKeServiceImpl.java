package iuh.fit.se.service.impl;

import iuh.fit.se.dto.thongke.*;
import iuh.fit.se.enums.LoaiDonHang;
import iuh.fit.se.repository.HoaDonRepository;
import iuh.fit.se.service.ThongKeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ThongKeServiceImpl implements ThongKeService {

    private final HoaDonRepository hoaDonRepository;

    public ThongKeServiceImpl(HoaDonRepository hoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardHomNay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startToday = LocalDate.now().atStartOfDay();

        // MỐC THỜI GIAN CHO TĂNG TRƯỞNG & SẢN PHẨM
        LocalDateTime startYesterday = startToday.minusDays(1);
        LocalDateTime endYesterday = now.minusDays(1);
        LocalDateTime sevenDaysAgo = startToday.minusDays(7); // Dùng cho Top 5

        // 1. LẤY DỮ LIỆU TÀI CHÍNH HÔM NAY (Theo ngày)
        BigDecimal dtHomNay = hoaDonRepository.tinhTongDoanhThu(startToday, now);
        if (dtHomNay == null) dtHomNay = BigDecimal.ZERO;
        long sdHomNay = hoaDonRepository.demSoDonHang(startToday, now);

        // 2. LẤY DỮ LIỆU HÔM QUA (ĐỂ TÍNH TĂNG TRƯỞNG)
        BigDecimal dtHomQua = hoaDonRepository.tinhTongDoanhThu(startYesterday, endYesterday);
        long sdHomQua = hoaDonRepository.demSoDonHang(startYesterday, endYesterday);

        double growthDT = tinhPhanTramTangTruong(dtHomNay, dtHomQua);
        double growthSD = tinhPhanTramTangTruong(BigDecimal.valueOf(sdHomNay), BigDecimal.valueOf(sdHomQua));

        // 3. LOGIC MÓN BÁN CHẠY NHẤT HÔM NAY (Hiển thị ở thẻ Summary)
        List<Object[]> topProd = hoaDonRepository.findTopProduct(startToday, now);
        String tenMon = "Chưa có";
        long slMon = 0;
        if (!topProd.isEmpty()) {
            tenMon = (String) topProd.get(0)[0];
            slMon = (long) topProd.get(0)[1];
        }

        // 4. BIỂU ĐỒ TỶ LỆ ĐƠN HÀNG (PIE CHART - Theo ngày)
        List<OrderSourceResponse> orderSources = new ArrayList<>();
        List<Object[]> sourcesRaw = hoaDonRepository.countOrderByLoai(startToday, now);

        for (Object[] row : sourcesRaw) {
            LoaiDonHang loai = (LoaiDonHang) row[0];
            long count = (long) row[1];
            double percent = (sdHomNay > 0) ? (double) count / sdHomNay * 100 : 0;

            orderSources.add(new OrderSourceResponse(
                    loai.name(),
                    loai == LoaiDonHang.TAI_BAN ? "Tại chỗ" : "Mang về",
                    count,
                    Double.parseDouble(String.format("%.1f", percent))
            ));
        }

        // 5. BIỂU ĐỒ KHUNG GIỜ CAO ĐIỂM (BAR CHART - Theo ngày)
        Map<Integer, Long> hourMap = new TreeMap<>();
        for (int i = 8; i <= 22; i++) hourMap.put(i, 0L);

        List<Object[]> hoursRaw = hoaDonRepository.countOrderByHour(startToday, now);
        for (Object[] row : hoursRaw) {
            Integer hour = (Integer) row[0];
            if (hourMap.containsKey(hour)) {
                hourMap.put(hour, (long) row[1]);
            }
        }

        List<PeakHourResponse> peakHours = hourMap.entrySet().stream()
                .map(entry -> new PeakHourResponse(String.format("%02d:00", entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());

        // 6. LẤY TOP 5 BÁN CHẠY (THỐNG KÊ 7 NGÀY QUA)
        List<Object[]> top5ChayRaw = hoaDonRepository.findTop5BestSellers(sevenDaysAgo, now, PageRequest.of(0, 5));
        List<SanPhamThongKeResponse> top5BanChay = top5ChayRaw.stream()
                .map(row -> new SanPhamThongKeResponse((String) row[0], (long) row[1], "Tang"))
                .collect(Collectors.toList());

        // 7. LẤY TOP 5 BÁN CHẬM (THỐNG KÊ 7 NGÀY QUA)
        List<Object[]> top5ChamRaw = hoaDonRepository.findTop5WorstSellers(sevenDaysAgo, now, PageRequest.of(0, 5));
        List<SanPhamThongKeResponse> top5BanCham = top5ChamRaw.stream()
                .map(row -> new SanPhamThongKeResponse((String) row[0], (long) row[1], "Giam"))
                .collect(Collectors.toList());

        return new DashboardResponse(
                dtHomNay, growthDT, sdHomNay, growthSD, tenMon, slMon,
                orderSources, peakHours,
                top5BanChay, top5BanCham
        );
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
    public List<BieuDoResponse> getBieuDoTheoNgay() {
        // Giữ nguyên hàm cũ hoặc cập nhật theo logic mới tùy nhu cầu của bạn
        return new ArrayList<>();
    }
}
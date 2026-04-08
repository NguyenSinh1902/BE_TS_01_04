package iuh.fit.se.service.impl;

import iuh.fit.se.dto.thongke.BieuDoResponse;
import iuh.fit.se.dto.thongke.DashboardResponse;
import iuh.fit.se.repository.HoaDonRepository;
import iuh.fit.se.service.ThongKeService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class ThongKeServiceImpl implements ThongKeService {

    private final HoaDonRepository hoaDonRepository;

    public ThongKeServiceImpl(HoaDonRepository hoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
    }

    @Override
    public DashboardResponse getDashboardHomNay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startToday = LocalDate.now().atStartOfDay();

        LocalDateTime startYesterday = startToday.minusDays(1);
        LocalDateTime endYesterday = now.minusDays(1);

        //LẤY DỮ LIỆU HÔM NAY
        BigDecimal dtHomNay = hoaDonRepository.tinhTongDoanhThu(startToday, now);
        if (dtHomNay == null) dtHomNay = BigDecimal.ZERO;
        long sdHomNay = hoaDonRepository.demSoDonHang(startToday, now);

        //LẤY DỮ LIỆU HÔM QUA
        BigDecimal dtHomQua = hoaDonRepository.tinhTongDoanhThu(startYesterday, endYesterday);
        long sdHomQua = hoaDonRepository.demSoDonHang(startYesterday, endYesterday);

        //TÍNH % TĂNG TRƯỞNG
        double growthDT = 0;
        if (dtHomQua != null && dtHomQua.compareTo(BigDecimal.ZERO) > 0) {
            growthDT = dtHomNay.subtract(dtHomQua)
                    .divide(dtHomQua, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
        } else if (dtHomNay.compareTo(BigDecimal.ZERO) > 0) {
            growthDT = 100.0; // Nếu hôm qua bằng 0 mà hôm nay có tiền thì tăng 100%
        }

        //TÍNH % TĂNG TRƯỞNG SỐ ĐƠN
        double growthSD = 0;
        if (sdHomQua > 0) {
            growthSD = ((double) (sdHomNay - sdHomQua) / sdHomQua) * 100;
        } else if (sdHomNay > 0) {
            growthSD = 100.0;
        }

        //LOGIC MÓN BÁN CHẠY
        List<Object[]> topProd = hoaDonRepository.findTopProduct(startToday, now);
        String tenMon = "Chưa có";
        long slMon = 0;

        if (!topProd.isEmpty()) {
            // TH1: Hôm nay đã có món bán chạy
            tenMon = (String) topProd.get(0)[0];
            slMon = (long) topProd.get(0)[1];
        } else {
            // TH2: Hôm nay chưa bán được gì -> Lấy món bán chạy 7 ngày qua
            LocalDateTime sevenDaysAgo = startToday.minusDays(7);
            List<Object[]> topSevenDays = hoaDonRepository.findTopProduct(sevenDaysAgo, now);

            if (!topSevenDays.isEmpty()) {
                tenMon = (String) topSevenDays.get(0)[0];
                slMon = (long) topSevenDays.get(0)[1];
                tenMon += " (Tuần này)";
            }
        }

        return new DashboardResponse(dtHomNay, growthDT, sdHomNay, growthSD, tenMon, slMon);
    }

    @Override
    public List<BieuDoResponse> getBieuDoTheoNgay() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        List<Object[]> results = hoaDonRepository.getDoanhThuTheoGio(start, end);

        Map<Integer, BigDecimal> dataMap = new TreeMap<>();
        int[] khungGio = {9, 11, 13, 15, 17, 19};
        for (int gio : khungGio) {
            dataMap.put(gio, BigDecimal.ZERO);
        }

        for (Object[] row : results) {
            Integer gioTuDB = (Integer) row[0];
            BigDecimal tongTien = (BigDecimal) row[1];

            for (int gioKhay : khungGio) {

                if (gioTuDB >= gioKhay && gioTuDB < gioKhay + 2) {
                    dataMap.put(gioKhay, dataMap.get(gioKhay).add(tongTien));
                    break;
                }
            }
        }

        return dataMap.entrySet().stream()
                .map(entry -> new BieuDoResponse(entry.getKey() + "h", entry.getValue()))
                .collect(Collectors.toList());
    }
}
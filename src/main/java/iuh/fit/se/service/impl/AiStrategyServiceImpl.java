package iuh.fit.se.service.impl;

import iuh.fit.se.client.GeminiClient;
import iuh.fit.se.dto.nhatkyai.NhatKyAiResponse;
import iuh.fit.se.entity.NhanVien;
import iuh.fit.se.entity.NhatKyAi;
import iuh.fit.se.exception.NotFoundException;
import iuh.fit.se.mapper.NhatKyAiMapper;
import iuh.fit.se.repository.HoaDonRepository;
import iuh.fit.se.repository.NhanVienRepository;
import iuh.fit.se.repository.NhatKyAiRepository;
import iuh.fit.se.service.AiStrategyService;
import iuh.fit.se.utils.SecurityUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiStrategyServiceImpl implements AiStrategyService {

    private final NhatKyAiRepository nhatKyAiRepository;
    private final NhanVienRepository nhanVienRepository;
    private final HoaDonRepository hoaDonRepository;
    private final NhatKyAiMapper nhatKyAiMapper;
    private final GeminiClient geminiClient;

    public AiStrategyServiceImpl(NhatKyAiRepository nhatKyAiRepository, NhanVienRepository nhanVienRepository, HoaDonRepository hoaDonRepository, NhatKyAiMapper nhatKyAiMapper, GeminiClient geminiClient) {
        this.nhatKyAiRepository = nhatKyAiRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.nhatKyAiMapper = nhatKyAiMapper;
        this.geminiClient = geminiClient;
    }

    @Override
    @Transactional
    public NhatKyAiResponse thucHienPhanTich(LocalDate ngay) {
        // 1. LẤY ID TỪ TOKEN (SECURITY CONTEXT)
        Integer idQuanLy = SecurityUtils.getCurrentIdNhanVien();
        if (idQuanLy == null) {
            throw new AccessDeniedException("Vui lòng đăng nhập với quyền Quản lý để sử dụng tính năng này!");
        }

        NhanVien quanLy = nhanVienRepository.findById(idQuanLy)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy Quản lý"));

        //(Từ 00:00:00 đến 23:59:59 của ngày được chọn)
        LocalDateTime start = ngay.atStartOfDay();
        LocalDateTime end = ngay.atTime(LocalTime.MAX);

        BigDecimal tongDoanhThu = hoaDonRepository.tinhTongDoanhThu(start, end);
        if (tongDoanhThu == null) tongDoanhThu = BigDecimal.ZERO;

        long soDonHang = hoaDonRepository.demSoDonHang(start, end);

        // Lấy Top 5 bán chạy
        List<Object[]> topBest = hoaDonRepository.findTop5BestSellers(start, end, PageRequest.of(0, 5));
        String topMónBánChạy = topBest.stream()
                .map(obj -> obj[0] + " (" + obj[1] + " ly)")
                .collect(Collectors.joining(", "));

        // Lấy Top 5 bán chậm
        List<Object[]> topWorst = hoaDonRepository.findTop5WorstSellers(start, end, PageRequest.of(0, 5));
        String mónBánChậm = topWorst.stream()
                .map(obj -> obj[0] + " (" + obj[1] + " ly)")
                .collect(Collectors.joining(", "));

        String duLieuInput = String.format(
                "{\"ngay\": \"%s\", \"doanhThu\": %s, \"soDon\": %d, \"topBanChay\": \"%s\", \"monBanCham\": \"%s\"}",
                ngay, tongDoanhThu, soDonHang, topMónBánChạy, mónBánChậm
        );

        // TẠO PROMPT NGẮN GỌN (Súc tích, gạch đầu dòng)
        String prompt = "Bạn là chuyên gia kinh doanh trà sữa. Với dữ liệu: " + duLieuInput + ". " +
                "Hãy viết báo cáo cực kỳ ngắn gọn (khoảng 150-200 từ), tập trung vào các ý chính sau: " +
                "1. Nhận xét doanh thu (1 câu). " +
                "2. Giải pháp cho món bán chậm (2-3 gạch đầu dòng). " +
                "3. Gợi ý 1 combo khuyến mãi tăng số (viết ngắn). " +
                "Yêu cầu: Dùng gạch đầu dòng, không viết văn bản dài dòng, bỏ qua các phần chào hỏi rườm rà.";

        //GỌI AI VÀ LƯU NHẬT KÝ
        String loiKhuyenTuAI = geminiClient.layLoiKhuyen(prompt);

        NhatKyAi nhatKy = new NhatKyAi();
        nhatKy.setQuanLy(quanLy);
        nhatKy.setNgayPhanTich(ngay);
        nhatKy.setLoiKhuyenAi(loiKhuyenTuAI);
        nhatKy.setDuLieuDauVao(duLieuInput);
        nhatKy.setThoiGianTao(LocalDateTime.now());

        return nhatKyAiMapper.toResponse(nhatKyAiRepository.save(nhatKy));
    }

//    @Override
//    public List<NhatKyAiResponse> layLichSuTheoNgay(LocalDate ngay) {
//        return nhatKyAiRepository.findByNgayPhanTichOrderByThoiGianTaoDesc(ngay)
//                .stream()
//                .map(nhatKyAiMapper::toResponse)
//                .collect(Collectors.toList());
//    }

    @Override
    public List<NhatKyAiResponse> layLichSuTheoNgay(LocalDate ngay) {
        List<NhatKyAi> danhSachLog;

        if (ngay == null) {
            // Nếu không truyền ngày -> Lấy tất cả
            danhSachLog = nhatKyAiRepository.findAllByOrderByThoiGianTaoDesc();
        } else {
            // Nếu có truyền ngày -> Lọc theo ngày chuẩn chỉ
            danhSachLog = nhatKyAiRepository.findByNgayPhanTichOrderByThoiGianTaoDesc(ngay);
        }

        return danhSachLog.stream()
                .map(nhatKyAiMapper::toResponse)
                .collect(Collectors.toList());
    }
}
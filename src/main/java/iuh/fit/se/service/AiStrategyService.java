package iuh.fit.se.service;

import iuh.fit.se.dto.nhatkyai.NhatKyAiResponse;
import java.time.LocalDate;
import java.util.List;

public interface AiStrategyService {
    NhatKyAiResponse thucHienPhanTich(LocalDate ngay);

    List<NhatKyAiResponse> layLichSuTheoNgay(LocalDate ngay);
}
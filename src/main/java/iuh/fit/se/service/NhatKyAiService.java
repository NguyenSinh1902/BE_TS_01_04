package iuh.fit.se.service;

import iuh.fit.se.dto.nhatkyai.NhatKyAiRequest;
import iuh.fit.se.dto.nhatkyai.NhatKyAiResponse;
import java.util.List;

public interface NhatKyAiService {

    NhatKyAiResponse luuNhatKy(NhatKyAiRequest request);

    List<NhatKyAiResponse> layLichSuTheoQuanLy(Integer idQuanLy);

    NhatKyAiResponse layChiTiet(Integer idNhatKy);
}
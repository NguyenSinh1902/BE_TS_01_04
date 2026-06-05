package iuh.fit.se.service;

import iuh.fit.se.dto.hoantra.PhieuHoanTraRequest;
import iuh.fit.se.dto.hoantra.PhieuHoanTraResponse;
import java.util.List;

public interface PhieuHoanTraService {
    PhieuHoanTraResponse taoPhieuHoanTra(PhieuHoanTraRequest request);
    PhieuHoanTraResponse pheDuyetPhieu(Integer id, boolean isDuyet);
    PhieuHoanTraResponse hoanThanhPhieu(Integer id);
    List<PhieuHoanTraResponse> layTatCaPhieu();
    PhieuHoanTraResponse layChiTietPhieu(Integer id);
}

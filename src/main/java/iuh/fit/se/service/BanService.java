package iuh.fit.se.service;

import iuh.fit.se.dto.ban.BanRequest;
import iuh.fit.se.dto.ban.BanResponse;
import java.util.List;

public interface BanService {

    List<BanResponse> layTatCaBan();

    BanResponse taoMoi(BanRequest request);

    BanResponse capNhat(Integer id, BanRequest request);

    void xoa(Integer id);
}
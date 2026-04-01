package iuh.fit.se.service;

import iuh.fit.se.dto.thuephi.ThuePhiRequest;
import iuh.fit.se.dto.thuephi.ThuePhiResponse;

import java.util.List;

public interface ThuePhiService {
    List<ThuePhiResponse> layTatCa();
    ThuePhiResponse taoMoi(ThuePhiRequest request);
    ThuePhiResponse capNhat(Integer id, ThuePhiRequest request);
    void xoa(Integer id);
}
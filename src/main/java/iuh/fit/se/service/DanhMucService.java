package iuh.fit.se.service;

import iuh.fit.se.dto.danhmuc.DanhMucRequest;
import iuh.fit.se.dto.danhmuc.DanhMucResponse;
import java.util.List;

public interface DanhMucService {

    List<DanhMucResponse> layTatCa();

    DanhMucResponse taoMoi(DanhMucRequest request);

    DanhMucResponse capNhat(Integer id, DanhMucRequest request);

    void xoa(Integer id);
}
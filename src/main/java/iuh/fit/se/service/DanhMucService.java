package iuh.fit.se.service;

import iuh.fit.se.dto.danhmuc.DanhMucRequest;
import iuh.fit.se.dto.danhmuc.DanhMucResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DanhMucService {

    List<DanhMucResponse> layTatCa();

    DanhMucResponse taoMoi(DanhMucRequest request, MultipartFile file);

    DanhMucResponse capNhat(Integer id, DanhMucRequest request, MultipartFile file);

    void xoa(Integer id);
}
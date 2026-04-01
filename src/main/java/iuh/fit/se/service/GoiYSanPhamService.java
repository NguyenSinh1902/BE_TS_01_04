package iuh.fit.se.service;

import iuh.fit.se.dto.goiy.GoiYSanPhamRequest;
import iuh.fit.se.dto.goiy.GoiYSanPhamResponse;
import java.util.List;

public interface GoiYSanPhamService {
    List<GoiYSanPhamResponse> layGoiYChoSanPham(Integer idSanPhamChinh);
}
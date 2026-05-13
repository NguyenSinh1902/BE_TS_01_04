package iuh.fit.se.service.impl;

import iuh.fit.se.dto.goiy.GoiYSanPhamResponse;
import iuh.fit.se.repository.HoaDonRepository;
import iuh.fit.se.service.GoiYSanPhamService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoiYSanPhamServiceImpl implements GoiYSanPhamService {

    private final HoaDonRepository hoaDonRepository;

    public GoiYSanPhamServiceImpl(HoaDonRepository hoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
    }

    @Override
    public List<GoiYSanPhamResponse> layGoiYNgayLapTuc(Integer idSanPhamChinh) {
        List<Object[]> results = hoaDonRepository.findTopToppingsForProduct(idSanPhamChinh, PageRequest.of(0, 3));

        return results.stream().map(obj -> new GoiYSanPhamResponse(
                null,
                (Integer) obj[0],
                (String) obj[1],
                (String) obj[2],
                1.0f
        )).collect(Collectors.toList());
    }
}
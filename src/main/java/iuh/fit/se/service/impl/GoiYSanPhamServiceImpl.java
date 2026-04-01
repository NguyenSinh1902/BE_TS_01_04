package iuh.fit.se.service.impl;

import iuh.fit.se.dto.goiy.GoiYSanPhamResponse;
import iuh.fit.se.entity.GoiYSanPham;
import iuh.fit.se.mapper.GoiYSanPhamMapper;
import iuh.fit.se.repository.GoiYSanPhamRepository;
import iuh.fit.se.service.GoiYSanPhamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoiYSanPhamServiceImpl implements GoiYSanPhamService {

    private final GoiYSanPhamRepository goiYSanPhamRepository;
    private final GoiYSanPhamMapper goiYSanPhamMapper;

    public GoiYSanPhamServiceImpl(GoiYSanPhamRepository goiYSanPhamRepository,
                                  GoiYSanPhamMapper goiYSanPhamMapper) {
        this.goiYSanPhamRepository = goiYSanPhamRepository;
        this.goiYSanPhamMapper = goiYSanPhamMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoiYSanPhamResponse> layGoiYChoSanPham(Integer idSanPhamChinh) {

        List<GoiYSanPham> recommendations = goiYSanPhamRepository.findTopSuggestions(idSanPhamChinh);

        return recommendations.stream()
                .map(goiYSanPhamMapper::toResponse)
                .collect(Collectors.toList());
    }
}
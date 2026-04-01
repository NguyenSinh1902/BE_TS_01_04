package iuh.fit.se.service.impl;

import iuh.fit.se.dto.khuyenmai.KhuyenMaiRequest;
import iuh.fit.se.dto.khuyenmai.KhuyenMaiResponse;
import iuh.fit.se.entity.KhuyenMai;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.mapper.KhuyenMaiMapper;
import iuh.fit.se.repository.KhuyenMaiRepository;
import iuh.fit.se.service.KhuyenMaiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhuyenMaiServiceImpl implements KhuyenMaiService {

    private final KhuyenMaiRepository khuyenMaiRepository;
    private final KhuyenMaiMapper khuyenMaiMapper;

    public KhuyenMaiServiceImpl(KhuyenMaiRepository khuyenMaiRepository, KhuyenMaiMapper khuyenMaiMapper) {
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.khuyenMaiMapper = khuyenMaiMapper;
    }

    @Override
    @Transactional
    public KhuyenMaiResponse taoMoi(KhuyenMaiRequest request) {
        if (khuyenMaiRepository.existsByMaCodeAndThoiGianXoa(request.maCode(), 0L)) {
            throw new BadRequestException("Mã khuyến mãi '" + request.maCode() + "' đã tồn tại!");
        }
        validateNgayKhuyenMai(request.ngayBatDau(), request.ngayHetHan());

        KhuyenMai km = khuyenMaiMapper.toEntity(request);
        return khuyenMaiMapper.toResponse(khuyenMaiRepository.save(km));
    }

    @Override
    @Transactional
    public KhuyenMaiResponse capNhat(Integer id, KhuyenMaiRequest request) {
        KhuyenMai existingKm = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã khuyến mãi!"));

        if (!existingKm.getMaCode().equals(request.maCode()) &&
                khuyenMaiRepository.existsByMaCodeAndThoiGianXoa(request.maCode(), 0L)) {
            throw new BadRequestException("Mã code mới đã tồn tại trên hệ thống!");
        }

        validateNgayKhuyenMai(request.ngayBatDau(), request.ngayHetHan());

        khuyenMaiMapper.updateEntityFromRequest(request, existingKm);

        return khuyenMaiMapper.toResponse(khuyenMaiRepository.save(existingKm));
    }

    @Override
    public List<KhuyenMaiResponse> layTatCa() {
        return khuyenMaiRepository.findByThoiGianXoaOrderByNgayBatDauDesc(0L).stream()
                .map(khuyenMaiMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<KhuyenMaiResponse> layDanhSachHoatDong() {
        return khuyenMaiRepository.findActivePromotions(LocalDateTime.now()).stream()
                .map(khuyenMaiMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public KhuyenMaiResponse kiemTraMaCode(String maCode, BigDecimal tongTienHang) {
        KhuyenMai km = khuyenMaiRepository.findByMaCodeAndThoiGianXoa(maCode, 0L)
                .orElseThrow(() -> new BadRequestException("Mã khuyến mãi không hợp lệ!"));

        LocalDateTime bayGio = LocalDateTime.now();

        if (bayGio.isBefore(km.getNgayBatDau())) {
            throw new BadRequestException("Chương trình khuyến mãi chưa bắt đầu!");
        }
        if (bayGio.isAfter(km.getNgayHetHan())) {
            throw new BadRequestException("Mã khuyến mãi đã hết hạn sử dụng!");
        }
        if (tongTienHang.compareTo(km.getDonToiThieu()) < 0) {
            throw new BadRequestException("Đơn hàng tối thiểu phải từ " + km.getDonToiThieu() + "đ để áp dụng mã này.");
        }

        return khuyenMaiMapper.toResponse(km);
    }

    @Override
    @Transactional
    public void xoa(Integer id) {
        KhuyenMai km = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã khuyến mãi!"));
        km.setThoiGianXoa(System.currentTimeMillis());
        khuyenMaiRepository.save(km);
    }

    private void validateNgayKhuyenMai(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new BadRequestException("Ngày bắt đầu không được sau ngày kết thúc!");
        }
    }
}
package iuh.fit.se.service.impl;

import iuh.fit.se.dto.khachhang.KhachHangRequest;
import iuh.fit.se.dto.khachhang.KhachHangResponse;
import iuh.fit.se.entity.KhachHang;
import iuh.fit.se.enums.HangThanhVien;
import iuh.fit.se.enums.TrangThaiKhachHang;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.mapper.KhachHangMapper;
import iuh.fit.se.repository.KhachHangRepository;
import iuh.fit.se.service.KhachHangService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final KhachHangMapper khachHangMapper;

    // Constructor
    public KhachHangServiceImpl(KhachHangRepository khachHangRepository, KhachHangMapper khachHangMapper) {
        this.khachHangRepository = khachHangRepository;
        this.khachHangMapper = khachHangMapper;
    }

    private KhachHang findActive(Integer id) {
        return khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng!"));
    }

    @Override
    public List<KhachHangResponse> layTatCa() {
        return khachHangRepository.findAll().stream()
                .map(khachHangMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public KhachHangResponse layChiTiet(Integer id) {
        KhachHang kh = findActive(id);
        return khachHangMapper.toResponse(kh);
    }

    @Override
    @Transactional
    public KhachHangResponse taoMoi(KhachHangRequest request) {
        if (khachHangRepository.existsBySoDienThoaiAndThoiGianXoa(request.soDienThoai(), 0L)) {
            throw new BadRequestException("Số điện thoại này đã được đăng ký!");
        }
        KhachHang kh = khachHangMapper.toEntity(request);
        return khachHangMapper.toResponse(khachHangRepository.save(kh));
    }

    @Override
    @Transactional
    public KhachHangResponse capNhat(Integer id, KhachHangRequest request) {
        KhachHang existingKh = findActive(id);

        if (!existingKh.getSoDienThoai().equals(request.soDienThoai()) &&
                khachHangRepository.existsBySoDienThoaiAndThoiGianXoa(request.soDienThoai(), 0L)) {
            throw new BadRequestException("Số điện thoại mới đã tồn tại trên hệ thống!");
        }

        khachHangMapper.updateEntityFromRequest(request, existingKh);

        return khachHangMapper.toResponse(khachHangRepository.save(existingKh));
    }

    @Override
    @Transactional
    public void xoa(Integer id) {
        KhachHang kh = findActive(id);
        kh.setThoiGianXoa(System.currentTimeMillis());
        khachHangRepository.save(kh);
    }

    @Override
    @Transactional
    public void congDiem(Integer id, Integer diem) {

        KhachHang kh = findActive(id);

        kh.setDiemTichLuy(kh.getDiemTichLuy() + diem);

        kh.setTongDiemDaTichLuy(kh.getTongDiemDaTichLuy() + diem);

        kh.capNhatHangThanhVien();

        khachHangRepository.save(kh);
    }

    @Override
    @Transactional
    public void capNhatTrangThai(Integer id, TrangThaiKhachHang trangThaiMoi) {

        KhachHang kh = findActive(id);

        kh.setTrangThai(trangThaiMoi);

        khachHangRepository.save(kh);
    }

    @Override
    @Transactional
    public void tichDiemVaThangHang(Integer idKhachHang, int diemCongThem) {
        KhachHang kh = khachHangRepository.findById(idKhachHang)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại"));

        kh.setDiemTichLuy(kh.getDiemTichLuy() + diemCongThem);
        kh.setTongDiemDaTichLuy(kh.getTongDiemDaTichLuy() + diemCongThem);

        kh.capNhatHangThanhVien();

        khachHangRepository.save(kh);
    }
}
package iuh.fit.se.service.impl;

import iuh.fit.se.dto.nhanvien.NhanVienRequest;
import iuh.fit.se.dto.nhanvien.NhanVienResponse;
import iuh.fit.se.entity.NhanVien;
import iuh.fit.se.enums.TrangThaiNhanVien;
import iuh.fit.se.enums.VaiTroNhanVien;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.mapper.NhanVienMapper;
import iuh.fit.se.repository.NhanVienRepository;
import iuh.fit.se.service.NhanVienService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository nhanVienRepository;
    private final NhanVienMapper nhanVienMapper;

    public NhanVienServiceImpl(NhanVienRepository nhanVienRepository, NhanVienMapper nhanVienMapper) {
        this.nhanVienRepository = nhanVienRepository;
        this.nhanVienMapper = nhanVienMapper;
    }

    private NhanVien findActive(Integer id) {
        return nhanVienRepository.findByIdNhanVienAndThoiGianXoa(id, 0L)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên không tồn tại hoặc đã bị xóa!"));
    }

    @Override
    public List<NhanVienResponse> layTatCaNhanVien() {
        return nhanVienRepository.findAll().stream()
                .map(nhanVienMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NhanVienResponse> layNhanVienChoDuyet() {
        return nhanVienRepository.findByEmailVerifiedTrueAndTrangThaiAndThoiGianXoa(
                        TrangThaiNhanVien.CHO_DUYET, 0L)
                .stream()
                .map(nhanVienMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NhanVienResponse> layDanhSachVanHanh() {
        return nhanVienRepository.findOperatingEmployees()
                .stream()
                .map(nhanVienMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void capNhatTrangThai(Integer id, TrangThaiNhanVien trangThai) {
        NhanVien nv = findActive(id);

        if (trangThai == TrangThaiNhanVien.HOAT_DONG && !nv.isEmailVerified()) {
            throw new BadRequestException("Không thể duyệt nhân viên chưa xác thực email!");
        }

        nv.setTrangThai(trangThai);
        nhanVienRepository.save(nv);
    }

    @Override
    @Transactional
    public void xoaNhanVien(Integer id) {
        NhanVien nv = findActive(id);
        nv.setThoiGianXoa(System.currentTimeMillis());
        nhanVienRepository.save(nv);
    }

    @Override
    public NhanVienResponse layChiTiet(Integer id) {
        return nhanVienMapper.toResponse(findActive(id));
    }

    @Override
    @Transactional
    public NhanVienResponse capNhatThongTin(Integer id, NhanVienRequest request) {
        NhanVien nv = findActive(id);

        if (!nv.getSoDienThoai().equals(request.soDienThoai()) &&
                nhanVienRepository.existsBySoDienThoaiAndThoiGianXoa(request.soDienThoai(), 0L)) {
            throw new BadRequestException("Số điện thoại mới đã được sử dụng bởi nhân viên khác!");
        }

        nhanVienMapper.updateEntityFromRequest(request, nv);

        return nhanVienMapper.toResponse(nhanVienRepository.save(nv));
    }

    @Override
    @Transactional
    public void doiVaiTro(Integer id, VaiTroNhanVien vaiTroMoi) {
        NhanVien nv = findActive(id);
        nv.setVaiTro(vaiTroMoi);
        nhanVienRepository.save(nv);
    }
}
package iuh.fit.se.service.impl;

import iuh.fit.se.dto.danhmuc.DanhMucRequest;
import iuh.fit.se.dto.danhmuc.DanhMucResponse;
import iuh.fit.se.entity.DanhMuc;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.mapper.DanhMucMapper;
import iuh.fit.se.repository.DanhMucRepository;
import iuh.fit.se.repository.SanPhamRepository;
import iuh.fit.se.service.DanhMucService;
import iuh.fit.se.service.FirebaseStorageService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DanhMucServiceImpl implements DanhMucService {

    private final DanhMucRepository danhMucRepository;
    private final SanPhamRepository sanPhamRepository;
    private final DanhMucMapper danhMucMapper;
    private final FirebaseStorageService firebaseStorageService;

    public DanhMucServiceImpl(DanhMucRepository danhMucRepository, SanPhamRepository sanPhamRepository, DanhMucMapper danhMucMapper, FirebaseStorageService firebaseStorageService) {
        this.danhMucRepository = danhMucRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.danhMucMapper = danhMucMapper;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Override
    public List<DanhMucResponse> layTatCa() {
        return danhMucRepository.findAll().stream()
                .map(danhMucMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DanhMucResponse taoMoi(DanhMucRequest request, MultipartFile file) {
        if (danhMucRepository.existsByTenDanhMucAndThoiGianXoa(request.tenDanhMuc(), 0L)) {
            throw new BadRequestException("Tên danh mục '" + request.tenDanhMuc() + "' đã tồn tại!");
        }

        DanhMuc dm = danhMucMapper.toEntity(request);

        try {
            if (file != null && !file.isEmpty()) {
                String url = firebaseStorageService.uploadFile(file);
                dm.setDuongDanAnh(url);
            }
        } catch (IOException e) {
            throw new BadRequestException("Lỗi khi upload ảnh danh mục lên Firebase: " + e.getMessage());
        }

        return danhMucMapper.toResponse(danhMucRepository.save(dm));
    }

    @Override
    @Transactional
    public DanhMucResponse capNhat(Integer id, DanhMucRequest request, MultipartFile file) {
        DanhMuc dm = danhMucRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        if (!dm.getTenDanhMuc().equals(request.tenDanhMuc()) &&
                danhMucRepository.existsByTenDanhMucAndThoiGianXoa(request.tenDanhMuc(), 0L)) {
            throw new BadRequestException("Tên danh mục này đã tồn tại!");
        }

        danhMucMapper.updateEntityFromRequest(request, dm);

        try {
            if (file != null && !file.isEmpty()) {
                String url = firebaseStorageService.uploadFile(file);
                dm.setDuongDanAnh(url);
            }
        } catch (IOException e) {
            throw new BadRequestException("Lỗi khi cập nhật ảnh danh mục: " + e.getMessage());
        }

        return danhMucMapper.toResponse(danhMucRepository.save(dm));
    }

    @Override
    @Transactional
    public void xoa(Integer id) {
        DanhMuc dmCanXoa = danhMucRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        if (dmCanXoa.getLaHeThong()) {
            throw new BadRequestException("Không thể xóa danh mục mặc định của hệ thống!");
        }

        DanhMuc dmHeThong = danhMucRepository.findByLaHeThongTrueAndThoiGianXoa(0L)
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Chưa cấu hình danh mục mặc định!"));

        sanPhamRepository.chuyenDanhMuc(dmCanXoa.getIdDanhMuc(), dmHeThong.getIdDanhMuc());

        dmCanXoa.setThoiGianXoa(System.currentTimeMillis());
        danhMucRepository.save(dmCanXoa);
    }

}
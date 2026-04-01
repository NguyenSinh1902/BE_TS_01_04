package iuh.fit.se.service.impl;

import iuh.fit.se.dto.sanpham.HomeResponse;
import iuh.fit.se.dto.sanpham.SanPhamRequest;
import iuh.fit.se.dto.sanpham.SanPhamResponse;
import iuh.fit.se.entity.BienTheSanPham;
import iuh.fit.se.entity.DanhMuc;
import iuh.fit.se.entity.SanPham;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.mapper.BienTheMapper;
import iuh.fit.se.mapper.SanPhamMapper;
import iuh.fit.se.repository.DanhMucRepository;
import iuh.fit.se.repository.SanPhamRepository;
import iuh.fit.se.service.SanPhamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;
    private final SanPhamMapper sanPhamMapper;
    private final BienTheMapper bienTheMapper;

    public SanPhamServiceImpl(SanPhamRepository sanPhamRepository,
                              DanhMucRepository danhMucRepository,
                              SanPhamMapper sanPhamMapper,
                              BienTheMapper bienTheMapper) {
        this.sanPhamRepository = sanPhamRepository;
        this.danhMucRepository = danhMucRepository;
        this.sanPhamMapper = sanPhamMapper;
        this.bienTheMapper = bienTheMapper;
    }

    private SanPham findActive(Integer id) {
        return sanPhamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại hoặc đã bị xóa!"));
    }

    @Override
    public List<SanPhamResponse> layTatCa() {
        return sanPhamRepository.findAll().stream()
                .map(sanPhamMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SanPhamResponse taoMoi(SanPhamRequest request) {
        if (sanPhamRepository.existsByTenSanPhamAndThoiGianXoa(request.tenSanPham(), 0L)) {
            throw new BadRequestException("Tên sản phẩm '" + request.tenSanPham() + "' đã tồn tại!");
        }

        DanhMuc dm = danhMucRepository.findById(request.idDanhMuc())
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại"));

        SanPham sanPham = sanPhamMapper.toEntity(request);
        sanPham.setDanhMuc(dm);

        if (sanPham.getDanhSachBienThe() != null) {
            sanPham.getDanhSachBienThe().forEach(bt -> bt.setSanPham(sanPham));
        }

        return sanPhamMapper.toResponse(sanPhamRepository.save(sanPham));
    }

    @Override
    @Transactional
    public SanPhamResponse capNhat(Integer id, SanPhamRequest request) {
        SanPham existingSp = findActive(id);

        if (!existingSp.getTenSanPham().equalsIgnoreCase(request.tenSanPham()) &&
                sanPhamRepository.existsByTenSanPhamAndThoiGianXoa(request.tenSanPham(), 0L)) {
            throw new BadRequestException("Tên sản phẩm mới đã tồn tại!");
        }

        sanPhamMapper.updateEntityFromRequest(request, existingSp);

        if (!existingSp.getDanhMuc().getIdDanhMuc().equals(request.idDanhMuc())) {
            DanhMuc dm = danhMucRepository.findById(request.idDanhMuc())
                    .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại"));
            existingSp.setDanhMuc(dm);
        }

        existingSp.getDanhSachBienThe().clear();
        if (request.danhSachBienThe() != null) {
            List<BienTheSanPham> newBienThes = request.danhSachBienThe().stream()
                    .map(btReq -> {
                        BienTheSanPham bt = bienTheMapper.toEntity(btReq);
                        bt.setSanPham(existingSp);
                        bt.setThoiGianXoa(0L);
                        return bt;
                    }).collect(Collectors.toList());
            existingSp.getDanhSachBienThe().addAll(newBienThes);
        }

        return sanPhamMapper.toResponse(sanPhamRepository.save(existingSp));
    }

    @Override
    @Transactional
    public void xoa(Integer id) {
        SanPham sp = findActive(id);
        long now = System.currentTimeMillis();

        sp.setThoiGianXoa(now);

        if (sp.getDanhSachBienThe() != null) {
            sp.getDanhSachBienThe().forEach(bt -> bt.setThoiGianXoa(now));
        }

        sanPhamRepository.save(sp);
    }

    @Override
    public List<SanPhamResponse> layMenuChinh() {
        return sanPhamRepository.findByLaToppingFalse().stream()
                .map(sanPhamMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SanPhamResponse> layDanhSachTopping() {
        return sanPhamRepository.findByLaToppingTrue().stream()
                .map(sanPhamMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SanPhamResponse> layTheoDanhMuc(Integer idDanhMuc) {
        return sanPhamRepository.findByDanhMuc_IdDanhMuc(idDanhMuc).stream()
                .map(sanPhamMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HomeResponse layDuLieuTrangChu() {
        List<SanPhamResponse> giamGia = sanPhamRepository.findSanPhamGiamGia().stream()
                .map(sanPhamMapper::toResponse).collect(Collectors.toList());

        List<SanPhamResponse> moi = sanPhamRepository.findTop10ByOrderByIdSanPhamDesc().stream()
                .map(sanPhamMapper::toResponse).collect(Collectors.toList());

        List<SanPhamResponse> hot = sanPhamRepository.findByLaToppingFalse().stream()
                .limit(5)
                .map(sanPhamMapper::toResponse).collect(Collectors.toList());

        return new HomeResponse(hot, giamGia, moi);
    }
}
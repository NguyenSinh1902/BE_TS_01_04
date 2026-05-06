package iuh.fit.se.service.impl;

import iuh.fit.se.dto.bienthe.BienTheRequest;
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
import java.util.Objects;
import java.util.Set;
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
        // 1. Tìm sản phẩm đang hoạt động
        SanPham existingSp = findActive(id);

        // 2. Kiểm tra trùng tên (Nếu đổi tên thì tên mới không được trùng với sản phẩm khác)
        if (!existingSp.getTenSanPham().equalsIgnoreCase(request.tenSanPham()) &&
                sanPhamRepository.existsByTenSanPhamAndThoiGianXoa(request.tenSanPham(), 0L)) {
            throw new BadRequestException("Tên sản phẩm '" + request.tenSanPham() + "' đã tồn tại!");
        }

        // 3. Map các thông tin cơ bản (tenSanPham, duongDanAnh, laTopping...)
        sanPhamMapper.updateEntityFromRequest(request, existingSp);

        // 4. Cập nhật Danh mục nếu có thay đổi
        if (!existingSp.getDanhMuc().getIdDanhMuc().equals(request.idDanhMuc())) {
            DanhMuc dm = danhMucRepository.findById(request.idDanhMuc())
                    .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại"));
            existingSp.setDanhMuc(dm);
        }

        // 5. XỬ LÝ DANH SÁCH BIẾN THỂ (PHẦN QUAN TRỌNG NHẤT)
        List<BienTheSanPham> currentList = existingSp.getDanhSachBienThe();

        // Thu thập danh sách ID biến thể mới từ request để phục vụ việc Xóa mềm
        Set<Integer> newIds = request.danhSachBienThe().stream()
                .map(BienTheRequest::idBienThe)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // A. XỬ LÝ CẬP NHẬT VÀ THÊM MỚI
        for (BienTheRequest btReq : request.danhSachBienThe()) {
            if (btReq.idBienThe() != null) {
                // Trường hợp CẬP NHẬT: Tìm trong danh sách hiện tại
                BienTheSanPham existingBt = currentList.stream()
                        .filter(bt -> bt.getIdBienThe().equals(btReq.idBienThe()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể ID: " + btReq.idBienThe() + " để cập nhật!"));

                // Cập nhật thông tin vào biến thể cũ
                existingBt.setTenKichCo(btReq.tenKichCo());
                existingBt.setGiaBan(btReq.giaBan());
                existingBt.setPhanTramGiamGia(btReq.phanTramGiamGia() != null ? btReq.phanTramGiamGia() : 0);
                existingBt.setSoLuongTonKho(btReq.soLuongTonKho() != null ? btReq.soLuongTonKho() : -1);
                existingBt.setThoiGianXoa(0L); // Đảm bảo nó không bị đánh dấu xóa
            } else {
                // Trường hợp THÊM MỚI (idBienThe == null)
                BienTheSanPham btMoi = bienTheMapper.toEntity(btReq);
                btMoi.setSanPham(existingSp);
                btMoi.setThoiGianXoa(0L);
                currentList.add(btMoi);
            }
        }

        // B. XỬ LÝ XÓA MỀM
        currentList.forEach(bt -> {
            if (bt.getIdBienThe() != null && !newIds.contains(bt.getIdBienThe())) {
                bt.setThoiGianXoa(System.currentTimeMillis());
            }
        });

        // --- THÊM DÒNG NÀY ĐỂ "LÀM SẠCH" LIST TRƯỚC KHI TRẢ VỀ ---
        existingSp.getDanhSachBienThe().removeIf(bt -> bt.getThoiGianXoa() > 0);

        // 6. Lưu và trả về
        SanPham savedSp = sanPhamRepository.save(existingSp);
        return sanPhamMapper.toResponse(savedSp);
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
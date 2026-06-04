package iuh.fit.se.service.impl;

import iuh.fit.se.dto.bep.CheBienRequest;
import iuh.fit.se.dto.bep.NguyenLieuRequest;
import iuh.fit.se.dto.bep.NguyenLieuResponse;
import iuh.fit.se.entity.ChiTietHoaDon;
import iuh.fit.se.entity.DinhLuongMonAn;
import iuh.fit.se.entity.NguyenLieu;
import iuh.fit.se.entity.RecipeChuyenDoi;
import iuh.fit.se.entity.SanPham;
import iuh.fit.se.entity.BienTheSanPham;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.mapper.NguyenLieuMapper;
import iuh.fit.se.repository.ChiTietHoaDonRepository;
import iuh.fit.se.repository.DinhLuongMonAnRepository;
import iuh.fit.se.repository.NguyenLieuRepository;
import iuh.fit.se.repository.RecipeChuyenDoiRepository;
import iuh.fit.se.repository.LichSuXuatHuyRepository;
import iuh.fit.se.entity.LichSuXuatHuy;
import iuh.fit.se.repository.SanPhamRepository;
import iuh.fit.se.repository.BienTheSanPhamRepository;
import iuh.fit.se.service.NguyenLieuService;
import iuh.fit.se.service.FirebaseMessagingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NguyenLieuServiceImpl implements NguyenLieuService {

    private final NguyenLieuRepository nguyenLieuRepository;
    private final RecipeChuyenDoiRepository recipeChuyenDoiRepository;
    private final DinhLuongMonAnRepository dinhLuongMonAnRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;
    private final SanPhamRepository sanPhamRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final LichSuXuatHuyRepository lichSuXuatHuyRepository;
    private final NguyenLieuMapper nguyenLieuMapper;
    private final FirebaseMessagingService firebaseMessagingService;

    public NguyenLieuServiceImpl(NguyenLieuRepository nguyenLieuRepository,
                                 RecipeChuyenDoiRepository recipeChuyenDoiRepository,
                                 DinhLuongMonAnRepository dinhLuongMonAnRepository,
                                 ChiTietHoaDonRepository chiTietHoaDonRepository,
                                 SanPhamRepository sanPhamRepository,
                                 BienTheSanPhamRepository bienTheSanPhamRepository,
                                 LichSuXuatHuyRepository lichSuXuatHuyRepository,
                                 NguyenLieuMapper nguyenLieuMapper,
                                 FirebaseMessagingService firebaseMessagingService) {
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.recipeChuyenDoiRepository = recipeChuyenDoiRepository;
        this.dinhLuongMonAnRepository = dinhLuongMonAnRepository;
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
        this.lichSuXuatHuyRepository = lichSuXuatHuyRepository;
        this.nguyenLieuMapper = nguyenLieuMapper;
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @Override
    public List<NguyenLieuResponse> layKhoNguyenLieu() {
        return nguyenLieuRepository.findByThoiGianXoa(0L).stream()
                .map(nguyenLieuMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NguyenLieuResponse> layKhoNguyenLieuTheoLoai(iuh.fit.se.enums.LoaiNguyenLieu loai) {
        return nguyenLieuRepository.findByLoaiNguyenLieuAndThoiGianXoa(loai, 0L).stream()
                .map(nguyenLieuMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NguyenLieuResponse themNguyenLieu(NguyenLieuRequest request) {
        NguyenLieu nl = nguyenLieuMapper.toEntity(request);
        return nguyenLieuMapper.toResponse(nguyenLieuRepository.save(nl));
    }

    @Override
    @Transactional
    public NguyenLieuResponse capNhatNguyenLieu(Integer id, NguyenLieuRequest request) {
        NguyenLieu nl = nguyenLieuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu"));
        
        nl.setTenNguyenLieu(request.tenNguyenLieu());
        nl.setDonViTinh(request.donViTinh());
        nl.setLoaiNguyenLieu(request.loaiNguyenLieu());
        nl.setGiaNhapDonVi(request.giaNhapDonVi());
        // KHÔNG cập nhật số lượng tồn ở đây
        
        return nguyenLieuMapper.toResponse(nguyenLieuRepository.save(nl));
    }

    @Override
    @Transactional
    public NguyenLieuResponse nhapThemHang(Integer id, BigDecimal soLuongNhap) {
        if (soLuongNhap.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Số lượng nhập phải lớn hơn 0");
        }
        NguyenLieu nl = nguyenLieuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu"));
        
        nl.setSoLuongTon(nl.getSoLuongTon().add(soLuongNhap));
        return nguyenLieuMapper.toResponse(nguyenLieuRepository.save(nl));
    }

    @Override
    @Transactional
    public void xuatHuyKho(Integer idNguyenLieu, BigDecimal soLuongHuy, String lyDo) {
        if (soLuongHuy.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Số lượng hủy phải lớn hơn 0");
        }
        NguyenLieu nl = nguyenLieuRepository.findById(idNguyenLieu)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu"));
        
        if (nl.getSoLuongTon().compareTo(soLuongHuy) < 0) {
            throw new BadRequestException("Số lượng trong kho không đủ để hủy (Tồn: " + nl.getSoLuongTon() + ")");
        }
        
        nl.setSoLuongTon(nl.getSoLuongTon().subtract(soLuongHuy));
        nguyenLieuRepository.save(nl);
        
        LichSuXuatHuy ls = new LichSuXuatHuy(nl, soLuongHuy, lyDo);
        lichSuXuatHuyRepository.save(ls);
    }

    @Override
    @Transactional
    public void cheBienBanThanhPham(CheBienRequest request) {
        NguyenLieu btp = nguyenLieuRepository.findById(request.idBanThanhPham())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bán thành phẩm"));

        List<RecipeChuyenDoi> recipes = recipeChuyenDoiRepository.findByBanThanhPham_IdNguyenLieu(request.idBanThanhPham());
        
        if (recipes.isEmpty()) {
            throw new BadRequestException("Bán thành phẩm này chưa được thiết lập công thức chuyển đổi!");
        }

        for (RecipeChuyenDoi rc : recipes) {
            BigDecimal luongThoCanDung = rc.getHamLuongCan()
                    .multiply(request.soLuongSanXuat())
                    .divide(rc.getSoLuongThuDuoc(), 2, RoundingMode.HALF_UP);
            NguyenLieu khoTho = rc.getNguyenLieuTho();
            
            if (khoTho.getSoLuongTon().compareTo(luongThoCanDung) < 0) {
                throw new BadRequestException("Kho không đủ " + khoTho.getTenNguyenLieu() + " (Cần: " + luongThoCanDung + ", Hiện có: " + khoTho.getSoLuongTon() + ")");
            }
            
            khoTho.setSoLuongTon(khoTho.getSoLuongTon().subtract(luongThoCanDung));
            nguyenLieuRepository.save(khoTho);
        }

        btp.setSoLuongTon(btp.getSoLuongTon().add(request.soLuongSanXuat()));
        nguyenLieuRepository.save(btp);
    }

    @Override
    public java.util.Map<String, Object> tinhSoLuongLyConLai(Integer idSanPham) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("idSanPham", idSanPham);
        
        SanPham sp = sanPhamRepository.findById(idSanPham).orElse(null);
        if (sp == null || sp.getDanhSachBienThe() == null || sp.getDanhSachBienThe().isEmpty()) {
            result.put("soLyConLai", 0);
            return result;
        }
        
        // Lấy biến thể đầu tiên (Thường là Size nhỏ nhất mặc định) để hiển thị số ly còn lại chung cho Món
        BienTheSanPham bienTheChuan = sp.getDanhSachBienThe().get(0);
        Integer idBienTheChuan = bienTheChuan.getIdBienThe();
        
        result.put("idBienTheApDung", idBienTheChuan);
        result.put("tenKichCoApDung", bienTheChuan.getTenKichCo());

        List<DinhLuongMonAn> dinhLuongs = dinhLuongMonAnRepository.findByBienThe_IdBienThe(idBienTheChuan);
        if (dinhLuongs.isEmpty()) {
            result.put("soLyConLai", 99);
            return result;
        }

        int soLyToiDa = Integer.MAX_VALUE;

        for (DinhLuongMonAn dl : dinhLuongs) {
            NguyenLieu nl = dl.getNguyenLieu();
            if (dl.getSoLuongTieuHao().compareTo(BigDecimal.ZERO) <= 0) continue;

            int phanChia = nl.getSoLuongTon().divide(dl.getSoLuongTieuHao(), 0, RoundingMode.DOWN).intValue();
            
            if (phanChia < soLyToiDa) {
                soLyToiDa = phanChia;
            }
        }
        
        result.put("soLyConLai", soLyToiDa == Integer.MAX_VALUE ? 0 : soLyToiDa);
        return result;
    }

    @Override
    @Transactional
    public void bepXongMon(Integer idChiTietDonHang) {
        ChiTietHoaDon cthd = chiTietHoaDonRepository.findById(idChiTietDonHang).orElse(null);
        if (cthd == null) return;
        
        Integer idSanPhamTarget = cthd.getBienThe().getSanPham().getIdSanPham(); 
        Integer idBienTheTarget = cthd.getBienThe().getIdBienThe();
        String tenMonAn = cthd.getBienThe().getSanPham().getTenSanPham() + " (" + cthd.getBienThe().getTenKichCo() + ")";
        String tenBan = cthd.getHoaDon().getPhieuDatBan() != null 
                        ? "Phục vụ tại quán (Theo mã đặt bàn)" : "Đơn mang về";

        BigDecimal tongGiaVonLyNuoc = BigDecimal.ZERO;
        BigDecimal soLuongLy = new BigDecimal(cthd.getSoLuong() != null ? cthd.getSoLuong() : 1);

        // 1. TRỪ KHO MÓN CHÍNH THEO SỐ LƯỢNG LY
        List<DinhLuongMonAn> dinhLuongs = dinhLuongMonAnRepository.findByBienThe_IdBienThe(idBienTheTarget);
        
        for (DinhLuongMonAn dl : dinhLuongs) {
            NguyenLieu nl = dl.getNguyenLieu();
            
            BigDecimal tongTieuHao = dl.getSoLuongTieuHao().multiply(soLuongLy);
            BigDecimal tonKhoMoi = nl.getSoLuongTon().subtract(tongTieuHao);
            nl.setSoLuongTon(tonKhoMoi.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : tonKhoMoi);
            nguyenLieuRepository.save(nl);

            BigDecimal chiPhiNguyenLieu = tongTieuHao.multiply(nl.getGiaNhapDonVi());
            tongGiaVonLyNuoc = tongGiaVonLyNuoc.add(chiPhiNguyenLieu);
        }

        // 2. TRỪ KHO TOPPING ĐI KÈM (NẾU CÓ)
        if (cthd.getDanhSachTopping() != null) {
            for (iuh.fit.se.entity.ChiTietHoaDonTopping tp : cthd.getDanhSachTopping()) {
                if (tp.getBienTheTopping() == null) continue;
                List<DinhLuongMonAn> dlToppings = dinhLuongMonAnRepository.findByBienThe_IdBienThe(tp.getBienTheTopping().getIdBienThe());
                
                for (DinhLuongMonAn dlTp : dlToppings) {
                    NguyenLieu nlTp = dlTp.getNguyenLieu();
                    BigDecimal tongTieuHaoTp = dlTp.getSoLuongTieuHao().multiply(soLuongLy); // Nhân theo số lượng ly chính
                    
                    BigDecimal tonKhoMoiTp = nlTp.getSoLuongTon().subtract(tongTieuHaoTp);
                    nlTp.setSoLuongTon(tonKhoMoiTp.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : tonKhoMoiTp);
                    nguyenLieuRepository.save(nlTp);

                    BigDecimal chiPhiTp = tongTieuHaoTp.multiply(nlTp.getGiaNhapDonVi());
                    tongGiaVonLyNuoc = tongGiaVonLyNuoc.add(chiPhiTp);
                }
            }
        }

        cthd.setGiaVonDonVi(tongGiaVonLyNuoc);
        chiTietHoaDonRepository.save(cthd);
        
        try {
            firebaseMessagingService.sendNotificationToTopic("THU_NGAN", "🥤 Món nước đã xong!", "Món [" + tenMonAn + "] x" + cthd.getSoLuong() + " của " + tenBan + " đã sẵn sàng phục vụ!");
            firebaseMessagingService.sendNotificationToTopic("PHUC_VU", "🥤 Món nước đã xong!", "Món [" + tenMonAn + "] x" + cthd.getSoLuong() + " của " + tenBan + " đã sẵn sàng phục vụ!");
        } catch (Exception e) {
            System.out.println("⚠️ Lỗi FCM: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void caiDatRecipeChuyenDoi(Integer idBanThanhPham, Integer idNguyenLieuTho, BigDecimal hamLuongCan, BigDecimal soLuongThuDuoc) {
        NguyenLieu btp = nguyenLieuRepository.findById(idBanThanhPham).orElseThrow(() -> new ResourceNotFoundException("Bán thành phẩm không tồn tại"));
        NguyenLieu tho = nguyenLieuRepository.findById(idNguyenLieuTho).orElseThrow(() -> new ResourceNotFoundException("Nguyên liệu thô không tồn tại"));
        
        RecipeChuyenDoi r = recipeChuyenDoiRepository.findByBanThanhPham_IdNguyenLieuAndNguyenLieuTho_IdNguyenLieu(idBanThanhPham, idNguyenLieuTho)
                .orElse(new RecipeChuyenDoi());

        r.setBanThanhPham(btp);
        r.setNguyenLieuTho(tho);
        r.setHamLuongCan(hamLuongCan);
        r.setSoLuongThuDuoc(soLuongThuDuoc);
        recipeChuyenDoiRepository.save(r);
    }

    @Override
    @Transactional
    public void caiDatDinhLuongMonAn(Integer idBienThe, Integer idNguyenLieu, BigDecimal soLuongTieuHao) {
        BienTheSanPham bt = bienTheSanPhamRepository.findById(idBienThe).orElseThrow(() -> new ResourceNotFoundException("Biến thể (Size) không tồn tại"));
        NguyenLieu nl = nguyenLieuRepository.findById(idNguyenLieu).orElseThrow(() -> new ResourceNotFoundException("Nguyên liệu không tồn tại"));
        
        DinhLuongMonAn d = dinhLuongMonAnRepository.findByBienThe_IdBienTheAndNguyenLieu_IdNguyenLieu(idBienThe, idNguyenLieu)
                .orElse(new DinhLuongMonAn());
                
        d.setBienThe(bt);
        d.setNguyenLieu(nl);
        d.setSoLuongTieuHao(soLuongTieuHao);
        dinhLuongMonAnRepository.save(d);
    }

    @Override
    public List<java.util.Map<String, Object>> layRecipeChuyenDoi(Integer idBanThanhPham) {
        return recipeChuyenDoiRepository.findByBanThanhPham_IdNguyenLieu(idBanThanhPham).stream().map(r -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("idRecipeChuyenDoi", r.getIdRecipeChuyenDoi());
            map.put("idBanThanhPham", r.getBanThanhPham().getIdNguyenLieu());
            map.put("idNguyenLieuTho", r.getNguyenLieuTho().getIdNguyenLieu());
            map.put("tenNguyenLieuTho", r.getNguyenLieuTho().getTenNguyenLieu());
            map.put("hamLuongCan", r.getHamLuongCan());
            map.put("soLuongThuDuoc", r.getSoLuongThuDuoc());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<java.util.Map<String, Object>> layDinhLuongMonAn(Integer idBienThe) {
        return dinhLuongMonAnRepository.findByBienThe_IdBienThe(idBienThe).stream().map(d -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("idDinhLuong", d.getIdDinhLuong());
            map.put("idBienThe", d.getBienThe().getIdBienThe());
            map.put("idNguyenLieu", d.getNguyenLieu().getIdNguyenLieu());
            map.put("tenNguyenLieu", d.getNguyenLieu().getTenNguyenLieu());
            map.put("soLuongTieuHao", d.getSoLuongTieuHao());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<java.util.Map<String, Object>> layTatCaDinhLuongMonAn() {
        return dinhLuongMonAnRepository.findAll().stream().map(d -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("idDinhLuong", d.getIdDinhLuong());
            map.put("idBienThe", d.getBienThe().getIdBienThe());
            map.put("tenSanPham", d.getBienThe().getSanPham() != null ? d.getBienThe().getSanPham().getTenSanPham() : "");
            map.put("tenKichCo", d.getBienThe().getTenKichCo());
            map.put("idNguyenLieu", d.getNguyenLieu().getIdNguyenLieu());
            map.put("tenNguyenLieu", d.getNguyenLieu().getTenNguyenLieu());
            map.put("soLuongTieuHao", d.getSoLuongTieuHao());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void xoaRecipeChuyenDoi(Integer idRecipe) {
        if (!recipeChuyenDoiRepository.existsById(idRecipe)) {
            throw new ResourceNotFoundException("Không tìm thấy công thức này để xóa");
        }
        recipeChuyenDoiRepository.deleteById(idRecipe);
    }

    @Override
    @Transactional
    public void xoaDinhLuongMonAn(Integer idDinhLuong) {
        if (!dinhLuongMonAnRepository.existsById(idDinhLuong)) {
            throw new ResourceNotFoundException("Không tìm thấy định lượng này để xóa");
        }
        dinhLuongMonAnRepository.deleteById(idDinhLuong);
    }
}

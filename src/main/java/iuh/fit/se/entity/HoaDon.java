package iuh.fit.se.entity;

import iuh.fit.se.enums.LoaiDonHang;
import iuh.fit.se.enums.PhuongThucThanhToan;
import iuh.fit.se.enums.TrangThaiHoaDon;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hoa_don")
@SQLRestriction("thoi_gian_xoa = 0")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thu_ngan", nullable = true)
    private NhanVien thuNgan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_dat")
    private PhieuDatBan phieuDatBan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khuyen_mai")
    private KhuyenMai khuyenMai;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoaiDonHang loaiDonHang;


    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal tongTienHang = BigDecimal.ZERO; // Tổng tiền món trước thuế & KM

    @Column(precision = 12, scale = 2)
    private BigDecimal giamGiaKhuyenMai = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal giamGiaThanhVien = BigDecimal.ZERO;

    private Integer diemSuDung = 0;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private List<HoaDonThuePhi> danhSachThuePhi = new ArrayList<>();

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal tongTienThue = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal tongThanhToan = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TrangThaiHoaDon trangThai;

    @Column(nullable = false)
    private LocalDateTime thoiGianTao = LocalDateTime.now();

    @Column(nullable = false)
    private Long thoiGianXoa = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "phuong_thuc_thanh_toan")
    private PhuongThucThanhToan phuongThucThanhToan;

    @Column(name = "thoi_gian_yeu_cau")
    private LocalDateTime thoiGianYeuCau;

    @Column(name = "thoi_gian_thanh_toan")
    private LocalDateTime thoiGianThanhToan;

    @Column(columnDefinition = "TEXT")
    private String thongTinChiTiet;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChiTietHoaDon> danhSachChiTiet = new LinkedHashSet<>();

    public HoaDon() {}

    public HoaDon(Integer idHoaDon, NhanVien thuNgan, KhachHang khachHang, PhieuDatBan phieuDatBan, KhuyenMai khuyenMai, LoaiDonHang loaiDonHang, BigDecimal tongTienHang, BigDecimal giamGiaKhuyenMai, BigDecimal giamGiaThanhVien, Integer diemSuDung, List<HoaDonThuePhi> danhSachThuePhi, BigDecimal tongTienThue, BigDecimal tongThanhToan, TrangThaiHoaDon trangThai, LocalDateTime thoiGianTao, Long thoiGianXoa, PhuongThucThanhToan phuongThucThanhToan, LocalDateTime thoiGianYeuCau, LocalDateTime thoiGianThanhToan, String thongTinChiTiet, Set<ChiTietHoaDon> danhSachChiTiet) {
        this.idHoaDon = idHoaDon;
        this.thuNgan = thuNgan;
        this.khachHang = khachHang;
        this.phieuDatBan = phieuDatBan;
        this.khuyenMai = khuyenMai;
        this.loaiDonHang = loaiDonHang;
        this.tongTienHang = tongTienHang;
        this.giamGiaKhuyenMai = giamGiaKhuyenMai;
        this.giamGiaThanhVien = giamGiaThanhVien;
        this.diemSuDung = diemSuDung;
        this.danhSachThuePhi = danhSachThuePhi;
        this.tongTienThue = tongTienThue;
        this.tongThanhToan = tongThanhToan;
        this.trangThai = trangThai;
        this.thoiGianTao = thoiGianTao;
        this.thoiGianXoa = thoiGianXoa;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.thoiGianYeuCau = thoiGianYeuCau;
        this.thoiGianThanhToan = thoiGianThanhToan;
        this.thongTinChiTiet = thongTinChiTiet;
        this.danhSachChiTiet = danhSachChiTiet;
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "id=" + idHoaDon +
                ", nhanVien=" + (thuNgan != null ? thuNgan.getHoTen() : "N/A") +
                ", ban=" + (phieuDatBan != null ? phieuDatBan.getIdPhieuDat() : "Mang về") +
                ", tong=" + tongThanhToan +
                ", chiTiet='" + thongTinChiTiet + '\'' +
                '}';
    }

    // Getters và Setters
    public Integer getIdHoaDon() {
        return idHoaDon;
    }

    public void setIdHoaDon(Integer idHoaDon) {
        this.idHoaDon = idHoaDon;
    }

    public NhanVien getThuNgan() {
        return thuNgan;
    }

    public void setThuNgan(NhanVien thuNgan) {
        this.thuNgan = thuNgan;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public PhieuDatBan getPhieuDatBan() {
        return phieuDatBan;
    }

    public void setPhieuDatBan(PhieuDatBan phieuDatBan) {
        this.phieuDatBan = phieuDatBan;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public LoaiDonHang getLoaiDonHang() {
        return loaiDonHang;
    }

    public void setLoaiDonHang(LoaiDonHang loaiDonHang) {
        this.loaiDonHang = loaiDonHang;
    }

    public BigDecimal getTongTienHang() {
        return tongTienHang;
    }

    public void setTongTienHang(BigDecimal tongTienHang) {
        this.tongTienHang = tongTienHang;
    }

    public BigDecimal getGiamGiaKhuyenMai() {
        return giamGiaKhuyenMai;
    }

    public void setGiamGiaKhuyenMai(BigDecimal giamGiaKhuyenMai) {
        this.giamGiaKhuyenMai = giamGiaKhuyenMai;
    }

    public BigDecimal getGiamGiaThanhVien() {
        return giamGiaThanhVien;
    }

    public void setGiamGiaThanhVien(BigDecimal giamGiaThanhVien) {
        this.giamGiaThanhVien = giamGiaThanhVien;
    }

    public Integer getDiemSuDung() {
        return diemSuDung;
    }

    public void setDiemSuDung(Integer diemSuDung) {
        this.diemSuDung = diemSuDung;
    }

    public List<HoaDonThuePhi> getDanhSachThuePhi() {
        return danhSachThuePhi;
    }

    public void setDanhSachThuePhi(List<HoaDonThuePhi> danhSachThuePhi) {
        this.danhSachThuePhi = danhSachThuePhi;
    }

    public BigDecimal getTongTienThue() {
        return tongTienThue;
    }

    public void setTongTienThue(BigDecimal tongTienThue) {
        this.tongTienThue = tongTienThue;
    }

    public BigDecimal getTongThanhToan() {
        return tongThanhToan;
    }

    public void setTongThanhToan(BigDecimal tongThanhToan) {
        this.tongThanhToan = tongThanhToan;
    }

    public TrangThaiHoaDon getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiHoaDon trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getThoiGianTao() {
        return thoiGianTao;
    }

    public void setThoiGianTao(LocalDateTime thoiGianTao) {
        this.thoiGianTao = thoiGianTao;
    }

    public Long getThoiGianXoa() {
        return thoiGianXoa;
    }

    public void setThoiGianXoa(Long thoiGianXoa) {
        this.thoiGianXoa = thoiGianXoa;
    }

    public PhuongThucThanhToan getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(PhuongThucThanhToan phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public LocalDateTime getThoiGianThanhToan() {
        return thoiGianThanhToan;
    }

    public void setThoiGianThanhToan(LocalDateTime thoiGianThanhToan) {
        this.thoiGianThanhToan = thoiGianThanhToan;
    }

    public LocalDateTime getThoiGianYeuCau() {
        return thoiGianYeuCau;
    }

    public void setThoiGianYeuCau(LocalDateTime thoiGianYeuCau) {
        this.thoiGianYeuCau = thoiGianYeuCau;
    }

    public String getThongTinChiTiet() {
        return thongTinChiTiet;
    }

    public void setThongTinChiTiet(String thongTinChiTiet) {
        this.thongTinChiTiet = thongTinChiTiet;
    }

    public Set<ChiTietHoaDon> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(Set<ChiTietHoaDon> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }
}
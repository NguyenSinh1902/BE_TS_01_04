package iuh.fit.se.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chi_tiet_hoa_don")
@SQLRestriction("thoi_gian_xoa = 0")
public class ChiTietHoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bien_the", nullable = false)
    private BienTheSanPham bienThe;

    @Column(nullable = false)
    private Integer soLuong = 1;

    @Column(name = "gia_thoi_diem_ban", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaThoiDiemBan;

    @Column(name = "phan_tram_giam_gia", nullable = false)
    private Integer phanTramGiamGia = 0; // Luôn để mặc định là 0 ở đây

    @Column(name = "tuy_chon_json", columnDefinition = "json")
    private String tuyChonJson;

    @OneToMany(mappedBy = "chiTietHoaDon", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChiTietHoaDonTopping> danhSachTopping = new LinkedHashSet<>(); // Đổi List thành Set

    @Column(nullable = false)
    private Long thoiGianXoa = 0L;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(Integer idChiTiet, HoaDon hoaDon, BienTheSanPham bienThe, Integer soLuong, BigDecimal giaThoiDiemBan, Integer phanTramGiamGia, String tuyChonJson, Long thoiGianXoa) {
        this.idChiTiet = idChiTiet;
        this.hoaDon = hoaDon;
        this.bienThe = bienThe;
        this.soLuong = soLuong;
        this.giaThoiDiemBan = giaThoiDiemBan;
        this.phanTramGiamGia = phanTramGiamGia;
        this.tuyChonJson = tuyChonJson;
        this.thoiGianXoa = thoiGianXoa;
    }

    public Integer getIdChiTiet() {
        return idChiTiet;
    }

    public void setIdChiTiet(Integer idChiTiet) {
        this.idChiTiet = idChiTiet;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public BienTheSanPham getBienThe() {
        return bienThe;
    }

    public void setBienThe(BienTheSanPham bienThe) {
        this.bienThe = bienThe;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getGiaThoiDiemBan() {
        return giaThoiDiemBan;
    }

    public void setGiaThoiDiemBan(BigDecimal giaThoiDiemBan) {
        this.giaThoiDiemBan = giaThoiDiemBan;
    }

    public Integer getPhanTramGiamGia() {
        return phanTramGiamGia;
    }

    public void setPhanTramGiamGia(Integer phanTramGiamGia) {
        this.phanTramGiamGia = phanTramGiamGia;
    }

    public String getTuyChonJson() {
        return tuyChonJson;
    }

    public void setTuyChonJson(String tuyChonJson) {
        this.tuyChonJson = tuyChonJson;
    }

    public Long getThoiGianXoa() {
        return thoiGianXoa;
    }

    public void setThoiGianXoa(Long thoiGianXoa) {
        this.thoiGianXoa = thoiGianXoa;
    }

    public Set<ChiTietHoaDonTopping> getDanhSachTopping() {
        return danhSachTopping;
    }

    public void setDanhSachTopping(Set<ChiTietHoaDonTopping> danhSachTopping) {
        this.danhSachTopping = danhSachTopping;
    }
}
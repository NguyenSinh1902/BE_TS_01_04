package iuh.fit.se.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import java.util.List;

@Entity
@Table(name = "san_pham")
@SQLRestriction("thoi_gian_xoa = 0")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc", nullable = false)
    private DanhMuc danhMuc;

    @Column(name = "ten_san_pham", nullable = false, length = 150)
    private String tenSanPham;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "duong_dan_anh", length = 500)
    private String duongDanAnh;

    @Column(name = "la_topping", nullable = false)
    private Boolean laTopping = false;

    @Column(name = "thoi_gian_xoa", nullable = false)
    private Long thoiGianXoa = 0L;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BienTheSanPham> danhSachBienThe;

    public SanPham() {
    }

    public SanPham(Integer idSanPham, DanhMuc danhMuc, String tenSanPham, String moTa, String duongDanAnh, Boolean laTopping, Long thoiGianXoa, List<BienTheSanPham> danhSachBienThe) {
        this.idSanPham = idSanPham;
        this.danhMuc = danhMuc;
        this.tenSanPham = tenSanPham;
        this.moTa = moTa;
        this.duongDanAnh = duongDanAnh;
        this.laTopping = laTopping;
        this.thoiGianXoa = thoiGianXoa;
        this.danhSachBienThe = danhSachBienThe;
    }

    public Integer getIdSanPham() {
        return idSanPham;
    }

    public void setIdSanPham(Integer idSanPham) {
        this.idSanPham = idSanPham;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getDuongDanAnh() {
        return duongDanAnh;
    }

    public void setDuongDanAnh(String duongDanAnh) {
        this.duongDanAnh = duongDanAnh;
    }

    public Boolean getLaTopping() {
        return laTopping;
    }

    public void setLaTopping(Boolean laTopping) {
        this.laTopping = laTopping;
    }

    public Long getThoiGianXoa() {
        return thoiGianXoa;
    }

    public void setThoiGianXoa(Long thoiGianXoa) {
        this.thoiGianXoa = thoiGianXoa;
    }

    public List<BienTheSanPham> getDanhSachBienThe() {
        return danhSachBienThe;
    }

    public void setDanhSachBienThe(List<BienTheSanPham> danhSachBienThe) {
        this.danhSachBienThe = danhSachBienThe;
    }
}

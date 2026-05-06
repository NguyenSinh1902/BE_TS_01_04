package iuh.fit.se.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import java.util.List;

@Entity
@Table(name = "danh_muc")
@SQLRestriction("thoi_gian_xoa = 0")
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDanhMuc;

    @Column(name = "ten_danh_muc", nullable = false, length = 100)
    private String tenDanhMuc;

    @Column(name = "duong_dan_anh", length = 500)
    private String duongDanAnh;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "la_he_thong", nullable = false)
    private Boolean laHeThong = false;

    @Column(name = "thoi_gian_xoa", nullable = false)
    private Long thoiGianXoa = 0L;

    @OneToMany(mappedBy = "danhMuc")
    private List<SanPham> danhSachSanPham;

    public DanhMuc() {
    }

    public DanhMuc(Integer idDanhMuc, String tenDanhMuc, String duongDanAnh, String moTa, Boolean laHeThong, Long thoiGianXoa, List<SanPham> danhSachSanPham) {
        this.idDanhMuc = idDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.duongDanAnh = duongDanAnh;
        this.moTa = moTa;
        this.laHeThong = laHeThong;
        this.thoiGianXoa = thoiGianXoa;
        this.danhSachSanPham = danhSachSanPham;
    }

    public Integer getIdDanhMuc() {
        return idDanhMuc;
    }

    public void setIdDanhMuc(Integer idDanhMuc) {
        this.idDanhMuc = idDanhMuc;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public String getDuongDanAnh() {
        return duongDanAnh;
    }

    public void setDuongDanAnh(String duongDanAnh) {
        this.duongDanAnh = duongDanAnh;
    }

    public Boolean getLaHeThong() {
        return laHeThong;
    }

    public void setLaHeThong(Boolean laHeThong) {
        this.laHeThong = laHeThong;
    }

    public Long getThoiGianXoa() {
        return thoiGianXoa;
    }

    public void setThoiGianXoa(Long thoiGianXoa) {
        this.thoiGianXoa = thoiGianXoa;
    }

    public List<SanPham> getDanhSachSanPham() {
        return danhSachSanPham;
    }

    public void setDanhSachSanPham(List<SanPham> danhSachSanPham) {
        this.danhSachSanPham = danhSachSanPham;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}

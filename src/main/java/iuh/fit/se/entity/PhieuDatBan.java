package iuh.fit.se.entity;

import iuh.fit.se.enums.TrangThaiDatBan;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "phieu_dat_ban")
@SQLRestriction("thoi_gian_xoa = 0")
public class PhieuDatBan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phieu_dat")
    private Integer idPhieuDat;

    @Column(name = "ten_khach_hang", length = 100)
    private String tenKhachHang;

    @Column(name = "sdt_khach_hang", length = 15)
    private String sdtKhachHang;

    @Column(name = "thoi_gian_dat")
    private LocalDateTime thoiGianDat;

    @Column(name = "so_luong_nguoi")
    private Integer soLuongNguoi;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_dat", length = 20)
    private TrangThaiDatBan trangThaiDat;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "thoi_gian_xoa", nullable = false)
    private Long thoiGianXoa = 0L;

    public PhieuDatBan() {
    }

    public PhieuDatBan(Integer idPhieuDat, String tenKhachHang, String sdtKhachHang, LocalDateTime thoiGianDat, Integer soLuongNguoi, TrangThaiDatBan trangThaiDat, String ghiChu, Long thoiGianXoa) {
        this.idPhieuDat = idPhieuDat;
        this.tenKhachHang = tenKhachHang;
        this.sdtKhachHang = sdtKhachHang;
        this.thoiGianDat = thoiGianDat;
        this.soLuongNguoi = soLuongNguoi;
        this.trangThaiDat = trangThaiDat;
        this.ghiChu = ghiChu;
        this.thoiGianXoa = thoiGianXoa;
    }

    public Integer getIdPhieuDat() {
        return idPhieuDat;
    }

    public void setIdPhieuDat(Integer idPhieuDat) {
        this.idPhieuDat = idPhieuDat;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSdtKhachHang() {
        return sdtKhachHang;
    }

    public void setSdtKhachHang(String sdtKhachHang) {
        this.sdtKhachHang = sdtKhachHang;
    }

    public LocalDateTime getThoiGianDat() {
        return thoiGianDat;
    }

    public void setThoiGianDat(LocalDateTime thoiGianDat) {
        this.thoiGianDat = thoiGianDat;
    }

    public Integer getSoLuongNguoi() {
        return soLuongNguoi;
    }

    public void setSoLuongNguoi(Integer soLuongNguoi) {
        this.soLuongNguoi = soLuongNguoi;
    }

    public TrangThaiDatBan getTrangThaiDat() {
        return trangThaiDat;
    }

    public void setTrangThaiDat(TrangThaiDatBan trangThaiDat) {
        this.trangThaiDat = trangThaiDat;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public Long getThoiGianXoa() {
        return thoiGianXoa;
    }

    public void setThoiGianXoa(Long thoiGianXoa) {
        this.thoiGianXoa = thoiGianXoa;
    }
}

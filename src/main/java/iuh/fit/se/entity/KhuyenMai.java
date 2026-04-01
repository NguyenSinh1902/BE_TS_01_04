package iuh.fit.se.entity;

import iuh.fit.se.enums.LoaiKhuyenMai;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "khuyen_mai", uniqueConstraints = {
        @UniqueConstraint(name = "uk_ma_code_xoa", columnNames = {"ma_code", "thoi_gian_xoa"})
})
@SQLRestriction("thoi_gian_xoa = 0")
public class KhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idKhuyenMai;

    @Column(name = "ma_code", nullable = false, length = 50)
    private String maCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_khuyen_mai", nullable = false)
    private LoaiKhuyenMai loaiKhuyenMai;

    @Column(name = "gia_tri_giam", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "la_giam_gia_sau_thue", nullable = false)
    private Boolean laGiamGiaSauThue = false;

    @Column(name = "don_toi_thieu", nullable = false, precision = 12, scale = 2)
    private BigDecimal donToiThieu = BigDecimal.ZERO;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_het_han", nullable = false)
    private LocalDateTime ngayHetHan;

    @Column(name = "thoi_gian_xoa", nullable = false)
    private Long thoiGianXoa = 0L;

    //Constructor
    public KhuyenMai() {
    }

    public KhuyenMai(Integer idKhuyenMai, String maCode, LoaiKhuyenMai loaiKhuyenMai, BigDecimal giaTriGiam, Boolean laGiamGiaSauThue, BigDecimal donToiThieu, LocalDateTime ngayBatDau, LocalDateTime ngayHetHan, Long thoiGianXoa) {
        this.idKhuyenMai = idKhuyenMai;
        this.maCode = maCode;
        this.loaiKhuyenMai = loaiKhuyenMai;
        this.giaTriGiam = giaTriGiam;
        this.laGiamGiaSauThue = laGiamGiaSauThue;
        this.donToiThieu = donToiThieu;
        this.ngayBatDau = ngayBatDau;
        this.ngayHetHan = ngayHetHan;
        this.thoiGianXoa = thoiGianXoa;
    }

    public Integer getIdKhuyenMai() {
        return idKhuyenMai;
    }

    public void setIdKhuyenMai(Integer idKhuyenMai) {
        this.idKhuyenMai = idKhuyenMai;
    }

    public String getMaCode() {
        return maCode;
    }

    public void setMaCode(String maCode) {
        this.maCode = maCode;
    }

    public LoaiKhuyenMai getLoaiKhuyenMai() {
        return loaiKhuyenMai;
    }

    public void setLoaiKhuyenMai(LoaiKhuyenMai loaiKhuyenMai) {
        this.loaiKhuyenMai = loaiKhuyenMai;
    }

    public BigDecimal getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(BigDecimal giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }

    public Boolean getLaGiamGiaSauThue() {
        return laGiamGiaSauThue;
    }

    public void setLaGiamGiaSauThue(Boolean laGiamGiaSauThue) {
        this.laGiamGiaSauThue = laGiamGiaSauThue;
    }

    public BigDecimal getDonToiThieu() {
        return donToiThieu;
    }

    public void setDonToiThieu(BigDecimal donToiThieu) {
        this.donToiThieu = donToiThieu;
    }

    public LocalDateTime getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDateTime ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDateTime getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(LocalDateTime ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }

    public Long getThoiGianXoa() {
        return thoiGianXoa;
    }

    public void setThoiGianXoa(Long thoiGianXoa) {
        this.thoiGianXoa = thoiGianXoa;
    }
}
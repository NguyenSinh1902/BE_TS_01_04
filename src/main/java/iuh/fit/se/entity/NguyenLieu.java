package iuh.fit.se.entity;

import iuh.fit.se.enums.LoaiNguyenLieu;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "nguyen_lieu")
public class NguyenLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNguyenLieu;

    @Column(nullable = false, length = 150)
    private String tenNguyenLieu;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal soLuongTon;

    @Column(nullable = false, length = 20)
    private String donViTinh; // g, ml, phan, cai...

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LoaiNguyenLieu loaiNguyenLieu;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal giaNhapDonVi = BigDecimal.ZERO; // Giá vốn trên 1 đơn vị

    @Column(nullable = false)
    private Long thoiGianXoa = 0L; // Hỗ trợ Soft Delete đồng bộ dự án của ông

    public NguyenLieu() {
    }

    public NguyenLieu(Integer idNguyenLieu, String tenNguyenLieu, BigDecimal soLuongTon, String donViTinh, LoaiNguyenLieu loaiNguyenLieu, BigDecimal giaNhapDonVi, Long thoiGianXoa) {
        this.idNguyenLieu = idNguyenLieu;
        this.tenNguyenLieu = tenNguyenLieu;
        this.soLuongTon = soLuongTon;
        this.donViTinh = donViTinh;
        this.loaiNguyenLieu = loaiNguyenLieu;
        this.giaNhapDonVi = giaNhapDonVi;
        this.thoiGianXoa = thoiGianXoa;
    }

    public Integer getIdNguyenLieu() {
        return idNguyenLieu;
    }

    public void setIdNguyenLieu(Integer idNguyenLieu) {
        this.idNguyenLieu = idNguyenLieu;
    }

    public String getTenNguyenLieu() {
        return tenNguyenLieu;
    }

    public void setTenNguyenLieu(String tenNguyenLieu) {
        this.tenNguyenLieu = tenNguyenLieu;
    }

    public BigDecimal getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(BigDecimal soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public LoaiNguyenLieu getLoaiNguyenLieu() {
        return loaiNguyenLieu;
    }

    public void setLoaiNguyenLieu(LoaiNguyenLieu loaiNguyenLieu) {
        this.loaiNguyenLieu = loaiNguyenLieu;
    }

    public BigDecimal getGiaNhapDonVi() {
        return giaNhapDonVi;
    }

    public void setGiaNhapDonVi(BigDecimal giaNhapDonVi) {
        this.giaNhapDonVi = giaNhapDonVi;
    }

    public Long getThoiGianXoa() {
        return thoiGianXoa;
    }

    public void setThoiGianXoa(Long thoiGianXoa) {
        this.thoiGianXoa = thoiGianXoa;
    }
}

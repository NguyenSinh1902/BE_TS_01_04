package iuh.fit.se.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "hoa_don_thue_phi")
public class HoaDonThuePhi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon hoaDon;

    @Column(nullable = false)
    private String tenThuePhi;

    @Column(nullable = false)
    private Float giaTriTaiThoiDiemBan;

    @Column(precision = 12, scale = 2)
    private BigDecimal soTienQuyDoi;

    // Constructor
    public HoaDonThuePhi() {}

    public HoaDonThuePhi(HoaDon hd, String ten, Float giaTri) {
        this.hoaDon = hd;
        this.tenThuePhi = ten;
        this.giaTriTaiThoiDiemBan = giaTri;
    }

    public HoaDonThuePhi(Integer id, HoaDon hoaDon, String tenThuePhi, Float giaTriTaiThoiDiemBan, BigDecimal soTienQuyDoi) {
        this.id = id;
        this.hoaDon = hoaDon;
        this.tenThuePhi = tenThuePhi;
        this.giaTriTaiThoiDiemBan = giaTriTaiThoiDiemBan;
        this.soTienQuyDoi = soTienQuyDoi;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public String getTenThuePhi() {
        return tenThuePhi;
    }

    public void setTenThuePhi(String tenThuePhi) {
        this.tenThuePhi = tenThuePhi;
    }

    public Float getGiaTriTaiThoiDiemBan() {
        return giaTriTaiThoiDiemBan;
    }

    public void setGiaTriTaiThoiDiemBan(Float giaTriTaiThoiDiemBan) {
        this.giaTriTaiThoiDiemBan = giaTriTaiThoiDiemBan;
    }

    public BigDecimal getSoTienQuyDoi() {
        return soTienQuyDoi;
    }

    public void setSoTienQuyDoi(BigDecimal soTienQuyDoi) {
        this.soTienQuyDoi = soTienQuyDoi;
    }
}

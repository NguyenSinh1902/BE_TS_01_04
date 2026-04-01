package iuh.fit.se.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_hoadon_topping")
public class ChiTietHoaDonTopping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chi_tiet_hoa_don")
    private ChiTietHoaDon chiTietHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bien_the_topping")
    private BienTheSanPham bienTheTopping;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham_topping")
    private SanPham topping;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal giaThoiDiemBan;
    public ChiTietHoaDonTopping() {
    }

    public ChiTietHoaDonTopping(Integer id, ChiTietHoaDon chiTietHoaDon, BienTheSanPham bienTheTopping, SanPham topping, BigDecimal giaThoiDiemBan) {
        this.id = id;
        this.chiTietHoaDon = chiTietHoaDon;
        this.bienTheTopping = bienTheTopping;
        this.topping = topping;
        this.giaThoiDiemBan = giaThoiDiemBan;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        this.chiTietHoaDon = chiTietHoaDon;
    }

    public SanPham getTopping() {
        return topping;
    }

    public void setTopping(SanPham topping) {
        this.topping = topping;
    }

    public BigDecimal getGiaThoiDiemBan() {
        return giaThoiDiemBan;
    }

    public void setGiaThoiDiemBan(BigDecimal giaThoiDiemBan) {
        this.giaThoiDiemBan = giaThoiDiemBan;
    }

    public BienTheSanPham getBienTheTopping() {
        return bienTheTopping;
    }

    public void setBienTheTopping(BienTheSanPham bienTheTopping) {
        this.bienTheTopping = bienTheTopping;
    }
}

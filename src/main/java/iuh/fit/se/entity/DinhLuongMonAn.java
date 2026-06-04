package iuh.fit.se.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "dinh_luong_mon_an")
public class DinhLuongMonAn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDinhLuong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bien_the", nullable = false)
    private BienTheSanPham bienThe; // Gắn định lượng vào TỪNG SIZE (Ví dụ: Size M, Size L)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguyen_lieu", nullable = false)
    private NguyenLieu nguyenLieu; 

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal soLuongTieuHao; 

    public DinhLuongMonAn() {
    }

    public DinhLuongMonAn(Integer idDinhLuong, BienTheSanPham bienThe, NguyenLieu nguyenLieu, BigDecimal soLuongTieuHao) {
        this.idDinhLuong = idDinhLuong;
        this.bienThe = bienThe;
        this.nguyenLieu = nguyenLieu;
        this.soLuongTieuHao = soLuongTieuHao;
    }

    public Integer getIdDinhLuong() {
        return idDinhLuong;
    }

    public void setIdDinhLuong(Integer idDinhLuong) {
        this.idDinhLuong = idDinhLuong;
    }

    public BienTheSanPham getBienThe() {
        return bienThe;
    }

    public void setBienThe(BienTheSanPham bienThe) {
        this.bienThe = bienThe;
    }

    public NguyenLieu getNguyenLieu() {
        return nguyenLieu;
    }

    public void setNguyenLieu(NguyenLieu nguyenLieu) {
        this.nguyenLieu = nguyenLieu;
    }

    public BigDecimal getSoLuongTieuHao() {
        return soLuongTieuHao;
    }

    public void setSoLuongTieuHao(BigDecimal soLuongTieuHao) {
        this.soLuongTieuHao = soLuongTieuHao;
    }
}

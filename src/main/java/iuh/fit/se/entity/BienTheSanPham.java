package iuh.fit.se.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;

@Entity
@Table(name = "bien_the_san_pham")
@SQLRestriction("thoi_gian_xoa = 0")
public class BienTheSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBienThe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private SanPham sanPham;

    @Column(name = "ten_kich_co", nullable = false, length = 50)
    private String tenKichCo; // Ví dụ: Size M, Size L

    @Column(name = "gia_ban", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "phan_tram_giam_gia", nullable = false)
    private Integer phanTramGiamGia = 0;

    @Column(name = "so_luong_ton_kho", nullable = false)
    private Integer soLuongTonKho = -1; // -1 là không giới hạn

    @Column(name = "thoi_gian_xoa", nullable = false)
    private Long thoiGianXoa = 0L;

    public BienTheSanPham() {
    }

    public BienTheSanPham(Integer idBienThe, SanPham sanPham, String tenKichCo, BigDecimal giaBan, Integer phanTramGiamGia, Integer soLuongTonKho, Long thoiGianXoa) {
        this.idBienThe = idBienThe;
        this.sanPham = sanPham;
        this.tenKichCo = tenKichCo;
        this.giaBan = giaBan;
        this.phanTramGiamGia = phanTramGiamGia;
        this.soLuongTonKho = soLuongTonKho;
        this.thoiGianXoa = thoiGianXoa;
    }

    public Integer getIdBienThe() {
        return idBienThe;
    }

    public void setIdBienThe(Integer idBienThe) {
        this.idBienThe = idBienThe;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public String getTenKichCo() {
        return tenKichCo;
    }

    public void setTenKichCo(String tenKichCo) {
        this.tenKichCo = tenKichCo;
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }

    public Integer getPhanTramGiamGia() {
        return phanTramGiamGia;
    }

    public void setPhanTramGiamGia(Integer phanTramGiamGia) {
        this.phanTramGiamGia = phanTramGiamGia;
    }

    public Integer getSoLuongTonKho() {
        return soLuongTonKho;
    }

    public void setSoLuongTonKho(Integer soLuongTonKho) {
        this.soLuongTonKho = soLuongTonKho;
    }

    public Long getThoiGianXoa() {
        return thoiGianXoa;
    }

    public void setThoiGianXoa(Long thoiGianXoa) {
        this.thoiGianXoa = thoiGianXoa;
    }
}
package iuh.fit.se.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_xuat_huy")
public class LichSuXuatHuy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLichSu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguyen_lieu", nullable = false)
    private NguyenLieu nguyenLieu;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal soLuongHuy;

    @Column(columnDefinition = "TEXT")
    private String lyDo;

    @Column(nullable = false)
    private LocalDateTime thoiGianHuy = LocalDateTime.now();

    public LichSuXuatHuy() {}

    public LichSuXuatHuy(NguyenLieu nguyenLieu, BigDecimal soLuongHuy, String lyDo) {
        this.nguyenLieu = nguyenLieu;
        this.soLuongHuy = soLuongHuy;
        this.lyDo = lyDo;
    }

    public Integer getIdLichSu() {
        return idLichSu;
    }

    public void setIdLichSu(Integer idLichSu) {
        this.idLichSu = idLichSu;
    }

    public NguyenLieu getNguyenLieu() {
        return nguyenLieu;
    }

    public void setNguyenLieu(NguyenLieu nguyenLieu) {
        this.nguyenLieu = nguyenLieu;
    }

    public BigDecimal getSoLuongHuy() {
        return soLuongHuy;
    }

    public void setSoLuongHuy(BigDecimal soLuongHuy) {
        this.soLuongHuy = soLuongHuy;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public LocalDateTime getThoiGianHuy() {
        return thoiGianHuy;
    }

    public void setThoiGianHuy(LocalDateTime thoiGianHuy) {
        this.thoiGianHuy = thoiGianHuy;
    }
}

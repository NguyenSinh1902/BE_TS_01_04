package iuh.fit.se.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "goi_y_san_pham")
@SQLRestriction("thoi_gian_xoa = 0")
public class GoiYSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_goiy")
    private Integer idGoiY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham_chinh", nullable = false)
    private SanPham sanPhamChinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham_goi_y", nullable = false)
    private SanPham sanPhamGoiY;

    @Column(name = "diem_tin_cay", nullable = false)
    private Float diemTinCay;

    public GoiYSanPham() {
    }

    public GoiYSanPham(Integer idGoiY, SanPham sanPhamChinh, SanPham sanPhamGoiY, Float diemTinCay) {
        this.idGoiY = idGoiY;
        this.sanPhamChinh = sanPhamChinh;
        this.sanPhamGoiY = sanPhamGoiY;
        this.diemTinCay = diemTinCay;
    }

    public Integer getIdGoiY() {
        return idGoiY;
    }

    public void setIdGoiY(Integer idGoiY) {
        this.idGoiY = idGoiY;
    }

    public SanPham getSanPhamChinh() {
        return sanPhamChinh;
    }

    public void setSanPhamChinh(SanPham sanPhamChinh) {
        this.sanPhamChinh = sanPhamChinh;
    }

    public SanPham getSanPhamGoiY() {
        return sanPhamGoiY;
    }

    public void setSanPhamGoiY(SanPham sanPhamGoiY) {
        this.sanPhamGoiY = sanPhamGoiY;
    }

    public Float getDiemTinCay() {
        return diemTinCay;
    }

    public void setDiemTinCay(Float diemTinCay) {
        this.diemTinCay = diemTinCay;
    }
}
package iuh.fit.se.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhat_ky_ai")
public class NhatKyAi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNhatKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quan_ly", nullable = false)
    private NhanVien quanLy;

    @Column(name = "ngay_phan_tich", nullable = false)
    private LocalDate ngayPhanTich;

    @Column(name = "loi_khuyen_ai", columnDefinition = "text", nullable = false)
    private String loiKhuyenAi;

    @Column(name = "du_lieu_dau_vao", columnDefinition = "json")
    private String duLieuDauVao;

    @Column(name = "thoi_gian_tao", nullable = false)
    private LocalDateTime thoiGianTao = LocalDateTime.now();

    public NhatKyAi() {}

    public NhatKyAi(Integer idNhatKy, NhanVien quanLy, LocalDate ngayPhanTich, String loiKhuyenAi, String duLieuDauVao, LocalDateTime thoiGianTao) {
        this.idNhatKy = idNhatKy;
        this.quanLy = quanLy;
        this.ngayPhanTich = ngayPhanTich;
        this.loiKhuyenAi = loiKhuyenAi;
        this.duLieuDauVao = duLieuDauVao;
        this.thoiGianTao = thoiGianTao;
    }

    public Integer getIdNhatKy() {
        return idNhatKy;
    }

    public void setIdNhatKy(Integer idNhatKy) {
        this.idNhatKy = idNhatKy;
    }

    public NhanVien getQuanLy() {
        return quanLy;
    }

    public void setQuanLy(NhanVien quanLy) {
        this.quanLy = quanLy;
    }

    public LocalDate getNgayPhanTich() {
        return ngayPhanTich;
    }

    public void setNgayPhanTich(LocalDate ngayPhanTich) {
        this.ngayPhanTich = ngayPhanTich;
    }

    public String getLoiKhuyenAi() {
        return loiKhuyenAi;
    }

    public void setLoiKhuyenAi(String loiKhuyenAi) {
        this.loiKhuyenAi = loiKhuyenAi;
    }

    public String getDuLieuDauVao() {
        return duLieuDauVao;
    }

    public void setDuLieuDauVao(String duLieuDauVao) {
        this.duLieuDauVao = duLieuDauVao;
    }

    public LocalDateTime getThoiGianTao() {
        return thoiGianTao;
    }

    public void setThoiGianTao(LocalDateTime thoiGianTao) {
        this.thoiGianTao = thoiGianTao;
    }
}
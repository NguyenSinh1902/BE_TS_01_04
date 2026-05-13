package iuh.fit.se.entity;

import iuh.fit.se.enums.LoaiGiaTriThuePhi;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "thue_phi")
@SQLRestriction("thoi_gian_xoa = 0")
public class ThuePhi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idThuePhi;

    @Column(nullable = false, length = 100)
    private String tenThuePhi;

    @Column(nullable = false)
    private Float giaTri;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoaiGiaTriThuePhi loaiGiaTri = LoaiGiaTriThuePhi.PHAN_TRAM;

    @Column(nullable = false)
    private Boolean laMacDinh = false;

    @Column(nullable = false)
    private Long thoiGianXoa = 0L;

    // Constructor
    public ThuePhi() {}

    public ThuePhi(Integer idThuePhi, String tenThuePhi, Float giaTri, LoaiGiaTriThuePhi loaiGiaTri, Boolean laMacDinh, Long thoiGianXoa) {
        this.idThuePhi = idThuePhi;
        this.tenThuePhi = tenThuePhi;
        this.giaTri = giaTri;
        this.loaiGiaTri = loaiGiaTri;
        this.laMacDinh = laMacDinh;
        this.thoiGianXoa = thoiGianXoa;
    }

    public Integer getIdThuePhi() {
        return idThuePhi;
    }

    public void setIdThuePhi(Integer idThuePhi) {
        this.idThuePhi = idThuePhi;
    }

    public String getTenThuePhi() {
        return tenThuePhi;
    }

    public void setTenThuePhi(String tenThuePhi) {
        this.tenThuePhi = tenThuePhi;
    }

    public Float getGiaTri() {
        return giaTri;
    }

    public void setGiaTri(Float giaTri) {
        this.giaTri = giaTri;
    }

    public LoaiGiaTriThuePhi getLoaiGiaTri() {
        return loaiGiaTri;
    }

    public void setLoaiGiaTri(LoaiGiaTriThuePhi loaiGiaTri) {
        this.loaiGiaTri = loaiGiaTri;
    }

    public Boolean getLaMacDinh() {
        return laMacDinh;
    }

    public void setLaMacDinh(Boolean laMacDinh) {
        this.laMacDinh = laMacDinh;
    }

    public Long getThoiGianXoa() {
        return thoiGianXoa;
    }

    public void setThoiGianXoa(Long thoiGianXoa) {
        this.thoiGianXoa = thoiGianXoa;
    }
}

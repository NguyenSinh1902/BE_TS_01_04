package iuh.fit.se.entity;

import iuh.fit.se.enums.TrangThaiPhieuHoanTra;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "phieu_hoan_tra")
public class PhieuHoanTra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPhieuHoanTra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon hoaDon;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal soTienHoan;

    @Column(columnDefinition = "TEXT")
    private String lyDo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TrangThaiPhieuHoanTra trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thu_ngan_tao")
    private NhanVien thuNganTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin_duyet")
    private NhanVien adminDuyet;

    @Column(nullable = false)
    private LocalDateTime thoiGianTao = LocalDateTime.now();

    private LocalDateTime thoiGianDuyet;
    private LocalDateTime thoiGianHoanThanh;

    public PhieuHoanTra() {}

    public PhieuHoanTra(Integer idPhieuHoanTra, HoaDon hoaDon, BigDecimal soTienHoan, String lyDo, TrangThaiPhieuHoanTra trangThai, NhanVien thuNganTao, NhanVien adminDuyet, LocalDateTime thoiGianTao, LocalDateTime thoiGianDuyet, LocalDateTime thoiGianHoanThanh) {
        this.idPhieuHoanTra = idPhieuHoanTra;
        this.hoaDon = hoaDon;
        this.soTienHoan = soTienHoan;
        this.lyDo = lyDo;
        this.trangThai = trangThai;
        this.thuNganTao = thuNganTao;
        this.adminDuyet = adminDuyet;
        this.thoiGianTao = thoiGianTao;
        this.thoiGianDuyet = thoiGianDuyet;
        this.thoiGianHoanThanh = thoiGianHoanThanh;
    }

    public Integer getIdPhieuHoanTra() {
        return idPhieuHoanTra;
    }

    public void setIdPhieuHoanTra(Integer idPhieuHoanTra) {
        this.idPhieuHoanTra = idPhieuHoanTra;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public BigDecimal getSoTienHoan() {
        return soTienHoan;
    }

    public void setSoTienHoan(BigDecimal soTienHoan) {
        this.soTienHoan = soTienHoan;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public TrangThaiPhieuHoanTra getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiPhieuHoanTra trangThai) {
        this.trangThai = trangThai;
    }

    public NhanVien getThuNganTao() {
        return thuNganTao;
    }

    public void setThuNganTao(NhanVien thuNganTao) {
        this.thuNganTao = thuNganTao;
    }

    public NhanVien getAdminDuyet() {
        return adminDuyet;
    }

    public void setAdminDuyet(NhanVien adminDuyet) {
        this.adminDuyet = adminDuyet;
    }

    public LocalDateTime getThoiGianTao() {
        return thoiGianTao;
    }

    public void setThoiGianTao(LocalDateTime thoiGianTao) {
        this.thoiGianTao = thoiGianTao;
    }

    public LocalDateTime getThoiGianDuyet() {
        return thoiGianDuyet;
    }

    public void setThoiGianDuyet(LocalDateTime thoiGianDuyet) {
        this.thoiGianDuyet = thoiGianDuyet;
    }

    public LocalDateTime getThoiGianHoanThanh() {
        return thoiGianHoanThanh;
    }

    public void setThoiGianHoanThanh(LocalDateTime thoiGianHoanThanh) {
        this.thoiGianHoanThanh = thoiGianHoanThanh;
    }
}

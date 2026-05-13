package iuh.fit.se.entity;

import iuh.fit.se.enums.GioiTinh;
import iuh.fit.se.enums.HangThanhVien;
import iuh.fit.se.enums.TrangThaiKhachHang;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "khach_hang", uniqueConstraints = {
        @UniqueConstraint(name = "uk_sdt_khach_hang_xoa", columnNames = {"so_dien_thoai", "thoi_gian_xoa"})
})
@SQLRestriction("thoi_gian_xoa = 0")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idKhachHang;

    @Column(nullable = false, length = 100)
    private String hoTen;

    @Column(name = "so_dien_thoai", nullable = false, length = 15)
    private String soDienThoai;

    @Enumerated(EnumType.STRING)
    @Column(name = "gioi_tinh", length = 10)
    private GioiTinh gioiTinh;

    @Column(name = "tong_diem_da_tich_luy", nullable = false)
    private Integer tongDiemDaTichLuy = 0;

    @Column(name = "diem_tich_luy", nullable = false)
    private Integer diemTichLuy = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "hang_thanh_vien", nullable = false, length = 20)
    private HangThanhVien hangThanhVien = HangThanhVien.MOI;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private TrangThaiKhachHang trangThai = TrangThaiKhachHang.DANG_HOAT_DONG;

    @Column(name = "thoi_gian_xoa", nullable = false)
    private Long thoiGianXoa = 0L;

    public KhachHang() {
    }

    public KhachHang(Integer idKhachHang, String hoTen, String soDienThoai, GioiTinh gioiTinh, Integer tongDiemDaTichLuy, Integer diemTichLuy, HangThanhVien hangThanhVien, TrangThaiKhachHang trangThai, Long thoiGianXoa) {
        this.idKhachHang = idKhachHang;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.gioiTinh = gioiTinh;
        this.tongDiemDaTichLuy = tongDiemDaTichLuy;
        this.diemTichLuy = diemTichLuy;
        this.hangThanhVien = hangThanhVien;
        this.trangThai = trangThai;
        this.thoiGianXoa = thoiGianXoa;
    }

    public Integer getIdKhachHang() {
        return idKhachHang;
    }

    public void setIdKhachHang(Integer idKhachHang) {
        this.idKhachHang = idKhachHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public GioiTinh getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(GioiTinh gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Integer getTongDiemDaTichLuy() {
        return tongDiemDaTichLuy;
    }

    public void setTongDiemDaTichLuy(Integer tongDiemDaTichLuy) {
        this.tongDiemDaTichLuy = tongDiemDaTichLuy;
    }

    public Integer getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(Integer diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public HangThanhVien getHangThanhVien() {
        return hangThanhVien;
    }

    public void setHangThanhVien(HangThanhVien hangThanhVien) {
        this.hangThanhVien = hangThanhVien;
    }

    public TrangThaiKhachHang getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiKhachHang trangThai) {
        this.trangThai = trangThai;
    }

    public Long getThoiGianXoa() {
        return thoiGianXoa;
    }

    public void setThoiGianXoa(Long thoiGianXoa) {
        this.thoiGianXoa = thoiGianXoa;
    }

    public void capNhatHangThanhVien() {
        if (this.tongDiemDaTichLuy >= 1000) {
            this.hangThanhVien = HangThanhVien.VANG;
        } else if (this.tongDiemDaTichLuy >= 400) {
            this.hangThanhVien = HangThanhVien.BAC;
        } else {
            this.hangThanhVien = HangThanhVien.MOI;
        }
    }
}
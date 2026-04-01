package iuh.fit.se.entity;

import iuh.fit.se.enums.*;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhan_vien", uniqueConstraints = {

        @UniqueConstraint(name = "uk_email_thoi_gian_xoa", columnNames = {"email", "thoi_gian_xoa"}),

        @UniqueConstraint(name = "uk_sdt_thoi_gian_xoa", columnNames = {"so_dien_thoai", "thoi_gian_xoa"})
})
@SQLRestriction("thoi_gian_xoa = 0")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNhanVien;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String matKhau;

    @Column(nullable = false, length = 100)
    private String hoTen;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private GioiTinh gioiTinh;

    private LocalDate ngaySinh;

    @Column(name = "so_dien_thoai", length = 15)
    private String soDienThoai;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VaiTroNhanVien vaiTro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TrangThaiNhanVien trangThai = TrangThaiNhanVien.CHO_DUYET;

    @Column(length = 6)
    private String maOtp;

    private LocalDateTime thoiHanOtp;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(nullable = false)
    private Long thoiGianXoa = 0L;

    public NhanVien() {
    }

    public NhanVien(Integer idNhanVien, String email, String matKhau, String hoTen, GioiTinh gioiTinh, LocalDate ngaySinh, String soDienThoai, VaiTroNhanVien vaiTro, TrangThaiNhanVien trangThai, String maOtp, LocalDateTime thoiHanOtp, boolean emailVerified, Long thoiGianXoa) {
        this.idNhanVien = idNhanVien;
        this.email = email;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.maOtp = maOtp;
        this.thoiHanOtp = thoiHanOtp;
        this.emailVerified = emailVerified;
        this.thoiGianXoa = thoiGianXoa;
    }

    // Getters and Setters

    public Integer getIdNhanVien() {
        return idNhanVien;
    }

    public void setIdNhanVien(Integer idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public GioiTinh getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(GioiTinh gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public VaiTroNhanVien getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(VaiTroNhanVien vaiTro) {
        this.vaiTro = vaiTro;
    }

    public TrangThaiNhanVien getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiNhanVien trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaOtp() {
        return maOtp;
    }

    public void setMaOtp(String maOtp) {
        this.maOtp = maOtp;
    }

    public LocalDateTime getThoiHanOtp() {
        return thoiHanOtp;
    }

    public void setThoiHanOtp(LocalDateTime thoiHanOtp) {
        this.thoiHanOtp = thoiHanOtp;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Long getThoiGianXoa() {
        return thoiGianXoa;
    }

    public void setThoiGianXoa(Long thoiGianXoa) {
        this.thoiGianXoa = thoiGianXoa;
    }
}
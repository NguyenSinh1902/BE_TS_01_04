package iuh.fit.se.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "recipe_chuyen_doi")
public class RecipeChuyenDoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRecipeChuyenDoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ban_thanh_pham", nullable = false)
    private NguyenLieu banThanhPham; // Cốt trà sữa, Trân châu đã luộc...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguyen_lieu_tho", nullable = false)
    private NguyenLieu nguyenLieuTho; // Trà lá, Sữa tươi, Trân châu sống...

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hamLuongCan; // Ví dụ: Cần 100g trà thô...

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal soLuongThuDuoc = BigDecimal.ONE; // ... để nấu ra 2500ml cốt trà

    public RecipeChuyenDoi() {
    }

    public RecipeChuyenDoi(Integer idRecipeChuyenDoi, NguyenLieu banThanhPham, NguyenLieu nguyenLieuTho, BigDecimal hamLuongCan, BigDecimal soLuongThuDuoc) {
        this.idRecipeChuyenDoi = idRecipeChuyenDoi;
        this.banThanhPham = banThanhPham;
        this.nguyenLieuTho = nguyenLieuTho;
        this.hamLuongCan = hamLuongCan;
        this.soLuongThuDuoc = soLuongThuDuoc;
    }

    public Integer getIdRecipeChuyenDoi() {
        return idRecipeChuyenDoi;
    }

    public void setIdRecipeChuyenDoi(Integer idRecipeChuyenDoi) {
        this.idRecipeChuyenDoi = idRecipeChuyenDoi;
    }

    public NguyenLieu getBanThanhPham() {
        return banThanhPham;
    }

    public void setBanThanhPham(NguyenLieu banThanhPham) {
        this.banThanhPham = banThanhPham;
    }

    public NguyenLieu getNguyenLieuTho() {
        return nguyenLieuTho;
    }

    public void setNguyenLieuTho(NguyenLieu nguyenLieuTho) {
        this.nguyenLieuTho = nguyenLieuTho;
    }

    public BigDecimal getHamLuongCan() {
        return hamLuongCan;
    }

    public void setHamLuongCan(BigDecimal hamLuongCan) {
        this.hamLuongCan = hamLuongCan;
    }

    public BigDecimal getSoLuongThuDuoc() {
        return soLuongThuDuoc;
    }

    public void setSoLuongThuDuoc(BigDecimal soLuongThuDuoc) {
        this.soLuongThuDuoc = soLuongThuDuoc;
    }
}

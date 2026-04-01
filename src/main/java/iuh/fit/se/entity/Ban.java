package iuh.fit.se.entity;

import iuh.fit.se.enums.TinhTrangBan;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Table(name = "ban")
@SQLRestriction("thoi_gian_xoa = 0")
public class Ban {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ban")
    private Integer idBan;

    @Column(name = "ten_ban", nullable = false, length = 50)
    private String tenBan;

    @Column(name = "suc_chua", nullable = false)
    private Integer sucChua;

    @Enumerated(EnumType.STRING)
    @Column(name = "tinh_trang_ban", nullable = false, length = 20)
    private TinhTrangBan tinhTrangBan;

    @Column(name = "thoi_gian_xoa", nullable = false)
    private Long thoiGianXoa = 0L;

    public boolean kiemTraTrong() {
        return this.tinhTrangBan == TinhTrangBan.TRONG && this.thoiGianXoa == 0L;
    }

    public Ban() {
    }

    public Ban(Integer idBan, String tenBan, Integer sucChua, TinhTrangBan tinhTrangBan, Long thoiGianXoa) {
        this.idBan = idBan;
        this.tenBan = tenBan;
        this.sucChua = sucChua;
        this.tinhTrangBan = tinhTrangBan;
        this.thoiGianXoa = thoiGianXoa;
    }

    public Integer getIdBan() {
        return idBan;
    }

    public void setIdBan(Integer idBan) {
        this.idBan = idBan;
    }

    public String getTenBan() {
        return tenBan;
    }

    public void setTenBan(String tenBan) {
        this.tenBan = tenBan;
    }

    public Integer getSucChua() {
        return sucChua;
    }

    public void setSucChua(Integer sucChua) {
        this.sucChua = sucChua;
    }

    public TinhTrangBan getTinhTrangBan() {
        return tinhTrangBan;
    }

    public void setTinhTrangBan(TinhTrangBan tinhTrangBan) {
        this.tinhTrangBan = tinhTrangBan;
    }

    public Long getThoiGianXoa() {
        return thoiGianXoa;
    }

    public void setThoiGianXoa(Long thoiGianXoa) {
        this.thoiGianXoa = thoiGianXoa;
    }
}
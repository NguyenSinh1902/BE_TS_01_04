package iuh.fit.se.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chi_tiet_dat_ban")
public class ChiTietDatBan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chi_tiet")
    private Integer idChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_dat", nullable = false)
    private PhieuDatBan phieuDatBan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ban", nullable = false)
    private Ban ban;

    public ChiTietDatBan() {
    }

    public ChiTietDatBan(Integer idChiTiet, PhieuDatBan phieuDatBan, Ban ban) {
        this.idChiTiet = idChiTiet;
        this.phieuDatBan = phieuDatBan;
        this.ban = ban;
    }

    public Integer getIdChiTiet() {
        return idChiTiet;
    }

    public void setIdChiTiet(Integer idChiTiet) {
        this.idChiTiet = idChiTiet;
    }

    public PhieuDatBan getPhieuDatBan() {
        return phieuDatBan;
    }

    public void setPhieuDatBan(PhieuDatBan phieuDatBan) {
        this.phieuDatBan = phieuDatBan;
    }

    public Ban getBan() {
        return ban;
    }

    public void setBan(Ban ban) {
        this.ban = ban;
    }
}

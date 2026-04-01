package iuh.fit.se.repository;

import iuh.fit.se.entity.ChiTietDatBan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietDatBanRepository extends JpaRepository<ChiTietDatBan, Integer> {

    List<ChiTietDatBan> findByPhieuDatBan_IdPhieuDat(Integer idPhieuDat);

    void deleteByPhieuDatBan_IdPhieuDat(Integer idPhieuDat);
}

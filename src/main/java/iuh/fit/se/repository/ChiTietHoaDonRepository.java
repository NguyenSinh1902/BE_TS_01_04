package iuh.fit.se.repository;

import iuh.fit.se.entity.ChiTietHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietHoaDonRepository extends JpaRepository<ChiTietHoaDon, Integer> {
    List<ChiTietHoaDon> findByHoaDon_IdHoaDon(Integer idHoaDon);
}

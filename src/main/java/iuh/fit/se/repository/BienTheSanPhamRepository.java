package iuh.fit.se.repository;

import iuh.fit.se.entity.BienTheSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BienTheSanPhamRepository extends JpaRepository<BienTheSanPham, Integer> {
    List<BienTheSanPham> findBySanPham_IdSanPham(Integer idSanPham);
}
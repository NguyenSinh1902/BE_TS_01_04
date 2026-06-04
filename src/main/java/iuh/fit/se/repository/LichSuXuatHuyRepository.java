package iuh.fit.se.repository;

import iuh.fit.se.entity.LichSuXuatHuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LichSuXuatHuyRepository extends JpaRepository<LichSuXuatHuy, Integer> {
}

package iuh.fit.se.repository;

import iuh.fit.se.entity.NhatKyAi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NhatKyAiRepository extends JpaRepository<NhatKyAi, Integer> {

    List<NhatKyAi> findByQuanLy_IdNhanVienOrderByThoiGianTaoDesc(Integer idQuanLy);
}

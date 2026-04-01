package iuh.fit.se.repository;

import iuh.fit.se.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {

    Optional<DanhMuc> findByLaHeThongTrueAndThoiGianXoa(Long thoiGianXoa);

    boolean existsByTenDanhMucAndThoiGianXoa(String ten, Long thoiGianXoa);
}

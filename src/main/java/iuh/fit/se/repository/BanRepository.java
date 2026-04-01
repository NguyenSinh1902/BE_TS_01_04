package iuh.fit.se.repository;

import iuh.fit.se.entity.Ban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BanRepository extends JpaRepository<Ban, Integer> {

    List<Ban> findByThoiGianXoaOrderByTenBanAsc(Long thoiGianXoa);

    boolean existsByTenBanAndThoiGianXoa(String tenBan, Long thoiGianXoa);

    @Query("SELECT b FROM Ban b WHERE b.idBan = :id AND b.thoiGianXoa = 0")
    Optional<Ban> findActiveById(Integer id);
}
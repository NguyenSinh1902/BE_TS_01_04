package iuh.fit.se.repository;

import iuh.fit.se.entity.ThuePhi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThuePhiRepository extends JpaRepository<ThuePhi, Integer> {

    List<ThuePhi> findByLaMacDinhTrue();
}

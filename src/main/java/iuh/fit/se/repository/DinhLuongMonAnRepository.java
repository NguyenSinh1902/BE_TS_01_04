package iuh.fit.se.repository;

import iuh.fit.se.entity.DinhLuongMonAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DinhLuongMonAnRepository extends JpaRepository<DinhLuongMonAn, Integer> {
    
    // Tìm công thức định lượng tiêu hao kho khi bán của 1 BIẾN THỂ cụ thể (Size M, Size L)
    List<DinhLuongMonAn> findByBienThe_IdBienThe(Integer idBienThe);

    // Dùng để Upsert (Chống trùng lặp định lượng cho cùng 1 size và 1 nguyên liệu)
    java.util.Optional<DinhLuongMonAn> findByBienThe_IdBienTheAndNguyenLieu_IdNguyenLieu(Integer idBienThe, Integer idNguyenLieu);
}

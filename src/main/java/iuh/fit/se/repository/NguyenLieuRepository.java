package iuh.fit.se.repository;

import iuh.fit.se.entity.NguyenLieu;
import iuh.fit.se.enums.LoaiNguyenLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NguyenLieuRepository extends JpaRepository<NguyenLieu, Integer> {
    
    // Lấy danh sách kho nguyên liệu chưa bị xóa mềm
    List<NguyenLieu> findByThoiGianXoa(Long thoiGianXoa);
    
    // Lấy nguyên liệu theo loại phục vụ màn hình App Bếp
    List<NguyenLieu> findByLoaiNguyenLieuAndThoiGianXoa(LoaiNguyenLieu loai, Long thoiGianXoa);
}

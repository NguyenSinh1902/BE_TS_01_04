package iuh.fit.se.repository;

import iuh.fit.se.entity.RecipeChuyenDoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeChuyenDoiRepository extends JpaRepository<RecipeChuyenDoi, Integer> {
    
    // Tìm các nguyên liệu thô cấu thành nên món Bán thành phẩm này
    List<RecipeChuyenDoi> findByBanThanhPham_IdNguyenLieu(Integer idBanThanhPham);

    // Tìm xem đã cấu hình công thức cho Bán thành phẩm và Nguyên liệu thô này chưa (để Update)
    java.util.Optional<RecipeChuyenDoi> findByBanThanhPham_IdNguyenLieuAndNguyenLieuTho_IdNguyenLieu(Integer idBanThanhPham, Integer idNguyenLieuTho);
}

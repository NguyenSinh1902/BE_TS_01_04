package iuh.fit.se.repository;

import iuh.fit.se.entity.GoiYSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GoiYSanPhamRepository extends JpaRepository<GoiYSanPham, Integer> {

    @Query("SELECT g FROM GoiYSanPham g " +
            "JOIN FETCH g.sanPhamGoiY " +
            "WHERE g.sanPhamChinh.idSanPham = :idSpChinh " +
            "ORDER BY g.diemTinCay DESC")
    List<GoiYSanPham> findTopSuggestions(Integer idSpChinh);

    void deleteBySanPhamChinh_IdSanPham(Integer idSpChinh);
}
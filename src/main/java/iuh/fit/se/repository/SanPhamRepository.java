package iuh.fit.se.repository;

import iuh.fit.se.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    List<SanPham> findByDanhMuc_IdDanhMuc(Integer idDanhMuc);

    List<SanPham> findByLaToppingTrue();

    List<SanPham> findByLaToppingFalse();

    List<SanPham> findByTenSanPhamContainingIgnoreCase(String ten);

    boolean existsByTenSanPhamAndThoiGianXoa(String ten, Long thoiGianXoa);

    @Query("SELECT DISTINCT s FROM SanPham s JOIN s.danhSachBienThe b WHERE b.phanTramGiamGia > 0")
    List<SanPham> findSanPhamGiamGia();

    List<SanPham> findTop10ByOrderByIdSanPhamDesc();

    @Modifying
    @Query("UPDATE SanPham s SET s.danhMuc.idDanhMuc = :idMoi WHERE s.danhMuc.idDanhMuc = :idCu")
    void chuyenDanhMuc(@Param("idCu") Integer idCu, @Param("idMoi") Integer idMoi);
}
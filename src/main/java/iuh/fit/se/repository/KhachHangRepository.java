package iuh.fit.se.repository;

import iuh.fit.se.entity.KhachHang;
import iuh.fit.se.enums.HangThanhVien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    Optional<KhachHang> findBySoDienThoai(String soDienThoai);

    boolean existsBySoDienThoaiAndThoiGianXoa(String sdt, Long thoiGianXoa);

    List<KhachHang> findByHoTenContainingIgnoreCaseOrSoDienThoaiContaining(String hoTen, String sdt);
}

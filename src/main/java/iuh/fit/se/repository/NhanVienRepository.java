package iuh.fit.se.repository;

import iuh.fit.se.entity.NhanVien;
import iuh.fit.se.enums.TrangThaiNhanVien;
import iuh.fit.se.enums.VaiTroNhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    @Query("SELECT n FROM NhanVien n WHERE n.email = :email AND n.thoiGianXoa = 0")
    Optional<NhanVien> findActiveByEmail(String email);

    boolean existsByEmailAndThoiGianXoa(String email, Long thoiGianXoa);

    boolean existsBySoDienThoaiAndThoiGianXoa(String sdt, Long thoiGianXoa);

    Optional<NhanVien> findByIdNhanVienAndThoiGianXoa(Integer id, Long thoiGianXoa);

    List<NhanVien> findByVaiTroAndTrangThaiAndThoiGianXoa(VaiTroNhanVien vaiTro, TrangThaiNhanVien trangThai, Long thoiGianXoa);

    List<NhanVien> findByEmailVerifiedTrueAndTrangThaiAndThoiGianXoa(TrangThaiNhanVien trangThai, Long thoiGianXoa);

    @Query("SELECT n FROM NhanVien n WHERE n.trangThai IN (iuh.fit.se.enums.TrangThaiNhanVien.HOAT_DONG, iuh.fit.se.enums.TrangThaiNhanVien.BI_KHOA) AND n.thoiGianXoa = 0")
    List<NhanVien> findOperatingEmployees();
}

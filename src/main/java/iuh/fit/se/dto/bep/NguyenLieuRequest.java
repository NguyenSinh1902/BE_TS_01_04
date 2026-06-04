package iuh.fit.se.dto.bep;

import iuh.fit.se.enums.LoaiNguyenLieu;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record NguyenLieuRequest(
    @NotBlank(message = "Tên nguyên liệu không được để trống") String tenNguyenLieu,
    @NotNull(message = "Số lượng tồn không được để trống") BigDecimal soLuongTon,
    @NotBlank(message = "Đơn vị tính không được để trống") String donViTinh,
    @NotNull(message = "Loại nguyên liệu không được để trống") LoaiNguyenLieu loaiNguyenLieu,
    @NotNull(message = "Giá nhập đơn vị không được rỗng") BigDecimal giaNhapDonVi
) {}

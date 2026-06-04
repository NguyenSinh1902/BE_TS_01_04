package iuh.fit.se.dto.bep;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CheBienRequest(
    @NotNull(message = "ID bán thành phẩm muốn nấu không được để trống") Integer idBanThanhPham,
    @NotNull(message = "Số lượng đầu ra muốn sản xuất không được rỗng") BigDecimal soLuongSanXuat
) {}

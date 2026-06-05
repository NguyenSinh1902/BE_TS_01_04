package iuh.fit.se.dto.hoantra;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PhieuHoanTraRequest(
    @NotNull(message = "ID hóa đơn không được để trống")
    Integer idHoaDon,
    
    @NotNull(message = "Số tiền hoàn không được để trống")
    BigDecimal soTienHoan,
    
    String lyDo
) {}

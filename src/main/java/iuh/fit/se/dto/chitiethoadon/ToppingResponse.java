package iuh.fit.se.dto.chitiethoadon;

import java.math.BigDecimal;

public record ToppingResponse(
        String tenTopping,
        BigDecimal giaTopping
) {}

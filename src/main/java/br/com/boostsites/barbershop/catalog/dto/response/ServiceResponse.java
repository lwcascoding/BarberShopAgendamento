package br.com.boostsites.barbershop.catalog.dto.response;

import java.math.BigDecimal;

public record ServiceResponse(
        String code,
        String name,
        int durationMinutes,
        BigDecimal price
) {
}

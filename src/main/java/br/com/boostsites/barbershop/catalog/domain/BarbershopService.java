package br.com.boostsites.barbershop.catalog.domain;

import java.math.BigDecimal;
import java.util.Arrays;

public enum BarbershopService {
    HAIRCUT("Corte de Cabelo", 30, new BigDecimal("50.00")),
    BEARD("Barba", 30, new BigDecimal("40.00")),
    HAIRCUT_AND_BEARD("Corte + Barba", 60, new BigDecimal("80.00")),
    EYEBROW("Sobrancelha", 15, new BigDecimal("20.00")),
    NECKLINE("Pezinho", 15, new BigDecimal("20.00"));

    private final String displayName;
    private final int durationMinutes;
    private final BigDecimal price;

    BarbershopService(
            String displayName,
            int durationMinutes,
            BigDecimal price
    ) {
        this.displayName = displayName;
        this.durationMinutes = durationMinutes;
        this.price = price;
    }

    public String getCode() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public static BarbershopService fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Service code must not be blank");
        }

        return Arrays.stream(values())
                .filter(service -> service.name().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown service code: " + code
                ));
    }
}

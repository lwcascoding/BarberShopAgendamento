package br.com.boostsites.barbershop.availability.dto.response;

import java.time.LocalDateTime;

public record AvailableSlotResponse(
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
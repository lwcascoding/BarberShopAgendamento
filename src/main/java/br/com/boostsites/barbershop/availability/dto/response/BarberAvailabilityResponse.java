package br.com.boostsites.barbershop.availability.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record BarberAvailabilityResponse(
        Long id,
        Long barberId,
        String barberName,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
}
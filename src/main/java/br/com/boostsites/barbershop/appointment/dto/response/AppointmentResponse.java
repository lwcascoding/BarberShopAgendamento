package br.com.boostsites.barbershop.appointment.dto.response;

import br.com.boostsites.barbershop.appointment.domain.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long barberId,
        String barberName,
        Long customerId,
        String customerName,
        String customerPhone,
        String serviceCode,
        String serviceName,
        BigDecimal servicePrice,
        Integer durationMinutes,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status
) {
}

package br.com.boostsites.barbershop.appointment.dto.response;

import br.com.boostsites.barbershop.appointment.domain.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long barberId,
        Long customerId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status
) {
}

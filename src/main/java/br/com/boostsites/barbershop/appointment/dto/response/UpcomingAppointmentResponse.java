package br.com.boostsites.barbershop.appointment.dto.response;

import java.time.LocalDateTime;

public record UpcomingAppointmentResponse(
        Long id,
        String barberName,
        String serviceName,
        LocalDateTime startTime
) {
}

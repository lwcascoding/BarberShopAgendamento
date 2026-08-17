package br.com.boostsites.barbershop.appointment.dto.request;

import br.com.boostsites.barbershop.appointment.domain.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAppointmentStatusRequest(
        @NotNull AppointmentStatus status
) {
}

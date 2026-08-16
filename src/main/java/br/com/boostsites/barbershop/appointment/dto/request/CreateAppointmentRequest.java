package br.com.boostsites.barbershop.appointment.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(

        @NotNull
        Long barberId,

        @NotNull
        Long customerId,

        @NotNull
        LocalDateTime startTime,

        @NotNull
        LocalDateTime endTime

) {
}
package br.com.boostsites.barbershop.appointment.dto.request;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(

        @NotNull
        Long barberId,

        @NotNull
        Long customerId,

        @NotBlank
        String serviceCode,

        @NotNull
        LocalDateTime startTime

) {
}

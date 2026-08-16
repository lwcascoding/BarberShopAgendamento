package br.com.boostsites.barbershop.availability.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record UpdateBarberAvailabilityRequest(

        @NotNull
        LocalTime startTime,

        @NotNull
        LocalTime endTime

) {
}
package br.com.boostsites.barbershop.barber.dto.response;

public record BarberResponse(
        Long    id,
        String  name,
        String  phone,
        boolean active
) {
}
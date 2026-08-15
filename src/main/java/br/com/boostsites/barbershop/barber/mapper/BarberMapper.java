package br.com.boostsites.barbershop.barber.mapper;

import br.com.boostsites.barbershop.barber.domain.Barber;
import br.com.boostsites.barbershop.barber.dto.response.BarberResponse;

public final class BarberMapper {

    private BarberMapper() {}

    public static BarberResponse toResponse(Barber barber) {
        return new BarberResponse(
                barber.getId(),
                barber.getName(),
                barber.getPhone(),
                barber.isActive()        
        );
    }
}
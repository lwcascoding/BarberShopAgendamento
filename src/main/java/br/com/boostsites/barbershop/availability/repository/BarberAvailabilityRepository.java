package br.com.boostsites.barbershop.availability.repository;

import br.com.boostsites.barbershop.availability.domain.BarberAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface BarberAvailabilityRepository
        extends JpaRepository<BarberAvailability, Long> {

    List<BarberAvailability> findByBarberId(Long barberId);

    List<BarberAvailability> findByBarberIdAndDayOfWeek(
            Long barberId,
            DayOfWeek dayOfWeek
    );

}

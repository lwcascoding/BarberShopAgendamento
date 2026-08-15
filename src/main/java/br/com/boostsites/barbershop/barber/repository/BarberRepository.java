package br.com.boostsites.barbershop.barber.repository;

import br.com.boostsites.barbershop.barber.domain.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarberRepository extends JpaRepository<Barber, Long> {
}
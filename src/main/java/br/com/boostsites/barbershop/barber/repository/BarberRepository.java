package br.com.boostsites.barbershop.barber.repository;

import br.com.boostsites.barbershop.barber.domain.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarberRepository extends JpaRepository<Barber, Long> {

    List<Barber> findAllByActiveTrueOrderByNameAsc();
}
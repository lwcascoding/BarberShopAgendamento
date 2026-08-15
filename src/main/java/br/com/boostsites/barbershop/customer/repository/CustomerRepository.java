package br.com.boostsites.barbershop.customer.repository;

import br.com.boostsites.barbershop.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
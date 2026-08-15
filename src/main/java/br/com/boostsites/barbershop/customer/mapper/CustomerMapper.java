package br.com.boostsites.barbershop.customer.mapper;

import br.com.boostsites.barbershop.customer.domain.Customer;
import br.com.boostsites.barbershop.customer.dto.response.CustomerResponse;

public final class CustomerMapper {

    private CustomerMapper() {
    }

    public static CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getPhone()
        );
    }
}
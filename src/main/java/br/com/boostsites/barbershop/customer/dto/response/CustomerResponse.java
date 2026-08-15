package br.com.boostsites.barbershop.customer.dto.response;

public record CustomerResponse(
        Long id,
        String name,
        String phone
) {
}
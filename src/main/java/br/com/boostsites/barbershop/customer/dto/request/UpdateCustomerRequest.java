package br.com.boostsites.barbershop.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(

        @NotBlank(message = "Name must not be blank")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Phone must not be blank")
        @Size(max = 20, message = "Phone must not exceed 20 characters")
        String phone

) {
}
package br.com.boostsites.barbershop.customer.controller;

import br.com.boostsites.barbershop.customer.dto.request.CreateCustomerRequest;
import br.com.boostsites.barbershop.customer.dto.response.CustomerResponse;
import br.com.boostsites.barbershop.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/customers")
public class PublicCustomerController {

    private final CustomerService customerService;

    public PublicCustomerController(
            CustomerService customerService
    ) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(
            @Valid @RequestBody CreateCustomerRequest request
    ) {
        CustomerResponse customer =
                customerService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customer);
    }
}
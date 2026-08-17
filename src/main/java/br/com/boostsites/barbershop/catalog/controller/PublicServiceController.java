package br.com.boostsites.barbershop.catalog.controller;

import br.com.boostsites.barbershop.catalog.domain.BarbershopService;
import br.com.boostsites.barbershop.catalog.dto.response.ServiceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/public/services")
public class PublicServiceController {

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> findAll() {
        List<ServiceResponse> services = Arrays.stream(BarbershopService.values())
                .map(service -> new ServiceResponse(
                        service.getCode(),
                        service.getDisplayName(),
                        service.getDurationMinutes(),
                        service.getPrice()
                ))
                .toList();

        return ResponseEntity.ok(services);
    }
}

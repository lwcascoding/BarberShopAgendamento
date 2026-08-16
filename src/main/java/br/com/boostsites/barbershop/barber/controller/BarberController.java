package br.com.boostsites.barbershop.barber.controller;

import br.com.boostsites.barbershop.barber.dto.response.BarberResponse;
import br.com.boostsites.barbershop.barber.service.BarberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/barbers")
public class BarberController {

    private final BarberService barberService;

    public BarberController(BarberService barberService) {
        this.barberService = barberService;
    }

    @GetMapping
    public ResponseEntity<List<BarberResponse>> findAllActive() {
        return ResponseEntity.ok(
                barberService.findAllActive()
        );
    }
}
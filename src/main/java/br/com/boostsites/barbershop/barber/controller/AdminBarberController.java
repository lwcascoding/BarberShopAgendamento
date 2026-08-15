package br.com.boostsites.barbershop.barber.controller;

import br.com.boostsites.barbershop.barber.dto.request.CreateBarberRequest;
import br.com.boostsites.barbershop.barber.dto.request.UpdateBarberRequest;
import br.com.boostsites.barbershop.barber.dto.response.BarberResponse;
import br.com.boostsites.barbershop.barber.service.BarberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/barbers")
public class AdminBarberController {

    private final BarberService barberService;

    public AdminBarberController(BarberService barberService) {
        this.barberService = barberService;
    }

    @PostMapping
    public ResponseEntity<BarberResponse> create(
            @Valid @RequestBody CreateBarberRequest request
    ) {
        BarberResponse response = barberService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<BarberResponse>> findAll() {
        return ResponseEntity.ok(barberService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BarberResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(barberService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BarberResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBarberRequest request
    ) {
        return ResponseEntity.ok(barberService.update(id, request));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<BarberResponse> activate(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(barberService.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<BarberResponse> deactivate(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(barberService.deactivate(id));
    }
}
package br.com.boostsites.barbershop.availability.controller;

import br.com.boostsites.barbershop.availability.dto.request.CreateBarberAvailabilityRequest;
import br.com.boostsites.barbershop.availability.dto.request.UpdateBarberAvailabilityRequest;
import br.com.boostsites.barbershop.availability.dto.response.BarberAvailabilityResponse;
import br.com.boostsites.barbershop.availability.service.BarberAvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/availabilities")
public class BarberAvailabilityController {

    private final BarberAvailabilityService availabilityService;

    public BarberAvailabilityController(
            BarberAvailabilityService availabilityService
    ) {
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public ResponseEntity<BarberAvailabilityResponse> create(
            @Valid @RequestBody CreateBarberAvailabilityRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(availabilityService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BarberAvailabilityResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                availabilityService.findById(id)
        );
    }

    @GetMapping("/barber/{barberId}")
    public ResponseEntity<List<BarberAvailabilityResponse>> findByBarber(
            @PathVariable Long barberId
    ) {
        return ResponseEntity.ok(
                availabilityService.findByBarberId(barberId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BarberAvailabilityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBarberAvailabilityRequest request
    ) {
        return ResponseEntity.ok(
                availabilityService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        availabilityService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
package br.com.boostsites.barbershop.availability.controller;

import br.com.boostsites.barbershop.availability.dto.response.AvailableSlotResponse;
import br.com.boostsites.barbershop.availability.service.BarberAvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/public/barbers")
public class PublicAvailabilityController {

    private final BarberAvailabilityService availabilityService;

    public PublicAvailabilityController(
            BarberAvailabilityService availabilityService
    ) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{barberId}/available-slots")
    public ResponseEntity<List<AvailableSlotResponse>> findAvailableSlots(
            @PathVariable Long barberId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(defaultValue = "30")
            @Min(15)
            @Max(180)
            int durationMinutes
    ) {
        List<AvailableSlotResponse> slots =
                availabilityService.findAvailableSlots(
                        barberId,
                        date,
                        durationMinutes
                );

        return ResponseEntity.ok(slots);
    }
}

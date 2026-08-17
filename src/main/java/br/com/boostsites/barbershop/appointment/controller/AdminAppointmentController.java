package br.com.boostsites.barbershop.appointment.controller;

import br.com.boostsites.barbershop.appointment.domain.Appointment;
import br.com.boostsites.barbershop.appointment.domain.AppointmentStatus;
import br.com.boostsites.barbershop.appointment.dto.request.UpdateAppointmentStatusRequest;
import br.com.boostsites.barbershop.appointment.dto.response.AppointmentResponse;
import br.com.boostsites.barbershop.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/appointments")
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    public AdminAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> findAll() {
        return ResponseEntity.ok(
                appointmentService.findAll()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentStatusRequest request
    ) {
        Appointment appointment;

        if (request.status() == AppointmentStatus.COMPLETED) {
            appointment = appointmentService.complete(id);
        } else if (request.status() == AppointmentStatus.CANCELED) {
            appointment = appointmentService.cancel(id);
        } else {
            throw new IllegalArgumentException(
                    "Status can only be changed to COMPLETED or CANCELED"
            );
        }

        return ResponseEntity.ok(toResponse(appointment));
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getBarber().getId(),
                appointment.getBarber().getName(),
                appointment.getCustomer().getId(),
                appointment.getCustomer().getName(),
                appointment.getCustomer().getPhone(),
                appointment.getServiceCode(),
                appointment.getServiceName(),
                appointment.getServicePrice(),
                appointment.getDurationMinutes(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus()
        );
    }
}

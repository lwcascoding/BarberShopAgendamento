package br.com.boostsites.barbershop.appointment.controller;

import br.com.boostsites.barbershop.appointment.domain.Appointment;
import br.com.boostsites.barbershop.appointment.dto.request.CreateAppointmentRequest;
import br.com.boostsites.barbershop.appointment.dto.request.RescheduleAppointmentRequest;
import br.com.boostsites.barbershop.appointment.dto.response.AppointmentResponse;
import br.com.boostsites.barbershop.appointment.dto.response.UpcomingAppointmentResponse;
import br.com.boostsites.barbershop.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService
    ) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(
            @Valid @RequestBody CreateAppointmentRequest request
    ) {
        Appointment appointment = appointmentService.create(
                request.barberId(),
                request.customerId(),
                request.serviceCode(),
                request.startTime()
        );

        return toResponse(appointment);
    }

    @GetMapping("/{id}")
    public AppointmentResponse findById(
            @PathVariable Long id
    ) {
        return toResponse(
                appointmentService.findById(id)
        );
    }

    @GetMapping("/upcoming")
    public ResponseEntity<UpcomingAppointmentResponse> findUpcoming() {
        Appointment appointment = appointmentService.findNextScheduled(
                java.time.LocalDateTime.now()
        );

        if (appointment == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(new UpcomingAppointmentResponse(
                appointment.getId(),
                appointment.getBarber().getName(),
                appointment.getServiceName(),
                appointment.getStartTime()
        ));
    }

    @GetMapping
    public List<AppointmentResponse> findByCustomerPhone(
            @RequestParam String phone
    ) {
        return appointmentService.findByCustomerPhone(phone)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(
            @PathVariable Long id
    ) {
        return toResponse(
                appointmentService.cancel(id)
        );
    }

    @PatchMapping("/{id}/reschedule")
    public AppointmentResponse reschedule(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleAppointmentRequest request
    ) {
        Appointment appointment =
                appointmentService.reschedule(
                        id,
                        request.startTime(),
                        request.endTime()
                );

        return toResponse(appointment);
    }

    private AppointmentResponse toResponse(
            Appointment appointment
    ) {
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

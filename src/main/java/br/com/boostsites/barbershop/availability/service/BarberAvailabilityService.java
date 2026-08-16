package br.com.boostsites.barbershop.availability.service;

import br.com.boostsites.barbershop.appointment.domain.Appointment;
import br.com.boostsites.barbershop.appointment.domain.AppointmentStatus;
import br.com.boostsites.barbershop.appointment.repository.AppointmentRepository;
import br.com.boostsites.barbershop.availability.domain.BarberAvailability;
import br.com.boostsites.barbershop.availability.dto.request.CreateBarberAvailabilityRequest;
import br.com.boostsites.barbershop.availability.dto.request.UpdateBarberAvailabilityRequest;
import br.com.boostsites.barbershop.availability.dto.response.AvailableSlotResponse;
import br.com.boostsites.barbershop.availability.dto.response.BarberAvailabilityResponse;
import br.com.boostsites.barbershop.availability.repository.BarberAvailabilityRepository;
import br.com.boostsites.barbershop.barber.domain.Barber;
import br.com.boostsites.barbershop.barber.repository.BarberRepository;
import br.com.boostsites.barbershop.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BarberAvailabilityService {

    private static final int SLOT_DURATION_MINUTES = 30;

    private final BarberAvailabilityRepository availabilityRepository;
    private final BarberRepository barberRepository;
    private final AppointmentRepository appointmentRepository;

    public BarberAvailabilityService(
            BarberAvailabilityRepository availabilityRepository,
            BarberRepository barberRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.availabilityRepository = availabilityRepository;
        this.barberRepository = barberRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public BarberAvailabilityResponse create(
            CreateBarberAvailabilityRequest request
    ) {
        Barber barber = findBarberById(request.barberId());

        BarberAvailability availability =
                new BarberAvailability(
                        barber,
                        request.dayOfWeek(),
                        request.startTime(),
                        request.endTime()
                );

        BarberAvailability savedAvailability =
                availabilityRepository.save(availability);

        return toResponse(savedAvailability);
    }

    @Transactional(readOnly = true)
    public List<BarberAvailabilityResponse> findByBarberId(
            Long barberId
    ) {
        findBarberById(barberId);

        return availabilityRepository
                .findByBarberId(barberId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BarberAvailabilityResponse findById(Long id) {
        return toResponse(findAvailabilityById(id));
    }

    @Transactional
    public BarberAvailabilityResponse update(
            Long id,
            UpdateBarberAvailabilityRequest request
    ) {
        BarberAvailability availability =
                findAvailabilityById(id);

        availability.changeTimeRange(
                request.startTime(),
                request.endTime()
        );

        return toResponse(availability);
    }

    @Transactional
    public void delete(Long id) {
        BarberAvailability availability =
                findAvailabilityById(id);

        availabilityRepository.delete(availability);
    }

    @Transactional(readOnly = true)
    public List<AvailableSlotResponse> findAvailableSlots(
            Long barberId,
            LocalDate date
    ) {
        findBarberById(barberId);

        List<BarberAvailability> availabilities =
                availabilityRepository.findByBarberIdAndDayOfWeek(
                        barberId,
                        date.getDayOfWeek()
                );

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments =
                appointmentRepository
                        .findByBarberIdAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThanOrderByStartTimeAsc(
                                barberId,
                                AppointmentStatus.CANCELED,
                                dayEnd,
                                dayStart
                        );

        List<AvailableSlotResponse> slots = new ArrayList<>();

        for (BarberAvailability availability : availabilities) {

            LocalDateTime slotStart =
                    date.atTime(availability.getStartTime());

            LocalDateTime availabilityEnd =
                    date.atTime(availability.getEndTime());

            while (!slotStart
                    .plusMinutes(SLOT_DURATION_MINUTES)
                    .isAfter(availabilityEnd)) {

                LocalDateTime slotEnd =
                        slotStart.plusMinutes(SLOT_DURATION_MINUTES);

                if (!hasConflict(
                        slotStart,
                        slotEnd,
                        appointments
                )) {
                    slots.add(
                            new AvailableSlotResponse(
                                    slotStart,
                                    slotEnd
                            )
                    );
                }

                slotStart = slotEnd;
            }
        }

        return slots;
    }

    @Transactional(readOnly = true)
    public List<Appointment> findAll() {
        return appointmentRepository.findAllByOrderByStartTimeAsc();
    }

    private boolean hasConflict(
            LocalDateTime slotStart,
            LocalDateTime slotEnd,
            List<Appointment> appointments
    ) {
        return appointments.stream()
                .anyMatch(appointment ->
                        appointment.getStartTime().isBefore(slotEnd)
                                &&
                        appointment.getEndTime().isAfter(slotStart)
                );
    }

    private BarberAvailability findAvailabilityById(Long id) {
        return availabilityRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Barber availability not found with id: " + id
                        )
                );
    }

    private Barber findBarberById(Long id) {
        return barberRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Barber not found with id: " + id
                        )
                );
    }

    private BarberAvailabilityResponse toResponse(
            BarberAvailability availability
    ) {
        return new BarberAvailabilityResponse(
                availability.getId(),
                availability.getBarber().getId(),
                availability.getBarber().getName(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }
}

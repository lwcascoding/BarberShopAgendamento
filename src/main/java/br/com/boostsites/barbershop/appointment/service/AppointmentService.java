package br.com.boostsites.barbershop.appointment.service;

import br.com.boostsites.barbershop.appointment.domain.Appointment;
import br.com.boostsites.barbershop.appointment.domain.AppointmentStatus;
import br.com.boostsites.barbershop.appointment.exception.AppointmentConflictException;
import br.com.boostsites.barbershop.appointment.exception.BarberUnavailableException;
import br.com.boostsites.barbershop.appointment.repository.AppointmentRepository;
import br.com.boostsites.barbershop.availability.domain.BarberAvailability;
import br.com.boostsites.barbershop.availability.repository.BarberAvailabilityRepository;
import br.com.boostsites.barbershop.barber.domain.Barber;
import br.com.boostsites.barbershop.barber.repository.BarberRepository;
import br.com.boostsites.barbershop.customer.domain.Customer;
import br.com.boostsites.barbershop.customer.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final BarberRepository barberRepository;
    private final CustomerRepository customerRepository;
    private final BarberAvailabilityRepository availabilityRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            BarberRepository barberRepository,
            CustomerRepository customerRepository,
            BarberAvailabilityRepository availabilityRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.barberRepository = barberRepository;
        this.customerRepository = customerRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Transactional
    public Appointment create(
            Long barberId,
            Long customerId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        Barber barber = findBarber(barberId);
        Customer customer = findCustomer(customerId);

        validateAvailability(
                barberId,
                startTime,
                endTime
        );

        validateConflict(
                barberId,
                startTime,
                endTime
        );

        Appointment appointment = new Appointment(
                barber,
                customer,
                startTime,
                endTime
        );

        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public Appointment findById(Long id) {
        return findAppointment(id);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findAll() {
        return appointmentRepository.findAllByOrderByStartTimeAsc();
    }

    @Transactional
    public Appointment cancel(Long id) {
        Appointment appointment = findAppointment(id);

        appointment.cancel();

        return appointment;
    }

    @Transactional
    public Appointment reschedule(
            Long id,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        Appointment appointment = findAppointment(id);
        Long barberId = appointment.getBarber().getId();

        validateAvailability(
                barberId,
                startTime,
                endTime
        );

        validateConflict(
                barberId,
                id,
                startTime,
                endTime
        );

        appointment.reschedule(startTime, endTime);

        return appointment;
    }

    private Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Appointment not found with id: " + id
                        )
                );
    }

    private Barber findBarber(Long barberId) {
        return barberRepository.findById(barberId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Barber not found with id: " + barberId
                        )
                );
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Customer not found with id: " + customerId
                        )
                );
    }

    private void validateConflict(
            Long barberId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        boolean conflict =
                appointmentRepository
                        .existsByBarberIdAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
                                barberId,
                                AppointmentStatus.CANCELED,
                                endTime,
                                startTime
                        );

        if (conflict) {
            throw new AppointmentConflictException(
                    "Barber already has an appointment in this time range"
            );
        }
    }

    private void validateConflict(
            Long barberId,
            Long appointmentId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        boolean conflict =
                appointmentRepository
                        .existsByBarberIdAndIdNotAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
                                barberId,
                                appointmentId,
                                AppointmentStatus.CANCELED,
                                endTime,
                                startTime
                        );

        if (conflict) {
            throw new AppointmentConflictException(
                    "Barber already has an appointment in this time range"
            );
        }
    }

    private void validateAvailability(
            Long barberId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        List<BarberAvailability> availabilities =
                availabilityRepository.findByBarberIdAndDayOfWeek(
                        barberId,
                        startTime.getDayOfWeek()
                );

        boolean sameDay = startTime.toLocalDate()
                .equals(endTime.toLocalDate());

        boolean available = sameDay
                && availabilities.stream()
                        .anyMatch(
                                availability ->
                                        !startTime.toLocalTime()
                                                .isBefore(availability.getStartTime())
                                        &&
                                        !endTime.toLocalTime()
                                                .isAfter(availability.getEndTime())
                        );

        if (!available) {
            throw new BarberUnavailableException(
                    "Barber is not available in this time range"
            );
        }
    }
}

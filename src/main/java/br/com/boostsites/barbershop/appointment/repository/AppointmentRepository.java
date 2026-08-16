package br.com.boostsites.barbershop.appointment.repository;

import br.com.boostsites.barbershop.appointment.domain.Appointment;
import br.com.boostsites.barbershop.appointment.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    boolean existsByBarberIdAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long barberId,
            AppointmentStatus ignoredStatus,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    boolean existsByBarberIdAndIdNotAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long barberId,
            Long appointmentId,
            AppointmentStatus ignoredStatus,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    List<Appointment> findByBarberIdAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThanOrderByStartTimeAsc(
            Long barberId,
            AppointmentStatus ignoredStatus,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    List<Appointment> findAllByOrderByStartTimeAsc();
}
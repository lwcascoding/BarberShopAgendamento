package br.com.boostsites.barbershop.appointment.domain;

import br.com.boostsites.barbershop.barber.domain.Barber;
import br.com.boostsites.barbershop.customer.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    private Barber barber;
    private Customer customer;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        barber = new Barber(
                "Carlos",
                "11999999999"
        );

        customer = new Customer(
                "Nicolas",
                "11888888888"
        );

        startTime = LocalDateTime.of(
                2026,
                8,
                17,
                14,
                0
        );

        endTime = startTime.plusMinutes(30);
    }

    @Test
    void shouldCreateScheduledAppointment() {
        Appointment appointment = new Appointment(
                barber,
                customer,
                startTime,
                endTime
        );

        assertNull(appointment.getId());
        assertSame(barber, appointment.getBarber());
        assertSame(customer, appointment.getCustomer());
        assertEquals(startTime, appointment.getStartTime());
        assertEquals(endTime, appointment.getEndTime());
        assertEquals(
                AppointmentStatus.SCHEDULED,
                appointment.getStatus()
        );
    }

    @Test
    void shouldRejectNullBarber() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Appointment(
                        null,
                        customer,
                        startTime,
                        endTime
                )
        );
    }

    @Test
    void shouldRejectNullCustomer() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Appointment(
                        barber,
                        null,
                        startTime,
                        endTime
                )
        );
    }

    @Test
    void shouldRejectNullStartTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Appointment(
                        barber,
                        customer,
                        null,
                        endTime
                )
        );
    }

    @Test
    void shouldRejectNullEndTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Appointment(
                        barber,
                        customer,
                        startTime,
                        null
                )
        );
    }

    @Test
    void shouldRejectEqualStartAndEndTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Appointment(
                        barber,
                        customer,
                        startTime,
                        startTime
                )
        );
    }

    @Test
    void shouldRejectStartTimeAfterEndTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Appointment(
                        barber,
                        customer,
                        endTime,
                        startTime
                )
        );
    }

    @Test
    void shouldCancelScheduledAppointment() {
        Appointment appointment = createAppointment();

        appointment.cancel();

        assertEquals(
                AppointmentStatus.CANCELED,
                appointment.getStatus()
        );
    }

    @Test
    void shouldCompleteScheduledAppointment() {
        Appointment appointment = createAppointment();

        appointment.complete();

        assertEquals(
                AppointmentStatus.COMPLETED,
                appointment.getStatus()
        );
    }

    @Test
    void shouldRejectCancelingCompletedAppointment() {
        Appointment appointment = createAppointment();
        appointment.complete();

        assertThrows(
                IllegalStateException.class,
                appointment::cancel
        );
    }

    @Test
    void shouldRejectCompletingCanceledAppointment() {
        Appointment appointment = createAppointment();
        appointment.cancel();

        assertThrows(
                IllegalStateException.class,
                appointment::complete
        );
    }

    @Test
    void shouldRescheduleScheduledAppointment() {
        Appointment appointment = createAppointment();

        LocalDateTime newStartTime =
                startTime.plusHours(2);

        LocalDateTime newEndTime =
                newStartTime.plusMinutes(30);

        appointment.reschedule(
                newStartTime,
                newEndTime
        );

        assertEquals(
                newStartTime,
                appointment.getStartTime()
        );

        assertEquals(
                newEndTime,
                appointment.getEndTime()
        );
    }

    @Test
    void shouldRejectReschedulingCanceledAppointment() {
        Appointment appointment = createAppointment();
        appointment.cancel();

        LocalDateTime newStartTime =
                startTime.plusHours(2);

        LocalDateTime newEndTime =
                newStartTime.plusMinutes(30);

        assertThrows(
                IllegalStateException.class,
                () -> appointment.reschedule(
                        newStartTime,
                        newEndTime
                )
        );
    }

    private Appointment createAppointment() {
        return new Appointment(
                barber,
                customer,
                startTime,
                endTime
        );
    }
}
package br.com.boostsites.barbershop.appointment.domain;

import br.com.boostsites.barbershop.barber.domain.Barber;
import br.com.boostsites.barbershop.customer.domain.Customer;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "barber_id", nullable = false)
    private Barber barber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status;

    protected Appointment() {
    }

    public Appointment(
            Barber barber,
            Customer customer,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        this.barber = validateBarber(barber);
        this.customer = validateCustomer(customer);

        validateTimeRange(startTime, endTime);

        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AppointmentStatus.SCHEDULED;
    }

    public Long getId()                  {return id;}
    public Barber getBarber()            {return barber;}
    public Customer getCustomer()        {return customer;}
    public LocalDateTime getStartTime()  {return startTime;}
    public LocalDateTime getEndTime()    {return endTime;}
    public AppointmentStatus getStatus() {return status;}

    public void cancel() {
        if (status == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Completed appointment cannot be canceled"
            );
        }

        status = AppointmentStatus.CANCELED;
    }

    public void complete() {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "Only scheduled appointments can be completed"
            );
        }

        status = AppointmentStatus.COMPLETED;
    }

    public void reschedule(
            LocalDateTime newStartTime,
            LocalDateTime newEndTime
    ) {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "Only scheduled appointments can be rescheduled"
            );
        }

        validateTimeRange(newStartTime, newEndTime);

        this.startTime = newStartTime;
        this.endTime = newEndTime;
    }

    private static Barber validateBarber(Barber barber) {
        if (barber == null) {
            throw new IllegalArgumentException(
                    "Barber must not be null"
            );
        }

        return barber;
    }

    private static Customer validateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer must not be null"
            );
        }

        return customer;
    }

    private static void validateTimeRange(
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException(
                    "Start time and end time must not be null"
            );
        }

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException(
                    "Start time must be before end time"
            );
        }
    }
}
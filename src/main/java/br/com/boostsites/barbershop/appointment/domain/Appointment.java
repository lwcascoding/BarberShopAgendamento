package br.com.boostsites.barbershop.appointment.domain;

import br.com.boostsites.barbershop.barber.domain.Barber;
import br.com.boostsites.barbershop.catalog.domain.BarbershopService;
import br.com.boostsites.barbershop.customer.domain.Customer;
import jakarta.persistence.*;

import java.math.BigDecimal;
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

    @Column(name = "service_code", nullable = false, length = 30)
    private String serviceCode;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "service_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal servicePrice;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    protected Appointment() {
    }

    public Appointment(
            Barber barber,
            Customer customer,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        this(
                barber,
                customer,
                BarbershopService.HAIRCUT,
                startTime,
                endTime
        );
    }

    private Appointment(
            Barber barber,
            Customer customer,
            BarbershopService service,
            LocalDateTime startTime
    ) {
        this(
                barber,
                customer,
                service,
                startTime,
                calculateEndTime(startTime, service)
        );
    }

    public static Appointment schedule(
            Barber barber,
            Customer customer,
            BarbershopService service,
            LocalDateTime startTime
    ) {
        return new Appointment(
                barber,
                customer,
                service,
                startTime
        );
    }

    private Appointment(
            Barber barber,
            Customer customer,
            BarbershopService service,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        this.barber = validateBarber(barber);
        this.customer = validateCustomer(customer);
        BarbershopService validService = validateService(service);

        validateTimeRange(startTime, endTime);

        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AppointmentStatus.SCHEDULED;
        this.serviceCode = validService.getCode();
        this.serviceName = validService.getDisplayName();
        this.servicePrice = validService.getPrice();
        this.durationMinutes = validService.getDurationMinutes();
    }

    public Long getId()                  {return id;}
    public Barber getBarber()            {return barber;}
    public Customer getCustomer()        {return customer;}
    public LocalDateTime getStartTime()  {return startTime;}
    public LocalDateTime getEndTime()    {return endTime;}
    public AppointmentStatus getStatus() {return status;}
    public String getServiceCode()        {return serviceCode;}
    public String getServiceName()        {return serviceName;}
    public BigDecimal getServicePrice()   {return servicePrice;}
    public Integer getDurationMinutes()   {return durationMinutes;}

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

    private static BarbershopService validateService(
            BarbershopService service
    ) {
        if (service == null) {
            throw new IllegalArgumentException(
                    "Service must not be null"
            );
        }

        return service;
    }

    private static LocalDateTime calculateEndTime(
            LocalDateTime startTime,
            BarbershopService service
    ) {
        if (startTime == null || service == null) {
            return null;
        }

        return startTime.plusMinutes(service.getDurationMinutes());
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

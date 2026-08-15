package br.com.boostsites.barbershop.availability.domain;

import br.com.boostsites.barbershop.barber.domain.Barber;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "barber_availabilities")
public class BarberAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "barber_id", nullable = false)
    private Barber barber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    protected BarberAvailability() {
    }

    public BarberAvailability(
            Barber barber,
            DayOfWeek dayOfWeek,
            LocalTime starTime,
            LocalTime endTime
    ) {
        this.barber = validateBarber(barber);
        this.dayOfWeek = validateDayOfWeek(dayOfWeek);
        validateTimeRange(startTime, endTime);
        this.startTime = starTime;
        this.endTime = endTime;
    }

    public Long getId()             {return id;}
    public Barber getBarber()       {return barber;}
    public DayOfWeek getDayOfWeek() {return dayOfWeek;}
    public LocalTime getStarTime()  {return startTime;}
    public LocalTime getEndTime()   {return endTime;}

    public void changeTimeRange(
            LocalTime starTime,
            LocalTime endTime
    ) {
        validateTimeRange(startTime, endTime);
        this.startTime = starTime;
        this.endTime = endTime;
    }
    private static Barber validateBarber(Barber barber) {
        if (barber == null) {
            throw new IllegalArgumentException("Barber must not be null");
        }

        return barber;
    }

    private static DayOfWeek validateDayOfWeek(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("Day of week must not be null");
        }

        return dayOfWeek;
    }

    private static void validateTimeRange(
            LocalTime startTime,
            LocalTime endTime
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
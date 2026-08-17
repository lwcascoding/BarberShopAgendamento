ALTER TABLE appointments
    ADD COLUMN service_code VARCHAR(30) NOT NULL DEFAULT 'HAIRCUT',
    ADD COLUMN service_name VARCHAR(100) NOT NULL DEFAULT 'Corte de Cabelo',
    ADD COLUMN service_price NUMERIC(10, 2) NOT NULL DEFAULT 50.00,
    ADD COLUMN duration_minutes INTEGER NOT NULL DEFAULT 30;

ALTER TABLE appointments
    ADD CONSTRAINT chk_appointments_service_price CHECK (service_price >= 0),
    ADD CONSTRAINT chk_appointments_duration CHECK (duration_minutes > 0);

CREATE UNIQUE INDEX uq_customers_phone ON customers (phone);

INSERT INTO barbers (name, phone, active)
SELECT 'Felipe Fernandes', '24 98140-9877', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM barbers WHERE phone = '24 98140-9877'
);

INSERT INTO barber_availabilities (barber_id, day_of_week, start_time, end_time)
SELECT b.id, schedule.day_of_week, schedule.start_time, schedule.end_time
FROM barbers b
CROSS JOIN (
    VALUES
        ('MONDAY',    TIME '09:00', TIME '20:00'),
        ('TUESDAY',   TIME '09:00', TIME '20:00'),
        ('WEDNESDAY', TIME '09:00', TIME '20:00'),
        ('THURSDAY',  TIME '09:00', TIME '20:00'),
        ('FRIDAY',    TIME '09:00', TIME '20:00'),
        ('SATURDAY',  TIME '08:00', TIME '18:00')
) AS schedule(day_of_week, start_time, end_time)
WHERE b.phone = '24 98140-9877'
  AND NOT EXISTS (
      SELECT 1
      FROM barber_availabilities existing
      WHERE existing.barber_id = b.id
        AND existing.day_of_week = schedule.day_of_week
  );

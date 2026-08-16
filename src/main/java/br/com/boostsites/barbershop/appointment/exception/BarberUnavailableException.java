package br.com.boostsites.barbershop.appointment.exception;

public class BarberUnavailableException extends RuntimeException {

    public BarberUnavailableException(String message) {
        super(message);
    }
}
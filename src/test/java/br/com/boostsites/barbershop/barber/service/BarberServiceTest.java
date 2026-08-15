package br.com.boostsites.barbershop.barber.service;

import br.com.boostsites.barbershop.barber.domain.Barber;
import br.com.boostsites.barbershop.barber.dto.request.CreateBarberRequest;
import br.com.boostsites.barbershop.barber.dto.response.BarberResponse;
import br.com.boostsites.barbershop.barber.repository.BarberRepository;
import br.com.boostsites.barbershop.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BarberServiceTest {

    private BarberRepository barberRepository;
    private BarberService barberService;

    @BeforeEach
    void setUp() {
        barberRepository = mock(BarberRepository.class);
        barberService = new BarberService(barberRepository);
    }

    @Test
    void shouldCreateBarber() {
        CreateBarberRequest request = new CreateBarberRequest(
                "Carlos",
                "11999999999"
        );

        when(barberRepository.save(any(Barber.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BarberResponse response = barberService.create(request);

        assertEquals("Carlos", response.name());
        assertEquals("11999999999", response.phone());
        assertTrue(response.active());

        verify(barberRepository).save(any(Barber.class));
    }

    @Test
    void shouldFindBarberById() {
        Barber barber = new Barber(
                "Carlos",
                "11999999999"
        );

        when(barberRepository.findById(1L))
                .thenReturn(Optional.of(barber));

        BarberResponse response = barberService.findById(1L);

        assertEquals("Carlos", response.name());
        assertEquals("11999999999", response.phone());

        verify(barberRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenBarberDoesNotExist() {
        when(barberRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> barberService.findById(99L)
        );

        verify(barberRepository).findById(99L);
    }
}
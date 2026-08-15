package br.com.boostsites.barbershop.barber.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BarberTest {

    @Test
    void shouldCreateActiveBarber() {
        Barber barber = new Barber(
                "Carlos",
                "11999999999"
        );

        assertNull(barber.getId());
        assertEquals("Carlos", barber.getName());
        assertEquals("11999999999", barber.getPhone());
        assertTrue(barber.isActive());
    }

    @Test
    void shouldTrimNameAndPhoneWhenCreatingBarber() {
        Barber barber = new Barber(
                "  Carlos  ",
                "  11999999999  "
        );

        assertEquals("Carlos", barber.getName());
        assertEquals("11999999999", barber.getPhone());
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Barber("   ", "11999999999")
        );
    }

    @Test
    void shouldRejectBlankPhone() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Barber("Carlos", "   ")
        );
    }

    @Test
    void shouldRenameBarber() {
        Barber barber = new Barber(
                "Carlos",
                "11999999999"
        );

        barber.rename("João");

        assertEquals("João", barber.getName());
    }

    @Test
    void shouldChangePhone() {
        Barber barber = new Barber(
                "Carlos",
                "11999999999"
        );

        barber.changePhone("11888888888");

        assertEquals("11888888888", barber.getPhone());
    }

    @Test
    void shouldDeactivateBarber() {
        Barber barber = new Barber(
                "Carlos",
                "11999999999"
        );

        barber.deactivate();

        assertFalse(barber.isActive());
    }

    @Test
    void shouldActivateBarber() {
        Barber barber = new Barber(
                "Carlos",
                "11999999999"
        );

        barber.deactivate();
        barber.activate();

        assertTrue(barber.isActive());
    }
}
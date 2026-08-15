package br.com.boostsites.barbershop.barber.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "barbers")
public class Barber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private boolean active;

    protected Barber() {
    }

    public Barber(String name, String phone) {
        this.name = validateName(name);
        this.phone = validatePhone(phone);
        this.active = true;
    }

    public Long getId()       {return id;}
    public String getName()   {return name;}
    public String getPhone()  {return phone;}
    public boolean isActive() {return active;}

    
    public void rename(String name) {
        this.name = validateName(name);
    }

    public void changePhone(String phone) {
        this.phone = validatePhone(phone);
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }

        return name.trim();
    }

    private static String validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone must not be blank");
        }

        return phone.trim();
    }
}
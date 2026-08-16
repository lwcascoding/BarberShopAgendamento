package br.com.boostsites.barbershop.barber.service;

import br.com.boostsites.barbershop.barber.domain.Barber;
import br.com.boostsites.barbershop.barber.dto.request.CreateBarberRequest;
import br.com.boostsites.barbershop.barber.dto.request.UpdateBarberRequest;
import br.com.boostsites.barbershop.barber.dto.response.BarberResponse;
import br.com.boostsites.barbershop.barber.mapper.BarberMapper;
import br.com.boostsites.barbershop.barber.repository.BarberRepository;
import br.com.boostsites.barbershop.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BarberService {

    private final BarberRepository barberRepository;

    public BarberService(BarberRepository barberRepository) {
        this.barberRepository = barberRepository;
    }

    @Transactional
    public BarberResponse create(CreateBarberRequest request) {
        Barber barber = new Barber(
                request.name(),
                request.phone()
        );

        Barber savedBarber = barberRepository.save(barber);

        return BarberMapper.toResponse(savedBarber);
    }
    
    @Transactional(readOnly = true)
    public BarberResponse findById(Long id) {
        Barber barber = findBarberById(id);

        return BarberMapper.toResponse(barber);
    }

    @Transactional(readOnly = true)
    public List<BarberResponse> findAll() {
        return barberRepository.findAll()
                .stream()
                .map(BarberMapper::toResponse)
                .toList();
    }

    @Transactional
    public BarberResponse update(Long id, UpdateBarberRequest request) {
        Barber barber = findBarberById(id);

        barber.rename(request.name());
        barber.changePhone(request.phone());

        return BarberMapper.toResponse(barber);
    }

    @Transactional
    public BarberResponse activate(Long id) {
        Barber barber = findBarberById(id);

        barber.activate();

        return BarberMapper.toResponse(barber);
    }

    @Transactional
    public BarberResponse deactivate(Long id) {
        Barber barber = findBarberById(id);

        barber.deactivate();

        return BarberMapper.toResponse(barber);
    }

    @Transactional(readOnly = true)
    public List<BarberResponse> findAllActive() {
        return barberRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(BarberMapper::toResponse)
                .toList();
    }

    private Barber findBarberById(Long id) {
        return barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Barber not found with id: " + id
                ));
    }
}

package br.com.boostsites.barbershop.customer.service;

import br.com.boostsites.barbershop.customer.domain.Customer;
import br.com.boostsites.barbershop.customer.dto.request.CreateCustomerRequest;
import br.com.boostsites.barbershop.customer.dto.request.UpdateCustomerRequest;
import br.com.boostsites.barbershop.customer.dto.response.CustomerResponse;
import br.com.boostsites.barbershop.customer.mapper.CustomerMapper;
import br.com.boostsites.barbershop.customer.repository.CustomerRepository;
import br.com.boostsites.barbershop.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        Customer customer = new Customer(
                request.name(),
                request.phone()
        );

        Customer savedCustomer = customerRepository.save(customer);

        return CustomerMapper.toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        Customer customer = findCustomerById(id);

        return CustomerMapper.toResponse(customer);
    }

    @Transactional
    public CustomerResponse update(
            Long id,
            UpdateCustomerRequest request
    ) {
        Customer customer = findCustomerById(id);

        customer.rename(request.name());
        customer.changePhone(request.phone());

        return CustomerMapper.toResponse(customer);
    }

    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + id
                ));
    }
}
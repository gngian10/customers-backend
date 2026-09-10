package com.example.customers.service;

import com.example.customers.dto.CreateCustomerRequest;
import com.example.customers.dto.CustomerResponse;
import com.example.customers.entity.Customer;
import com.example.customers.exception.DuplicateCustomerException;
import com.example.customers.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse crearCliente(CreateCustomerRequest request) {
        if (customerRepository.existsByDni(request.dni())) {
            throw new DuplicateCustomerException("Ya existe un cliente con el DNI: " + request.dni());
        }
        if (customerRepository.existsByEmail(request.email())) {
            throw new DuplicateCustomerException("Ya existe un cliente con el email: " + request.email());
        }

        Customer customer = new Customer();
        customer.setNombre(request.nombre());
        customer.setApellido(request.apellido());
        customer.setEmail(request.email());
        customer.setDni(request.dni());
        customer.setFechaNacimiento(request.fechaNacimiento());
        customer.setFechaCreacion(LocalDateTime.now());

        Customer saved = customerRepository.save(customer);

        return toResponse(saved);
    }

    public List<CustomerResponse> buscarClientes(String dni, String email) {
        List<Customer> customers;

        if (dni != null && email != null) {
            customers = customerRepository.findByDniAndEmail(dni, email)
                    .map(List::of)
                    .orElseGet(List::of);
        } else if (dni != null) {
            customers = customerRepository.findByDni(dni)
                    .map(List::of)
                    .orElseGet(List::of);
        } else if (email != null) {
            customers = customerRepository.findByEmail(email)
                    .map(List::of)
                    .orElseGet(List::of);
        } else {
            customers = customerRepository.findAll();
        }

        return customers.stream()
                .map(this::toResponse)
                .toList();
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getNombre(),
                customer.getApellido(),
                customer.getEmail(),
                customer.getDni(),
                customer.getFechaCreacion(),
                customer.getFechaNacimiento()
        );
    }
}

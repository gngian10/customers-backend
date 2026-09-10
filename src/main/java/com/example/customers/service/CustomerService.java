package com.example.customers.service;

import com.example.customers.dto.CreateCustomerRequest;
import com.example.customers.dto.CustomerResponse;
import com.example.customers.entity.Customer;
import com.example.customers.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse crearCliente(CreateCustomerRequest request) {
        if (customerRepository.existsByDni(request.dni())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con el DNI: " + request.dni());
        }
        if (customerRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con el email: " + request.email());
        }

        Customer customer = new Customer();
        customer.setNombre(request.nombre());
        customer.setApellido(request.apellido());
        customer.setEmail(request.email());
        customer.setDni(request.dni());
        customer.setFechaNacimiento(request.fechaNacimiento());
        customer.setFechaCreacion(LocalDateTime.now());

        Customer saved = customerRepository.save(customer);

        return new CustomerResponse(
                saved.getId(),
                saved.getNombre(),
                saved.getApellido(),
                saved.getEmail(),
                saved.getDni(),
                saved.getFechaCreacion(),
                saved.getFechaNacimiento()
        );
    }
}

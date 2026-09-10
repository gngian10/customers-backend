package com.example.customers.service;

import com.example.customers.dto.BirthRateByMonthResponse;
import com.example.customers.dto.CreateCustomerRequest;
import com.example.customers.dto.CustomerIndicatorsResponse;
import com.example.customers.dto.CustomerResponse;
import com.example.customers.entity.Customer;
import com.example.customers.exception.DuplicateCustomerException;
import com.example.customers.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

    public CustomerIndicatorsResponse obtenerIndicadores() {
        List<Customer> customers = customerRepository.findAll();
        long total = customers.size();

        Map<YearMonth, Long> conteoPorMes = customers.stream()
                .collect(Collectors.groupingBy(
                        c -> YearMonth.from(c.getFechaNacimiento()),
                        TreeMap::new,
                        Collectors.counting()
                ));

        List<BirthRateByMonthResponse> natalidadPorMesAnio = conteoPorMes.entrySet().stream()
                .map(entry -> toBirthRateResponse(entry.getKey(), entry.getValue(), total))
                .toList();

        BirthRateByMonthResponse mesAnioConMayorNatalidad = natalidadPorMesAnio.stream()
                .max(Comparator.comparingLong(BirthRateByMonthResponse::cantidad))
                .orElse(null);

        BirthRateByMonthResponse mesAnioConMenorNatalidad = natalidadPorMesAnio.stream()
                .min(Comparator.comparingLong(BirthRateByMonthResponse::cantidad))
                .orElse(null);

        return new CustomerIndicatorsResponse(natalidadPorMesAnio, mesAnioConMayorNatalidad, mesAnioConMenorNatalidad);
    }

    private BirthRateByMonthResponse toBirthRateResponse(YearMonth yearMonth, long cantidad, long total) {
        double tasaNatalidad = total == 0 ? 0.0 : (cantidad * 100.0) / total;
        double tasaRedondeada = Math.round(tasaNatalidad * 100.0) / 100.0;

        return new BirthRateByMonthResponse(
            yearMonth.getMonthValue(),
            yearMonth.getYear(), 
            cantidad, 
            tasaRedondeada
        );
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

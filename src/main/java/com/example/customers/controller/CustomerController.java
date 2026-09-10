package com.example.customers.controller;

import com.example.customers.dto.CreateCustomerRequest;
import com.example.customers.dto.CustomerResponse;
import com.example.customers.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> crearCliente(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerService.crearCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> consultarClientes(
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String email) {
        List<CustomerResponse> response = customerService.buscarClientes(dni, email);
        return ResponseEntity.ok(response);
    }
}

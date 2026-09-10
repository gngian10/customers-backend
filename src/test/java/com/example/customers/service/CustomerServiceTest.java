package com.example.customers.service;

import com.example.customers.dto.BirthRateByMonthResponse;
import com.example.customers.dto.CreateCustomerRequest;
import com.example.customers.dto.CustomerIndicatorsResponse;
import com.example.customers.dto.CustomerResponse;
import com.example.customers.entity.Customer;
import com.example.customers.exception.DuplicateCustomerException;
import com.example.customers.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void deberiaCrearClienteExitosamenteCuandoDniYEmailNoExisten() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "John", "Doe", "john.doe@example.com", "12345678", LocalDate.of(1990, 5, 20)
        );

        when(customerRepository.existsByDni(request.dni())).thenReturn(false);
        when(customerRepository.existsByEmail(request.email())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(1L);
            return customer;
        });

        CustomerResponse response = customerService.crearCliente(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nombre()).isEqualTo("John");
        assertThat(response.apellido()).isEqualTo("Doe");
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.dni()).isEqualTo("12345678");
        assertThat(response.fechaNacimiento()).isEqualTo(LocalDate.of(1990, 5, 20));
        assertThat(response.fechaCreacion()).isNotNull();

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void deberiaRechazarCreacionCuandoElDniYaExiste() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Michael", "Brown", "michael.brown@example.com", "87654321", LocalDate.of(1985, 3, 15)
        );

        when(customerRepository.existsByDni(request.dni())).thenReturn(true);

        assertThatThrownBy(() -> customerService.crearCliente(request))
                .isInstanceOf(DuplicateCustomerException.class);

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deberiaCalcularIndicadoresAgrupadosPorMesYAnio() {
        List<Customer> customers = List.of(
                clienteConFechaNacimiento("Anna", "Taylor", LocalDate.of(2020, 1, 5)),
                clienteConFechaNacimiento("John", "Doe", LocalDate.of(2020, 1, 18)),
                clienteConFechaNacimiento("Michael", "Brown", LocalDate.of(2020, 1, 27)),
                clienteConFechaNacimiento("Anna", "Taylor", LocalDate.of(2020, 2, 10)),
                clienteConFechaNacimiento("John", "Doe", LocalDate.of(2021, 1, 3)),
                clienteConFechaNacimiento("Michael", "Brown", LocalDate.of(2021, 1, 22))
        );

        when(customerRepository.findAll()).thenReturn(customers);

        CustomerIndicatorsResponse indicadores = customerService.obtenerIndicadores();

        assertThat(indicadores.natalidadPorMesAnio()).hasSize(3);

        BirthRateByMonthResponse enero2020 = buscarPorMesYAnio(indicadores.natalidadPorMesAnio(), 1, 2020);
        assertThat(enero2020.cantidad()).isEqualTo(3);
        assertThat(enero2020.tasaNatalidad()).isEqualTo(50.0);

        BirthRateByMonthResponse febrero2020 = buscarPorMesYAnio(indicadores.natalidadPorMesAnio(), 2, 2020);
        assertThat(febrero2020.cantidad()).isEqualTo(1);
        assertThat(febrero2020.tasaNatalidad()).isEqualTo(16.67);

        BirthRateByMonthResponse enero2021 = buscarPorMesYAnio(indicadores.natalidadPorMesAnio(), 1, 2021);
        assertThat(enero2021.cantidad()).isEqualTo(2);
        assertThat(enero2021.tasaNatalidad()).isEqualTo(33.33);

        assertThat(indicadores.mesAnioConMayorNatalidad().mes()).isEqualTo(1);
        assertThat(indicadores.mesAnioConMayorNatalidad().anio()).isEqualTo(2020);
        assertThat(indicadores.mesAnioConMayorNatalidad().cantidad()).isEqualTo(3);

        assertThat(indicadores.mesAnioConMenorNatalidad().mes()).isEqualTo(2);
        assertThat(indicadores.mesAnioConMenorNatalidad().anio()).isEqualTo(2020);
        assertThat(indicadores.mesAnioConMenorNatalidad().cantidad()).isEqualTo(1);
    }

    private Customer clienteConFechaNacimiento(String nombre, String apellido, LocalDate fechaNacimiento) {
        Customer customer = new Customer();
        customer.setNombre(nombre);
        customer.setApellido(apellido);
        customer.setFechaNacimiento(fechaNacimiento);
        return customer;
    }

    private BirthRateByMonthResponse buscarPorMesYAnio(List<BirthRateByMonthResponse> lista, int mes, int anio) {
        return lista.stream()
                .filter(item -> item.mes() == mes && item.anio() == anio)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No se encontró el mes/año esperado: " + mes + "/" + anio));
    }
}

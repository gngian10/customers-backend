package com.example.customers.dto;

import java.util.List;

public record CustomerIndicatorsResponse(
        List<BirthRateByMonthResponse> natalidadPorMesAnio,
        BirthRateByMonthResponse mesAnioConMayorNatalidad,
        BirthRateByMonthResponse mesAnioConMenorNatalidad
) {
}
